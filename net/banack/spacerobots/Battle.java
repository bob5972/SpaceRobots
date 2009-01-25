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
import net.banack.spacerobots.util.SpaceText;

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
	
	//this is actually used between ticks
	private HashSet<ServerShip> toDie;	
	private Random myRandom;
	
	private double myWidth;
	private double myHeight;
	private int myTick;
	private ShipTypeDefinitions myShipTypes;
	private int myNextFleetIndex;

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
		toDie = new HashSet<ServerShip>();
		myNextFleetIndex=0;
	}
	
	public void seedRandom(long x)
	{
		myRandom= new Random(x);
	}
	
	//returns teamID's
	public int[] createTeams(int num)
	{
		int[] oup = new int[num];
		for(int x=0;x<num;x++)
		{
			oup[x] = getNewID();
			int nextIndex = myTeams.size();
			myTeams.add(new ServerTeam(oup[x],nextIndex,"Team "+(nextIndex+1)));
		}
		return oup;
	}
	
	public int addFleet(String name, FleetAI ai, int teamID, int startingCredits,int creditIncrement)
	{
		int oup = getNewID();
		ServerTeam t = myTeams.get(teamID);
		ServerFleet f = new ServerFleet(name, oup, myNextFleetIndex++,teamID,t,ai);
		f.setCredits(startingCredits);
		f.setCreditIncrement(creditIncrement);
		myFleets.add(f);
		t.incrementLiveFleets(1);
		return oup;
	}
	
	
	
	
	public void initialize() throws IOException
	{
		//setup stuff...
		//notify the AI's a battle is starting
		// etc
		Iterator<ServerFleet> i = myFleets.iterator();
		ServerTeam[] t = myTeams.toArray();
		ServerFleet[] fa = myFleets.toArray();
		
		while(i.hasNext())
		{
			ServerFleet f = i.next();
			f.getAI().loadInfo();
			
			int launchType = TYPE_CRUISER;
			double x = Math.rint(myRandom.nextDouble()*myWidth);
			double y = Math.rint(myRandom.nextDouble()*myHeight);
			double heading = myRandom.nextDouble()*Math.PI*2;
			ServerShip oup = new ServerShip(f,getNewID(), launchType, myShipTypes.get(launchType), x,y, heading,getDefaultLife(launchType),myTick);
			myShips.add(oup);
			f.setNumShips(f.getNumShips()+1);
			f.setCredits(0);
			
			ServerShip[] s = new ServerShip[1];
			s[0] = oup;

			f.getAI().initBattle(f.getFleetID(), f.getTeamID(), f.getCredits(), s, t, fa);
		}
		
	}
	
	public void cleanup() throws IOException
	{
		ServerFleet[] f = myFleets.toArray();
		ServerTeam[] t = myTeams.toArray();
		
		for(int x=0;x<f.length;x++)
		{
			FleetAI ai = f[x].getAI();
			ai.endBattle(f[x], t, f);
		}
	}
	
	public boolean isOver()
	{
		int alive=0;
		Iterator<ServerTeam> i = myTeams.iterator();
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
		Iterator<ServerFleet> fi;
		FleetAI ai;
		myTick++;
		
		aggregate.makeEmpty();

		fi=myFleets.iterator();
		while(fi.hasNext())
		{
			f=fi.next();
			ai = f.getAI();
			
			//add credits to fleets
			int credit = f.getCreditIncrement();
			f.incrementCredits(credit);
			
			//Write ships to AI sockets
			ai.beginFleetStatusUpdate(myTick,f.getCredits(),contacts.getFleetList(f),f.getNumShips());
		}
		
		Iterator<ServerShip> si = myShips.iterator();
			
		while(si.hasNext())
		{
			s=si.next();
			ai = s.getFleet().getAI(); 
			ai.writeShip(s);
		}
		
		fi=myFleets.iterator();
		while(fi.hasNext())
		{
			f=(ServerFleet)fi.next();
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
		
		//blow stuff up (from last round)
		//this way the AI's get to see the ship as "dead," but it doesn't actually get to interact with anything
		si = toDie.iterator();
		while(si.hasNext())
		{
			s = (ServerShip)si.next();
			
			myShips.remove(s);
			Debug.verbose("Blowing up ship="+s.toString());
			f = s.getFleet();
			f.setNumShips(f.getNumShips() -1);
			if(f.getNumShips() <= 0)
			{
				if(Debug.isDebug() && f.getNumShips() < 0)
					Debug.warn("Fleet "+f.getName()+" has a negative number of ships!");
				f.setAlive(false);
				ServerTeam t = myTeams.get(f.getTeamID());
				t.decrementLiveFleets(1);
			}			
		}
		
		//reset the kill list
		toDie.clear();
		si = myShips.iterator();
		while(si.hasNext())
		{
			s=(ServerShip)si.next();
			s.reset();//tick damage counters and the like
			a = aggregate.get(s.getShipID());
			
			//apply a to s (if a exists)
			if(a != null)
			{
				
				if(s.getCanStop())
				{
					if(!a.willMove())
						s.setWillMove(false);
					else
						s.setWillMove(true);
				}
				
				s.setHeading(SpaceMath.calculateAdjustedHeading(s.getHeading(),a.getHeading(),s.getMaxTurningRate()));
				if(s.getCanMoveScanner())
				{
					s.setScannerHeading(a.getScannerHeading());
				}
			}
			
			//move s
			if(s.willMove())
			{
				s.addPosition(SpaceMath.calculateOffset(s.getHeading(),s.getMaxSpeed()));
				
				while(s.getX() > myWidth)
					s.setX(s.getX()-myWidth);
				while(s.getX() < 0)
					s.setX(s.getX()+myWidth);
				while(s.getY() > myWidth)
					s.setY(s.getY()-myWidth);
				while(s.getY() < 0)
					s.setX(s.getY()+myWidth);
			}
			if(s.getMaxTickCount() > 0)
			{
				if(myTick - s.getCreationTick() > s.getMaxTickCount())
				{
					Debug.verbose("Expiring ship="+s.toString());
					markForDeath(s);//it's flown too long					
				}
			}
		}
		
		//do spawns
		//	No guarantee made as to order of spawns
		//	ie if you try to spawn more than you can, some get through, some don't
		
		Iterator<ShipAction> ait = aggregate.spawnIterator();
		while(ait.hasNext())
		{
			a=(ShipAction)ait.next();
			
			if(Debug.isDebug() && Debug.showWarnings())
			{
				if(myShips.get(a.getShipID()) == null)
				{
					Debug.warn("ShipAction for an invalid ship!  shipID="+a.getShipID());
				}
			}
			
			if (canSpawn(a))
			{
				doSpawn(a);
			}
			else
			{
				//error message
				ServerShip ship = myShips.get(a.getShipID());
				int credits = ship.getFleet().getCredits();
				Debug.aiwarn("AI attempted an illegal spawn: ai="+myShips.get(a.getShipID()).getFleet().getAIName()+", credits="+credits+" ship="+SpaceText.prettyPrint(ship));
			}
		}


		contacts.makeEmpty();
		Iterator<ServerShip> outer = myShips.iterator();
		
		
		while(outer.hasNext())
		{
			ServerShip sho = (ServerShip)outer.next();
			if(toDie.contains(sho))
				continue;
			
			int oTeam = sho.getFleet().getTeamID();
			int oType = sho.getTypeID();
			Iterator<ServerShip> inner = myShips.iterator();
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
								markForDeath(sho);
								toDie.add(sho);//the rocket blows up
								shi.decrementLife(1);
								if(!shi.isAlive())
								{
									//mark stuff to be blown up
									markForDeath(shi);
								}
								
							}
						}
					}
				}
			}
		}
	}
	
	private void markForDeath(ServerShip s)
	{
		s.setLife(0);
		toDie.add(s);
		Debug.verbose("Marked for death ship="+s.toString());
	}
	
	private void validateShipIDs(ActionList al, ServerFleet f)
	{
		Iterator<ShipAction> i = al.iterator();
		while(i.hasNext())
		{
			ShipAction a = (ShipAction)i.next();
			ServerShip s = myShips.get(a.getShipID());
			
			if(Debug.showWarnings() && !s.isAlive() && !toDie.contains(s))
			{
				Debug.warn("A dead ship is not contained in toDie!  shipID="+s.getID());
			}
			
			if(s==null || s.getFleet() != f || !s.isAlive())
			{
				al.remove(a);
				Debug.aiwarn("AI returned an action for an invalid ship id.");
				Debug.verbose("UnValidated ship="+s.toString());
			}
			else
			{
				Debug.verbose("Validated ship="+s.toString());
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
		
		return s.isReadyToLaunch() &&  hasCreditsToLaunch(s.getFleet(),launchType);
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
		ShipType newType = myShipTypes.get(launchType);
		ServerFleet f = cur.getFleet();
		cur.setLaunchDelay(getLaunchDelay(cur,newType));
		
		ServerShip oup = new ServerShip(f,getNewID(), launchType, myShipTypes.get(launchType), cur.getXPos(), cur.getYPos(), getDefaultLife(launchType),myTick);
		myShips.add(oup);
		
		f.setNumShips(f.getNumShips()+1);
		f.decrementCredits(oup.getCost());
	}
	
	//this needs to be communicated to AI's some how (either statically in documentation, or dynamically)
	private static int getLaunchDelay(ServerShip s, ShipType t)
	{
		//These numbers are just made up and need to be balanced.
		return 5+(int)(t.getCost()/20.0);		
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
	
	public Iterator<ServerShip> shipIterator()
	{
		return myShips.iterator();
	}
	
	public Iterator<ServerFleet> fleetIterator()
	{
		return myFleets.iterator();
	}
	
	public Iterator<ServerTeam> teamIterator()
	{
		return myTeams.iterator();
	}
	
	public int getNumShips()
	{
		return myShips.size();
	}
}
