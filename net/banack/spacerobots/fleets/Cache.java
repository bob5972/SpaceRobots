package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;

public class Cache extends AIFleet
{
	public Cache()
	{
		super();
	}
	
	public Cache(long seed)
	{
		super(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getVersion()
	{
		return "1.0";
	}
	
	public Iterator<ShipAction> runTick(ContactList c)
	{
		
		Iterator<AIShip> i;
		
		i = myShips.getAliveIterator();
		while(i.hasNext())
		{
			AIShip cur = (AIShip) i.next();
			
			if(cur == myCruiser)
				continue;
			
			if(tick % 20 == 0)
				cur.intercept(myCruiser);
		}
		
		
		if(myCruiser.isAlive())
		{
			if(myCruiser.canLaunch(FIGHTER) && credits >= FIGHTER.getCost()*2)
				myCruiser.launch(FIGHTER);
			
			myCruiser.setScannerHeading(myCruiser.projHeading());			
			
			if(tick % 100 == 0)
			{
				double h = random.nextDouble();
				myCruiser.setHeading(h);
			}
		}
		
		Iterator<Integer> ci = c.enemyIterator();
		while(ci.hasNext())
		{
			Integer eid = ci.next();
			Iterator<Integer> si = c.getSpotters(eid).iterator();
			while(si.hasNext())
			{
				AIShip s = (AIShip) myShips.get(si.next());
				if(s.canLaunch(ROCKET))
					s.launch(ROCKET);
			}
		}
		
		return myShips.getActionIterator();
	}
	
}
