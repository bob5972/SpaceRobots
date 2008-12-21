package net.banack.spacerobots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ActionList;
import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;

public class Battle
{
	private ShipList myShips;
	private FleetList myFleets;
	private Collection myTeams;
	
	
	//leave some room for stuff (like types)
	static private int myIDCount=100;
	
	public static final int TYPE_INVALID   = -1;
	public static final int TYPE_CRUISER   = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int TYPE_DESTROYER = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int TYPE_FIGHTER   = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int TYPE_MISSILE   = DefaultShipTypeDefinitions.MISSILE_ID;
	public static final int TYPE_ROCKET	   = DefaultShipTypeDefinitions.ROCKET_ID;
	
	//this way we can re-use it
	//saves on (de)allocation
	private ActionList aggregate;
	private FleetContactList contacts;
	
	private int myWidth;
	private int myHeight;
	private int myTick;
	private ShipTypeDefinitions myShipTypes;
	
	
	public Battle(int width, int height)
	{
		myWidth = width;
		myHeight = height;
		myTick=0;
		myShips = new ShipList();
		myFleets = new FleetList();
		myShipTypes = new DefaultShipTypeDefinitions();
		
		aggregate = new ActionList();
		contacts = new FleetContactList();
		myTeams = new ArrayList();
	}
	
	//returns teamID's
	public int[] createTeams(int num)
	{
		int[] oup = new int[num];
		for(int x=0;x<num;x++)
		{
			oup[x] = getNewID();
			myTeams.add(new Team(oup[x],"Team "+(x+1)));
		}
		return oup;
	}
	
	public int addFleet(String name, FleetAI ai, int teamID)
	{
		int oup = getNewID();
		myFleets.add(new Fleet(name, oup,teamID,ai));
		return oup;
	}	
	
	public void initialize()
	{
		//setup stuff...
		//notify the AI's a battle is starting
		// etc
		throw new MethodNotImplementedException();
	}
	
	public boolean isOver()
	{
		//check if anyone won
		throw new MethodNotImplementedException();
	}
	
	public void runTick()throws IOException
	{
		Fleet f;
		Ship s;
		ShipAction a;
		Iterator i;
		FleetAI ai;
		
		aggregate.makeEmpty();

		i=myFleets.iterator();
		while(i.hasNext())
		{
			f=(Fleet)i.next();
			ai = f.getAI();
			
			//add credits to fleets
			int credit = f.getCreditIncrement();
			f.incrementCredits(credit);
			
			//Write ships to AI sockets
			ai.beginFleetStatusUpdate(myTick,f.getCredits(),contacts.getFleetList(f),f.getNumShips());
		}
		
		Iterator si = myShips.iterator();
			
		while(si.hasNext())
		{
			s=(Ship)si.next();
			ai = s.getFleet().getAI(); 
			ai.writeShip(s);
		}
		
		i=myFleets.iterator();
		while(i.hasNext())
		{
			f=(Fleet)i.next();
			ai = f.getAI();
			
			ai.endFleetStatusUpdate();
			
			ActionList AL = ai.readFleetActions();
			aggregate.add(AL);
		}
		
		i = myShips.iterator();
		while(i.hasNext())
		{
			s=(Ship)i.next();
			s.reset();//tick damage counters and the like
			a = aggregate.get(s.getShipID());
			
			//apply a to s (if a exists)
			if(a != null)
			{
				throw new MethodNotImplementedException();
			}
			
			//move s
			throw new MethodNotImplementedException();
		}
		
		//do spawns
		//	No guarantee made as to order of spawns
		//	ie if you try to spawn more than you can, some get through, some don't
		
		i = aggregate.spawnIterator();
		while(i.hasNext())
		{
			a=(ShipAction)i.next();
			
			if (canSpawn(a))
			{
				doSpawn(a);
			}
			else
			{
				//error message
				if(SpaceRobots.showBadAIWarnings())
					System.err.println("Illegal Spawn attempted by: ");
			}
		}

		contacts.makeEmpty();
//		//generate sensor contacts
//		//check collisions
//		//blow stuff up
		
		throw new MethodNotImplementedException();
	}
	
	//canSpawn checks if s.ship is alive, is of the correct type, and s.ship.fleet has the correct credits, etc
	public boolean canSpawn(ShipAction a)
	{
		int sID = a.getShipID();
		int launchType = a.getLaunch();
		Ship s = myShips.get(sID);
		if(!s.isAlive())
			return false;
		switch(s.getType())
		{
			case TYPE_MISSILE:
			case TYPE_ROCKET:
			case TYPE_INVALID:
				return false;
			case TYPE_FIGHTER:
				if(launchType != TYPE_ROCKET)
					return false;
				break;
			case TYPE_DESTROYER:
				if(launchType != TYPE_ROCKET && launchType != TYPE_MISSILE)
					return false;
				break;
			case TYPE_CRUISER:
				if(launchType == TYPE_INVALID)
					return false;
				break;
			default:
				throw new MethodNotImplementedException("No error handler");
		}
		
		return hasCreditsToLaunch(s.getFleet(),launchType);
	}
	
	public boolean hasCreditsToLaunch(Fleet f, int launchType)
	{
		int credits = f.getCredits();
		ShipType t = myShipTypes.get(launchType);
		if(t == null)
		{
			//Invalid type
			throw new MethodNotImplementedException("No error handler");
		}
		
		return credits >= t.getCost();
		
	}	
	
	public void doSpawn(ShipAction a)
	{
		if(!canSpawn(a))
			throw new MethodNotImplementedException("No error handler");
		Ship cur = myShips.get(a.getShipID());
		int launchType = a.getLaunch();
		Fleet f = cur.getFleet();
		Ship oup = new Ship(f,getNewID(), launchType, cur.getXPos(), cur.getYPos(), getDefaultLife(launchType));
		myShips.add(oup);
	}
	
	static private int getNewID()
	{
		if(myIDCount == Integer.MAX_VALUE)
			throw new MethodNotImplementedException("No error handler");
		return myIDCount++;
	}
	
	private int getDefaultLife(int type)
	{
		ShipType t = myShipTypes.get(type);
		if(t == null)
			throw new MethodNotImplementedException("No error handler");
		return t.getMaxLife();
	}
	
	public int getWidth()
	{
		return myWidth;
	}
	
	public int getHeight()
	{
		return myHeight;
	}
}
