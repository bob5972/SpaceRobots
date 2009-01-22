package net.banack.spacerobots.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.SensorContact;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;

public class SimpleFleet extends AbstractFleetAI
{	
	private Random myRandom;
	
	public SimpleFleet()
	{
		myRandom = new Random();
	}
	
	public SimpleFleet(long seed)
	{
		myRandom = new Random(seed);		
	}
	
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getName()
	{
		return "SimpleFleet";
	}
		
	public String getVersion()
	{
		return "1.0";
	}
	
	public ActionList runTick(int tick, int credits, ContactList c, Ship[] s)
	{
		ActionList oup = new ActionList();
		oup.setTick(tick);
		HashSet<Integer> cantSpawn = new HashSet<Integer>();
		
	
		for(int x=0;x<s.length;x++)
		{
			ShipAction a = new ShipAction(s[x]);
			
			if(s[x].isAlive())
			{
				if(!s[x].readyToLaunch())
					cantSpawn.add(s[x].getID());
				if(s[x].getTypeID() == DefaultShipTypeDefinitions.FIGHTER_ID)
				{
					//nothing to see here, move along...
				}
				else if(s[x].getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID)
				{
					if(s[x].readyToLaunch() &&  credits > DefaultShipTypeDefinitions.FIGHTER.getCost()*2)
					{
						credits-=DefaultShipTypeDefinitions.FIGHTER.getCost();
						a.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
					}
				}
				else if(s[x].getTypeID() == DefaultShipTypeDefinitions.ROCKET_ID)
				{
					cantSpawn.add(s[x].getID());
				}
				oup.add(a);
			}		
			else
			{
				cantSpawn.add(s[x].getID());
			}
		}
		
		Iterator<Integer> ci = c.enemyIterator();
		
		while(ci.hasNext())
		{
			int eID = ci.next().intValue();
			Set<Integer> spot = c.getSpotters(eID);
			
			Iterator<Integer> spotI = spot.iterator();
			while(spotI.hasNext())
			{			
				ShipAction a = oup.get(spotI.next());
				if(a != null && !cantSpawn.contains(a.getShipID()) && credits > DefaultShipTypeDefinitions.ROCKET.getCost())
				{
					credits-=DefaultShipTypeDefinitions.ROCKET.getCost();
					a.setLaunchWhat(DefaultShipTypeDefinitions.ROCKET_ID);
				}
			}
		}
		
		return oup;		
	}
}
