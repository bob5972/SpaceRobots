package net.banack.spacerobots.util;

import net.banack.geometry.DPoint;
import net.banack.util.MethodNotImplementedException;
import net.banack.util.SkipList;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.banack.util.IntMap;
import java.util.Iterator;

public class ContactList
{
	//HashMap of active enemyID's to Contacts
	private HashMap<Integer,Contact> myContacts;
	//HashMap of enemyID's to HashSets of spotterID's
	private HashMap<Integer, Set<Integer> > mySpotters;
	//number of enemyID's in this list
	private int mySize;
	//ie all the references we've ever given out
	private HashMap<Integer,Contact> myMasterContacts;
	
	public ContactList()
	{
		myMasterContacts = new HashMap<Integer,Contact>();
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
	
	public void resetForTick()
	{
		myContacts.clear();
		mySpotters.clear();
		mySize=0;
	}
		
	
	public void addContact(Contact e, int spotterID)
	{
		Integer eID = new Integer(e.getID());
		Integer sID = new Integer(spotterID);
		Contact old;
		
		Set<Integer> sSet;

		if(myMasterContacts.containsKey(eID))
		{
			old = myMasterContacts.get(eID);
		}
		else
		{
			old = new Contact(e);
			myMasterContacts.put(old.getID(),old);
		}
		
		if(!myContacts.containsKey(eID))
			mySize++;
		
		old.update(e);
		myContacts.put(eID,old);
		
		if(mySpotters.containsKey(eID))
		{
			sSet = mySpotters.get(eID);
		}
		else
		{
			sSet = new HashSet<Integer>();
			mySpotters.put(eID,sSet);
		}	
		
		sSet.add(sID);
	}
	
	//spotters MUST BE a HashSet of Integers of spotters
	// or BAD THINGS will  happen (like ClassCastExceptions)
	public void addContact(Contact e, Set<Integer> spotters)
	{
		Integer eID = new Integer(e.getID());
		
		Set<Integer> sSet;
		Contact old;
		
		if(myMasterContacts.containsKey(eID))
		{
			old = myMasterContacts.get(eID);
		}
		else
		{
			old = new Contact(e);
			myMasterContacts.put(old.getID(),old);
		}
		
		if(!myContacts.containsKey(eID))
			mySize++;
		
		old.update(e);
		myContacts.put(eID,old);
		
		if(mySpotters.containsKey(eID))
		{
			sSet = mySpotters.get(eID);
		}
		else
		{
			sSet = new HashSet<Integer>();
			mySpotters.put(eID,sSet);
		}
		
		sSet.addAll(spotters);
	}
	
	//You probably really shouldn't be modifying this...I miss constant reference
	public Collection<Contact> getContacts()
	{
		return myContacts.values();
	}
	
	//includes active ones...
	public Collection<Contact> getOldContacts()
	{
		return myMasterContacts.values();
	}
	
	
	
	//Iterator over Integers of enemyID's
	public Iterator<Integer> enemyIterator()
	{
		return myContacts.keySet().iterator();
	}
	
	public final Contact get(int enemyID)
	{
		return getContact(enemyID);
	}
	
	public final Contact get(Integer enemyID)
	{
		return getContact(enemyID);
	}
	
	public final Contact getOld(int enemyID)
	{
		return getOldContact(new Integer(enemyID));
	}
	
	public final Contact getOld(Integer enemyID)
	{
		return getOldContact(enemyID);
	}
	
	public final Contact getContact(int enemyID)
	{
		return getContact(new Integer(enemyID));
	}
	
	public Contact getContact(Integer enemyID)
	{
		return myContacts.get(enemyID);
	}
	
	public final Contact getOldContact(int enemyID)
	{
		return getOldContact(new Integer(enemyID));
	}
	
	public Contact getOldContact(Integer enemyID)
	{
		return myMasterContacts.get(enemyID);
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
	
	public Set<Integer> getSpotters(int eID)
	{
		return mySpotters.get(new Integer(eID));
	}
	
	public Set<Integer> getSpotters(Integer eID)
	{
		return mySpotters.get(eID);
	}
	
	public Set<Integer> getIDSet()
	{
		return myContacts.keySet();
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
