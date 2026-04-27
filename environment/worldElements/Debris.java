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

public class Debris extends DynamicWorldElement {

	private long launchTime;
	private float force = 0.2f;
	Environment2DMultiBot simEnvironment;

	public Debris(Vec2 worldPos, float worldAng, Environment2DMultiBot simEnv)
	{
		super(worldPos, worldAng,0.2f);
		sd.userData = this;
		sd.shape = new CircleShape();
		sd.shape.m_radius = 0.1f;
		//sd.friction = 1.0f;
		sd.density = 0.1f;
		sd.filter.categoryBits = CollisionDefines.CDVisual;
		sd.filter.maskBits = CollisionDefines.CDEnv;

		bd.angularDamping = 0.0f;
		bd.linearDamping = 0.0f;
		bd.allowSleep = false;
		simEnvironment = simEnv;
	}

	public void launch(long time)
	{
		launchTime = time;
		simEnvironment.addWorldElement(this);
		body = Environment2D.createWorldBody(bd,worldPosition,worldAngle);
		body.createFixture(sd);
		Vec2 f = new Vec2((float)Math.cos(body.getAngle())*force,(float)Math.sin(body.getAngle())*force);
		body.applyForce(f, body.getPosition());
	}

	public void removeDebrisFromWorld() {
			simEnvironment.removeWorldElement(this);
			removeFromWorld();
	}

	public long getLaunchTime() {
		return launchTime;
	}
}
