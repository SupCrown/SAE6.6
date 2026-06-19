package androidgameserver.aicontrollers;

import androidgameserver.bot.BotBody2D;

/**
 * IA statique : maintient le canon en position de tir mais ne se déplace jamais.
 * Sert de cible fixe ou de test minimal de la détection des projectiles.
 */
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
