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

import androidgameserver.environment.Environment2D;

public class MovingObstacle extends DynamicWorldElement {

	public MovingObstacle(Vec2 worldPos, float worldAng, float s) {
		super(worldPos, worldAng, s);
		sd.shape = new CircleShape(); 
		
		sd.friction = 0.0f;
		sd.density = 0.0f;
		//sd.filter.categoryBits = CollisionDefines.CDEnv;
		//sd.filter.maskBits = CollisionDefines.CDAllButEnvMask;
		sd.shape.m_radius = size;
		sd.restitution = 1.0f;
		bd.angularDamping = 0.0f;
		bd.linearDamping = 0.00f;
		body = Environment2D.createWorldBody(bd,worldPosition,worldAngle);
		body.createFixture(sd);
	}
}
