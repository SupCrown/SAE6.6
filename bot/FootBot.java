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
package androidgameserver.bot;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import androidgameserver.bot.elements.A_Kicker;
import androidgameserver.bot.elements.A_Wheel;
import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Environment2D;
import androidgameserver.environment.Environment2DMultiBot;

public class FootBot extends BotBody2D{
	Environment2DMultiBot multEnv = null;

	public FootBot(Environment2DMultiBot env)
	{
		super(env);
		if(env instanceof Environment2DMultiBot)
			multEnv = (Environment2DMultiBot) env;
		sd = new FixtureDef();
		sd.shape = new CircleShape(); 
		sd.shape.m_radius = (float)size;
		sd.friction = 0.0f;
		sd.density = 2.0f;
		sd.filter.categoryBits = CollisionDefines.CDBot;
		sd.filter.maskBits = CollisionDefines.CDAllMask;

		bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.angularDamping = 20.0f;
		bd.linearDamping = 5.0f;
		
		bd.allowSleep = false;
		
		//Actuators
		actuators.put("MotL",new A_Wheel(new Vec2(0.0f,-(float)size),0, this,80.0f));
		actuators.put("MotR",new A_Wheel(new Vec2(0.0f,(float)size),0, this,80.0f));
		
		actuators.put("KickerL",new A_Kicker(new Vec2((float)Math.cos(size/2.0f),-(float)size/2.0f),(float)Math.PI/4, this));
		actuators.put("KickerR",new A_Kicker(new Vec2((float)Math.cos(size/2.0f),(float)size/2.0f),(float)-Math.PI/4, this));
	}
}
