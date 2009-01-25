package net.banack.spacerobots;

import net.banack.spacerobots.util.Team;

public class ServerTeam extends Team
{
	public ServerTeam(int teamID, int teamIndex, String name)
	{
		super(teamID,name);
		myLiveFleets=0;
		myTeamIndex = teamIndex;
	}
	
	private int myLiveFleets;
	private int myTeamIndex;
	
	public final int getIndex()
	{
		return getTeamIndex();
	}
	
	public int getTeamIndex()
	{
		return myTeamIndex;
	}
	
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

