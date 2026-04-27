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

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import androidgameserver.environment.Environment2D;

public class BorderBox implements PhysicalElement2D{

	World world;
	final float k_restitution = 0.1f;
	Body borders;
	
	public BorderBox(World w, Vec2[] corners){
		world = w;
		BodyDef bd = new BodyDef();
		bd.position.set(0.0f, 0.0f);

		borders = Environment2D.createWorldBody(bd,new Vec2(),0);			
		while(borders == null)
		{
			borders = Environment2D.createWorldBody(bd,new Vec2(),0);
			System.out.println("oops.. that's ugly");
		}
		
		ChainShape shape = new ChainShape();
		
		FixtureDef sd = new FixtureDef();
		sd.shape = shape;
		sd.density = 0.0f;
		sd.restitution = k_restitution;

		shape.createLoop(corners, corners.length);
		/*
		// Top horizontal
		shape.setAsEdge(getCorners()[0],getCorners()[1]);
		ground.createFixture(sd);
		// Right vertical
		shape.setAsEdge(getCorners()[1],getCorners()[2]);
		ground.createFixture(sd);
		// Bottom horizontal
		shape.setAsEdge(getCorners()[2],getCorners()[3]);
		ground.createFixture(sd);
		// Left vertical
		shape.setAsEdge(getCorners()[3],getCorners()[0]);
		*/
		sd.userData = this;
		borders.createFixture(sd);
		borders.setUserData(this);
	}

	public BorderBox(World w, Vec2[] corners, int offset) {
		world = w;
		BodyDef bd = new BodyDef();
		bd.position.set(0.0f, 0.0f);

		borders = Environment2D.createWorldBody(bd,new Vec2(),0);			
		while(borders == null)
		{
			borders = Environment2D.createWorldBody(bd,new Vec2(),0);
			System.out.println("oops.. that's ugly");
		}
		
		ChainShape shape = new ChainShape();
		
		FixtureDef sd = new FixtureDef();
		sd.shape = shape;
		sd.density = 0.0f;
		sd.restitution = k_restitution;
		Vec2[] cornersOff = new Vec2[4];
		cornersOff[0] = new Vec2(corners[0]).addLocal(-offset,offset);
		cornersOff[1] = new Vec2(corners[1]).addLocal(offset,offset);
		cornersOff[2] = new Vec2(corners[2]).addLocal(offset,-offset);
		cornersOff[3] = new Vec2(corners[3]).addLocal(-offset,-offset);
		shape.createLoop(cornersOff, cornersOff.length);

		sd.userData = this;

		borders.createFixture(sd);
		borders.setUserData(this);
	}
	
		public void removeFromWorld() {
			Environment2D.destroyWorldBody(this.borders);		
	}
}
