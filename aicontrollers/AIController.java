package androidgameserver.aicontrollers;

import androidgameserver.AndroidGameServer;
import androidgameserver.bot.BotBody2D;

/**
 * Classe de base des contrôleurs IA locaux (tournant dans le processus serveur).
 * Chaque sous-classe implémente {@link #doStep()} pour piloter son bot à chaque tick.
 * À la différence des clients réseau, les IA partagent directement les valeurs d'actionneurs
 * du bot sans passer par le protocole TCP.
 */
public class AIController {
	BotBody2D bot = null;
	private static int aiCount = 1;
	
	public AIController(BotBody2D b, String name)
	{
		bot = b; 
		bot.setName(name+' '+aiCount);
		aiCount++;
	}

	public void doStep() {
	}

	public void remove() {
		bot.removeBotFromWorld();
		AndroidGameServer.getMap().removeBot(bot);
	}
}
