package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;
import java.util.HashMap;
import java.util.HashSet;
import net.banack.spacerobots.Ship;
import net.banack.spacerobots.Fleet;
import net.banack.util.IntMap;
import java.util.Iterator;
import net.banack.spacerobots.util.SensorContact;
import net.banack.spacerobots.util.ContactList;

public class FleetContactList
{
	//HashMap of enemyID's to SensorContacts
	private HashMap myContacts;
	//HashMap of fleetID's to (HashMap's of enemyID's to (HashSets of spotterID's))	
	private HashMap mySpotters;
	private IntMap myFleetSize;
	
	
	public FleetContactList()
	{
		myContacts = new HashMap();
		mySpotters = new HashMap();
		myFleetSize = new IntMap();
	}
	

	public void makeEmpty()
	{
		myContacts.clear();
		mySpotters.clear();
		myFleetSize.makeEmpty();
	}
	
	public void addContact(Ship enemy, Ship spotter)
	{
		HashMap eMap;
		
		
		
		Integer eID = new Integer(enemy.getID());
		Integer sID = new Integer(spotter.getID());
		Integer sFID = new Integer(spotter.getFleetID());
		
		if(!myContacts.containsKey(eID))
			myContacts.put(eID,new SensorContact(enemy));
		
		if(mySpotters.containsKey(sFID))
		{
			eMap = (HashMap)mySpotters.get(sFID);
		}
		else
		{
			eMap = new HashMap();
			mySpotters.put(sFID,eMap);
		}
		
		HashSet sSet;
		if(eMap.containsKey(eID))
		{
			sSet = (HashSet)eMap.get(eID);
		}
		else
		{
			sSet = new HashSet();
			eMap.put(eID,sSet);
		}
		
		sSet.add(sID);
		myFleetSize.increment(sFID.intValue());
	}
		
	//returns a new contact list that is linked to this one
	//ie, any additions or deletions will be reflected in the master list
	//  (but don't add anything from a different fleet...)
	public ContactList getFleetList(Fleet f)
	{
		int fid = f.getFleetID();
		Integer ifid = new Integer(fid);
		ContactList oup = new ContactList();
		
		if(!mySpotters.containsKey(ifid))
			return oup;
		
		HashMap eMap = (HashMap)mySpotters.get(ifid);
		
		Iterator i = eMap.keySet().iterator();
		
		while(i.hasNext())
		{
			Integer eid = (Integer)i.next();
			SensorContact c = (SensorContact)myContacts.get(eid);
			
			oup.addContact(c,(HashSet)eMap.get(eid));			
		}
		
		return oup;
	}
}
