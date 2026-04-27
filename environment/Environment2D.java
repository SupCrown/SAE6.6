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
package androidgameserver.environment;

import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import androidgameserver.AppDefines;
import androidgameserver.bot.elements.A_Kicker.ClawQueryCallback;
import androidgameserver.environment.worldElements.BorderBox;
import androidgameserver.environment.worldElements.ControlledElement;
import androidgameserver.environment.worldElements.ObstacleStaticBox;
import androidgameserver.environment.worldElements.ObstacleStaticCyninder;
import androidgameserver.environment.worldElements.PhysicalElement2D;
import androidgameserver.environment.worldElements.VirtualWorldElement;
import androidgameserver.environment.worldElements.WorldElement;

public abstract class Environment2D extends Environment{


	protected String name = "DefaultWorld";
	private static World World = null;    
   
	protected float timeStep = (1.0f / AppDefines.targetFPS2DPhysics); 
	protected int viterations = 8;  
	protected int piterations = 3;  
	
	protected ContactListener worldContactlistener;

	protected BorderBox borders;
	protected BorderBox borders2;
	protected Vec2[] corners = new Vec2[]{new Vec2(-70.0f, 70.0f), new Vec2(70.0f, 70.0f),new Vec2(70.0f, -70.0f),new Vec2(-70.0f, -70.0f)}; 
    //  haut gauche - haut droite - bas droite - bas gauche

    protected double minObstacleSize = 1.0;
    protected double maxObstacleSizeVariability = 2.0;
    protected double maxObstacleSpacingVariability = 2.0;
    protected double obstacleSpacing = 18.0;

	protected ArrayList<VirtualWorldElement> worldElements = new ArrayList<>();
	protected ArrayList<ControlledElement> controlledElements = new ArrayList<>();
	protected ArrayList<WorldElement> obstacles = new ArrayList<>();
		
	protected boolean triggeredInterrupt = false;
	
	protected boolean makeCynlinderObstacles = false;
	protected boolean makeCircleWorld = false;	

	protected long tick = 0;
	
	
    public Environment2D()
    {
    	worldContactlistener = new ContactListener() {	
			@Override
			public void beginContact(Contact contact) {
				((PhysicalElement2D)contact.m_fixtureB.getUserData()).addContact((PhysicalElement2D) contact.m_fixtureA.getUserData());
				((PhysicalElement2D)contact.m_fixtureA.getUserData()).addContact((PhysicalElement2D) contact.m_fixtureB.getUserData());
			}
			@Override
			public void endContact(Contact contact) {
				((PhysicalElement2D)contact.m_fixtureB.getUserData()).removeContact((PhysicalElement2D) contact.m_fixtureA.getUserData());
				((PhysicalElement2D)contact.m_fixtureA.getUserData()).removeContact((PhysicalElement2D) contact.m_fixtureB.getUserData());
			}
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
		};
    }	
    
    public boolean isCircleWorld()
	{
    	return makeCircleWorld;
	}

	public void doWorldStep()
	{
		getWorld().step(timeStep,viterations,piterations);
		tick++;
	}
	
	public void doStep() {
		super.doStep();
    	for(ControlledElement c: controlledElements)
       		c.step();
    	}
    		
/*	
	@Override
	public void doStep(ScoreCounter scoreCounter, SimulationInterruptFlag interruptFlag) {
		super.doStep(scoreCounter, interruptFlag);
    	for(ControlledElement c: controlledElements)
    		c.step();
	}

	@Override
	public void doAutoStep(ScoreCounter scoreCounter, SimulationInterruptFlag interruptFlag) {
		super.doAutoStep(scoreCounter, interruptFlag);    	
		for(ControlledElement c: controlledElements)
    		c.step();
	}

	@Override
	public HashMap<String,Double> doStep(HashMap<String,Double> actuatorValues, ScoreCounter scoreCounter, SimulationInterruptFlag interruptFlag) {
		super.doStep(actuatorValues, scoreCounter, interruptFlag);
    	for(ControlledElement c: controlledElements)
    		c.step();
		return null;
	}
	*/
    public void reset() {
    	super.reset();
    	for(ControlledElement c: controlledElements)
    		c.reset();
		getWorld().clearForces();
		triggeredInterrupt = false;
	}	
    
    
	public World makeWorld()
	{
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		setWorld(new World(gravity));
		if(hasBorders)
			makeBorders(getWorld());
		if(hasObstacles)
			loadObstacles(generateObstaclesParameters());
		World.setContactListener(worldContactlistener);
		return getWorld();
	}
    
