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

public class TeamList
{
	private HashMap<Integer, ServerTeam> m;
	
	public TeamList()
	{
		m = new HashMap<Integer, ServerTeam>();
	}
	
	public int size()
	{
		return m.size();
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
		m.put(new Integer(t.getTeamID()), t);
	}
	
	public ServerTeam[] toArray()
	{
		ServerTeam[] oup = new ServerTeam[m.size()];
		
		Iterator<Integer> i = m.keySet().iterator();
		for (int x = 0; x < oup.length; x++) {
			oup[x] = ((ServerTeam) m.get(i.next()));
		}
		
		return oup;
	}
	
}
