/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with SpaceRobots. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots;

import java.util.HashMap;

public class ShipList
{
	private HashMap<Integer, ServerShip> myShips;
	
	public ShipList()
	{
		myShips = new HashMap<Integer, ServerShip>();
	}
	
	public boolean add(ServerShip s)
	{
		return (myShips.put(new Integer(s.getShipID()), s) == null);
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
		return (ServerShip) myShips.get(new Integer(shipID));
	}
	
	public java.util.Iterator<ServerShip> iterator()
	{
		return myShips.values().iterator();
	}
}
