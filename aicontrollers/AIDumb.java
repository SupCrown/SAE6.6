package androidgameserver.aicontrollers;

import androidgameserver.bot.BotBody2D;

public class AIDumb extends AIController {
	
	public AIDumb(BotBody2D b)
	{
		super(b,"DumbAI");

		bot.getActuatorValues().put("GunTrig", 0.6);
		bot.setMessage("derp!");	
	}

	public void doStep() {
		
	}
}
