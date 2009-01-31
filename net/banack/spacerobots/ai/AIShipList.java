package net.banack.spacerobots.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;

public class AIShipList
{
	private HashMap<Integer,AIShip> myShips;
	private HashSet<Integer> myNewShips;
	private HashSet<Integer> myDeadShips;
	
	public AIShipList()
	{
		myShips = new HashMap<Integer,AIShip>();
		myNewShips = new HashSet<Integer>();
		myDeadShips = new HashSet<Integer>();
	}
	
	//DOES NOT CLEAR THE LIST
	//Instead it wipes the new and dead ship lists
	//I could probably call this something better... --bob5972
	public void reset()
	{
		myNewShips.clear();
		
		Iterator<Integer> i =myDeadShips.iterator();
		while(i.hasNext())
		{
			Integer id = i.next();;
			myShips.remove(id);
		}
		
		myDeadShips.clear();
	}
	
	//note that this will replace the old AIShip associated with the idea
	// (probably causing any outstanding client-side references to it to stop updating...
	//Use with care...
	//RETURN VALUES:
	//return true if the ship was new
	//return false if the ship was not new
	//  (the map is modified either way... unless you happened to add the same reference it had previously)
	public boolean add(AIShip s)
	{
		AIShip old = myShips.put(new Integer(s.getShipID()),s);
		
		if(old == null)
			myNewShips.add(s.getID());		
		if(!s.isAlive())
			myDeadShips.add(s.getID());
		
		return old ==null;
	}
	
	public void makeEmpty()
	{
		myShips.clear();
		myDeadShips.clear();
		myNewShips.clear();
	}
	
	public final void clear()
	{
		makeEmpty();
	}
	
	//includes dead ships
	public int size()
	{
		return myShips.size();
	}
	
	public int getAliveSize()
	{
		return (size()-myDeadShips.size());		
	}
	
	public int getNewSize()
	{
		return myNewShips.size();
	}
	
	//completely removes the ship (matching the ID)
	// including from any new and dead lists
	public void remove(AIShip s)
	{
		remove(s.getShipID());
	}
	
	public void remove(int shipID)
	{
		Integer id = new Integer(shipID);
		myShips.remove(id);
		myNewShips.remove(id);
		myDeadShips.remove(id);
	}
	
	public void remove(Set<Integer> S)
	{
		Iterator<Integer> i = S.iterator();
		while(i.hasNext())
		{
			remove(i.next());
		}		
	}
	
	public AIShip get(int shipID)
	{
		return (AIShip)myShips.get(new Integer(shipID));
	}
	
	public void update(Ship s)
	{
		AIShip cur = get(s.getID());
		if(cur==null)
		{
			//takes care of new and dead lists for us
			add(new AIShip(s));
		}
		else
		{
			cur.update(s);
			if(cur.isDead())
				myDeadShips.add(cur.getID());
		}
	}
	
	public void update(Ship s,AIShipFactory f)
	{
		AIShip cur = get(s.getID());
		if(cur==null)
		{
			//takes care of the new and dead lists for us
			add(f.createShip(s));
		}
		else
		{
			cur.update(s);
			if(cur.isDead())
				myDeadShips.add(cur.getID());
		}
	}
	
	public void update(Ship[] s)
	{
		for(int x=0;x<s.length;x++)
		{
			update(s[x]);
		}
	}	
	
	public void update(AIShipList s)
	{
		Iterator<AIShip> i = s.iterator();
		while(i.hasNext())
		{
			update(i.next());
		}
	}
	
	public void update(Ship[] s,AIShipFactory f)
	{
		for(int x=0;x<s.length;x++)
		{
			update(s[x],f);
		}
	}
	
	public void update(AIShipList s,AIShipFactory f)
	{
		Iterator<AIShip> i = s.iterator();
		while(i.hasNext())
		{
			update(i.next(),f);
		}
	}
	
	//iterates over all ships (including dead ones)
	public java.util.Iterator<AIShip> iterator()
	{
		return myShips.values().iterator();
	}
	
	public java.util.Iterator<AIShip> getAliveIterator()
	{
		return new AliveIterator(iterator());
	}
	
	public java.util.Iterator<AIShip> getNewIterator()
	{
		return new IDIterator(myNewShips.iterator());
	}
	
	public java.util.Iterator<AIShip> getDeadIterator()
	{
		return new IDIterator(myDeadShips.iterator());
	}
	
	public Iterator<ShipAction> getActionIterator()
	{
		return new ActionIterator(getAliveIterator());
	}
	
	private class IDIterator implements Iterator<AIShip>
	{
		private Iterator<Integer> myIt;
		
		public IDIterator(Iterator<Integer> i)
		{
			myIt = i;
		}
		
		public boolean hasNext()
		{
			return myIt.hasNext();
		}
		
		public AIShip next()
		{
			return get(myIt.next());
		}
		
		public void remove()
		{
			//I could probably implement this if i wanted to, but I really don't --bob5972
			throw new UnsupportedOperationException();
		}
	}
	
	private class AliveIterator implements Iterator<AIShip>
	{
		private Iterator<AIShip> myIt;
		private int myCount;
		
		public AliveIterator(Iterator<AIShip> i)
		{
			myIt = i;
			myCount=1;
		}
		
		public boolean hasNext()
		{
			return myCount <= getAliveSize();
		}
		
		public AIShip next()
		{
			AIShip s = myIt.next();
			while(s.isDead())
				s = myIt.next();
			myCount++;
			return s;
		}
		
		public void remove()
		{
			//I could probably implement this if i wanted to, but I really don't --bob5972
			throw new UnsupportedOperationException();
		}
	}
		
	private class ActionIterator implements Iterator<ShipAction>
	{
		private Iterator<AIShip> myIt;
		
		public ActionIterator(Iterator<AIShip> i)
		{
			myIt = i;
		}
		
		public boolean hasNext()
		{
			return myIt.hasNext();
		}
		
		public ShipAction next()
		{
			return myIt.next().getAction();
		}
		
		public void remove()
		{
			//I could probably implement this if i wanted to, but I really don't --bob5972
			throw new UnsupportedOperationException();
		}
		
	}
}
