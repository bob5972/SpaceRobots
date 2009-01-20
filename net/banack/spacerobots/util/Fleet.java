package net.banack.spacerobots.util;


public class Fleet
{
	private int myFleetID;//internal battle ID
	private int myTeamID;
	private boolean isAlive;
	private int winOrLose;
	private String myFleetName;
	private String myAIName;
	private String myAIAuthor;
	private String myAIVersion;
	
	public static final int STATUS_WIN = 1;
	public static final int STATUS_LOSE = -1;
	public static final int STATUS_IN_PROGRESS=0; 
	
	public Fleet(int fleetID,int teamID,String fleetName, String aiName, String aiAuthor, String aiVersion)
	{
		myFleetID = fleetID;
		myTeamID = teamID;
		winOrLose=STATUS_IN_PROGRESS;
		isAlive=true;
		myFleetName = fleetName;
		myAIName = aiName;
		myAIVersion = aiVersion;
		myAIAuthor = aiAuthor;
	}
	
	public Fleet(int fleetID,int teamID,String fleetName, String aiName, String aiAuthor, String aiVersion,boolean alive, boolean won)
	{
		myFleetID = fleetID;
		myTeamID = teamID;
		winOrLose=(won?STATUS_WIN:STATUS_LOSE);
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
	
	public int getWinOrLose()
	{
		return winOrLose;
	}
	
	public void setWinOrLose(int x)
	{
		winOrLose = x;
		if(x != STATUS_WIN && x!= STATUS_LOSE && x != STATUS_IN_PROGRESS)
			throw new IllegalArgumentException("Invalid setting for winOrLose x="+x);
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

