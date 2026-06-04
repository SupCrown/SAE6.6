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

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

	// Listes partagées entre le thread de simulation, les threads réseau (connexion de bots) et le
	// thread de rendu : on utilise CopyOnWriteArrayList pour itérer sans ConcurrentModificationException.
	protected List<BotBody2D> bots = new CopyOnWriteArrayList<>();
	protected ArrayList<ArrayList<Vec2>> startPos = new ArrayList<ArrayList<Vec2>>();
	protected ArrayList<ArrayList<Double>> startAngle = new ArrayList<ArrayList<Double>>();
	protected Constructor<?> botConstructor;
	protected List<Team> teams = new CopyOnWriteArrayList<>();
	private final Random teamColorRandom = new Random();
	private int nextTeamId = 0;

	protected long debrisLifespan = 100;
	protected ArrayList<BotBody2D> botsInPenalty = new ArrayList<>();
	protected ArrayList<Long> botsReleaseTime = new ArrayList<>();

	protected ArrayList<Projectile> projectileScheduleForRemoval = new ArrayList<>();
	protected ArrayList<BotBody2D> botScheduleForRemoval = new ArrayList<>();
	protected Map<BotBody2D, ArrayList<Projectile>> projectiles = new ConcurrentHashMap<BotBody2D, ArrayList<Projectile>>();
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
				placeBot(b, 10, 3, 100);
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
        if (bots == null) {
            return;
        }

        for (BotBody2D b : bots) {
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

	/**
	 * Renvoie l'équipe portant cet id si elle existe déjà, sinon la crée avec ce nom/couleur.
	 * Garantit une seule instance par id (la comparaison d'équipes peut donc se faire par référence).
	 */
	public synchronized Team getOrCreateTeam(int id, String name, Color color) {
		for (Team t : teams)
			if (t.getTeamId() == id)
				return t;
		Team t = new Team(id, name, color);
		teams.add(t);
		if (id >= nextTeamId)
			nextTeamId = id + 1;
		return t;
	}

	/**
	 * Crée une équipe « clé en main », sans paramètre : id suivant la dernière équipe,
	 * nom généré et couleur aléatoire (lisible sur fond sombre).
	 */
	public synchronized Team createTeam() {
		int id = nextTeamId++;
		Team t = new Team(id, "Team " + id, randomTeamColor());
		teams.add(t);
		return t;
	}

	private Color randomTeamColor() {
		// Teinte aléatoire, saturation et luminosité élevées pour des couleurs vives et distinctes.
		float hue = teamColorRandom.nextFloat();
		float saturation = 0.7f + teamColorRandom.nextFloat() * 0.3f;
		float brightness = 0.8f + teamColorRandom.nextFloat() * 0.2f;
		return Color.getHSBColor(hue, saturation, brightness);
	}

	public List<Team> getTeams() {
		return teams;
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

	public Map<BotBody2D, ArrayList<Projectile>> getProjectiles() {
		return projectiles;
	}

	public ArrayList<Projectile> getProjectileScheduleForRemoval() {
		return projectileScheduleForRemoval;
	}

	public void doStep() {
		super.doStep();
		
		for (BotBody2D b : botScheduleForRemoval)
			bots.remove(b);
		botScheduleForRemoval.clear();

		for (int i = 0; i < bots.size(); i++) {
			BotBody2D b = bots.get(i);
			if (b.clientOffline())
			{
				b.removeBotFromWorld();
				removeBot(b);
			}
			else if (b.isActive())
				b.step();
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
		for (BotBody2D b : getBots())
			if (MathUtils.distance(b.body.getPosition(), wp) < s + b.size)
				return false;
		return true;
	}

	public synchronized List<BotBody2D> getBots() {
		return bots;
	}

	/**
	 * Deux bots sont-ils alliés ? Règle unique réutilisée partout (tir ami, capteurs, scores) :
	 *  - un bot est toujours allié de lui-même (pas d'auto-dégâts) ;
	 *  - un bot sans équipe est l'adversaire de tout le monde (cas FFA) ;
	 *  - sinon, alliés s'ils partagent la même instance d'équipe.
	 */
	public boolean areAllies(BotBody2D a, BotBody2D b) {
		if (a == null || b == null)
			return false;
		if (a == b)
			return true;
		Team ta = a.getBotTeam();
		Team tb = b.getBotTeam();
		if (ta == null || tb == null)
			return false;
		return ta == tb;
	}

	/** Les coéquipiers d'un bot (sans le bot lui-même). Liste vide s'il n'a pas d'équipe. */
	public List<BotBody2D> getTeammates(BotBody2D bot) {
		List<BotBody2D> mates = new ArrayList<>();
		if (bot == null || bot.getBotTeam() == null)
			return mates;
		for (BotBody2D b : bot.getBotTeam().getMembers())
			if (b != bot)
				mates.add(b);
		return mates;
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

	protected BotBody2D makeBot(String name, ClientHandler clienth, Team botTeam) {
		BotBody2D bot = null;
		try {
			bot = (BotBody2D) botConstructor.newInstance(this);
			bot.setName(name);
			bot.setClientHandler(clienth);
			if (botTeam != null)
				botTeam.addMember(bot); // relie le bot à son équipe (référence + couleur) de façon atomique
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
		for (BotBody2D b : bots)
			if (b.isInContact())
				return true;
		return false;
	}

	// L'ajout réel (création de l'équipe + du bot) est fait par la carte concrète, ex. MAP_FFA.
	synchronized public BotBody2D addBot(ClientHandler clientHandler) {
		return null;
	}

	public void removeBot(BotBody2D bot) {
		botScheduleForRemoval.add(bot);
	}

	public BotBody2D getBotByName(String name) {
		for (BotBody2D b : getBots()) {
			if (b.name.equals(name))
				return b;
		}
		return null;
	}

}