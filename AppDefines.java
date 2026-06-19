package androidgameserver;

/**
 * Constantes globales de configuration du serveur de jeu.
 * Toutes les valeurs sont publiques et modifiables à chaud via la fenêtre d'options ({@link AppFrame}).
 */
public class AppDefines {

	// ENVIRONMENT

    // Vies
    public static int maxLives = 3;

    // Mort
    public static long deathRespawnTime = 600;

	// calculation per sec (or step length): 60 = fine , 30 = cheaper
	public static float targetFPS2DPhysics = 30.0f;
	public static float arenaWidth = 100; 
	public static float arenaHeight = 80; 
    public static double ObstacleProbability = 0.3;

	//BOT Gameplay
	public final static String[] gameModes = {"FreeForAll","TeamDeathMatch"};
	public static int bot_RateOfFire = 100; //ticks
	public static long penaltyTime = 1000; //ticks
	public static int connectionTimeoutDelay = 10*1000; // 10 seconds
	public static int messageDuration = 1000; //ticks
	public static int messageLength = 40; //characters
	public static int scoreLimit = -1; //points
	
	//USER command keywords
	public static String setName 		= "NAME"; 	// arg: string
	// args: 3 integers for RGB 
	public static String setColor 		= "COL";	
	public static String quit 			= "EXIT";	// no arguments
	public static String alive 			= "LIVE";	// no arguments
	public static String message 		= "MSG";	// arg string : a short message 
	public static String skin 			= "SKN";	// arg : a set of floats [-3,3] representing 2d points  

	public static String queryClosestBot 		= "CBOT";	// no arguments
	public static String queryClosestProjectile	= "CPROJ";	// no arguments 
	public static String queryByName	 		= "NBOT";	// arg string : name of the player 
	public static String queryNameList	 		= "NLIST";	// no arguments
	public static String queryOrientation	 	= "ORIENT";	// no arguments
	public static String queryMessagesFromUser	= "USRMSG";	// arg string : name of the player

	public static String emptyReply	 			= "EMPTY";	// no arguments
	
	
	public static String commandSep		= "#";
	public static String argumentSep	= "=";	
}
