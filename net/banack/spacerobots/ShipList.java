package net.banack.spacerobots;

import java.util.HashMap;

public class ShipList
{
	private HashMap myShips;
	
	public ShipList()
	{
		myShips = new HashMap();
	}
	
	public boolean add(ServerShip s)
	{
		return (myShips.put(new Integer(s.getShipID()),s)==null);
	}
	
	public int size()
	{
		return myShips.size();
	}
	
	public void remove(ServerShip s)
	{
		myShips.remove(new Integer(s.getID()));
	}
	
	public ServerShip get(int shipID)
	{
		return (ServerShip)myShips.get(new Integer(shipID));
	}
	
	public java.util.Iterator iterator()
	{
		return myShips.values().iterator();
	}
}
