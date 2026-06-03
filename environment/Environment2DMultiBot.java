/*******************************************************************************
 * EvoAgents : A simulation platform for agents using the MIND architecture
 * Copyright (c) 2016, 2020 Suro François (suro@lirmm.fr)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package androidgameserver.environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import androidgameserver.bot.WarBot;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;

import androidgameserver.AppDefines;
import androidgameserver.ClientHandler;
import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.worldElements.ControlledElement;
import androidgameserver.environment.worldElements.Debris;
import androidgameserver.environment.worldElements.Projectile;
import androidgameserver.environment.worldElements.VirtualWorldElement;

public class Environment2DMultiBot extends Environment2D {

	public static final int NOTEAM_ID = 0;

	protected ArrayList<ArrayList<BotBody2D>> bots = new ArrayList<ArrayList<BotBody2D>>();
	protected ArrayList<ArrayList<Vec2>> startPos = new ArrayList<ArrayList<Vec2>>();
	protected ArrayList<ArrayList<Double>> startAngle = new ArrayList<ArrayList<Double>>();
	protected Constructor<?> botConstructor;

	protected long debrisLifespan = 100;
	protected ArrayList<BotBody2D> botsInPenalty = new ArrayList<>();
	protected ArrayList<Long> botsReleaseTime = new ArrayList<>();

	protected ArrayList<Projectile> projectileScheduleForRemoval = new ArrayList<>();
	protected ArrayList<ArrayList<BotBody2D>> botScheduleForRemoval = new ArrayList<>();
	protected HashMap<BotBody2D, ArrayList<Projectile>> projectiles = new HashMap<BotBody2D, ArrayList<Projectile>>();
	protected ArrayList<Debris> debrisList = new ArrayList<>();

	public Environment2DMultiBot() {
		super();
	}

	public void botToPenalty(BotBody2D warBot) {
		botsInPenalty.add(warBot);
		botsReleaseTime.add(tick + 1 + AppDefines.penaltyTime);
	}

	protected void checkPenalty() {
		int i = 0;
		while (i < botsReleaseTime.size()) {
			BotBody2D b = botsInPenalty.get(i);
			if (botsReleaseTime.get(i) <= tick) {

				botsReleaseTime.remove(i);
				botsInPenalty.remove(i);
				if (b.ID == NOTEAM_ID) {
					// FFA
					placeBot(b, 10, 3, 100);
				} else {
					// Set something for TDM ...
					/*
					 * b.setWorldPosition(startPos.get(1).get(b.ID), 0); team2++;
					 */
				}
				b.reset();
				b.setActive(true);
			} else if (botsReleaseTime.get(i) == tick + AppDefines.penaltyTime) {
				destructionEffect(b);
				b.reset();
				b.setWorldPosition(new Vec2(getCorners()[1].x + 50 + (5 * botsInPenalty.size()),
						getCorners()[1].y + 50 + (5 * botsInPenalty.size())), 0);
				b.setActive(false);
			}
			i++;
		}
	}

    protected void checkDeadBots() {
        if (bots.size() <= NOTEAM_ID || bots.get(NOTEAM_ID) == null) {
            return;
        }

        for (BotBody2D b : bots.get(NOTEAM_ID)) {
            if (b instanceof WarBot) {
                WarBot wb = (WarBot) b;
                if (wb.isDead() && wb.getRespawnTick() <= getTick()) {
                    wb.reset();
                    wb.respawn();
                    placeBot(wb, 10, 3, 100);
                }
            }
        }
    }

	private void destructionEffect(BotBody2D b) {
		Debris d;
		for (int i = 0; i < 16; i++) {
			d = new Debris(b.getWorldPosition(), i * (MathUtils.QUARTER_PI / 2), this);
			d.launch(tick);
			debrisList.add(d);
		}
	}

	public boolean placeBot(BotBody2D bot, float elemMargin, float obstMargin, int maxTries) {
		int tries = 0;
		Vec2 pos = new Vec2(generatePositionInBoundaries());
		while (!checkElementPositionConficts(pos, elemMargin) || !checkObstaclePositionConficts(pos, obstMargin))
		{
			pos.set(generatePositionInBoundaries());			
			tries++;
			if(tries > maxTries)
				return false;
		}
		bot.setWorldPosition(pos, Math.random() * Math.PI * 2);
		bot.body.setLinearVelocity(new Vec2(0, 0));
		bot.body.setAngularVelocity(0);
		return true;
	}

	@Override
	public void doWorldStep() {
		super.doWorldStep();
		for (Projectile p : projectileScheduleForRemoval)
			p.removeProjectileFromWorld();
		projectileScheduleForRemoval.clear();

		ListIterator<Debris> iterDebris = debrisList.listIterator();
		while (iterDebris.hasNext()) {
			Debris d = iterDebris.next();
			if (d.getLaunchTime() + debrisLifespan < tick) {
				d.removeDebrisFromWorld();
				iterDebris.remove();
			}
		}
	}

	public HashMap<BotBody2D, ArrayList<Projectile>> getProjectiles() {
		return projectiles;
	}

	public ArrayList<Projectile> getProjectileScheduleForRemoval() {
		return projectileScheduleForRemoval;
	}

	public void doStep() {
		super.doStep();
		
		for (int i = 0; i < botScheduleForRemoval.size(); i++) {
			for (int j = 0; j < botScheduleForRemoval.get(i).size(); j++) {
					bots.get(i).remove(botScheduleForRemoval.get(i).get(j));
			}
			botScheduleForRemoval.get(i).clear();
		}

		for (int i = 0; i < bots.size(); i++) {
			for (int j = 0; j < bots.get(i).size(); j++) {
				if (bots.get(i).get(j).clientOffline())
				{
					bots.get(i).get(j).removeBotFromWorld();
					removeBot(bots.get(i).get(j));
				}
				else if (bots.get(i).get(j).isActive())
					bots.get(i).get(j).step();
			}
		}
	}

	@Override
    public void postStepOps() {
        checkPenalty();
        checkDeadBots();
    }

	protected boolean checkElementPositionConficts(Vec2 wp, float s) {

		for (VirtualWorldElement el : getWorldElements())
			if (MathUtils.distance(el.worldPosition, wp) < s + el.size)
				return false;
		for (ArrayList<BotBody2D> ab : getBots())
			for (BotBody2D b : ab)
				if (MathUtils.distance(b.body.getPosition(), wp) < s + b.size)
					return false;
		return true;
	}

	public synchronized ArrayList<ArrayList<BotBody2D>> getBots() {
		return bots;
	}

	public void reset() {
		super.reset();
		for (Projectile p : projectileScheduleForRemoval)
			p.removeProjectileFromWorld();
		projectileScheduleForRemoval.clear();
		for(ArrayList<Projectile> pl : projectiles.values())
		{
			for(Projectile p : pl)
			{
				removeWorldElement(p);
				p.removeFromWorld();
			}
			pl.clear();
		}
		ListIterator<Debris> iterDebris = debrisList.listIterator();
		while (iterDebris.hasNext()) {
			Debris d = iterDebris.next();
			d.removeDebrisFromWorld();
			iterDebris.remove();
		}
		botsReleaseTime.clear();
		for(BotBody2D b : botsInPenalty)
			b.setActive(true);
		botsInPenalty.clear();
	}

	public void init() {

	}

	protected void makeBotConstructor(String botModel) {
		Class<?> clazz;
		botConstructor = null;
		try {
			clazz = Class.forName("androidgameserver.bot." + botModel);
			botConstructor = clazz.getConstructor(Environment2DMultiBot.class);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			System.err.println("Bot constructor not found for type '" + botModel + "': " + e.getMessage());
		}
	}

	protected BotBody2D makeBot(String teamLabel, String name, int teamId, ClientHandler clienth) {
		BotBody2D bot = null;
		try {
			bot = (BotBody2D) botConstructor.newInstance(this);
			bot.label = teamLabel;
			bot.ID = teamId;
			if (teamId != 0)
				bot.setName(name + " (" + teamLabel + ")");
			else
				bot.setName(name);
			bot.setClientHandler(clienth);
			projectiles.put(bot, new ArrayList<Projectile>());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			System.err.println("Failed to instantiate bot of type '" + botConstructor.getName() + "': " + e.getMessage());
			e.printStackTrace();
		}
		return bot;
	}

	@Override
	public boolean hasBotCollision() {
		for (int i = 0; i < bots.size(); i++)
			for (int j = 0; j < bots.get(i).size(); j++)
				if (bots.get(i).get(j).isInContact())
					return true;
		return false;
	}

	synchronized public BotBody2D addBot(ClientHandler clientHandler) {
		if (bots.size() <= 0 || bots.get(NOTEAM_ID) == null) {
			bots.add(NOTEAM_ID, new ArrayList<BotBody2D>());
			botScheduleForRemoval.add(NOTEAM_ID, new ArrayList<BotBody2D>());
		}
		return null;
	}

	public BotBody2D addBot(ClientHandler clientHandler, int team) {
		if (bots.size() < team || bots.get(team) == null) {
			bots.add(team, new ArrayList<BotBody2D>());
			botScheduleForRemoval.add(team, new ArrayList<BotBody2D>());
		}
		return null;
	}

	public void removeBot(BotBody2D bot, int team) {
		if (bots.size() > team && bots.get(team) != null) {
			botScheduleForRemoval.get(team).add(bot);
		}
	}

	public void removeBot(BotBody2D bot) {
		if (bots.size() > NOTEAM_ID && bots.get(NOTEAM_ID) != null) {
			botScheduleForRemoval.get(NOTEAM_ID).add(bot);
		}
	}

	public BotBody2D getBotByName(String name) {
		for (ArrayList<BotBody2D> l : getBots()) {
			for (BotBody2D b : l) {
				if (b.name.equals(name))
					return b;
			}
		}
		return null;
	}

}