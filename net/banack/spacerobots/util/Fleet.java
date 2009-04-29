package net.banack.spacerobots.util;


public class Fleet
{
	private int myFleetID;//internal battle ID
	private int myTeamID;
	private boolean isAlive;
	private String myFleetName;
	private String myAIName;
	private String myAIAuthor;
	private String myAIVersion;
	
	public Fleet(int fleetID,int teamID,String fleetName, String aiName, String aiAuthor, String aiVersion)
	{
		myFleetID = fleetID;
		myTeamID = teamID;
		isAlive=true;
		myFleetName = fleetName;
		myAIName = aiName;
		myAIVersion = aiVersion;
		myAIAuthor = aiAuthor;
	}
	
	public Fleet(int fleetID,int teamID,String fleetName, String aiName, String aiAuthor, String aiVersion,boolean alive)
	{
		myFleetID = fleetID;
		myTeamID = teamID;
		isAlive=alive;
		myFleetName = fleetName;
		myAIName = aiName;
		myAIAuthor = aiAuthor;
		myAIVersion = aiVersion;
	}
	
	public String getFleetName()
	{
		return myFleetName;
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public void setAlive(boolean b)
	{
		isAlive=b;
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
	
	
	public String getAIName()
	{
		return myAIName;
	}
	
	public String getAIVersion()
	{
		return myAIVersion;
	}
	
	public String getAIAuthor()
	{
		return myAIAuthor;
	}

}

