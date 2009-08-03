package net.banack.spacerobots;

import java.util.HashMap;

import net.banack.spacerobots.util.Ship;

public class FleetStats
{
	private HashMap<Integer, Integer> typeTotals;
	private HashMap<Integer, HashMap<Integer, Integer>> fleetTotals;
	
	public FleetStats()
	{
		typeTotals = new HashMap<Integer, Integer>();
		fleetTotals = new HashMap<Integer, HashMap<Integer, Integer>>();
	}
	
	public void add(ServerShip s)
	{
		increment(typeTotals, s.getTypeID());
		int fleetID = s.getFleetID();
		
		HashMap<Integer, Integer> f = fleetTotals.get(fleetID);
		if (f == null) {
			f = new HashMap<Integer, Integer>();
			fleetTotals.put(fleetID, f);
		}
		
		increment(f, s.getTypeID());
	}
	
	private void increment(HashMap<Integer, Integer> t, int typeID)
	{
		Integer cur = t.get(typeID);
		if (cur == null)
			cur = new Integer(0);
		
		int curCount = cur.intValue();
		curCount++;
		t.put(typeID, curCount);
	}
	
	public int getCount(int type)
	{
		Integer cur = typeTotals.get(type);
		if (cur == null)
			cur = new Integer(0);
		return cur.intValue();
	}
	
	public int getCount(int fleetID, int type)
	{
		HashMap<Integer, Integer> f = fleetTotals.get(fleetID);
		if (f == null) {
			return 0;
		}
		
		Integer cur = f.get(type);
		if (cur == null)
			cur = new Integer(0);
		return cur.intValue();
	}
}
