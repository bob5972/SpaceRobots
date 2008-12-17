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
	
	public Fleet(int fleetID,int teamID, FleetAI ai)
	{
		myAI = ai;
		myFleetID = fleetID;
		myTeamID = teamID;
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
	
	public int getNumShips()
	{
		return myNumShips;
	}
}
