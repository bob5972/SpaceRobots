/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <github@banack.net>
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
import java.util.Iterator;

public class FleetList
{
	private HashMap<Integer, ServerFleet> m;
	
	public FleetList()
	{
		m = new HashMap<Integer, ServerFleet>();
	}
	
	public java.util.Iterator<ServerFleet> iterator()
	{
		return m.values().iterator();
	}
	
	public ServerFleet get(int fleetID)
	{
		return (ServerFleet) m.get(new Integer(fleetID));
	}
	
	public void add(ServerFleet f)
	{
		m.put(new Integer(f.getFleetID()), f);
	}
	
	public ServerFleet[] toArray()
	{
		ServerFleet[] oup = new ServerFleet[m.size()];
		
		Iterator<Integer> i = m.keySet().iterator();
		for (int x = 0; x < oup.length; x++) {
			oup[x] = ((ServerFleet) m.get(i.next()));
		}
		
		return oup;
	}
	
	public int size()
	{
		return m.size();
	}
}
