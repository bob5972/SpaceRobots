package net.banack.spacerobots;

import java.util.HashMap;
import java.util.Iterator;

public class FleetList
{
	private HashMap m;
	
	public FleetList()
	{
		m = new HashMap();
	}
	
	public java.util.Iterator iterator()
	{
		return m.values().iterator();
	}
	
	public ServerFleet get(int fleetID)
	{
		return (ServerFleet) m.get(new Integer(fleetID));
	}
	
	public void add(ServerFleet f)
	{
		m.put(new Integer(f.getFleetID()),f);
	}
	
	public ServerFleet[] toArray()
	{
		ServerFleet[] oup = new ServerFleet[m.size()];
		
		Iterator i = m.keySet().iterator(); 
		for(int x=0;x<oup.length;x++)
		{
			oup[x] = ((ServerFleet)m.get(i.next()));
		}
		
		return oup;
	}
	
}
