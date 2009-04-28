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
		super();
	}
	
	public DummyFleet(long seed)
	{
		super(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getVersion()
	{
		return "1.2";
	}
	
	public Iterator<ShipAction> runTick(ContactList c)
	{		
		Iterator<AIShip> i = myShips.getAliveIterator();
		
		while(i.hasNext())
		{
			AIShip ship = i.next();
			
			if(tick % 100 == 0)
			{
				ship.setHeading(random.nextDouble()*Math.PI/2);
			}			
		}
		
		if(myCruiser != null && myCruiser.canLaunch(FIGHTER))
		{
			myCruiser.launch(FIGHTER);
		}
		
		return myShips.getActionIterator();
	}

	
}
