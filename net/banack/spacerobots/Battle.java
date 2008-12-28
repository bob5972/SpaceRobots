package net.banack.spacerobots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ActionList;
import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;
import net.banack.spacerobots.util.SpaceMath;

public class Battle
{
	private ShipList myShips;
	private FleetList myFleets;
	private TeamList myTeams;
	
	
	//leave some room for stuff (like types?)
	static private int myIDCount=100;
	
	public static final int TYPE_INVALID   = -1;
	public static final int TYPE_CRUISER   = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int TYPE_DESTROYER = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int TYPE_FIGHTER   = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int TYPE_MISSILE   = DefaultShipTypeDefinitions.MISSILE_ID;
	public static final int TYPE_ROCKET	   = DefaultShipTypeDefinitions.ROCKET_ID;
	
	//the maximum heading is HEADING_MAX
	//so HEADING_WRAP is really the mod value
	//(but I wasn't creative enough for a better name)
	public static final int HEADING_WRAP = 64;
	public static final int HEADING_MAX = HEADING_WRAP-1;
	
	//this way we can re-use it
	//saves on (de)allocation
	private ActionList aggregate;
	private FleetContactList contacts;
	private Random myRandom;
	
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
		myTeams = new TeamList();
		myRandom = new Random();
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
	
	public int addFleet(String name, FleetAI ai, int teamID, int startingCredits,int creditIncrement)
	{
		int oup = getNewID();
		Fleet f = new Fleet(name, oup,teamID,ai);
		f.setCredits(startingCredits);
		f.setCreditIncrement(creditIncrement);
		myFleets.add(f);
		return oup;
	}
	
	
	public void initialize()
	{
		//setup stuff...
		//notify the AI's a battle is starting
		// etc
		Iterator i = myFleets.iterator();
		while(i.hasNext())
		{
			Fleet f = (Fleet)i.next();
			int launchType = TYPE_CRUISER;
			Ship oup = new Ship(f,getNewID(), launchType, myRandom.nextInt(myWidth), myRandom.nextInt(myHeight), getDefaultLife(launchType));
			myShips.add(oup);
		}
	}
	
	public boolean isOver()
	{
		int alive=0;
		Iterator i = myTeams.iterator();
		while(i.hasNext())
		{
			Team t = (Team)i.next();
			if(t.isAlive())
				alive++;
		}
		
		return alive <= 1;
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
			ShipType t = myShipTypes.get(s.getTypeID());
			
			//apply a to s (if a exists)
			if(a != null)
			{
				
				if(t.getCanStop())
				{
					if(!a.willMove())
						s.setWillMove(false);
					else
						s.setWillMove(true);
				}
				
				s.setHeading(SpaceMath.calculateAdjustedHeading(s.getHeading(),a.getHeading(),t.getMaxTurningRate()));
				if(t.canMoveScanner())
				{
					s.setScannerHeading(a.getScannerHeading());
				}
			}
			
			//move s
			if(s.willMove())
			{
				s.setX(s.getX()+SpaceMath.calculateXOffset(s.getHeading(),t.getMaxSpeed()));
				s.setY(s.getY()+SpaceMath.calculateYOffset(s.getHeading(),t.getMaxSpeed()));
				while(s.getX() > myWidth)
					s.setX(s.getX()-myWidth);
				while(s.getX() < 0)
					s.setX(s.getX()+myWidth);
				while(s.getY() > myWidth)
					s.setY(s.getY()-myWidth);
				while(s.getY() < 0)
					s.setX(s.getY()+myWidth);
			}
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
				{
					System.err.println("Illegal Spawn attempted by: ");
					throw new MethodNotImplementedException("No error handler");
				}
			}
		}


		contacts.makeEmpty();
		Iterator outer = myShips.iterator();
		HashSet toDie = new HashSet();
		
		while(outer.hasNext())
		{
			Ship sho = (Ship)outer.next();
			if(toDie.contains(sho))
				continue;
			
			int oTeam = sho.getFleet().getTeamID();
			int oType = sho.getTypeID();
			Iterator inner = myShips.iterator();
			while(inner.hasNext())
			{
				Ship shi = (Ship)inner.next();
				if(toDie.contains(shi))
					continue;
				
				int iTeam = shi.getFleet().getTeamID();
				int iType = shi.getTypeID();
				
				//generate sensor contacts
				if(oTeam != iTeam && canScan(sho,shi))
				{
					contacts.addContact(shi,sho);
					
					//check collisions
					if(oType == TYPE_ROCKET || oType == TYPE_MISSILE)
					{
						if(iType != TYPE_ROCKET && iType != TYPE_MISSILE)
						{
							if(isCollision(sho,shi))
							{
								//	do damage
								toDie.add(sho);//the rocket blows up
								shi.decrementLife(1);
								if(!shi.isAlive())
								{
									//mark stuff to be blown up
									toDie.add(shi);
								}
								
							}
						}
					}
				}
			}
		}
		
		//blow stuff up
		i = toDie.iterator();
		while(i.hasNext())
		{
			s = (Ship)i.next();
			
			myShips.remove(s);
			f = s.getFleet();
			f.setNumShips(f.getNumShips() -1);
			if(f.getNumShips() <= 0)
			{
				f.setAlive(false);
				Team t = myTeams.get(f.getTeamID());
				t.decrementLiveFleets(1);
			}
		}				
			
	}
	
	public boolean canScan(Ship spotter, Ship enemy)
	{
		throw new MethodNotImplementedException();
	}
	
	public boolean isCollision(Ship o, Ship i)
	{
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
		switch(s.getTypeID())
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
