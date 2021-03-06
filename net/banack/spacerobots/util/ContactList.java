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

package net.banack.spacerobots.util;

import net.banack.geometry.DPoint;
import net.banack.util.JoinIterator;
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

/**
 * A list of sensor Contacts and the IDs of ships that scanned them (spotters).
 * <p>
 * Contacts given out by this ContactList are stable (ie, they will be updated if the ContactList is updated.
 */
public class ContactList
{
	// HashMap of active enemyID's to Contacts
	private HashMap<Integer, Contact> myShipContacts;
	private HashMap<Integer, Contact> myAmmoContacts;
	// HashMap of enemyID's to HashSets of spotterID's
	private HashMap<Integer, Set<Integer>> mySpotters;
	// number of enemyID's in this list
	private int mySize;
	// ie all the references we've ever given out
	private HashMap<Integer, Contact> myMasterContacts;
	
	public ContactList()
	{
		myMasterContacts = new HashMap<Integer, Contact>();
		myShipContacts = new HashMap<Integer, Contact>();
		myAmmoContacts = new HashMap<Integer, Contact>();
		mySpotters = new HashMap<Integer, Set<Integer>>();
		mySize = 0;
	}
	
	public void makeEmpty()
	{
		myShipContacts.clear();
		myAmmoContacts.clear();
		mySpotters.clear();
		mySize = 0;
	}
	
	/** Clears the current contact and spotter information, but retains the previously seen contacts. */
	public void resetForTick()
	{
		makeEmpty();
	}
	
	private HashMap<Integer, Contact> getContactMap(Contact e)
	{
		if (e.isAmmo()) {
			return myAmmoContacts;
		} else {
			return myShipContacts;
		}
	}
	
	
	public void addContact(Contact e, int spotterID)
	{
		Integer eID = new Integer(e.getID());
		Integer sID = new Integer(spotterID);
		Contact old;
		HashMap<Integer, Contact> contactMap = getContactMap(e);
		

		Set<Integer> sSet;
		
		if (myMasterContacts.containsKey(eID)) {
			old = myMasterContacts.get(eID);
		} else {
			old = new Contact(e);
			myMasterContacts.put(old.getID(), old);
		}
		
		if (!contactMap.containsKey(eID))
			mySize++;
		
		old.update(e);
		contactMap.put(eID, old);
		
		if (mySpotters.containsKey(eID)) {
			sSet = mySpotters.get(eID);
		} else {
			sSet = new HashSet<Integer>();
			mySpotters.put(eID, sSet);
		}
		
		sSet.add(sID);
	}
	
	/** Adds a contact with the set of spotters. */
	public void addContact(Contact e, HashSet<Integer> spotters)
	{
		Integer eID = new Integer(e.getID());
		HashMap<Integer, Contact> contactMap = getContactMap(e);
		
		Set<Integer> sSet;
		Contact old;
		
		if (myMasterContacts.containsKey(eID)) {
			old = myMasterContacts.get(eID);
		} else {
			old = new Contact(e);
			myMasterContacts.put(old.getID(), old);
		}
		
		if (!contactMap.containsKey(eID))
			mySize++;
		
		old.update(e);
		contactMap.put(eID, old);
		
		if (mySpotters.containsKey(eID)) {
			sSet = mySpotters.get(eID);
		} else {
			sSet = new HashSet<Integer>();
			mySpotters.put(eID, sSet);
		}
		
		sSet.addAll(spotters);
	}
	
	/**
	 * Returns the collection of contacts (currently) in this list.
	 * <p>
	 * Please don't modify it.
	 */
	// You probably really shouldn't be modifying this...I miss constant reference
	public Collection<Contact> getShipContacts()
	{
		return myShipContacts.values();
	}
	
	/**
	 * Returns the collection of contacts (currently) in this list.
	 * <p>
	 * Please don't modify it.
	 */
	// You probably really shouldn't be modifying this...I miss constant reference
	public Collection<Contact> getAmmoContacts()
	{
		return myAmmoContacts.values();
	}
	
