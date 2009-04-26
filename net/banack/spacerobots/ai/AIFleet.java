package net.banack.spacerobots.ai;

import java.util.Iterator;
import java.util.Random;

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
	
	protected int myFleetID;
	protected int myTeamID;
	protected int myCredits;
	protected AIShipList myShips;
	protected AIShip myCruiser;
	protected Random myRandom;
	
	protected double battleWidth,battleHeight;
	protected Fleet[] battleFleets;
	protected Team[] battleTeams;
	
	public AIFleet()
	{
		myRandom = new Random();
	}
	
	public AIFleet(long seed)
	{
		myRandom = new Random(seed);
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

	public AIShip createShip(Ship s)
	{
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
		myFleetID = fleetID;
		myTeamID = teamID;
		myCredits = startingCredits;
		
		battleTeams = teams;
		battleFleets = f;
		
		battleWidth = width;
		battleHeight = height;
		
		myShips = s;
		
		Iterator<AIShip> i = myShips.iterator();		
		while(i.hasNext())
		{
			BasicAIShip cur = i.next();
			if(cur.getTypeID() == CRUISER_ID)
			{
				myCruiser= (AIShip) cur;
				break;
			}
		}
		
		if(myRandom == null)
			myRandom = new Random();
	}
	
	public final Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s)
	{
		myCredits=credits;
		myShips=s;
		
		//Do other stuff (like processing ContactList
		
		return runTick(tick,c);
	}
	
	public abstract Iterator<ShipAction> runTick(int tick, ContactList c);
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		//It's over!
		return;
	}
	
	public void setRandomSeed(long seed)
	{
		if(myRandom == null)
			myRandom = new Random(seed);
		else
			myRandom.setSeed(seed);
	}
	
	public boolean isAmmo(int type)
	{
		return (type == MISSILE_ID) || (type == ROCKET_ID);
	}
	
}
