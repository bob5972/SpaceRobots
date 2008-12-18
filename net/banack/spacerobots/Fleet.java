package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;

public class Fleet {
	//Internal Representation of an entire Fleet
	
	private FleetAI myAI;
	private int myCredits;
	private int myCreditIncrement;
	private int myFleetID;
	private int myTeamID;
	private int myNumShips;
	private boolean isAlive;
	private int winOrLose;
	
	public static final int WIN = 1;
	public static final int LOSE = -1;
	public static final int IN_PROGRESS=0; 
	
	public Fleet(int fleetID,int teamID, FleetAI ai)
	{
		myAI = ai;
		myFleetID = fleetID;
		myTeamID = teamID;
		winOrLose=IN_PROGRESS;
		isAlive=true;
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public void setAlive(boolean b)
	{
		isAlive=b;
	}
	
	public int getWinOrLose()
	{
		return winOrLose;
	}
	
	public void setWinOrLose(int x)
	{
		winOrLose = x;
		if(x != WIN && x!= LOSE && x != IN_PROGRESS)
			throw new IllegalArgumentException("Invalid setting for winOrLose x="+x);
	}
	
	public int getCreditIncrement()
	{
		return myCreditIncrement;
	}
	
	public void setCreditIncrement(int c)
	{
		myCreditIncrement=c;
	}
	
	public int getCredits()
	{
		return myCredits;
	}
	
	public void setCredits(int c)
	{
		myCredits =c;
	}
	
	public void incrementCredits(int c)
	{
		myCredits+=c;
	}
	
	public void decrementCredits(int c)
	{
		incrementCredits(-c);
	}
	
	public int getFleetID()
	{
		return myFleetID;
	}
	
	public final int getID()
	{
		return getFleetID();
	}
	
	public int getTeamID()
	{
		return myTeamID;
	}
	
	public FleetAI getAI()
	{
		return myAI;
	}
	
	public String getName()
	{
		throw new MethodNotImplementedException();
	}
	
	public String getAIName()
	{
		throw new MethodNotImplementedException();
	}
	
	public String getAIVersion()
	{
		throw new MethodNotImplementedException();
	}
	
	public int getNumShips()
	{
		return myNumShips;
	}
}
