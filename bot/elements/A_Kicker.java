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
package androidgameserver.bot.elements;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Environment2D;

public class A_Kicker extends Actuator {
	public float maxForce = 300.0f;		
	public float value = 0.0f;
	Body ball = null;
	public Vec2 kickerArea = new Vec2(0,0);
	public float kickerReach = 1f;
	
	private ClawQueryCallback cb = new ClawQueryCallback();

	public A_Kicker(Vec2 pos , float angle, BotBody2D b){
		super(pos, angle, b);
	}

	public A_Kicker(Vec2 pos , float angle, BotBody2D b,float reach, float maxFor){
		super(pos, angle, b);
		kickerReach = reach;
		maxForce = maxFor;
	}

	@Override
	public void step() {
		computeWorldPosAndAngle();
		if(normalizedValue>0)
		{
			ball = null;
			kickerArea.set(worldPosition.x+((float)(Math.cos(worldAngle)*kickerReach)),worldPosition.y+((float)(Math.sin(worldAngle)*kickerReach)));
			AABB sel = makeSel();
			Environment2D.queryAABB(cb, sel);	
			if(ball != null)
			{
				value = (float) normalizedValue * maxForce;
				Vec2 f = new Vec2((float)Math.cos(worldAngle)*value,(float)Math.sin(worldAngle)*value);
				Vec2 p = new Vec2(worldPosition);
				ball.applyForce(f, p);
			}			
		}
	}
	
	private AABB makeSel() {
		AABB ret = new AABB(new Vec2((float)(kickerArea.x-kickerReach),(float)(kickerArea.y-kickerReach)),new Vec2((float)(kickerArea.x+kickerReach),(float)(kickerArea.y+kickerReach)));
		return ret;
	}

	public class ClawQueryCallback implements QueryCallback {
		@Override
		public boolean reportFixture(Fixture fixture) {
			if(fixture.m_filter.categoryBits == CollisionDefines.CDTargetObj)
			{
				ball = fixture.getBody();
				return false;
			}
			return true;
		}
	}

	@Override
	public void reset() {
	}
	
	public float boundingBoxPointy(int pt)
	{
		switch(pt)
		{
		case 1:
			return kickerArea.y-kickerReach/2;
		case 2:
			return kickerArea.y+kickerReach/2;
		case 3:
			return kickerArea.y+kickerReach/2;
		case 4:
			return kickerArea.y-kickerReach/2;
		}
		return 0;
	}
	
	public float boundingBoxPointx(int pt)
	{
		switch(pt)
		{
		case 1:
			return kickerArea.x-kickerReach/2;
		case 2:
			return kickerArea.x-kickerReach/2;
		case 3:
			return kickerArea.x+kickerReach/2;
		case 4:
			return kickerArea.x+kickerReach/2;
		}
		return 0;
	}
}
