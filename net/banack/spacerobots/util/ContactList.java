package net.banack.spacerobots.util;

import net.banack.util.MethodNotImplementedException;
import java.util.HashMap;
import java.util.HashSet;
import net.banack.spacerobots.Ship;
import net.banack.spacerobots.Fleet;
import net.banack.util.IntMap;
import java.util.Iterator;

public class ContactList
{
	//HashMaps of eID to HashSets of spotterID's
	private HashMap myContacts;
	private int mySize;
	
	//m MUST BE a HashMap of eID's to HashSets of spotterID's
	//size MUST BE the total number of spotterID's in all HashSets
	public ContactList(int size, HashMap m)
	{
		mySize=size;
		myContacts = m;
	}
	
	
	public ContactList()
	{
		myContacts = new HashMap();
		mySize=0;
	}
		

	public void makeEmpty()
	{
		myContacts.clear();
		mySize=0;
	}
	
	public void addContact(Ship enemy, Ship spotter)
	{
		addContact(enemy.getShipID(),spotter.getShipID());
	}
	
	public void addContact(int enemyID, int spotterID)
	{
		HashMap eMap = myContacts;
		Integer eID = new Integer(enemyID);
		Integer sID = new Integer(spotterID);
		
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
	}
	
	public void addContact(SensorContact c)
	{
		addContact(c.getEnemyID(), c.getSpotterID());
	}
	
	public Iterator iterator()
	{
		throw new MethodNotImplementedException();
	}
	
	
	public int size()
	{
		return mySize;
	}
	
	public boolean containsEnemy(int eID)
	{
		return myContacts.containsKey(new Integer(eID));
	}
	
	public boolean containsSpotter(int sID)
	{
		throw new MethodNotImplementedException();
	}
	
	public HashSet getSpotters(int eID)
	{
		return (HashSet)myContacts.get(new Integer(eID));
	}
	
	public int[] getEnemies(int sID)
	{
		throw new MethodNotImplementedException();
	}
	
	public boolean contains(int eID, int sID)
	{
		Integer e = new Integer(eID);
		if(myContacts.containsKey(e))
		{
			HashSet s = (HashSet)myContacts.get(e);
			if(s.contains(new Integer(sID)))
				return true;
		}
		return false;
	}
}
