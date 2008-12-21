package net.banack.spacerobots;

import java.util.HashMap;

public class ShipList
{
	private HashMap myShips;
	
	public ShipList()
	{
		myShips = new HashMap();
	}
	
	public boolean add(Ship s)
	{
		return (myShips.put(new Integer(s.getShipID()),s)==null);
	}
	
	public Ship get(int shipID)
	{
		return (Ship)myShips.get(new Integer(shipID));
	}
	
	public java.util.Iterator iterator()
	{
		return myShips.values().iterator();
	}
}
