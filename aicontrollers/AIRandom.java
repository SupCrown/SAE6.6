package androidgameserver.aicontrollers;

import androidgameserver.bot.BotBody2D;

/**
 * IA aléatoire : tire de manière aléatoire et change de direction toutes les 200 ticks.
 * Utile pour peupler rapidement l'arène avec des adversaires imprévisibles.
 */
public class AIRandom extends AIController{
	
	public AIRandom(BotBody2D b)
	{
		super(b,"RandomAI");

		bot.setMessage("hello meatbags!");	
		bot.getActuatorValues().put("MotL", 0.6+(Math.random()*0.4));
		bot.getActuatorValues().put("MotR", 0.6+(Math.random()*0.4));
		bot.getActuatorValues().put("GunTrig", Math.random()*0.7);		
	}

	public void doStep() {
		if(bot.getEnvironment().getTick()%200 == 0)
		{
			bot.getActuatorValues().put("MotL", 0.6+(Math.random()*0.4));
			bot.getActuatorValues().put("MotR", 0.6+(Math.random()*0.4));
			bot.getActuatorValues().put("GunTrig", Math.random()*0.7);			
		}
	}
}
