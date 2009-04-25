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
	private int myFleetIndex;
	private ServerTeam myTeam;

	
	public ServerFleet(int fleetID,int fleetIndex,int teamID, ServerTeam t, FleetAI ai)
	{
		super(fleetID,teamID,"Fleet "+fleetID,null,null,null);
		myFleetIndex=fleetIndex;
		myAI = ai;
		setAlive(true);
		myTeam =t;
	}
	
	public ServerFleet(String name, int fleetID, int fleetIndex, int teamID, ServerTeam t, FleetAI ai)
	{
		super(fleetID,teamID,name,ai.getName(),ai.getAuthor(),ai.getVersion());
		myFleetIndex=fleetIndex;
		myAI = ai;
		setAlive(true);
		myTeam=t;
	}
	
	
	public String getAIName()
	{
		return myAI.getName();
	}
	
	public String getAIVersion()
	{
		return myAI.getVersion();
	}
	
	public String getAIAuthor()
	{
		return myAI.getAuthor();
	}

	
	public ServerTeam getTeam()
	{
		return myTeam;
	}
	
	public final int getIndex()
	{
		return getFleetIndex();
	}
	
	public int getFleetIndex()
	{
		return myFleetIndex;
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
