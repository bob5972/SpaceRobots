package net.banack.spacerobots.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;

public class AIShipList
{
	private HashMap<Integer,AIShip> myShips;
	
	public AIShipList()
	{
		myShips = new HashMap<Integer,AIShip>();
	}
	
	public boolean add(AIShip s)
	{
		return (myShips.put(new Integer(s.getShipID()),s)==null);
	}
	
	public void makeEmpty()
	{
		myShips.clear();
	}
	
	public final void clear()
	{
		makeEmpty();
	}
	
	public int size()
	{
		return myShips.size();
	}
	
	public void remove(AIShip s)
	{
		remove(s.getShipID());
	}
	
	public void remove(int shipID)
	{
		myShips.remove(new Integer(shipID));
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
	
	public java.util.Iterator<AIShip> iterator()
	{
		return myShips.values().iterator();
	}
	
	public void update(Ship[] s)
	{
		for(int x=0;x<s.length;x++)
		{
			update(s[x]);
		}
	}
	
	public void update(Ship s)
	{
		AIShip cur = get(s.getID());
		if(cur==null)
			add(new AIShip(s));
		else
			cur.update(s);
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
	
	public void update(Ship s,AIShipFactory f)
	{
		AIShip cur = get(s.getID());
		if(cur==null)
			add(f.createShip(s));
		else
			cur.update(s);
	}
	
	public void update(AIShipList s,AIShipFactory f)
	{
		Iterator<AIShip> i = s.iterator();
		while(i.hasNext())
		{
			update(i.next(),f);
		}
	}
	
	public Iterator<ShipAction> getActionIterator()
	{
		return new ActionIterator(iterator());
	}
		
	private static class ActionIterator implements Iterator<ShipAction>
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
			throw new UnsupportedOperationException();
		}
		
	}
}
