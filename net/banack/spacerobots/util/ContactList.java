package net.banack.spacerobots.util;

import net.banack.util.MethodNotImplementedException;
import java.util.HashMap;
import java.util.HashSet;
import net.banack.util.IntMap;
import java.util.Iterator;

public class ContactList
{
	//HashMap of enemyID's to SensorContacts
	private HashMap myContacts;
	//HashMap of enemyID's to HashSets of spotterID's
	private HashMap mySpotters;
	//number of enemyID's in this list
	private int mySize;
	
	
	public ContactList()
	{
		myContacts = new HashMap();
		mySpotters = new HashMap();
		mySize=0;
	}
		

	public void makeEmpty()
	{
		myContacts.clear();
		mySpotters.clear();
		mySize=0;
	}
	
	//if two SensorContacts for the same enemy are added, only the first one will be stored
	public void addContact(SensorContact e, int spotterID)
	{
		Integer eID = new Integer(e.getID());
		Integer sID = new Integer(spotterID);
		
		HashSet sSet;

		if(mySpotters.containsKey(eID))
		{
			sSet = (HashSet)mySpotters.get(eID);
		}
		else
		{
			sSet = new HashSet();
			mySpotters.put(eID,sSet);
			myContacts.put(eID,e);
			mySize++;
		}
		
		sSet.add(sID);
	}
	
	//spotters MUST BE a HashSet of Integers of spotters
	// or BAD THINGS will  happen (like ClassCastExceptions)
	public void addContact(SensorContact e, HashSet spotters)
	{
		Integer eID = new Integer(e.getID());
		
		HashSet sSet;
		
		if(mySpotters.containsKey(eID))
		{
			sSet = (HashSet)mySpotters.get(eID);
		}
		else
		{
			sSet = new HashSet();
			mySpotters.put(eID,sSet);
			myContacts.put(eID,e);
			mySize++;
		}
		sSet.addAll(spotters);
	}
	
	
	//Iterator over Integers of enemyID's
	public Iterator enemyIterator()
	{
		return myContacts.keySet().iterator();
	}
	
	public SensorContact getContact(int enemyID)
	{
		return (SensorContact)myContacts.get(new Integer(enemyID));
	}
	
	public SensorContact getContact(Integer enemyID)
	{
		return (SensorContact)myContacts.get(enemyID);
	}
	
	//number of enemies listed
	public int size()
	{
		return mySize;
	}
	
	public boolean containsEnemy(int eID)
	{
		return myContacts.containsKey(new Integer(eID));
	}
	
	public boolean containsEnemy(Integer eID)
	{
		return myContacts.containsKey(eID);
	}
	
	public boolean containsSpotter(int sID)
	{
		throw new MethodNotImplementedException();
	}
	
	public HashSet getSpotters(int eID)
	{
		return (HashSet)mySpotters.get(new Integer(eID));
	}
	
	public HashSet getSpotters(Integer eID)
	{
		return (HashSet)mySpotters.get(eID);
	}
	
	public int[] getEnemies(int sID)
	{
		throw new MethodNotImplementedException();
	}
	
	public boolean contains(int eID, int sID)
	{
		Integer e = new Integer(eID);
		if(mySpotters.containsKey(e))
		{
			HashSet s = (HashSet)mySpotters.get(e);
			if(s.contains(new Integer(sID)))
				return true;
		}
		return false;
	}
}
