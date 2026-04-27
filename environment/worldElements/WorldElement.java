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

import java.awt.Color;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.Environment2D;


public abstract class WorldElement extends VirtualWorldElement implements PhysicalElement2D{
	
	public Body body;
	protected BodyDef bd;
	protected FixtureDef sd;
	protected Color color = Color.orange;
	
	public WorldElement(Vec2 worldPos, float worldAng, float s)
	{
		super(worldPos,  worldAng,  s);

	}

	@Override
	public Vec2 getWorldPoint(Vec2 vec2) {
		return body.getWorldPoint(vec2);
	}
	
	@Override
	public Vec2 getWorldPosition() {
		if(body != null)
			return body.getPosition();
		else
			return null;
	}
	
	public void setWorldPosition(float x, float y) {
		if(body != null)
		{
			body.setTransform(new Vec2(x,y), worldAngle);
		}
	}
	public void setWorldPosition(Vec2 pos, double ang)
	{
		if(body != null)
		{
			body.setTransform(pos,(float) ang);
		}
	}

	public Color getColor() {
		
		return color;
	}

	public Color setColor(Color c) {
		
		color = c;
		return color;
	}
	
	public synchronized void registerToWorld() {
		body = Environment2D.createWorldBody(bd,worldPosition,worldAngle);
		sd.userData = this;
		body.createFixture(sd);
		body.setUserData(this);
	}
	
	public void removeFromWorld() {
		Environment2D.destroyWorldBody(this.body);		
	}
	
	public void initPosition(Vec2 Pos,double Angle)
	{
		bd.position.set(Pos.x,Pos.y);
		bd.angle = (float)Angle;
	}
	
	public void setPosition(Vec2 botStartPos, float ang) {
		body.setTransform(botStartPos, ang);
	}

	public void setAngle(float Angle) {
		body.setTransform(body.getPosition(), Angle);
	}	

	public float getAngle() {
		return body.getAngle();
	}	
	
	public float getNormalizedAngle() {
		return body.getTransform().q.getAngle();
	}	
	
	public String elementInfoToString(BotBody2D bot)
	{
		Vec2 vec = new Vec2(getWorldPosition().x-bot.getWorldPosition().x,getWorldPosition().y-bot.getWorldPosition().y);
		vec = bot.getLocalVector(vec);			
		double orient = (-Math.atan2(vec.y, vec.x)/(MathUtils.TWOPI))+0.5;
		return orient + "/" + MathUtils.distance(bot.getWorldPosition(), getWorldPosition());
	}
}
