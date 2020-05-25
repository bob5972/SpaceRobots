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

package net.banack.spacerobots.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;

/**
 * A list of AIShips.
 * 
 * @author Michael Banack <github@banack.net>
 * 
 */
public class AIShipList
{
	private HashMap<Integer, AIShip> myShips;
	private HashSet<Integer> myNewShips;
	private HashSet<Integer> myDeadShips;
	
	public AIShipList()
	{
		myShips = new HashMap<Integer, AIShip>();
		myNewShips = new HashSet<Integer>();
		myDeadShips = new HashSet<Integer>();
	}
	
	/**
	 * Clears the new and dead ship lists.
	 * <p>
	 * This does <i>NOT</i> wipe the entire list.
	 */
	// DOES NOT CLEAR THE LIST
	// Instead it wipes the new and dead ship lists
	// I could probably name this something better... --bob5972
	public void reset()
	{
		myNewShips.clear();
		
		Iterator<Integer> i = myDeadShips.iterator();
		while (i.hasNext()) {
			Integer id = i.next();;
			myShips.remove(id);
		}
		
		myDeadShips.clear();
	}
	
	// note that this will replace the old BasicAIShip associated with the idea
	// (probably causing any outstanding client-side references to it to stop updating...
	// Use with care...
	// RETURN VALUES:
	// return true if the ship was new
	// return false if the ship was not new
	// (the map is modified either way... unless you happened to add the same reference it had previously)
	public boolean add(AIShip s)
	{
		AIShip old = myShips.put(new Integer(s.getShipID()), s);
		
		if (old == null)
			myNewShips.add(s.getID());
		if (!s.isAlive())
			myDeadShips.add(s.getID());
		
		return old == null;
	}
	
	public boolean addAll(AIShipList list)
	{
		boolean oup = false;
		Iterator<AIShip> it = list.iterator();
		while (it.hasNext()) {
			oup |= add(it.next());
		}
		
		return oup;
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
	
	/**
	 * The current size (including dead ships).
	 */
	public int size()
	{
		return myShips.size();
	}
	
	/** The number of alive ships. */
	public int getAliveSize()
	{
		return (size() - myDeadShips.size());
	}
	
	/** The number of new ships. */
	public int getNewSize()
	{
		return myNewShips.size();
	}
	
	// completely removes the ship (matching the ID)
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
		while (i.hasNext()) {
			remove(i.next());
		}
	}
	
	public AIShip get(int shipID)
	{
		return get(new Integer(shipID));
	}
	
	public AIShip get(Integer shipID)
	{
		return myShips.get(shipID);
	}
	
	public void update(Ship s, AIShipFactory f)
	{
		BasicAIShip cur = get(s.getID());
		if (cur == null) {
			// takes care of the new and dead lists for us
			add(f.createShip(s));
		} else {
			cur.update(s);
			if (cur.isDead())
				myDeadShips.add(cur.getID());
		}
	}
	
	public void update(Ship[] s, AIShipFactory f)
	{
		for (int x = 0; x < s.length; x++) {
			update(s[x], f);
		}
	}
	
	public void update(AIShipList s, AIShipFactory f)
	{
		Iterator<AIShip> i = s.iterator();
		while (i.hasNext()) {
			update(i.next(), f);
		}
	}
	
	/** Runs the given AIGovernor on every live ship in the list. */
	public void apply(AIGovernor g)
	{
		apply(null, g);
	}
	
	/** Runs the given AIGovernor on every live ship in the list that passes filter. */
	public void apply(AIFilter f, AIGovernor g)
	{
		Iterator<AIShip> i = getAliveIterator();
		while (i.hasNext()) {
			AIShip s = i.next();
			
			if (f == null || f.test(s))
				g.run(s);
		}
	}
	
	/** Iterates over all ships (including dead ones). */
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
			// I could probably implement this if i wanted to, but I really don't --bob5972
			throw new UnsupportedOperationException();
		}
	}
	
	private class AliveIterator implements Iterator<AIShip>
	{
		private Iterator<AIShip> myIt;
		private AIShip myNext;
		
		public AliveIterator(Iterator<AIShip> i)
		{
			myIt = i;
			findNext();
		}
		
		public boolean hasNext()
		{
			return myNext != null;
		}
		
		public AIShip next()
		{
			AIShip oup = myNext;
			findNext();
			return oup;
		}
		
		private void findNext()
		{
			while (myIt.hasNext()) {
				AIShip s = myIt.next();
				if (s.isAlive()) {
					myNext = s;
					return;
				}
			}
			myNext = null;
		}
		
		public void remove()
		{
			// I could probably implement this if i wanted to, but I really don't --bob5972
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
			// I could probably implement this if i wanted to, but I really don't --bob5972
			throw new UnsupportedOperationException();
		}
		
	}
}
