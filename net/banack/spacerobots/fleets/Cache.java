package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.Contact;
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
		return "1.2";
	}
	
	private Contact myTarget;
	
	public Iterator<ShipAction> runTick()
	{
		
		Iterator<AIShip> i;
		
		Iterator<Integer> ci;
		
		ci = myContacts.enemyIterator();
		while(ci.hasNext())
		{
			Contact enemy = myContacts.get(ci.next());
			if(!isAmmo(enemy))
			{
				myTarget = enemy;
				break;
			}
		}
		
		
		i = myShips.getAliveIterator();
		while(i.hasNext())
		{
			AIShip cur = (AIShip) i.next();
			
			if(cur == myCruiser)
				continue;
			
			if(myTarget != null)
			{
				cur.intercept(myTarget);
			}
			else
			{
				if(tick % 40 == 0)
					cur.intercept(myCruiser);
			}
		}
		
		if(myTarget != null && myTarget.getScanTick() < tick - 10)
			myTarget = null;
		
		
		if(myCruiser.isAlive())
		{
			if(myCruiser.canLaunch(FIGHTER) && credits >= FIGHTER.getCost()*2)
				myCruiser.launch(FIGHTER);
			
			myCruiser.advanceScannerHeading();			
			
			if(tick % 100 == 0)
			{
				double h = random.nextDouble();
				myCruiser.setHeading(h);
			}
		}
		
		ci = myContacts.enemyIterator();
		while(ci.hasNext())
		{
			Integer eid = ci.next();
			Iterator<Integer> si = myContacts.getSpotters(eid).iterator();
			while(si.hasNext())
			{
				AIShip s = (AIShip) myShips.get(si.next());
				if(s.getTypeID() != FIGHTER_ID)
				{
					Contact enemy = myContacts.get(eid);
					s.setScannerHeading(enemy);
					if(s.canLaunch(MISSILE))
						s.launchMissile(enemy);
				}
				else
				{
					if(s.canLaunch(ROCKET))
						s.launch(ROCKET);
				}
			}
		}
		
		return myShips.getActionIterator();
	}
	
	
	
}
