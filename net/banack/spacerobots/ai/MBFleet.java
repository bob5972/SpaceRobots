package net.banack.spacerobots.ai;

import java.util.Iterator;
import java.util.Random;

import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;

public abstract class MBFleet extends AbstractFleetAI
{
	protected int myFleetID;
	protected int myTeamID;
	protected int myCredits;
	protected AIShipList myShips;
	protected MBShip myCruiser;
	protected Random myRandom;
	
	protected double battleWidth,battleHeight;
	protected Fleet[] battleFleets;
	protected Team[] battleTeams;
	
	public MBFleet()
	{
		myRandom = new Random();
	}
	
	public MBFleet(long seed)
	{
		myRandom = new Random(seed);
	}

	public abstract String getAuthor();

	public AIShip createShip(Ship s)
	{
		return new MBShip(s,this);
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
			AIShip cur = i.next();
			if(cur.getTypeID() == CRUISER_ID)
			{
				myCruiser= (MBShip) cur;
				break;
			}
		}
		
		if(myRandom == null)
			myRandom = new Random();
	}
	
	public abstract Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s);
	
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
