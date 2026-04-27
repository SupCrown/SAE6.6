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

import java.util.ArrayList;

import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;

import androidgameserver.bot.BotBody2D;

public abstract class VirtualWorldElement implements SituatedElement2D {
	public Vec2 worldPosition;
	public float worldAngle;
	public float size; 
	private Mat22 R= new Mat22();
	public boolean lock = false;
	public String name;
	//protected ArrayList<SimulationEnvironmentCallback> callbacks = null;

	public VirtualWorldElement(Vec2 worldPos, float worldAng, float s) {
		worldPosition = worldPos;
		worldAngle = worldAng; 
		size = s;
		R.set(worldAng);
	}
	/*
	public void addCallBack(SimulationEnvironmentCallback cb)
	{
		if(callbacks == null)
			callbacks = new ArrayList<SimulationEnvironmentCallback>();
		callbacks.add(cb);
	}
*/
	public Vec2 getWorldPoint(Vec2 v) {
		Vec2 out = R.mul(v);
		out.addLocal(worldPosition);
		return out;
	}

	public Vec2 getWorldPosition() {
			return worldPosition;
	}
	
	public void setAngle(float a)
	{
		worldAngle = a; 
		R.set(worldAngle);
	}
	
	public float getAngle()
	{
		return worldAngle;
	}
	
	public void setWorldPosition(Vec2 pos, double ang)
	{
		worldPosition = pos;
		setAngle((float) ang);
	}
	
	public void setWorldPosition(float x, float y) {
		worldPosition.x = x;
		worldPosition.y = y;
	}
	
	public void setSize(float s)
	{
		size = s;
	}
	
	public String getName()
	{
		return name;
	}
}
