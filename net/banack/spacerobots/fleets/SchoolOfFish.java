package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.banack.debug.Debug;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIFilter;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.CompositeGovernor;
import net.banack.spacerobots.ai.MBFleet;
import net.banack.spacerobots.ai.MBShip;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;


public class SchoolOfFish extends MBFleet
{
	private int nextMove;
	private double groupHeading;
	
	
	public SchoolOfFish()
	{
		super();
	}
	
	public SchoolOfFish(long seed)
	{
		super(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getVersion()
	{
		return "1.1";
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double width, double height)
	{
		super.initBattle(fleetID, teamID, startingCredits, s, teams, f, width, height);
		
		nextMove=100;
		groupHeading=0;
	}

	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s)
	{
		myCredits = credits;
		myShips=s;
		
		Iterator<MBShip> i;
		
		boolean adjTick = false;
		
		if(tick> nextMove)
		{
			nextMove = tick+50;
			adjTick=true;
			
			if(myRandom.nextInt(10) < 4)
			{
				double ax= 0;
				double ay= 0;
				
				i = MBShip.wrapIterator(myShips.iterator());
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
		
		CompositeGovernor govna = new CompositeGovernor();
		
		if(adjTick)
		{
			govna.insert(AIFilter.FIGHTERS, new AIGovernor(){
				public void run(AIShip s)
				{
					MBShip t = (MBShip) s;
					if(myRandom.nextInt(10) < 3)
					{
						t.intercept(myCruiser);
					}
					else
					{
						t.setHeading(groupHeading); 
					}
				};
			});
		}
		
		if(c.size() > 0)
		{
			govna.insert(new AIGovernor(){
				public void run(AIShip s)
				{
					MBShip t = (MBShip) s;
					if(t.canLaunch(ROCKET) && Math.abs(t.getHeading() -groupHeading) < 0.1)
					{
						t.launch(ROCKET);
					}
				}
			});
		}
		
		myShips.apply(govna);
		
		if(myCruiser.isAlive())
		{
			if(myCruiser.readyToLaunch() &&  credits > DefaultShipTypeDefinitions.FIGHTER.getCost()*2)
			{
				myCruiser.launch(FIGHTER);
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
