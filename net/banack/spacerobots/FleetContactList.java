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
	//HashMap of fleetID to HashMaps of eID to HashSets of spotterID's
	private HashMap myContacts;
	private IntMap myFleetSize;
	private int mySize;
	
	
	public FleetContactList()
	{
		myContacts = new HashMap();
		myFleetSize = new IntMap();
		mySize=0;
	}
	

	public void makeEmpty()
	{
		myContacts.clear();
		mySize=0;
		myFleetSize.makeEmpty();
	}
	
	public void addContact(Ship enemy, Ship spotter)
	{
		addContact(enemy.getShipID(),spotter.getShipID(), spotter.getFleetID());
	}
	
	public void addContact(int enemyID, int spotterID, int fleetID)
	{
		HashMap eMap;
		Integer eID = new Integer(enemyID);
		Integer sID = new Integer(spotterID);
		Integer fID = new Integer(fleetID);
		
		if(myContacts.containsKey(fID))
		{
			eMap = (HashMap)myContacts.get(fID);
		}
		else
		{
			eMap = new HashMap();
			myContacts.put(fID,eMap);
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
		mySize++;
		myFleetSize.increment(fleetID);
	}
	
	public void addContact(SensorContact c)
	{
		addContact(c.getEnemyID(), c.getSpotterID(), c.getFleetID());
	}
	
	public void addContact(int enemyID, int spotterID)
	{
		addContact(enemyID, spotterID, -1);
	}
	
	//returns a new contact list that is linked to this one
	//ie, any additions or deletions will be reflected in the master list
	//  (but don't add anything from a different fleet...)
	public ContactList getFleetList(Fleet f)
	{
		int id = f.getFleetID();
		return new ContactList(myFleetSize.get(id),(HashMap)myContacts.get(new Integer(id)));
	}
	
	public int size()
	{
		return mySize;
	}
}
