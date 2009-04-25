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
	//HashMap of enemyID's to SensorContacts
	private HashMap<Integer,Contact> myContacts;
	//HashMap of fleetID's to (HashMap's of enemyID's to (HashSets of spotterID's))	
	private HashMap<Integer, Map<Integer, Set<Integer> > > mySpotters;
	private IntMap myFleetSize;
	
	
	public FleetContactList()
	{
		myContacts = new HashMap<Integer,Contact>();
		mySpotters = new HashMap<Integer, Map<Integer, Set<Integer> > >();
		myFleetSize = new IntMap();
	}
	

	public void makeEmpty()
	{
		myContacts.clear();
		mySpotters.clear();
		myFleetSize.makeEmpty();
	}
	
	public void addContact(ServerShip enemy, ServerShip spotter)
	{
		Map<Integer, Set<Integer> > eMap;
		
		
		
		Integer eID = new Integer(enemy.getID());
		Integer sID = new Integer(spotter.getID());
		Integer sFID = new Integer(spotter.getFleetID());
		
		if(!myContacts.containsKey(eID))
			myContacts.put(eID,new Contact(enemy));
		
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
	public ContactList getFleetList(ServerFleet f)
	{
		int fid = f.getFleetID();
		Integer ifid = new Integer(fid);
		ContactList oup = new ContactList();
		
		if(!mySpotters.containsKey(ifid))
			return oup;
		
		Map<Integer, Set<Integer> > eMap = mySpotters.get(ifid);
		
		Iterator<Integer> i = eMap.keySet().iterator();
		
		while(i.hasNext())
		{
			Integer eid = (Integer)i.next();
			Contact c = (Contact)myContacts.get(eid);
			
			oup.addContact(c,eMap.get(eid));			
		}
		
		return oup;
	}
}
