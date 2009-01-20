package net.banack.spacerobots.util;

public class Team
{
	private int myTeamID;
	private String myName;
	
	public Team(int teamID, String name)
	{
		myTeamID = teamID;
		myName = name;
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
}
