package net.banack.spacerobots;

import java.util.HashMap;
import java.util.Iterator;

public class TeamList
{
	private HashMap<Integer,ServerTeam> m;
	
	public TeamList()
	{
		m = new HashMap<Integer,ServerTeam>();
	}
	
	public java.util.Iterator<ServerTeam> iterator()
	{
		return m.values().iterator();
	}
	
	public ServerTeam get(int teamID)
	{
		return (ServerTeam) m.get(new Integer(teamID));
	}
	
	public void add(ServerTeam t)
	{
		m.put(new Integer(t.getTeamID()),t);
	}
	
	public ServerTeam[] toArray()
	{
		ServerTeam[] oup = new ServerTeam[m.size()];
		
		Iterator<Integer> i = m.keySet().iterator(); 
		for(int x=0;x<oup.length;x++)
		{
			oup[x] = ((ServerTeam)m.get(i.next()));
		}
		
		return oup;
	}
	
}
