package androidgameserver;

import java.util.ArrayList;

import javax.swing.JFrame;

import androidgameserver.aicontrollers.AIController;
import androidgameserver.aicontrollers.AIDumb;
import androidgameserver.aicontrollers.AIRandom;
import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.maps.MAP_FFA;

public class AndroidGameServer {

	static public boolean stop = false;
	static boolean endTask = false;
	static boolean makeGui = true;
	static public AppFrame frame = null;
	static public JFrame envframe;
	static Environment2DMultiBot map;

	private static int port = 6969;
	static ServerThread serv;
	
	protected static long timePrev = 0;
	protected static float throttleDelay = 4;
	
	protected static ArrayList<AIController> aiBots = new ArrayList<>();
	
	public static void main(String[] args) {
		createAndShowGUI();
		createAndShowGame();
		try {
			serv = new ServerThread(port);
			serv.start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		runTask();
	}
	
	public static void runTask()
	{
		//map.preRunOps();
		while(!endTask)
		{
			map.reset();
			//map.preRepetitionOps();
			while(!endTask && !map.hasTriggeredInterrupt())
			{
				if(throttleDelay <= 0 || System.currentTimeMillis()-timePrev>throttleDelay)
				{
					if(!map.isPaused())
					{
						//map.preStepOps();
						map.doWorldStep();
						map.doStep();
						map.postStepOps();	
						for(AIController ai : aiBots)
							ai.doStep();
					}
					envframe.repaint();
					timePrev = System.currentTimeMillis();			
				}
				else
				{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						
						e1.printStackTrace();
					}					
				}				
			}
			map.postRepetitionOps();
		}
		//map.postRunOps();
	}

	private static void createAndShowGame() {
		map = new MAP_FFA();
		map.init();
		Env2DViewer viewer = new Env2DViewer(map);
		envframe = new JFrame("Map");
		envframe.add(viewer);
		envframe.setSize(800, 600);
		envframe.setVisible(true);
	}

	private static void createAndShowGUI() {
		frame = new AppFrame();
    }

	public static Environment2DMultiBot getMap() {
		return map;
	}
	
	public static synchronized void addRandomAI()
	{
			BotBody2D bot;
			bot = AndroidGameServer.getMap().addBot(null);
			bot.registerBotToWorld();
			if(AndroidGameServer.getMap().placeBot(bot,10,3, 100))
			{
				aiBots.add(new AIRandom(bot));
				bot.setActive(true);
			}
			else
			{
				bot.removeBotFromWorld();
				AndroidGameServer.getMap().removeBot(bot);
			}
	}
	
	public static synchronized void addDumbAI()
	{
		BotBody2D bot;
		bot = AndroidGameServer.getMap().addBot(null);
		bot.registerBotToWorld();
		if(AndroidGameServer.getMap().placeBot(bot,10,3, 100))
		{
			aiBots.add(new AIDumb(bot));
			bot.setActive(true);
		}
		else
		{
			bot.removeBotFromWorld();
			AndroidGameServer.getMap().removeBot(bot);
		}
	}
	
	public static synchronized void removeBots()
	{
		for(AIController a : aiBots)
		{
			a.remove();
		}
		aiBots.clear();
	}
}
