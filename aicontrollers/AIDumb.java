package androidgameserver.aicontrollers;

import java.awt.Color;

import androidgameserver.bot.BotBody2D;

public class AIDumb extends AIController {
	
	public AIDumb(BotBody2D b)
	{
		super(b,"DumbAI");
		bot.setColor(new Color(0x12FF12));
		bot.getActuatorValues().put("GunTrig", 0.6);
		bot.setMessage("derp!");	
	}

	public void doStep() {
		
	}
}
