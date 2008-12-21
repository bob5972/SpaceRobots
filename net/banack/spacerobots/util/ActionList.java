package net.banack.spacerobots.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class ActionList
{
	private HashMap myActions;
	private HashSet mySpawns;
	private int myTick;
	
	public ActionList()
	{
		myActions = new HashMap();
		mySpawns = new HashSet();
		myTick = -1;
	}
	
	//So the battle can check that the AI knows what tick this is
	public int getTick()
	{
		return myTick;
	}
	
	public void setTick(int t)
	{
		myTick =t;
	}
	
	//returns true if this is a new action
	//returns false if an action with a.getShipID() already exists
	//		in which case it replaces it
	public boolean add(ShipAction a)
	{
		Object oup;
		oup = myActions.put(new Integer(a.getShipID()),a);
		if(a.isSpawn())
		{
			mySpawns.add(a);
		}
		
		return oup==null;
	}
	
	//clones the spawn set
	public Set getSpawns()
	{
		return (Set)mySpawns.clone();
	}
	
	public Iterator spawnIterator()
	{
		return mySpawns.iterator();
	}
	
	//adds all the actions in ActionList
	public void add(ActionList a)
	{
		myActions.putAll(a.myActions);
		mySpawns.addAll(a.mySpawns);
	}
	
	public void makeEmpty()
	{
		myActions.clear();
	}
	
	//gets an iterator over the actions
	public Iterator iterator()
	{
		return new ActionListIterator();
	}
	
	//returns null if there is no action for shipID
	public ShipAction get(int shipID)
	{
		return (ShipAction)myActions.get(new Integer(shipID));
	}
	
	private class ActionListIterator implements Iterator
	{
		private Iterator i;
		public ActionListIterator()
		{
			i=myActions.values().iterator();
		}
		
		public boolean hasNext()
		{
			return i.hasNext();
		}
		
		public Object next()
		{
			return i.next();
		}
		
		public void remove()
		{
			i.remove();
		}
	}
	
	
}
