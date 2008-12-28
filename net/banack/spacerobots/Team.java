package net.banack.spacerobots;

public class Team
{
	public Team(int teamID, String name)
	{
		myTeamID = teamID;
		myName = name;
		myLiveFleets=0;
	}
	
	private int myLiveFleets;
	private int myTeamID;
	private String myName;
	
	public boolean isAlive()
	{
		return myLiveFleets >0;
	}
	
	public int getTeamID()
	{
		return myTeamID;
	}
	
	public final int getID()
	{
		return getTeamID();
	}
	
	public String getName()
	{
		return myName;
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

