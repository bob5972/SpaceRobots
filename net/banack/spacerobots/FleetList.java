package net.banack.spacerobots;

import java.util.HashMap;

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
	
}
