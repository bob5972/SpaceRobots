/*
 * This file is part of SpaceRobots.
 * Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SpaceRobots.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.banack.spacerobots.ServerShip;
import net.banack.spacerobots.ServerFleet;
import net.banack.util.IntMap;
import java.util.Iterator;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;

public class FleetContactList
{
	//Map of fleetID's to (HashMap's of enemyID's to (HashSets of spotterID's))	
	private Map<Integer, Map<Integer, HashSet<Integer> > > mySpotters;
	private IntMap myFleetSize;	
	
	public FleetContactList()
	{
		mySpotters = new HashMap<Integer, Map<Integer, HashSet<Integer> > >();
		myFleetSize = new IntMap();
	}
	

	public void makeEmpty()
	{
		mySpotters.clear();
		myFleetSize.makeEmpty();
	}
	
	public void addContact(ServerShip enemy, ServerShip spotter)
	{
		Map<Integer, HashSet<Integer> > eMap;
		
		
		
		Integer eID = new Integer(enemy.getID());
		Integer sID = new Integer(spotter.getID());
		Integer sFID = new Integer(spotter.getFleetID());
		
		if(mySpotters.containsKey(sFID))
		{
			eMap = mySpotters.get(sFID);
		}
		else
		{
			eMap = new HashMap<Integer, HashSet<Integer> >();
			mySpotters.put(sFID,eMap);
		}
		
		HashSet<Integer> sSet;
		if(eMap.containsKey(eID))
		{
			sSet = eMap.get(eID);
		}
		else
		{
			sSet = new HashSet<Integer>();
			eMap.put(eID,sSet);
		}
		
		sSet.add(sID);
		myFleetSize.increment(sFID.intValue());
	}
		
	//returns a new contact list that is linked to this one
	//ie, any additions or deletions will be reflected in the master list
	//  (but don't add anything from a different fleet...)
	public Map<Integer,HashSet<Integer>> getFleetList(ServerFleet f)
	{
		int fid = f.getFleetID();
		Integer ifid = new Integer(fid);
		
		return mySpotters.get(ifid);
	}
}
