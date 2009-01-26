package net.banack.spacerobots.ai;

import java.util.HashMap;
import java.util.Iterator;

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
			AIShip cur = get(s[x].getID());
			if(cur == null)
				add(new AIShip(s[x]));
			else
				cur.update(s[x]);
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