	protected void makeBorders(World w)
	{
		// for generation
		getCorners()[0] = new Vec2(-AppDefines.arenaWidth, AppDefines.arenaHeight);
		getCorners()[1] = new Vec2(AppDefines.arenaWidth, AppDefines.arenaHeight);
		getCorners()[2] = new Vec2(AppDefines.arenaWidth, -AppDefines.arenaHeight);
		getCorners()[3] = new Vec2(-AppDefines.arenaWidth, -AppDefines.arenaHeight);
		borders = new BorderBox(w, getCorners());
		borders2 = new BorderBox(w, getCorners(),2);
		if(makeCircleWorld && !hasObstacles)
		{
			for(int i = (int) getCorners()[0].x;i < getCorners()[1].x;i+=minObstacleSize*4)
			{
				getObstacles().add(new ObstacleStaticCyninder(new Vec2(i,getCorners()[0].y), 0,(float) minObstacleSize*2.5f, getWorld()));	
				getObstacles().add(new ObstacleStaticCyninder(new Vec2(i,getCorners()[2].y), 0,(float) minObstacleSize*2.5f, getWorld()));	
			}

			for(int i = (int) getCorners()[3].y;i < getCorners()[0].y;i+=minObstacleSize*4)
			{
				getObstacles().add(new ObstacleStaticCyninder(new Vec2(getCorners()[0].x,i), 0,(float) minObstacleSize*2.5f, getWorld()));	
				getObstacles().add(new ObstacleStaticCyninder(new Vec2(getCorners()[1].x,i), 0,(float) minObstacleSize*2.5f, getWorld()));	
			}
		}

	}
	
	public ArrayList<ObstaclePos> generateObstaclesParameters() {
		ArrayList<ObstaclePos> ret = new ArrayList<>();
		
		double width = getCorners()[1].x - getCorners()[0].x;
		double height = getCorners()[0].y - getCorners()[2].y;
		int widthStep = (int)(width / obstacleSpacing) + 1;
		int heightStep = (int)(height / obstacleSpacing) + 1;
		float s;

		Vec2 wp ;
		for (int i = 0 ; i < widthStep; i++)
		{
			for (int j = 0 ; j < heightStep; j++)
			{
				if(Math.random()< AppDefines.ObstacleProbability)
				{
					wp =  new Vec2();
					wp.x  = (float)(getCorners()[3].x + (i * obstacleSpacing)+(Math.random()*maxObstacleSpacingVariability-(maxObstacleSpacingVariability/2.0)));
					wp.y  = (float)(getCorners()[3].y + (j * obstacleSpacing)+(Math.random()*maxObstacleSpacingVariability-(maxObstacleSpacingVariability/2.0)));
					s = (float)((Math.random()*maxObstacleSizeVariability)+minObstacleSize);
					if(checkElementPositionConficts(wp,s))
						ret.add(new ObstaclePos(wp, (float)(Math.PI*Math.random()), s));						
				}
			}			
		}
		return ret;
	}

	protected boolean checkElementPositionConficts(Vec2 wp, float s) {
		for(VirtualWorldElement el: getWorldElements())
			if(MathUtils.distance(el.worldPosition,wp)<s+el.size)
				return false;			
		return true;
	}
	
	protected boolean checkObstaclePositionConficts(Vec2 wp, float s) {
		for(WorldElement ob: getObstacles())
			if(MathUtils.distance(ob.worldPosition,wp)<s+ob.size)
				return false;			
		return true;
	}
	
	protected boolean checkInBoundaries(Vec2 wp, float s) {
		if((wp.x - s > getCorners()[0].x) &&
				(wp.x + s < getCorners()[1].x) &&
				(wp.y - s > getCorners()[3].y) &&
				(wp.y + s < getCorners()[0].y) )
		{
				return true;			
		}
		return false;
	}
	
	
	protected Vec2 generatePositionInBoundaries(float margin) {
		return new Vec2(
				(float)(Math.random() * (getCorners()[1].x - getCorners()[0].x - (2*margin)))+getCorners()[3].x+margin,
				(float)(Math.random() * (getCorners()[0].y - getCorners()[2].y - (2*margin)))+getCorners()[3].y+margin
				);
	}
	
	protected Vec2 generatePositionInBoundaries() {
		return new Vec2(
				(float)(Math.random() * (getCorners()[1].x - getCorners()[0].x - (20f)))+getCorners()[3].x+10f,
				(float)(Math.random() * (getCorners()[0].y - getCorners()[2].y - (20f)))+getCorners()[3].y+10f
				);
	}
	
	public void loadObstacles(ArrayList<ObstaclePos> obs) {
		for(WorldElement o : getObstacles())
			o.removeFromWorld();
		getObstacles().clear();
			if(makeCynlinderObstacles || makeCircleWorld)
				for(ObstaclePos o : obs)
					getObstacles().add(new ObstacleStaticCyninder(o.wp, o.orientation,o.size, getWorld()));		
			else
				for(ObstaclePos o : obs)
					getObstacles().add(new ObstacleStaticBox(o.wp, o.orientation,o.size, getWorld()));	
			if(makeCynlinderObstacles || makeCircleWorld)
			{
				for(int i = (int) getCorners()[0].x;i < getCorners()[1].x;i+=minObstacleSize*4)
				{
					getObstacles().add(new ObstacleStaticCyninder(new Vec2(i,getCorners()[0].y), 0,(float) minObstacleSize*2.5f, getWorld()));	
					getObstacles().add(new ObstacleStaticCyninder(new Vec2(i,getCorners()[2].y), 0,(float) minObstacleSize*2.5f, getWorld()));	
				}

				for(int i = (int) getCorners()[3].y;i < getCorners()[0].y;i+=minObstacleSize*4)
				{
					getObstacles().add(new ObstacleStaticCyninder(new Vec2(getCorners()[0].x,i), 0,(float) minObstacleSize*2.5f, getWorld()));	
					getObstacles().add(new ObstacleStaticCyninder(new Vec2(getCorners()[1].x,i), 0,(float) minObstacleSize*2.5f, getWorld()));	
				}
			}
	}
	
