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

import org.jbox2d.common.Vec2;

import androidgameserver.AppDefines;
import androidgameserver.bot.BotBody2D;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.worldElements.Projectile;


public class A_Gun extends Actuator {
	int rof = AppDefines.bot_RateOfFire;
	double startOffset = 2.0;
	A_GunTraverse traverse;
	Environment2DMultiBot simEnvironment;

	public A_Gun(Vec2 lp, float la, BotBody2D b, Environment2DMultiBot simEnv) {
		super(lp, la, b);
		simEnvironment = simEnv;
	}

	@Override
	public void step() {
		computeWorldPosAndAngle();
		if(rof < AppDefines.bot_RateOfFire)
		{
			rof++;
		}
		else if (normalizedValue > 0.5) {
			rof = 0;
			fire();
		}
	}

	private void fire() {
		if (simEnvironment != null) {
			float angle = (float) (worldAngle + ((1.0 - (traverse.normalizedValue * 2)) * Math.PI));
			Projectile p = new Projectile(new Vec2(worldPosition.x + ((float) (Math.cos(angle) * startOffset)),
					worldPosition.y + ((float) (Math.sin(angle) * startOffset))), angle, bot, simEnvironment);
			p.launch();
		}
	}

	@Override
	public void reset() {
	}

	public void setTraverse(A_GunTraverse tra) {
		traverse = tra;
	}
}
