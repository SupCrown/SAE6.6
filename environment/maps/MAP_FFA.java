package androidgameserver.environment.maps;

import org.jbox2d.common.Vec2;

import androidgameserver.AppDefines;
import androidgameserver.ClientHandler;
import androidgameserver.bot.BotBody2D;
import androidgameserver.bot.WarBot;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Mode Free For All : chaque bot combat seul contre tous.
 * Chaque connexion crée une équipe individuelle à couleur aléatoire.
 * Le classement est trié par score décroissant et affiché en temps réel dans la fenêtre de rendu.
 * Un score limite optionnel ({@link AppDefines#scoreLimit}) met fin à la partie.
 * Cette classe sert aussi de base aux autres modes (CTP, TDM) via l'héritage.
 */
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
            // copie pour l'affichage : on ne réordonne pas la liste partagée (lue par le thread de rendu).
            List<BotBody2D> bots = new ArrayList<>(getBots());
            bots.sort(Comparator.comparingInt(o -> ((WarBot) o).getScore() * (-1)));
			for (BotBody2D b : bots) {
                WarBot wb = (WarBot) b;

                if (!wb.isDead()) {
                    addMessage(wb.getScore() + "       ♥ " + wb.getLives() + "       " + b.getName());
                }
            }

            addMessage("");
            addMessage("Morts : Réapparition");

            for (BotBody2D b : getBots()) {
                WarBot wb = (WarBot) b;

                if (wb.isDead()) {
                    long remainingTicks = wb.getRespawnTick() - getTick();
                    long remainingSeconds = remainingTicks / 50;
                    if (remainingSeconds < 0) remainingSeconds = 0;

                    addMessage(b.getName() + "       " + remainingSeconds + "s");
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
			for (BotBody2D b : getBots()) {
				((WarBot) b).reinit();
				placeBot(b, 10, 3, 100);
			}
		}
	}

	@Override
	synchronized public BotBody2D addBot(ClientHandler clientHandler) {
		super.addBot(clientHandler);
		// En FFA, chaque bot est seul dans sa propre équipe (id/nom/couleur générés automatiquement).
		Team team = createTeam();
		BotBody2D bot = makeBot(ClientHandler.makeDefaultName(), clientHandler, team);
		bots.add(bot);
		if (clientHandler != null)
			clientHandler.setBot(bot);
		return bot;
	}
}