	public class ObstaclePos{
		public Vec2 wp;
		public float orientation, size;
		
		public ObstaclePos(Vec2 inp,float orient, float s)
		{
			wp = inp;
			orientation = orient;
			size = s;
		}
	}
	
	public synchronized void generateAllObstaclesParameters(ArrayList<ArrayList<ObstaclePos>> obstaclesData, int repeatCount) {
		if(obstaclesData.size() < 1)
			for(int i = 0; i < repeatCount; i++)
				obstaclesData.add(generateObstaclesParameters());
	}

	public void init() {
		//Override me
	}
	
	public void preStepOps() {
		//Override me
		// called before a simulation step
	}
	
	public void preMindOps() {
		//Override me
		// called after the world step (physics), before the Mind step (agent logic)
	}
	
	public void postStepOps() {
		//Override me
		// called after a simulation step
	}
    
	public void preRepetitionOps() {
		//Override me
		// called before a repetition
	}
	
	//public void postRepetitionOps(ScoreCounter scoreCounter, SimulationInterruptFlag simulationInterruptFlag) {
	public void postRepetitionOps() {
		//Override me		
		// called after a repetition
	}
	
	public void preRunOps() {
		//Override me
		// called after the init, before starting the first repetition
	}

	//public void postRunOps(ScoreCounter score, SimulationInterruptFlag interruptFlag) {
	public void postRunOps() {
		//Override me		
		// called after the last repetition
	}

	public ArrayList<VirtualWorldElement> getWorldElements() {
		return worldElements;
	}
	
	public ArrayList<ControlledElement> getControlledElements() {
		return controlledElements;
	}

	public void setWorldElements(ArrayList<VirtualWorldElement> worldElements) {
		this.worldElements = worldElements;
		for(VirtualWorldElement v :worldElements)
			if(v instanceof ControlledElement)
			{
				controlledElements.add((ControlledElement) v);
			}
	}
	
	public void addWorldElement(VirtualWorldElement worldElement) {
		this.worldElements.add(worldElement);
		if(worldElement instanceof ControlledElement)
		{
			controlledElements.add((ControlledElement) worldElement);
		}
	}
	
	public void removeWorldElement(VirtualWorldElement worldElement) {
		this.worldElements.remove(worldElement);
		if(worldElement instanceof ControlledElement)
		{
			controlledElements.remove((ControlledElement) worldElement);
		}
	}

	public static synchronized World getWorld() {
		return World;
	}
	
	public static synchronized Body createWorldBody(BodyDef bd, Vec2 pos, float ang) {
		while(World.isLocked())
			;
		Body body = World.createBody(bd);
		while(body == null)
		{
			body =  World.createBody(bd);
			System.out.println("looping till callback unlocks.. that's ugly");
		}
		body.setTransform(pos, ang);
		return body;
	}
	
	public static synchronized void destroyWorldBody(Body body) {
		while(World.isLocked())
			;
		World.destroyBody(body);
	}

	public void setWorld(World world) {
		World = world;
	}

	public Vec2[] getCorners() {
		return corners;
	}

	public ArrayList<WorldElement> getObstacles() {
		return obstacles;
	}

	public long getTick() {
		return tick;
	}
	
	public void setSquareBorders (float halfWidth)
	{
		corners = new Vec2[]{
				new Vec2(-halfWidth , halfWidth),
				new Vec2(halfWidth, halfWidth),
				new Vec2(halfWidth, -halfWidth),
				new Vec2(-halfWidth, -halfWidth)}; 
	}

	public void setrectangularBorders (float halfWidth, float halfHeight)
	{
		corners = new Vec2[]{
				new Vec2(-halfWidth , halfHeight),
				new Vec2(halfWidth, halfHeight),
				new Vec2(halfWidth, -halfHeight),
				new Vec2(-halfWidth, -halfHeight)}; 
	}
	
	public synchronized void regenerateObstacles()
	{
		if(hasObstacles)
			loadObstacles(generateObstaclesParameters());
	}
	

	public void regenerateAll() {
		borders.removeFromWorld();
		borders2.removeFromWorld();
		makeBorders(getWorld());
		regenerateObstacles();
	}

	public float getMapWidth()
	{
		return corners[1].x;
	}
	public float getMapHeight()
	{
		return corners[1].y;
	}

	public abstract boolean hasBotCollision();

	public static synchronized void queryAABB(ClawQueryCallback cb, AABB sel) {
		World.queryAABB(cb, sel);
	}
}