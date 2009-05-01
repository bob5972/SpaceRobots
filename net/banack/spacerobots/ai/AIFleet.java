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
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.Team;

public abstract class AIFleet implements AIShipFactory
{
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
	
	protected int fleetID;
	protected int teamID;
	protected int credits;
	protected int tick;
	protected AIShipList myShips;
	protected AIShip myCruiser;
	protected ContactList myContacts;
	protected Random random;
	
	protected double battleWidth,battleHeight;
	protected Fleet[] battleFleets;
	protected Team[] battleTeams;
	
	protected HashMap<Integer,AIShip> myNewSpawns;
	
	public AIFleet()
	{
		random = new Random();
		myNewSpawns = new HashMap<Integer,AIShip>();
	}
	
	public AIFleet(long seed)
	{
		random = new Random(seed);
		myNewSpawns = new HashMap<Integer,AIShip>();
	}

	public abstract String getAuthor();
	
	public String getName()
	{
		String className = this.getClass().getName();
		String[] oup = className.split("\\.");
		if(oup.length == 0)
			return "null";
		return oup[oup.length-1];
	}
	
	public String getVersion()
	{
		return "0";
	}
	
	public void queueSpawnAI(AIShip s, int parentID)
	{
		myNewSpawns.put(parentID,s);
	}
	
	public void runShipAI()
	{
		Iterator<AIShip> i = myShips.iterator();		
		while(i.hasNext())
		{
			AIShip cur = i.next();
			cur.run();
		}
	}

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
	
	public void initTick()
	{
		return;
	}
	
	public abstract Iterator<ShipAction> runTick();
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		//It's over!
		return;
	}
	
	public void setRandomSeed(long seed)
	{
		if(random == null)
			random = new Random(seed);
		else
			random.setSeed(seed);
	}
	
	public boolean isAmmo(int type)
	{
		return (type == MISSILE_ID) || (type == ROCKET_ID);
	}	
}
