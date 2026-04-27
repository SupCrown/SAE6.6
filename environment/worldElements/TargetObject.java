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
import org.jbox2d.dynamics.BodyType;

import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.CollisionDefines;

public class TargetObject extends DynamicWorldElement {

	private int obstacleContactCounter = 0;
	private int borderContactCounter = 0;
	
	public TargetObject(Vec2 worldPos, float worldAng, float s) {
		super(worldPos, worldAng, s);
		sd.shape = new CircleShape(); 
		sd.friction = 0.0f;
		sd.restitution = 1.8f;
		sd.density = 2.0f;
		sd.filter.categoryBits = CollisionDefines.CDTargetObj;
		sd.shape.m_radius = size;
		bd.angularDamping = 1.5f;
		bd.linearDamping = 0.15f;
		bd.userData = this;
		
	}
	
	public TargetObject(Vec2 worldPos, float worldAng, float s, String n) {
		this(worldPos, worldAng, s);
		name = n;
	}
	
	public void setFriction(float frict)
	{
		sd.friction = frict;
	}

	public void setDamping(float damp)
	{
		bd.linearDamping  = damp;
	}

	public void setStatic() {
		bd.type = BodyType.STATIC;
	}
	
	public void setDynamic() {
		bd.type = BodyType.DYNAMIC;
	}
	
	public void addContact(PhysicalElement2D obj) {
		if(obj instanceof WorldElement)
		{
			obstacleContactCounter++;
		}
		else if(obj instanceof BorderBox)
		{
			borderContactCounter++;
		}
	}
	
	public void removeContact(PhysicalElement2D obj) {
		if(obj instanceof WorldElement)
		{
			obstacleContactCounter--;
		}
		else if(obj instanceof BorderBox)
		{
			borderContactCounter--;
		}
	}
	
	public boolean isInContactWithObstacles()
	{
		return obstacleContactCounter > 0;
	}
	
	public boolean isInContactWithBorder()
	{
		return borderContactCounter > 0;
	}
}
