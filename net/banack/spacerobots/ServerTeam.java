package net.banack.spacerobots;

import net.banack.spacerobots.util.Team;

public class ServerTeam extends Team
{
	public ServerTeam(int teamID, String name)
	{
		super(teamID,name);
		myLiveFleets=0;
	}
	
	private int myLiveFleets;
	
	public boolean isAlive()
	{
		return myLiveFleets >0;
	}
	

	public int getLiveFleets()
	{
		return myLiveFleets;
	}
	
	public void setLiveFleets(int n)
	{
		myLiveFleets = n;
	}
	
	public void incrementLiveFleets(int n)
	{
		myLiveFleets += n;
	}
	
	public void decrementLiveFleets(int n)
	{
		myLiveFleets-=n;
	}
}

