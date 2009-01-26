package net.banack.spacerobots.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.spacerobots.Debug;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;

public class DummyFleet extends AbstractFleetAI
{
	private AIShip myCruiser;
	private Random myRandom;
	private AIShipList myShips;
	
	public DummyFleet()
	{
		myRandom = new Random();
		myShips=new AIShipList();
	}
	
	public DummyFleet(long seed)
	{
		myRandom = new Random(seed);
		myShips=new AIShipList();
	}
	
	public void seedRandom(long seed)
	{
		myRandom=new Random(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getName()
	{
		return "DummyFleet";
	}
	
	public String getVersion()
	{
		return "1.1";
	}
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose)
	{
		return;
	}
		
	public void initBattle(int fleetID, int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f)
	{
		for(int x=0;x<s.length;x++)
		{
			if(Debug.isDebug())
			{
				if(s[x] == null)
					Debug.warn("Null pointer in s["+x+"]!");
			}
			
			myShips.add(new AIShip(s[x]));
			if(s[x].getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID)
			{
				myCruiser=myShips.get(s[x].getID());
			}
		}
		return;
	}
	
	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, Ship[] s)
	{
		myShips.update(s);
		
		Iterator<AIShip> i = myShips.iterator();
		HashSet<Integer> died= new HashSet<Integer>();
		
		while(i.hasNext())
		{
			AIShip ship = i.next();
			if(!ship.isAlive())
			{
				died.add(ship.getID());
				continue;
			}
			
			if(tick % 100 == 0)
			{
				ship.setHeading(myRandom.nextDouble()*Math.PI/2);
			}			
		}
		
		if(myCruiser != null && myCruiser.isAlive())
		{
			if(credits > DefaultShipTypeDefinitions.FIGHTER.getCost())
			{
				myCruiser.setLaunch(DefaultShipTypeDefinitions.FIGHTER_ID);
			}
			myCruiser.setScannerHeading(myCruiser.getScannerHeading()+1);
		}
		
		Iterator<Integer> di = died.iterator();
		while(di.hasNext())
		{
			myShips.remove(di.next());
		}
		
		
		return myShips.getActionIterator();
	}

	
}
