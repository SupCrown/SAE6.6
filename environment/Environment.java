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
import java.util.HashMap;


public class Environment {

	protected String name = "DefaultWorld";
	protected String botModel;	
	protected ArrayList<String> botModels;	
	protected boolean triggeredInterrupt = false;
	protected boolean hasObstacles = false;
	protected boolean hasBorders = true;
	protected boolean paused = false;
	private ArrayList<String> messageStrings = new ArrayList<>();
	private String winMessage = null;
	
	
    public Environment()
    {
		
    }

	public void doWorldStep()
	{
		
	}
	
	public void doStep() {

	}
/*
	public void doStep(ScoreCounter scoreCounter, SimulationInterruptFlag interruptFlag) {
		interruptFlag.registerInterrupt(triggeredInterrupt);
	}
	
	public void doAutoStep(ScoreCounter scoreCounter, SimulationInterruptFlag interruptFlag) {
		interruptFlag.registerInterrupt(triggeredInterrupt);
	}

	public HashMap<String,Double> doStep(HashMap<String,Double> actuatorValues, ScoreCounter scoreCounter, SimulationInterruptFlag interruptFlag) {
		interruptFlag.registerInterrupt(triggeredInterrupt);
		return null;
	}
*/	
    public void reset() {
		triggeredInterrupt = false;
		paused = false;
		setWinMessage(null);
	}
    
    public boolean hasTriggeredInterrupt()
    {
    	return triggeredInterrupt;
    }
    
    public synchronized void manualReset()
	{
    	triggeredInterrupt = true;
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
	
//	public void postRepetitionOps(ScoreCounter scoreCounter, SimulationInterruptFlag simulationInterruptFlag) {
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

	protected void makeBot() {
		
	}

	public void setBotModel(String botModel)
	{
		
	}

	public void setBotModel(ArrayList<String> botModel)
	{
		
	}
	
	public boolean getHasObstacles()
	{
		return hasObstacles;
	}

	public ArrayList<String> getMessageStrings()
	{
		return messageStrings;
	}

	public String getWinMessage() {
		return winMessage;
	}

	public void setWinMessage(String winMessage) {
		this.winMessage = winMessage;
	}

	public void addMessage(String msg)
	{
		messageStrings.add(msg);
	}
	
	public void clearMessages()
	{
		messageStrings.clear();
	}
	
	public boolean isPaused()
	{
		return paused;
	}
}