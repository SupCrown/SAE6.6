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
package androidgameserver.environment.worldElements;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;

import androidgameserver.bot.BotBody2D;
import androidgameserver.bot.WarBot;
import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Environment2D;
import androidgameserver.environment.Environment2DMultiBot;

public class Projectile extends DynamicWorldElement {

	private float force = 1000;
	Environment2DMultiBot simEnvironment;
	BotBody2D bot;

	public Projectile(Vec2 worldPos, float worldAng, BotBody2D ibot, Environment2DMultiBot simEnv)
	{
		super(worldPos, worldAng,0.5f);
		sd.userData = this;
		sd.shape = new CircleShape();
		sd.shape.m_radius = 0.5f;
		//sd.friction = 1.0f;
		sd.density = 2.0f;
		sd.filter.categoryBits = CollisionDefines.CDProjectile;
		sd.filter.maskBits = CollisionDefines.CDAllMask;

		bd.angularDamping = 0.0f;
		bd.linearDamping = 0.0f;
		bd.allowSleep = false;
		simEnvironment = simEnv;
		bot = ibot;
	}


	public void launch()
	{
		simEnvironment.getProjectiles().get(bot).add(this);
		simEnvironment.addWorldElement(this);
		body = Environment2D.createWorldBody(bd,worldPosition,worldAngle);
		body.createFixture(sd);
		Vec2 f = new Vec2((float)Math.cos(body.getAngle())*force,(float)Math.sin(body.getAngle())*force);
		body.applyForce(f, body.getPosition());
	}

	/** Le bot qui a tiré ce projectile (utile pour ne pas toucher ses alliés). */
	public BotBody2D getOwner() {
		return bot;
	}

	public void addContact(PhysicalElement2D obj) {
		// Pas de tir ami : on ne marque un point que si la cible n'est pas une alliée du tireur.
		if(obj instanceof WarBot && !simEnvironment.areAllies(this.bot, (WarBot)obj))
			((WarBot)this.bot).hitTarget((WarBot)obj);
		simEnvironment.getProjectileScheduleForRemoval().add(this);
	}
	
	public void initProjectilePosition(Vec2 startPos,double startAngle)
	{
		bd.position.set(startPos.x,startPos.y);
		bd.angle = (float)startAngle;
	}

	public void removeProjectileFromWorld() 
	{
		simEnvironment.getProjectiles().get(bot).remove(this);
		simEnvironment.removeWorldElement(this);
		removeFromWorld();
	}
}
