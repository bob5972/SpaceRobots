package net.banack.spacerobots.fleets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.banack.debug.Debug;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AbstractFleetAI;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;


public class SchoolOfFish extends AbstractFleetAI
{
	private AIShipList myShips;
	private Random myRandom;
	private AIShip myCruiser;
	private int nextMove;
	private double groupHeading;
	
	private static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	private static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	private static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	private static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	
	public SchoolOfFish()
	{
		myRandom = new Random();
	}
	
	public SchoolOfFish(long seed)
	{
		myRandom = new Random(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getVersion()
	{
		return "1.0";
	}
	
	public String getName()
	{
		return "SchoolOfFish";
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f)
	{
		myShips=s;
		Iterator<AIShip> i = myShips.iterator();
		
		while(i.hasNext())
		{
			AIShip cur = i.next();
			if(cur.getTypeID() == CRUISER_ID)
			{
				myCruiser=cur;
				break;
			}
		}
		
		nextMove=100;
		groupHeading=0;
	}

	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s)
	{
		myShips=s;
		
		Iterator<AIShip> i;
		
		HashSet<Integer> cantSpawn = new HashSet<Integer>();
		
		boolean adjTick = false;
		
		if(tick> nextMove)
		{
			nextMove = tick+50;
			adjTick=true;
			
			if(myRandom.nextInt(10) < 4)
			{
				double ax= 0;
				double ay= 0;
				
				i = myShips.iterator();
				while(i.hasNext())
				{
					AIShip cur = i.next();
					ax+= cur.getX();
					ay+= cur.getY();					
				}
				ax /= myShips.size();
				ay /= myShips.size();
				
				groupHeading = SpaceMath.getAngle(new DPoint(ax,ay), myCruiser.getPosition());
			}
			else
			{
				groupHeading += (myRandom.nextDouble())*Math.PI*2;
				groupHeading = SpaceMath.wrapHeading(groupHeading);
			}
		}
		
		i = myShips.iterator();
		while(i.hasNext())
		{
			AIShip cur = i.next();
			
			if(!cur.isAlive())
			{
				continue;
			}
			
			if(!cur.isReadyToLaunch() || cur.getTypeID() == ROCKET_ID)
				cantSpawn.add(cur.getShipID());
			
			if(adjTick && cur != myCruiser)
			{
				if(myRandom.nextInt(10) < 3)
				{
					cur.setHeading(myCruiser.getPosition());
				}
				else
				{
					cur.setHeading(groupHeading);
				}
			}
			
			if(c.size() > 0 && !cantSpawn.contains(cur.getShipID()) && credits > DefaultShipTypeDefinitions.ROCKET.getCost())
			{
				if(cur.getHeading() == groupHeading)
				{
					credits-=DefaultShipTypeDefinitions.ROCKET.getCost();
					cur.setLaunchWhat(DefaultShipTypeDefinitions.ROCKET_ID);
				}
			}
		}
		
		
		if(myCruiser.isAlive())
		{
			if(myCruiser.readyToLaunch() &&  credits > DefaultShipTypeDefinitions.FIGHTER.getCost()*2)
			{
				credits-=DefaultShipTypeDefinitions.FIGHTER.getCost();
				myCruiser.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
			}
			myCruiser.setScannerHeading(myCruiser.projHeading());			
			if(tick % 100 == 0)
			{
				double h = myCruiser.curHeading();
				h += myRandom.nextDouble();
				myCruiser.setHeading(h);
			}
		}
		
		return myShips.getActionIterator();
	}
	
}
