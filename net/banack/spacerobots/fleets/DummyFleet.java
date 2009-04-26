package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.spacerobots.Debug;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;

public class DummyFleet extends AIFleet
{	
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
	
	public String getVersion()
	{
		return "1.2";
	}
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose)
	{
		return;
	}
		
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] t, Fleet[] f, double width, double height)
	{
		myShips = s;
		Iterator<AIShip> i = myShips.iterator();
		while(i.hasNext())
		{
			AIShip cur = i.next();
						
			if(cur.getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID)
			{
				myCruiser=myShips.get(cur.getID());
			}
		}
		return;
	}
	
	public Iterator<ShipAction> runTick(int tick, ContactList c)
	{		
		Iterator<AIShip> i = myShips.getAliveIterator();
		
		while(i.hasNext())
		{
			AIShip ship = i.next();
			
			if(tick % 100 == 0)
			{
				ship.setHeading(myRandom.nextDouble()*Math.PI/2);
			}			
		}
		
		if(myCruiser != null && myCruiser.isAlive())
		{
			if(myCredits > DefaultShipTypeDefinitions.FIGHTER.getCost())
			{
				myCruiser.setLaunch(DefaultShipTypeDefinitions.FIGHTER_ID);
			}
		}
		
		return myShips.getActionIterator();
	}

	
}
