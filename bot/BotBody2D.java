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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import androidgameserver.environment.Team;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import androidgameserver.AppDefines;
import androidgameserver.ClientHandler;
import androidgameserver.Env2DViewer;
import androidgameserver.bot.elements.Actuator;
import androidgameserver.environment.CollisionDefines;
import androidgameserver.environment.Environment2D;
import androidgameserver.environment.Environment2DMultiBot;
import androidgameserver.environment.worldElements.PhysicalElement2D;
import androidgameserver.environment.worldElements.SituatedElement2D;
import androidgameserver.environment.worldElements.TargetObject;
import androidgameserver.environment.worldElements.WorldElement;

public class BotBody2D extends WorldElement{
	private boolean active = false;
	public String name = "";
	public String message = null;
	public long messageTime;
	public Team BotTeam;
	public Environment2DMultiBot environment;
	protected HashMap<String,Actuator> actuators = new HashMap<String,Actuator>();
	private HashMap<String,Double> actuatorValues = new HashMap<String,Double>();
	public int contactCounter = 0;
	public int targetContactCounter = 0;
	protected Color transparentColor = makeTransparentColor(getColor());
	
	public ArrayList<Vec2> customSkin = null;
	
	FixtureDef sd;
	BodyDef bd;
	
	ClientHandler clienth = null;
	
	public BotBody2D(Environment2DMultiBot env)
	{
		super(new Vec2(env.getMapWidth()*2,env.getMapHeight()*2),0,1);
		environment = env;
	}
	
	public void setPentagonSkin() // beeeeeg
	{
		customSkin = new ArrayList<>();
		customSkin.add(new Vec2(3,0)); // front
		customSkin.add(new Vec2(0,3)); //right up
		customSkin.add(new Vec2(-3,1.5f)); // right down
		customSkin.add(new Vec2(-3,-1.5f)); // left down
		customSkin.add(new Vec2(0,-3));	// left up
	}
	
	public void step()
	{
		if(active)
		{
			if(message != null && messageTime + AppDefines.messageDuration <= getEnvironment().getTick())
				message = null;
			if(actuatorValues != null)
				for(String k : actuatorValues.keySet())
					setNormalisedActuatorData(actuators.get(k), actuatorValues.get(k));			
		}		
	}
	
	 protected void setNormalisedActuatorData(Actuator actuator, double parseDouble) {
		 if(actuator != null)
		 {
				actuator.setNormalizedValue(parseDouble);
				actuator.step();			 
		 }
	}

	 public HashMap<String,Double> getActuatorValues()
	 {
		 return actuatorValues;
	 }
	 
	public Environment2DMultiBot getEnvironment() {
		return environment;
	}
	
	public void resetActurators() {
		for(Actuator a : actuators.values())
			a.reset();
	}

	public void reset() {
		for(Actuator a : actuators.values())
			a.reset();
		contactCounter = 0;
		targetContactCounter = 0;
	}
	
	public void registerBotToWorld() {
		sd.filter.categoryBits = CollisionDefines.CDBot;
		sd.filter.maskBits = CollisionDefines.CDAllMask;
		sd.userData = this;
		body = Environment2D.createWorldBody(bd,worldPosition,worldAngle);			
		while(body == null)
		{
			body = Environment2D.createWorldBody(bd,worldPosition,worldAngle);
			System.out.println("oops.. that's ugly");
		}
		body.createFixture(sd);
		body.setUserData(this);
	}
	
	public void removeBotFromWorld() {
		Environment2D.destroyWorldBody(this.body);
	}


	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	
	public void setName(String name) {
		this.name = name;
	}	

	public String getName() {
		return this.name;
	}

	public Team getBotTeam() {
		return BotTeam;
	}

	public void setBotTeam(Team botTeam) {
		BotTeam = botTeam;
	}

	public void addContact(PhysicalElement2D obj) {
		if(obj instanceof TargetObject)
		{
			targetContactCounter++;
		}
		else
		{
			contactCounter++;
		}
	}
	
	public void removeContact(PhysicalElement2D obj) {
		if(obj instanceof TargetObject)
		{
			targetContactCounter--;
		}
		else
		{
			contactCounter--;			
		}
	}
	
	//ugly
	public boolean isInContact()
	{
		return contactCounter > 0;
	}
	
	public Vec2 getLocalVector(Vec2 in) {
		return body.getLocalVector(in);
	}
	
	public boolean isInTargetContact() 
	{
		return targetContactCounter > 0;
	}

	public Color getTransparentColor() {
		return transparentColor;
	}

	public Color setColor(Color col) {
		transparentColor = makeTransparentColor(col);
		return super.setColor(col);
	}
	
	private static Color makeTransparentColor(Color c)
	{
		return new Color((float)c.getRed()/255.0f,(float)c.getGreen()/255.0f,(float)c.getBlue()/255.0f,0.5f);
	}
	
	public void addActuator(String name, Actuator act)
	{
		actuators.put(name, act);
		actuatorValues.put(name, 0.5);
	}

	public Collection<Actuator> getActuators() {
		return actuators.values();
	}
	
	public void setMessage(String msg)
	{
		message = msg.substring(0, Math.min(msg.length(), AppDefines.messageLength));
		messageTime = environment.getTick();
	}

    public BotBody2D getClosestBot()
    {
        BotBody2D ret = null;
        double tmpDist;
        double ndist = -1;

        for (BotBody2D B : environment.getBots())
        {

            if (B.equals(this)) {
                continue;
            }

            if (B instanceof WarBot && ((WarBot) B).isDead()) {
                continue;
            }

            if (environment.areAllies(this, B)) { // on ne vise que les adversaires
                continue;
            }

            tmpDist = MathUtils.distance(B.getWorldPosition(), getWorldPosition());

            if ((ndist < 0) || (tmpDist <= ndist && tmpDist > 0)) {
                ndist = tmpDist;
                ret = B;
            }
        }

        return ret;
    }

	public void setClientHandler(ClientHandler clienth) {
		this.clienth = clienth;
	}

	public boolean clientOffline() {
		if(clienth != null)
			return clienth.isOffline();
		return false;
	}
}
