package net.banack.spacerobots;

import java.util.HashMap;

public class TeamList
{
	private HashMap m;
	
	public TeamList()
	{
		m = new HashMap();
	}
	
	public java.util.Iterator iterator()
	{
		return m.values().iterator();
	}
	
	public Team get(int teamID)
	{
		return (Team) m.get(new Integer(teamID));
	}
	
	public void add(Team t)
	{
		m.put(new Integer(t.getTeamID()),t);
	}
	
}