	/**
	 * Returns the collection of <i>all</i> contacts in this list (including old ones).
	 * <p>
	 * Please don't modify it.
	 */
	public Collection<Contact> getOldContacts()
	{
		return myMasterContacts.values();
	}
	
	
	/** Returns an iterator over Integers of enemyID's. */
	public Iterator<Integer> enemyIterator()
	{
		return new JoinIterator<Integer>(enemyShipIterator(), enemyAmmoIterator());
	}
	
	/** Returns an iterator over Integers of enemyID's. */
	public Iterator<Integer> enemyShipIterator()
	{
		return myShipContacts.keySet().iterator();
	}
	
	/** Returns an iterator over Integers of enemyID's. */
	public Iterator<Integer> enemyAmmoIterator()
	{
		return myAmmoContacts.keySet().iterator();
	}
	
	/** Gets a (current) contact for a given enemy id. */
	public final Contact get(int enemyID)
	{
		return getContact(enemyID);
	}
	
	/** Gets a (current) contact for a given enemy id. */
	public final Contact get(Integer enemyID)
	{
		return getContact(enemyID);
	}
	
	/** Gets a (possibly old) contact for a given enemy id. */
	public final Contact getOld(int enemyID)
	{
		return getOldContact(new Integer(enemyID));
	}
	
	/** Gets a (possibly old) contact for a given enemy id. */
	public final Contact getOld(Integer enemyID)
	{
		return getOldContact(enemyID);
	}
	
	/** Gets a (current) contact for a given enemy id. */
	public final Contact getContact(int enemyID)
	{
		return getContact(new Integer(enemyID));
	}
	
	/** Gets a (current) contact for a given enemy id. */
	public Contact getContact(Integer enemyID)
	{
		Contact oup;
		
		oup = myShipContacts.get(enemyID);
		if (oup == null)
			oup = myAmmoContacts.get(enemyID);
		return oup;
	}
	
	/** Gets a (possibly old) contact for a given enemy id. */
	public final Contact getOldContact(int enemyID)
	{
		return getOldContact(new Integer(enemyID));
	}
	
	/** Gets a (possibly old) contact for a given enemy id. */
	public Contact getOldContact(Integer enemyID)
	{
		return myMasterContacts.get(enemyID);
	}
	
	/** Number of enemies (currently) listed. */
	public int size()
	{
		return mySize;
	}
	
	/** Number of enemies (currently) listed. */
	public int shipSize()
	{
		return myShipContacts.size();
	}
	
	/** Number of enemies (currently) listed. */
	public int ammoSize()
	{
		return myAmmoContacts.size();
	}
	
	/** Returns true if we contain the specified enemy id (currently). */
	public boolean containsEnemy(int eID)
	{
		return myShipContacts.containsKey(new Integer(eID)) || myAmmoContacts.containsKey(new Integer(eID));
	}
	
	/** Returns true if we contain the specified enemy id (currently). */
	public boolean containsEnemy(Integer eID)
	{
		return myShipContacts.containsKey(eID) || myAmmoContacts.containsKey(eID);
	}
	
	/**
	 * Returns a set of all spotters for a given enemy id.
	 * <p>
	 * Please don't modify it.
	 */
	public Set<Integer> getSpotters(int eID)
	{
		return mySpotters.get(new Integer(eID));
	}
	
	/**
	 * Returns a set of all spotters for a given enemy id.
	 * <p>
	 * Please don't modify it.
	 */
	public Set<Integer> getSpotters(Integer eID)
	{
		return mySpotters.get(eID);
	}
	
	/**
	 * Returns a set of enemy ids (currently) in the list.
	 * <p>
	 * Please don't modify it.
	 */
	public Set<Integer> getShipIDSet()
	{
		return myShipContacts.keySet();
	}
	
	/**
	 * Returns a set of enemy id's (currently) in the list.
	 * <p>
	 * Please don't modify it.
	 */
	public Set<Integer> getAmmoIDSet()
	{
		return myAmmoContacts.keySet();
	}
	
	/** Returns true iff the enemy is currently being scanned by the given spotter. */
	public boolean contains(int eID, int sID)
	{
		Integer e = new Integer(eID);
		if (mySpotters.containsKey(e)) {
			Set<Integer> s = mySpotters.get(e);
			if (s.contains(new Integer(sID)))
				return true;
		}
		return false;
	}
}
