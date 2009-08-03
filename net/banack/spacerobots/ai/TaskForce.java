package net.banack.spacerobots.ai;

import java.util.Iterator;

public class TaskForce
{
	protected AIShipList myShips;
	
	public TaskForce()
	{
		myShips = new AIShipList();
		
	}
	
	public boolean add(AIShip s)
	{
		return myShips.add(s);
	}
	
	public void remove(AIShip s)
	{
		myShips.remove(s);
	}
	
	public boolean addAll(AIShipList s)
	{
		return myShips.addAll(s);
	}
	
	// adds the ships into this TaskForce (but leaves them in t
	public boolean addAll(TaskForce t)
	{
		return myShips.addAll(t.myShips);
	}
	
	// adds the ships into this TaskForce, and removes them from t
	public boolean transferAll(TaskForce t)
	{
		boolean oup = addAll(t);
		t.myShips.makeEmpty();
		return oup;
	}
	
	// removes one ship from t and adds it to this
	// returns true iff successful
	public boolean transferOne(TaskForce t)
	{
		if (t.size() > 0) {
			return false;
		}
		
		add(t.removeOneShip());
		return true;
	}
	
	// removes a single ship from the task force
	// returns the removed ship, or null if not possible
	public AIShip removeOneShip()
	{
		if (size() <= 0)
			return null;
		
		Iterator<AIShip> it = myShips.iterator();
		AIShip s = it.next();
		remove(s);
		return s;
	}
	
	public int size()
	{
		return myShips.size();
	}
	
	public boolean isIdle()
	{
		return false;
	}
	
	public void run()
	{
		
	}
}
