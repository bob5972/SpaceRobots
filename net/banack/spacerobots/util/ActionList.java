package net.banack.spacerobots.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class ActionList
{
	private HashMap<Integer,ShipAction> myActions;
	private HashSet<ShipAction> mySpawns;
	private int myTick;
	
	public ActionList()
	{
		myActions = new HashMap<Integer,ShipAction>();
		mySpawns = new HashSet<ShipAction>();
		myTick = -1;
	}
	
	public ActionList(Ship[] s)
	{
		this();
		for(int x=0;x<s.length;x++)
		{
			add(new ShipAction(s[x]));
		}
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
	@SuppressWarnings("unchecked")
	public Set<ShipAction> getSpawns()
	{
		return (Set<ShipAction>)mySpawns.clone();
	}
	
	public Iterator<ShipAction> spawnIterator()
	{
		return mySpawns.iterator();
	}
	
	//adds all the actions in ActionList
	public void add(ActionList a)
	{
		if(a == null)
			throw new NullPointerException();
		myActions.putAll(a.myActions);
		mySpawns.addAll(a.mySpawns);
	}
	
	public void makeEmpty()
	{
		myActions.clear();
		mySpawns.clear();
	}
	
	public int size()
	{
		return myActions.size();
	}
	
	//gets an iterator over the actions
	public Iterator<ShipAction> iterator()
	{
		return new ActionListIterator();
	}
	
	//returns null if there is no action for shipID
	public ShipAction get(int shipID)
	{
		return (ShipAction)myActions.get(new Integer(shipID));
	}
	
	private class ActionListIterator implements Iterator<ShipAction>
	{
		private Iterator<ShipAction> i;
		public ActionListIterator()
		{
			i=myActions.values().iterator();
		}
		
		public boolean hasNext()
		{
			return i.hasNext();
		}
		
		public ShipAction next()
		{
			return i.next();
		}
		
		public void remove()
		{
			i.remove();
		}
	}

	public void remove(ShipAction a)
	{
		myActions.remove(a);
		mySpawns.remove(a);		
	}
	
	
}
