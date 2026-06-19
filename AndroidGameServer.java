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
	protected static float throttleDelay = 4;
	
	protected static ArrayList<AIController> ai = new ArrayList<>();

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameModeMenu().setVisible(true);
        });
    }
	
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
	
	public static synchronized void removeBots()
	{
		for(AIController a : ai)
		{
			a.remove();
		}
		ai.clear();
	}
}
