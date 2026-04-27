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

import androidgameserver.bot.BotBody2D;

public class A_GunTraverse extends Actuator {
	
	public A_GunTraverse(Vec2 lp, float la, BotBody2D b) {
		super(lp, la, b);
		normalizedValue = 0.5;
	}

	@Override
	public void step() {

	}


	@Override
	public void reset()
	{
		
	}
	
}
