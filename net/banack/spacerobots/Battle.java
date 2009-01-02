package net.banack.spacerobots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import net.banack.util.MethodNotImplementedException;
import net.banack.geometry.DArc;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.util.ActionList;
import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
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
	
	public static final int INVALID_ID 	   = -1;
	public static final int TYPE_INVALID   = INVALID_ID;
	public static final int TYPE_CRUISER   = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int TYPE_DESTROYER = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int TYPE_FIGHTER   = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int TYPE_MISSILE   = DefaultShipTypeDefinitions.MISSILE_ID;
	public static final int TYPE_ROCKET	   = DefaultShipTypeDefinitions.ROCKET_ID;
	
	//this way we can re-use it
	//saves on (de)allocation
	private ActionList aggregate;
	private FleetContactList contacts;
	private Random myRandom;
	
	private double myWidth;
	private double myHeight;
	private int myTick;
	private ShipTypeDefinitions myShipTypes;

	public int getTick()
	{
		return myTick;
	}
	
	public Battle(double width, double height)
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
			myTeams.add(new ServerTeam(oup[x],"Team "+(x+1)));
		}
		return oup;
	}
	
	public int addFleet(String name, FleetAI ai, int teamID, int startingCredits,int creditIncrement)
	{
		int oup = getNewID();
		ServerFleet f = new ServerFleet(name, oup,teamID,ai);
		f.setCredits(startingCredits);
		f.setCreditIncrement(creditIncrement);
		myFleets.add(f);
		myTeams.get(teamID).incrementLiveFleets(1);
		return oup;
	}
	
	
	
	
	public void initialize() throws IOException
	{
		//setup stuff...
		//notify the AI's a battle is starting
		// etc
		Iterator i = myFleets.iterator();
		ServerTeam[] t = myTeams.toArray();
		ServerFleet[] fa = myFleets.toArray();
		
		while(i.hasNext())
		{
			ServerFleet f = (ServerFleet)i.next();
			int launchType = TYPE_CRUISER;
			double x = Math.rint(myRandom.nextDouble()*myWidth);
			double y = Math.rint(myRandom.nextDouble()*myHeight);
			ServerShip oup = new ServerShip(f,getNewID(), launchType, myShipTypes.get(launchType), x,y, getDefaultLife(launchType),myTick);
			myShips.add(oup);
			f.setNumShips(f.getNumShips()+1);
			f.setCredits(0);
			
			ServerShip[] s = new ServerShip[1];
			s[0] = oup;

			f.getAI().initBattle(f.getFleetID(), f.getTeamID(), f.getCredits(), s, t, fa);
		}
		
	}
	
	public boolean isOver()
	{
		int alive=0;
		Iterator i = myTeams.iterator();
		while(i.hasNext())
		{
			ServerTeam t = (ServerTeam)i.next();
			if(t.isAlive())
				alive++;
		}
		
		return alive <= 1;
	}
	
	public void runTick()throws IOException
	{
		ServerFleet f;
		ServerShip s;
		ShipAction a;
		Iterator i;
		FleetAI ai;
		myTick++;
		
		aggregate.makeEmpty();

		i=myFleets.iterator();
		while(i.hasNext())
		{
			f=(ServerFleet)i.next();
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
			s=(ServerShip)si.next();
			ai = s.getFleet().getAI(); 
			ai.writeShip(s);
		}
		
		i=myFleets.iterator();
		while(i.hasNext())
		{
			f=(ServerFleet)i.next();
			ai = f.getAI();
			
			ai.endFleetStatusUpdate();
			
			ActionList AL = ai.readFleetActions();
			if(Debug.showAIWarnings())
			{
				if(AL.getTick() != myTick)
					Debug.aiwarn("AI responded with an invalid tick: Expected="+myTick+", received="+AL.getTick());
			}
			validateShipIDs(AL,f);
			aggregate.add(AL);
		}
		
		HashSet toDie = new HashSet();
		i = myShips.iterator();
		while(i.hasNext())
		{
			s=(ServerShip)i.next();
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
			if(t.getMaxTickCount() > 0)
			{
				if(myTick - s.getCreationTick() > t.getMaxTickCount())
					toDie.add(s);//it's flown too long
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
				Debug.aiwarn("AI attempted an illegal spawn: "+myShips.get(a.getShipID()).getFleet().getAIName());
			}
		}


		contacts.makeEmpty();
		Iterator outer = myShips.iterator();
		
		
		while(outer.hasNext())
		{
			ServerShip sho = (ServerShip)outer.next();
			if(toDie.contains(sho))
				continue;
			
			int oTeam = sho.getFleet().getTeamID();
			int oType = sho.getTypeID();
			Iterator inner = myShips.iterator();
			while(inner.hasNext())
			{
				ServerShip shi = (ServerShip)inner.next();
				if(toDie.contains(shi))
					continue;
				
				int iTeam = shi.getFleet().getTeamID();
				int iType = shi.getTypeID();
				
				//generate sensor contacts
				if(oTeam != iTeam)
				{
					if(canScan(sho,shi))
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
			s = (ServerShip)i.next();
			
			myShips.remove(s);
			f = s.getFleet();
			f.setNumShips(f.getNumShips() -1);
			if(f.getNumShips() <= 0)
			{
				f.setAlive(false);
				ServerTeam t = myTeams.get(f.getTeamID());
				t.decrementLiveFleets(1);
			}
		}
			
	}
	
	private void validateShipIDs(ActionList al, ServerFleet f)
	{
		Iterator i = al.iterator();
		while(i.hasNext())
		{
			ShipAction a = (ShipAction)i.next();
			ServerShip s = myShips.get(a.getShipID());
			if(s==null || s.getFleet() != f)
			{
				al.remove(a);
				Debug.aiwarn("AI returned an action for an invalid ship id.");
			}
		}
				
	}
	
	private boolean canScan(ServerShip spotter, ServerShip enemy)
	{		
		return SpaceMath.isCollision(spotter.getScannerArc(),enemy.getLocation());
	}
	
	private boolean isCollision(ServerShip o, ServerShip i)
	{
		return SpaceMath.isCollision(o.getLocation(), i.getLocation());
	}
	
	

	//canSpawn checks if s.ship is alive, is of the correct type, and s.ship.fleet has the correct credits, etc
	private boolean canSpawn(ShipAction a)
	{
		int sID = a.getShipID();
		int launchType = a.getLaunch();
		ServerShip s = myShips.get(sID);
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
				Debug.warn("Invalid shipType: "+s.getTypeID());
				return false;
		}
		
		return hasCreditsToLaunch(s.getFleet(),launchType);
	}
	
	private boolean hasCreditsToLaunch(ServerFleet f, int launchType)
	{
		int credits = f.getCredits();
		ShipType t = myShipTypes.get(launchType);
		if(t == null)
		{
			//Invalid type
			Debug.warn("Invalid shipType: "+launchType);
			return false;
		}
		
		return credits >= t.getCost();
		
	}	
	
	private void doSpawn(ShipAction a)
	{
		if(!canSpawn(a))
			Debug.crash("Attempted an invalid spawn!");
		ServerShip cur = myShips.get(a.getShipID());
		int launchType = a.getLaunch();
		ServerFleet f = cur.getFleet();
		ServerShip oup = new ServerShip(f,getNewID(), launchType, myShipTypes.get(launchType), cur.getXPos(), cur.getYPos(), getDefaultLife(launchType),myTick);
		myShips.add(oup);
		
		f.setNumShips(f.getNumShips()+1);
		f.decrementCredits(oup.getCost());
	}
	
	static private int getNewID()
	{
		if(myIDCount == Integer.MAX_VALUE)
			Debug.crash("Ran out of ID values!");
		return myIDCount++;
	}
	
	private int getDefaultLife(int type)
	{
		ShipType t = myShipTypes.get(type);
		if(t == null)
			Debug.crash("Invalid ship type");
		return t.getMaxLife();
	}
	
	public double getWidth()
	{
		return myWidth;
	}
	
	public double getHeight()
	{
		return myHeight;
	}
	
	public Iterator shipIterator()
	{
		return myShips.iterator();
	}
	
	public Iterator fleetIterator()
	{
		return myFleets.iterator();
	}
	
	public Iterator teamIterator()
	{
		return myTeams.iterator();
	}
	
	public int getNumShips()
	{
		return myShips.size();
	}
}
