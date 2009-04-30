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
	private Map<Integer, Map<Integer, Set<Integer> > > mySpotters;
	private IntMap myFleetSize;	
	
	public FleetContactList()
	{
		mySpotters = new HashMap<Integer, Map<Integer, Set<Integer> > >();
		myFleetSize = new IntMap();
	}
	

	public void makeEmpty()
	{
		mySpotters.clear();
		myFleetSize.makeEmpty();
	}
	
	public void addContact(ServerShip enemy, ServerShip spotter)
	{
		Map<Integer, Set<Integer> > eMap;
		
		
		
		Integer eID = new Integer(enemy.getID());
		Integer sID = new Integer(spotter.getID());
		Integer sFID = new Integer(spotter.getFleetID());
		
		if(mySpotters.containsKey(sFID))
		{
			eMap = mySpotters.get(sFID);
		}
		else
		{
			eMap = new HashMap<Integer, Set<Integer> >();
			mySpotters.put(sFID,eMap);
		}
		
		Set<Integer> sSet;
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
	public Map<Integer,Set<Integer>> getFleetList(ServerFleet f)
	{
		int fid = f.getFleetID();
		Integer ifid = new Integer(fid);
		
		return mySpotters.get(ifid);
	}
}
