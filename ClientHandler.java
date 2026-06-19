package androidgameserver;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;

import androidgameserver.bot.BotBody2D;
import androidgameserver.bot.WarBot;
import androidgameserver.environment.worldElements.Projectile;

/**
 * Thread réseau associé à un client connecté.
 *
 * À la connexion, {@link #joinGame()} crée et place un {@link BotBody2D} dans l'environnement.
 * La boucle principale de {@link #run()} lit les lignes envoyées par le client et les délègue
 * à {@link #processCommand(String)} qui met à jour les valeurs d'actionneurs du bot ou répond
 * aux requêtes d'information sur l'environnement. La connexion est fermée proprement en cas
 * d'inactivité (timeout), d'erreur réseau ou sur réception de la commande {@code EXIT}.
 */
public class ClientHandler implements Runnable{

	private final Socket soc;
	private final BufferedReader dis;
	private final PrintWriter dos;
	private BotBody2D bot;
	private static long connectionCount = 0;
	private boolean interrupt = false;
	long prevTime;

	public ClientHandler(Socket s, BufferedReader dis, PrintWriter dos) {
		this.soc = s;
		this.dis = dis;
		this.dos = dos;
	}

	public void run() {
		String received;
		boolean run = true;
		//System.out.println(this + " started run");
		if (joinGame()) {
			bot.setActive(true);
			prevTime = System.currentTimeMillis();
			while (run && !interrupt) {
				try {
					if (System.currentTimeMillis() - prevTime < AppDefines.connectionTimeoutDelay) {
						received = dis.readLine();
						if (received != null) {
							prevTime = System.currentTimeMillis();
							received = received.trim();
							if (received.contains(AppDefines.quit)) {
								System.out.println("Client " + this.soc + " sends exit...");
								closeSocket();
								quit();
								run = false;
							} else {
								// System.out.println(received);
								if (bot != null)
									processCommand(received);
							}
						}

					}else
					{
						run = false;
						closeSocket();
						quit();
						System.out.println("Client " + this.soc + " timed out ");
					}
				} catch (IOException e) {
					run = false;
					closeSocket();
					quit();
					System.out.println("Client " + this.soc + " connection error: " + e.getMessage());
				} catch (NullPointerException e) {
					run = false;
					closeSocket();
					quit();
					System.err.println("Null reference in client handler for " + this.soc + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Game full");
			closeSocket();
		}
		//System.out.println(this + " ended run");
	}

	private void closeSocket() {
		System.out.println("Closing this connection.");
		if (soc != null) {
			try {
				this.soc.close();
			} catch (IOException e) {
				System.err.println("Error closing socket: " + e.getMessage());
			}
		}
		System.out.println("Connection closed");
	}

	synchronized private void quit() {
		bot.removeBotFromWorld();
		AndroidGameServer.getMap().removeBot(bot);
	}

	/**
	 * Interprète une ligne de commandes reçue du client.
	 * Plusieurs commandes peuvent être séparées par {@link AppDefines#commandSep} (#).
	 * Les actionneurs connus sont mis à jour directement ; les requêtes d'information
	 * accumulent une réponse envoyée en une seule fois en fin de méthode.
	 */
	private void processCommand(String received) throws IOException {
		// "MotL" "MotR" "GunTrig"
		String[] cut = received.split(AppDefines.commandSep);
		String response = "";
		for (String com : cut) {
			String[] kv = com.split(AppDefines.argumentSep);
			if (bot.getActuatorValues().containsKey(kv[0]) && kv.length > 1)
				bot.getActuatorValues().put(kv[0],
						Math.min(Math.max(Double.valueOf(kv[1].replace(',', '.')), 0.0), 1.0));
			else if (kv[0].equals(AppDefines.setName) && kv.length > 1)
				bot.setName(kv[1]);
			else if (kv[0].equals(AppDefines.message) && kv.length > 1) {
				bot.setMessage(kv[1]);
			} else if (kv[0].equals(AppDefines.queryByName) && kv.length > 1) {
				BotBody2D b = bot.getEnvironment().getBotByName(kv[1]);
				if (b == null)
					response = addToResponse(response, AppDefines.emptyReply);
				else
					response = addToResponse(response, b.elementInfoToString(bot));
			} else if (kv[0].equals(AppDefines.queryMessagesFromUser) && kv.length > 1) {
				BotBody2D b = bot.getEnvironment().getBotByName(kv[1]);
				if (b == null || b.message == null)
					response = addToResponse(response, AppDefines.emptyReply);
				else
					response = addToResponse(response, b.message);
			} else if (kv[0].equals(AppDefines.queryClosestBot)) {

				BotBody2D b = bot.getClosestBot();
				if (b == null)
					response = addToResponse(response, AppDefines.emptyReply);
				else
					response = addToResponse(response, b.elementInfoToString(bot));
			} else if (kv[0].equals(AppDefines.queryClosestProjectile)) {
				Projectile b = ((WarBot) bot).getClosestProjectile();
				if (b == null)
					response = addToResponse(response, AppDefines.emptyReply);
				else
					response = addToResponse(response, b.elementInfoToString(bot));
			} else if (kv[0].equals(AppDefines.queryNameList))
				response = addToResponse(response, makeNameList());
			else if (kv[0].equals(AppDefines.queryOrientation))
				response = addToResponse(response,
						String.valueOf((((WarBot) bot).getNormalizedAngle() + MathUtils.PI) / MathUtils.TWOPI));
			else if (kv[0].equals(AppDefines.skin))
			{
				if(kv.length >= 3)
				{
					bot.customSkin = new ArrayList<>();
					for(int i = 1 ; i < kv.length; i+=2)
					{
						bot.customSkin.add(new Vec2(
							Math.max(-3f,Math.min(3f,Float.parseFloat(kv[i]))),
							Math.max(-3f,Math.min(3f,Float.parseFloat(kv[i+1])))));
					}					
				}
			}
		}
		if (!response.equals("")) {
			// System.out.println(response);
			dos.println(response);
			dos.flush();
		}
	}

	private String makeNameList() {
		String list = "";
		for (BotBody2D b : bot.getEnvironment().getBots()) {
			if (list != "")
				list += "=";
			list += b.name;
		}
		return list;
	}

	public String addToResponse(String resp, String msg) {
		if (resp != "")
			resp += "#";
		return resp += msg;
	}

	/**
	 * Crée le bot du client dans la carte et le place à une position valide.
	 * @return {@code true} si le placement a réussi (partie pas pleine), {@code false} sinon.
	 */
	synchronized public boolean joinGame() {
		connectionCount++;
		AndroidGameServer.getMap().addBot(this);
		this.bot.registerBotToWorld();
		if (!AndroidGameServer.getMap().placeBot(bot, 10, 3, 100)) {
			quit();
			return false;
		}
		return true;
	}

	public void setBot(BotBody2D bot) {
		this.bot = bot;
	}

	public static String makeDefaultName() {
		return "Player " + connectionCount;
	}

	public boolean isOffline() {
		return soc.isClosed();
	}
}
