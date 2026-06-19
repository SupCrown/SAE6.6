package androidgameserver;

import java.util.ArrayList;

import javax.swing.JFrame;

import androidgameserver.aicontrollers.AIController;
import androidgameserver.aicontrollers.AIDumb;
import androidgameserver.aicontrollers.AIRandom;
import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.maps.MAP_CTP;
import androidgameserver.environment.maps.MAP_FFA;
import androidgameserver.environment.maps.MAP_TDM;

/**
 * Point d'entrée de l'application et contrôleur principal du serveur de jeu.
 *
 * Responsabilités :
 * - Afficher le menu de sélection du mode de jeu au démarrage.
 * - Créer l'environnement de simulation (carte) selon le mode choisi.
 * - Lancer le {@link ServerThread} qui accepte les connexions TCP.
 * - Exécuter la boucle de simulation ({@link #runTask()}) dans le thread courant.
 * - Fournir des méthodes statiques pour ajouter/supprimer des bots IA en cours de partie.
 */
public class AndroidGameServer {

	static public boolean stop = false;
	static boolean endTask = false;
	static boolean makeGui = true;
	static public AppFrame frame = null;
	static public JFrame envframe;
	static Environment2DMultiBot map;
    public static String selectedMode = "FFA";

	private static int port = 6969;
	static ServerThread serv;

	protected static long timePrev = 0;
	/** Délai minimal entre deux étapes de simulation (ms). 0 = pas de throttle. */
	protected static float throttleDelay = 4;

	protected static ArrayList<AIController> ai = new ArrayList<>();

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameModeMenu().setVisible(true);
        });
    }
	
	/**
	 * Boucle principale de simulation : exécute les étapes physiques et logiques
	 * jusqu'à ce que {@code endTask} soit vrai ou qu'une interruption de répétition survienne.
	 * Chaque itération appelle {@code preStepOps / doWorldStep / doStep / postStepOps}
	 * sur la carte active et {@code doStep} sur chaque IA locale.
	 */
	public static void runTask()
	{
		map.preRunOps();
		while(!endTask)
		{
			map.reset();
			map.preRepetitionOps();
			while(!endTask && !map.hasTriggeredInterrupt())
			{
				if(throttleDelay <= 0 || System.currentTimeMillis()-timePrev>throttleDelay)
				{
					if(!map.isPaused())
					{
						map.preStepOps();
						map.doWorldStep();
						map.doStep();
						map.postStepOps();	
						for(AIController a : ai)
							a.doStep();
					}
					envframe.repaint();
					timePrev = System.currentTimeMillis();			
				}
				else
				{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}				
				}				
			}
			map.postRepetitionOps();
		}
		map.postRunOps();
	}

    /** Lance le serveur immédiatement (mode FFA) sans fenêtre d'attente. */
    public static void startServerDirect() {
        createAndShowGUI();
        createAndShowGame();

        serv = new ServerThread(port);
        serv.start();

        runTask();
    }

    public static int getPlayerCountInMap() {
        if (map == null || map.getBots() == null) {
            return 0;
        }

        return map.getBots().size();
    }

    /**
     * Lance le serveur en mode équipe : attend qu'au moins 2 joueurs soient connectés,
     * décompte 10 secondes, puis ferme {@code window} et démarre la simulation.
     */
    public static void startServerWithWaitingWindow(TeamWaitingWindow window) {
        createAndShowGUI();

        createGame();

        serv = new ServerThread(port);
        serv.start();

        new Thread(() -> {
            while (getPlayerCountInMap() < 2) {
                int count = getPlayerCountInMap();
                window.updateStatus("Joueurs/bots présents : " + count + " / 2 minimum");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int i = 10; i > 0; i--) {
                int count = getPlayerCountInMap();
                window.updateStatus("Joueurs/bots présents : " + count + " | Lancement dans " + i + " secondes...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                javax.swing.SwingUtilities.invokeAndWait(() -> {
                    window.dispose();
                    showGameWindow();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            runTask();
        }).start();
    }

    private static void createGame() {
        if (selectedMode.equals("CTP")) {
            map = new MAP_CTP();
        } else if (selectedMode.equals("TDM")) {
            map = new MAP_TDM();
        } else {
            map = new MAP_FFA();
        }

        map.init();
    }

    private static void showGameWindow() {
        Env2DViewer viewer = new Env2DViewer(map);
        envframe = new JFrame("Map");
        envframe.add(viewer);
        envframe.setSize(800, 600);
        envframe.setVisible(true);
    }

    private static void createAndShowGame() {
        createGame();
        showGameWindow();
    }

	private static void createAndShowGUI() {
		frame = new AppFrame();
    }

	public static Environment2DMultiBot getMap() {
		return map;
	}
	
	/** Ajoute un bot IA aléatoire ({@link AIRandom}) dans la carte courante. */
	public static synchronized void addRandomAI()
	{
			BotBody2D bot;
			bot = AndroidGameServer.getMap().addBot(null);
			bot.registerBotToWorld();
			if(AndroidGameServer.getMap().placeBot(bot,10,3, 100))
			{
				ai.add(new AIRandom(bot));
				bot.setActive(true);
			}
			else
			{
				bot.removeBotFromWorld();
				AndroidGameServer.getMap().removeBot(bot);
			}
	}
	
	/** Ajoute un bot IA statique ({@link AIDumb}) dans la carte courante. */
	public static synchronized void addDumbAI()
	{
		BotBody2D bot;
		bot = AndroidGameServer.getMap().addBot(null);
		bot.registerBotToWorld();
		if(AndroidGameServer.getMap().placeBot(bot,10,3, 100))
		{
			ai.add(new AIDumb(bot));
			bot.setActive(true);
		}
		else
		{
			bot.removeBotFromWorld();
			AndroidGameServer.getMap().removeBot(bot);
		}
	}
	
	/** Supprime tous les bots IA actuellement en jeu. */
	public static synchronized void removeBots()
	{
		for(AIController a : ai)
		{
			a.remove();
		}
		ai.clear();
	}
}
