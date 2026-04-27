package androidgameserver.aicontrollers;

import androidgameserver.AndroidGameServer;
import androidgameserver.bot.BotBody2D;

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
