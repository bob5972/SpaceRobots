package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.Fleet;

public class ServerFleet extends Fleet
{
	//Internal Representation of an entire Fleet
	
	private FleetAI myAI;
	private int myCredits;
	private int myCreditIncrement;
	private int myNumShips;

	
	public ServerFleet(int fleetID,int teamID, FleetAI ai)
	{
		super(fleetID,teamID,"Fleet "+fleetID,ai.getName(),ai.getAuthor(),ai.getVersion());
		myAI = ai;
		setWinOrLose(STATUS_IN_PROGRESS);
		setAlive(true);
	}
	
	public ServerFleet(String name, int fleetID, int teamID, FleetAI ai)
	{
		super(fleetID,teamID,name,ai.getName(),ai.getAuthor(),ai.getVersion());
		myAI = ai;
		setWinOrLose(STATUS_IN_PROGRESS);
		setAlive(true);
	}
	
	public final String getName()
	{
		return getFleetName();
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
		
	public FleetAI getAI()
	{
		return myAI;
	}
	
	public int getNumShips()
	{
		return myNumShips;
	}
	
	public void setNumShips(int n)
	{
		myNumShips = n;
	}
}
