package net.banack.spacerobots;

public class Team
{
	public Team(int teamID, String name)
	{
		myTeamID = teamID;
		myName = name;
	}
	
	private int myTeamID;
	private String myName;
	
	public int getTeamID()
	{
		return myTeamID;
	}
	
	public String getName()
	{
		return myName;
	}
}
