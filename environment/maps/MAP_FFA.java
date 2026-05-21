package androidgameserver.environment.maps;

import org.jbox2d.common.Vec2;

import androidgameserver.AppDefines;
import androidgameserver.ClientHandler;
import androidgameserver.bot.BotBody2D;
import androidgameserver.bot.WarBot;
import androidgameserver.environment.Environment2DMultiBot;

public class MAP_FFA extends Environment2DMultiBot {
	WarBot winner = null;

	public MAP_FFA() {
		super();
		this.name = "FFA";
		hasObstacles = true;
		hasBorders = true;
		obstacleSpacing = 20;
		minObstacleSize = 3;
		maxObstacleSizeVariability = 2;
		maxObstacleSpacingVariability = 18;

	}

	@Override
	public void init() {
		super.init();
		makeWorld();
		makeBotConstructor("WarBot");
	}

	@Override
	protected Vec2 generatePositionInBoundaries(float margin) {
		return new Vec2((float) (Math.random() * (getCorners()[1].x - getCorners()[0].x - (2 * margin)))
				+ getCorners()[3].x + margin, (float) (Math.random() * (getCorners()[0].y - margin)));
	}

	protected Vec2 generateLocalPosition(Vec2 ref, float spread) {
		return new Vec2((float) ((Math.random() * spread * 2) + ref.x - spread),
				(float) ((Math.random() * spread * 2) + ref.y - spread));
	}

	protected Vec2 generateLocalPositionMinDist(Vec2 ref, float minDist, float spread) {
		float ang = (float) (Math.random() * Math.PI * 2);
		return new Vec2((float) ((Math.random() * spread) + ref.x + (Math.sin(ang) * minDist)),
				(float) ((Math.random() * spread) + ref.y + (Math.cos(ang) * minDist)));
	}

	@Override
	public void preRunOps() {
		super.preRunOps();
	}

	@Override
	public void reset() {
		super.reset();
		winner = null;
	}

	public void postStepOps() {
		super.postStepOps();
		clearMessages();
		if (AppDefines.scoreLimit > 0)
			addMessage("Score : Name  (Match in " + AppDefines.scoreLimit + " points):");
		else
            addMessage("Score : Vies : Name");
        if (getBots() != null && getBots().size() > 0) {
            for (BotBody2D b : getBots().get(NOTEAM_ID)) {
                WarBot wb = (WarBot) b;

                if (!wb.isDead()) {
                    addMessage(wb.getScore() + "       ♥ " + wb.getLives() + "       " + b.getName());
                }
            }

            addMessage("");
            addMessage("Morts : Réapparition");

            for (BotBody2D b : getBots().get(NOTEAM_ID)) {
                WarBot wb = (WarBot) b;

                if (wb.isDead()) {
                    long remaining = wb.getRespawnTick() - getTick();
                    if (remaining < 0) remaining = 0;

                    addMessage(b.getName() + "       " + remaining);
                }
            }
        }
		if (winner != null) {
			setWinMessage(winner.getName() + " wins !");
			paused = true;
		}
	}

	@Override
	public void postRepetitionOps() {
		if (getBots() != null && getBots().size() > 0) {
			for (BotBody2D b : getBots().get(NOTEAM_ID)) {
				((WarBot) b).reinit();
				placeBot(b, 10, 3, 100);
			}
		}
	}

	@Override
	synchronized public BotBody2D addBot(ClientHandler clientHandler) {
		super.addBot(clientHandler);
		BotBody2D bot = makeBot("", ClientHandler.makeDefaultName(), NOTEAM_ID,clientHandler);
		bots.get(NOTEAM_ID).add(bot);
		if (clientHandler != null)
			clientHandler.setBot(bot);
		return bot;
	}
}
