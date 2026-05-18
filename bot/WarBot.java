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

import androidgameserver.AppDefines;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import androidgameserver.bot.elements.A_Gun;
import androidgameserver.bot.elements.A_GunTraverse;
import androidgameserver.bot.elements.A_Wheel;
import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.worldElements.PhysicalElement2D;
import androidgameserver.environment.worldElements.Projectile;

public class WarBot extends BotBody2D{
	int score = 0;
    private int lives = AppDefines.maxLives;

	public WarBot(Environment2DMultiBot env)
	{
		super(env);
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
		addActuator("MotL",new A_Wheel(new Vec2(0.0f,-(float)size),0, this,80.0f));
		addActuator("MotR",new A_Wheel(new Vec2(0.0f,(float)size),0, this,80.0f));

		A_Gun gun = new A_Gun(new Vec2(0,0),0, this,environment);
		A_GunTraverse tra = new A_GunTraverse(new Vec2(0,0),0, this);
		gun.setTraverse(tra);
		addActuator("GunTrig",gun);		
		addActuator("GunTrav",tra);		
	}

    public int getLives(){
        return lives;
    }

    public void loseLife(){
        lives--;
    }

    public boolean isDead() {
        return lives <= 0;
    }

	public void addContact(PhysicalElement2D obj) {
		super.addContact(obj);
		if (obj instanceof Projectile) {
            loseLife();

            if (isDead()) {
                environment.removeBot(this);
                removeBotFromWorld();
            } else {
                environment.botToPenalty(this);
            }
        }
	}
	
	public void hitTarget(BotBody2D obj) {
		score++;
	}
	
	public Projectile getClosestProjectile()
	{
		Projectile ret = null;
		double tmpDist;
		double ndist = -1;
		for (BotBody2D B : environment.getBots().get(0))
		{
			if (!B.equals(this))
			{
			for (Projectile P : environment.getProjectiles().get(B))
			{
					tmpDist = MathUtils.distance(P.getWorldPosition(), getWorldPosition());
					if ((ndist < 0)||(tmpDist <= ndist && tmpDist >0)){
							ndist = tmpDist;
							ret = P;
					}					
				}
			}
		}
		return ret;
	}

	public int getScore() {
		return score;
	}

	public void reinit() {
		score = 0;
	}
}
