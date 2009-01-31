package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AbstractFleetAI;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.SensorContact;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;

public class SimpleFleet extends AbstractFleetAI
{	
	private Random myRandom;
	private AIShipList myShips;
	
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
		return "1.1";
	}
	
	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s)
	{
		myShips=s;
		
		Iterator<AIShip> i = myShips.getAliveIterator();
		
		HashSet<Integer> cantSpawn = new HashSet<Integer>();
		
		while(i.hasNext())
		{
			AIShip ship = i.next();
		
			if(!ship.readyToLaunch())
				cantSpawn.add(ship.getID());
			
			if(ship.getTypeID() == DefaultShipTypeDefinitions.FIGHTER_ID)
			{
				//nothing to see here, move along...
			}
			else if(ship.getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID)
			{
				if(ship.readyToLaunch() &&  credits > DefaultShipTypeDefinitions.FIGHTER.getCost()*(1+myRandom.nextDouble()))
				{
					credits-=DefaultShipTypeDefinitions.FIGHTER.getCost();
					ship.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
				}
			}
			else if(ship.getTypeID() == DefaultShipTypeDefinitions.ROCKET_ID)
			{
				cantSpawn.add(ship.getID());
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
				AIShip a = myShips.get(spotI.next());
				if(a != null && !cantSpawn.contains(a.getShipID()) && credits > DefaultShipTypeDefinitions.ROCKET.getCost())
				{
					credits-=DefaultShipTypeDefinitions.ROCKET.getCost();
					a.setLaunchWhat(DefaultShipTypeDefinitions.ROCKET_ID);
				}
			}
		}
		
		return myShips.getActionIterator();	
	}
}