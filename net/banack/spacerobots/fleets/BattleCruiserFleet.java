package net.banack.spacerobots.fleets;

import java.util.Comparator;
import java.util.Iterator;

import net.banack.debug.Debug;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIFilter;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.BasicAIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.BasicMissile;
import net.banack.spacerobots.ai.ContactFilter;
import net.banack.spacerobots.ai.TargetingSystem;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;
import net.banack.util.Pile;
import net.banack.util.Queue;
import net.banack.util.Stack;

public class BattleCruiserFleet extends AIFleet
{
	private final int STATE_IDLE = 1;
	private final int STATE_ATTACK = 2;
	private final int STATE_RETREAT =3;
	
	private int myState;
	private int stateTimer;
	protected TargetingSystem myTargets;
	
	public BattleCruiserFleet()
	{
		super();
	}
	
	public BattleCruiserFleet(long seed)
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
		myTargets = new TargetingSystem(this);
		myTargets.setDefaultMissilePriority(new Comparator<Contact>() {
			public int compare(Contact lhs, Contact rhs)
			{
				int ltype = lhs.getTypeID();
				int rtype = rhs.getTypeID();
				
				int left = myTargets.getAllocationCount(lhs);
				int right = myTargets.getAllocationCount(rhs);
				
				if(ltype == CRUISER_ID && rtype != CRUISER_ID)
					return -1;
				if(rtype == CRUISER_ID && ltype != CRUISER_ID)
					return 1;
				if(left < right)
					return -1;
				if(left > right)
					return 1;
				return 0;
			}
		});
		
		myState = 1;
	}
	
	public AIShip createShip(Ship s)
	{
		if(s.getTypeID() == MISSILE_ID)
		{
			BasicMissile oup;
			oup = new BasicMissile(this);
			oup.update(s);
			Contact target = myTargets.getMissileTarget(oup);
			oup.setTarget(target);
			myTargets.allocate(target);
			if(target == null)
				oup.setHeading(myCruiser.getScannerHeading()+(MISSILE.getMaxTickCount()-5)*myCruiser.getScannerAngleSpan());
			return oup;
		}
		
		return new AIShip(s,this);
	}
	
	public void initTick()
	{
		myTargets.update();
	}
	
	public Iterator<ShipAction> runTick()
	{
		Contact target;
		
		if(stateTimer > 0)
			stateTimer--;
		
		if(myContacts.size() == 0 && stateTimer <= 0)
			myState = STATE_IDLE;
		else if(myContacts.size() > 0 && myState == STATE_IDLE)
			myState = STATE_ATTACK;
				
		if(myContacts.size() > 3)
		{
			myState=STATE_RETREAT;
			stateTimer=100;
		}
		
		myCruiser.setScannerHeading(myCruiser.getScannerHeading()+CRUISER.getScannerAngleSpan());
		
		
		
		target = myTargets.getMissileTarget(myCruiser, new ContactFilter(){
			public boolean test(Contact c)
			{
				return c.getTypeID() == CRUISER_ID;
			}
		});
		
		if(target != null)
		{
			myCruiser.setScannerHeading(target);
			myState = STATE_RETREAT;
		}
		
		
		
		switch(myState)
		{
			case STATE_IDLE:
				if(tick % 400 == 0)
					myCruiser.setHeading(random.nextGaussian()-0.5+myCruiser.getHeading());
			break;				
			case STATE_RETREAT:
				target = (target != null)? target : myTargets.getClosestTarget(myCruiser,myCruiser.getScannerRadius());
				if(target != null)
				{
					myCruiser.setHeading(target.getPosition());
					myCruiser.setHeading(myCruiser.getHeading()+Math.PI);
				}
			break;
		}
		
		if(myCruiser.canLaunch(MISSILE))
			myCruiser.launch(MISSILE);
		
		myShips.apply(AIFilter.MISSILES, new AIGovernor() {
			public void run(AIShip s)
			{
				if(s instanceof BasicMissile)
				{
					BasicMissile t = (BasicMissile) s;
					if(t.isDead())
					{
						//this should only run once, the tick after the missile dies
						myTargets.deallocate(t.getTarget());
					}
					else
					{
						if(!t.hasTarget())
						{
							t.setTarget(myTargets.getMissileTarget(t));
						}
					}
				}
				s.run();
			}
		});
		
		
		return myShips.getActionIterator();
	}
		
	public boolean isAmmo(ShipStatus s)
	{
		return s.getTypeID() == ROCKET_ID || s.getTypeID() == MISSILE_ID;
	}
	
}
