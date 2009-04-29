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
	//contacts sorted by xPos
	private SkipList<Contact> byPos;
	
	
	public ContactList()
	{
		myMasterContacts = new HashMap<Integer,Contact>();
		myContacts = new HashMap<Integer,Contact>();
		mySpotters = new HashMap<Integer, Set<Integer> >();
		byPos = new SkipList<Contact>(new Comparator<Contact>() {
			public int compare(Contact lhs, Contact rhs)
			{
				double rx = rhs.getX();
				double lx = lhs.getX();
				return (int)(rx - lx);
			}
		});
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
		byPos.clear();
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
		else
			byPos.remove(old);
		
		old.update(e);
		myContacts.put(eID,old);
		byPos.add(old);

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
		else
			byPos.remove(old);
		
		old.update(e);
		myContacts.put(eID,old);
		byPos.add(old);
		
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
	
	public Contact getContact(int enemyID)
	{
		return myContacts.get(new Integer(enemyID));
	}
	
	public Contact getContact(Integer enemyID)
	{
		return myContacts.get(enemyID);
	}
	
	public Contact getOldContact(int enemyID)
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
	
	public Iterator<Contact> getRange(DPoint bottomLeft, DPoint topRight)
	{
		Collection<Contact> oup = byPos.subCollection(new Contact(-1,-1,-1,bottomLeft.getX(),-1,0,0),new Contact(-1,-1,-1,topRight.getX(),-1,0,0));
		return new YLimitedIterator(oup,bottomLeft.getY(),topRight.getY());		
	}
	
	private class YLimitedIterator implements Iterator<Contact>
	{
		private Contact nextContact;
		private Iterator<Contact> myIt;
		private double ymin,ymax;
		
		public YLimitedIterator(Collection<Contact> list, double ymin,double ymax)
		{
			myIt = list.iterator();
			this.ymin = ymin;
			this.ymax = ymax;
			
			findNext();
		}
		
		private void findNext()
		{
			Contact c;
			while(myIt.hasNext())
			{
				c = myIt.next();
				if(ymin <= c.getY() && c.getY() <= ymax)
				{
					nextContact = c;
					return;
				}
			}
			nextContact = null;
		}

		@Override
		public boolean hasNext()
		{
			return nextContact != null;
		}

		@Override
		public Contact next()
		{
			Contact oup = nextContact;
			if(oup == null)
				throw new NoSuchElementException();
			findNext();
			return oup;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();			
		}		
	}
	
	
}
