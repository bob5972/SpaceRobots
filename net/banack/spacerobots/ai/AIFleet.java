/*
 * This file is part of SpaceRobots.
 * Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SpaceRobots.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.banack.spacerobots.Debug;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.Team;

/**
 * Super class for AIFleets.  Provides some default functionality.
 * @author Michael Banack <bob5972@banack.net>
 */
public abstract class AIFleet implements AIShipFactory
{
	//Type Constants for convenience.
	public static final DefaultShipTypeDefinitions TYPE = new DefaultShipTypeDefinitions();
	
	public static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	public static final int MISSILE_ID = DefaultShipTypeDefinitions.MISSILE_ID;
	
	public static final ShipType CRUISER = TYPE.CRUISER;
	public static final ShipType DESTROYER = TYPE.DESTROYER;
	public static final ShipType FIGHTER = TYPE.FIGHTER;
	public static final ShipType ROCKET = TYPE.ROCKET;
	public static final ShipType MISSILE = TYPE.MISSILE;
	
	//Protected data for subclasses.
	
	/** The fleet ID of this fleet. */
	protected int fleetID;
	
	/** The team ID of this fleet's team.*/
	protected int teamID;
	
	/** Updated each tick with the current number of credits.*/
	protected int credits;
	
	/** Updated each tick to the current tick number of the battle.*/
	protected int tick;
	
	/**
	 * Each tick the current status of each ship will be found here.
	 * <p>Normally <code>runTick()</code> would return an iterator from this.
	 */
	protected AIShipList myShips;
	
	/** If the fleet starts with at least one cruiser, this will be initialized to one of them.*/
	protected AIShip myCruiser;
	
	/** List of raw sensor contact information updated each tick.*/
	protected ContactList myContacts;
	
	/** Random number generator that's seeded by setRandomSeed.*/
	protected Random random;
	
	/** The dimensions of the current battle.*/
	protected double battleWidth,battleHeight;
	
	/** All of the fleets currently in the battle.*/
	
	protected Fleet[] battleFleets;
	/** All of the teams currently in the battle.*/
	protected Team[] battleTeams;
	
	/**
	 * A map of parent ship ID's to AIShips that have been queued to spawn.
	 * <p>By default createShip will use an AIShip from here with a matching parent ID before creating a new one.
	 * <p>AI's can be added with {@link queueSpawnAI(AIShip s, int parentID)}.
	 */
	protected HashMap<Integer,AIShip> myNewSpawns;
	
	/** Default constructor. */
	public AIFleet()
	{
		random = new Random();
		myNewSpawns = new HashMap<Integer,AIShip>();
	}
	
	/** Seed the random number generator as specified.*/
	public AIFleet(long seed)
	{
		random = new Random(seed);
		myNewSpawns = new HashMap<Integer,AIShip>();
	}

	/** Returns the author of this fleet. */
	public abstract String getAuthor();
	
	/** Returns the name of the current fleet.  Defaults to the unqualified class name.*/
	public String getName()
	{
		String className = this.getClass().getName();
		String[] oup = className.split("\\.");
		if(oup.length == 0)
			return "null";
		return oup[oup.length-1];
	}
	
	/** Returns a version string for the current fleet.*/
	public String getVersion()
	{
		return "0";
	}
	
	/** Adds AIShips to <code>myNewSpawns</code> to create AI's next tick.*/
	public void queueSpawnAI(AIShip s, int parentID)
	{
		myNewSpawns.put(parentID,s);
	}
	
	/** Calls <code>run()</code> on all ships in <code>myShips</code>.*/
	public void runShipAI()
	{
		Iterator<AIShip> i = myShips.iterator();		
		while(i.hasNext())
		{
			AIShip cur = i.next();
			cur.run();
		}
	}

	/** Called by the <code>AIProtocol</code> to create AIShips for this fleet.*/
	public AIShip createShip(Ship s)
	{
		AIShip oup = myNewSpawns.get(s.getParentID());
		if(oup != null)
		{
			myNewSpawns.remove(s.getParentID());
			oup.update(s);
			return oup;
		}
		
		return new AIShip(s,this);
	}
	
	/** Called by the <code>AIProtocol</code> whenever a fleet or team dies.*/
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa)
	{
		for(int x=0;x<battleFleets.length;x++)
		{
			if(battleFleets[x].getID() == fleetID)
			{
				battleFleets[x].setAlive(doa);
				return;
			}
		}
	}
	
	/** Called by the <code>AIProtocol</code> at the start of a battle.*/
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double width, double height)
	{		
		this.fleetID = fleetID;
		this.teamID = teamID;
		credits = startingCredits;
		
		battleTeams = teams;
		battleFleets = f;
		
		battleWidth = width;
		battleHeight = height;
		
		myShips = s;		
		
		Iterator<AIShip> i = myShips.iterator();		
		while(i.hasNext())
		{
			AIShip cur = i.next();
			if(cur.getTypeID() == CRUISER_ID)
			{
				myCruiser= (AIShip) cur;
				break;
			}
		}
		
		if(random == null)
			random = new Random();
	}
	
	/** Called by the <code>AIProtocol</code> each tick to get the new actions.
	 * This in turn calls <code>runTick()</code> to run the fleet's AI code.
	 */
	public final Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s)
	{
		this.credits=credits;
		myShips=s;
		this.tick = tick;
		
		//wipe this every tick, so that if a spawn doesn't happen, you don't accidentally assign the ship later
		//(this /should/ be empty anyway, barring a stupid fleet, but if not...)
		if(!myNewSpawns.isEmpty())
		{
			Debug.aiwarn(getName()+": myNewSpawns had size "+myNewSpawns.size()+" on tick "+tick);
			myNewSpawns.clear();
		}
		
		//Do other stuff (like processing ContactList)
		myContacts = c;
		
		//let the children init stuff (this way they can get some polymorphism going)
		initTick();
		
		return runTick();
	}
	
	/**
	 * This is called each tick before <code>runTick()</code>, and is intended mainly for pre-tick maintenance things that subclasses should have available.
	 */
	public void initTick()
	{
		return;
	}
	
	/**
	 * This is where the main AI processing occurs.  Normally this would update the actions in <code>myShips</code> and then return myShips.getActionIterator().
	 * @return An iterator of all ShipActions that need to occur.
	 */
	public abstract Iterator<ShipAction> runTick();
	
	/** Called when the battle is over with the battle results for anyone that cares. */
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		//It's over!
		return;
	}
	
	/** Initializes <code>random</code> with the specified seed.*/
	public void setRandomSeed(long seed)
	{
		if(random == null)
			random = new Random(seed);
		else
			random.setSeed(seed);
	}
	
	/**
	 * Utility function to determine if a given ship type is ammunition.
	 * 
	 * @param type the typeID to be tested.
	 * @return true iff type is a missile or rocket.
	 */
	public boolean isAmmo(int type)
	{
		return !TYPE.get(type).isShip();
	}
	
	/**
	 * Utility function to determine if a given ship type is ammunition.
	 * 
	 * @param type the Ship to be tested.
	 * @return true iff type is a missile or rocket.
	 */

	public boolean isAmmo(ShipStatus s)
	{
		return s.isAmmo();
	}
}
