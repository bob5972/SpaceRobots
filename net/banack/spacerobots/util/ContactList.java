package net.banack.spacerobots.util;

import net.banack.util.MethodNotImplementedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.banack.util.IntMap;
import java.util.Iterator;

public class ContactList
{
	//HashMap of enemyID's to SensorContacts
	private HashMap<Integer,Contact> myContacts;
	//HashMap of enemyID's to HashSets of spotterID's
	private HashMap<Integer, Set<Integer> > mySpotters;
	//number of enemyID's in this list
	private int mySize;
	
	
	public ContactList()
	{
		myContacts = new HashMap<Integer,Contact>();
		mySpotters = new HashMap<Integer, Set<Integer> >();
		mySize=0;
	}
		

	public void makeEmpty()
	{
		myContacts.clear();
		mySpotters.clear();
		mySize=0;
	}
	
	//if two SensorContacts for the same enemy are added, only the first one will be stored
	public void addContact(Contact e, int spotterID)
	{
		Integer eID = new Integer(e.getID());
		Integer sID = new Integer(spotterID);
		
		Set<Integer> sSet;

		if(mySpotters.containsKey(eID))
		{
			sSet = mySpotters.get(eID);
		}
		else
		{
			sSet = new HashSet<Integer>();
			mySpotters.put(eID,sSet);
			myContacts.put(eID,e);
			mySize++;
		}
		
		sSet.add(sID);
	}
	
	//spotters MUST BE a HashSet of Integers of spotters
	// or BAD THINGS will  happen (like ClassCastExceptions)
	public void addContact(Contact e, Set<Integer> spotters)
	{
		Integer eID = new Integer(e.getID());
		
		Set<Integer> sSet;
		
		if(mySpotters.containsKey(eID))
		{
			sSet = mySpotters.get(eID);
		}
		else
		{
			sSet = new HashSet<Integer>();
			mySpotters.put(eID,sSet);
			myContacts.put(eID,e);
			mySize++;
		}
		sSet.addAll(spotters);
	}
	
	
	//Iterator over Integers of enemyID's
	public Iterator<Integer> enemyIterator()
	{
		return myContacts.keySet().iterator();
	}
	
	public Contact getContact(int enemyID)
	{
		return myContacts.get(new Integer(enemyID));
	}
	
	public Contact getContact(Integer enemyID)
	{
		return myContacts.get(enemyID);
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
	
	public Set<Integer> getSpotters(int eID)
	{
		return mySpotters.get(new Integer(eID));
	}
	
	public Set<Integer> getSpotters(Integer eID)
	{
		return mySpotters.get(eID);
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
			Set<Integer> s = mySpotters.get(e);
			if(s.contains(new Integer(sID)))
				return true;
		}
		return false;
	}
}
