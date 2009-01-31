package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AbstractFleetAI;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;

public class Cache extends AbstractFleetAI
{
	private AIShipList myShips;
	private Random myRandom;
	private AIShip myCruiser;
	private int myCredits;
	private double myWidth,myHeight;
	
	private static DefaultShipTypeDefinitions TYPE = new DefaultShipTypeDefinitions();
	
	private static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	//private static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	private static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	private static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	
	public Cache()
	{
		myRandom = new Random();
	}
	
	public Cache(long seed)
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
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double width, double height)
	{
		myWidth=width;
		myHeight = height;
		
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
	}
	
	private boolean canLaunch(AIShip s)
	{
		return (s.isAlive() && s.isReadyToLaunch() && s.getTypeID() != ROCKET_ID);
	}
	
	private boolean canLaunch(AIShip s, ShipType t)
	{
		return canLaunch(s) && myCredits >= t.getCost();
	}
	
	private boolean canLaunch(AIShip s , int type)
	{
		return canLaunch(s,TYPE.get(type));
	}
	
	private double getRawDistance(AIShip s, AIShip t)
	{
		return getRawDistance(s.getPosition(),t.getPosition());
	}
	
	private double getRawDistance(DPoint x, DPoint y)
	{
		return (x.subtract(y)).getRadius();
	}
	
	private DPoint getClosestMirror(AIShip s, AIShip t)
	{
		return SpaceMath.getClosestMirror(s.getPosition(),t.getPosition(),myWidth,myHeight);				
	}
	
	private double interceptHeading(AIShip s, AIShip target)
	{
		DPoint sPos = s.getPosition();
		DPoint tPos = target.getPosition();
		DPoint offset = sPos.subtract(tPos);
		
		double h = - ( ( ( target.getHeading() - offset.getTheta()) + Math.PI * 3 ) % (Math.PI * 2) ) - Math.PI;
		h = Math.asin( Math.sin(h)* (target.getMaxSpeed()/s.getMaxSpeed()) ) + offset.getTheta()+Math.PI;
		return h;
		
		//old way
		//DPoint newPos = SpaceMath.calculateNewPos(target.getPosition(),target.getHeading(),target.getMaxSpeed());
		//return SpaceMath.getAngle(s.getPosition(), newPos);
	}
	
	private double intercept(AIShip s, AIShip target)
	{
		double oup = interceptHeading(s,target);
		s.setHeading(oup);
		return oup;
	}

	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList sIn)
	{
		myShips=sIn;
		myCredits=credits;
		
		Iterator<AIShip> i;
		
		i = myShips.getAliveIterator();
		while(i.hasNext())
		{
			AIShip cur = i.next();
			
			if(cur == myCruiser)
				continue;
			
			if(tick % 20 == 0)
			{
				intercept(cur,myCruiser);
			}
		}
		
		
		if(myCruiser.isAlive())
		{
			if(canLaunch(myCruiser,FIGHTER_ID) && myCredits >= TYPE.FIGHTER.getCost()*2)
			{
				credits-=DefaultShipTypeDefinitions.FIGHTER.getCost();
				myCruiser.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
			}
			myCruiser.setScannerHeading(myCruiser.projHeading());			
			if(tick % 100 == 0)
			{
				double h = myRandom.nextDouble();
				myCruiser.setHeading(h);
			}
		}
		
		Iterator<Integer> ci = c.enemyIterator();
		while(ci.hasNext())
		{
			Integer eid = ci.next();
			Iterator<Integer> si = c.getSpotters(eid).iterator();
			while(si.hasNext())
			{
				AIShip s = myShips.get(si.next());
				if(canLaunch(s,ROCKET_ID))
				{
					myCredits-=TYPE.ROCKET.getCost();
					s.setLaunch(ROCKET_ID);
				}
			}
		}
		
		return myShips.getActionIterator();
	}
	
}
