package net.banack.spacerobots.fleets;

import java.util.Iterator;

import net.banack.debug.Debug;
import net.banack.spacerobots.ai.AIFilter;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.BasicAIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.BasicMissile;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;
import net.banack.util.Queue;
import net.banack.util.Stack;

public class BattleCruiserFleet extends AIFleet
{
	private final int STATE_IDLE = 1;
	private final int STATE_ATTACK = 2;
	private final int STATE_RETREAT =3;
	
	private int myState;
	private int stateTimer;
	
	private Queue<Contact> myTargets;
	
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
		return "1.0";
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double width, double height)
	{
		super.initBattle(fleetID, teamID, startingCredits, s, teams, f, width, height);
		myTargets = new Queue<Contact>();
		myState = 1;
	}
	
	public AIShip createShip(Ship s)
	{
		AIShip oup;
		if(s.getTypeID() == MISSILE_ID)
		{
			Contact target = getTarget();
			oup = new BasicMissile(this,target);
			oup.update(s);
			return oup;
		}
		
		return new AIShip(s,this);
	}	
	
	public Iterator<ShipAction> runTick()
	{		
		if(stateTimer > 0)
			stateTimer--;
		
		if(myContacts.size() == 0 && stateTimer <= 0)
			myState = STATE_IDLE;
		else if(myContacts.size() > 0 && myState == STATE_IDLE)
			myState = STATE_ATTACK;
				
		if(myContacts.size() > 5)
		{
			myState=STATE_RETREAT;
			stateTimer=100;
		}
		
		Iterator<Integer> ei = myContacts.enemyIterator();
		Contact cur;
		
		myCruiser.setScannerHeading(myCruiser.getScannerHeading()+CRUISER.getScannerAngleSpan());
		
		while(ei.hasNext())
		{
			cur = myContacts.get(ei.next());
			if(!isAmmo(cur))
				myTargets.enqueue(cur);
			
			if(cur.getTypeID() == CRUISER_ID)
				myCruiser.setScannerHeading(cur);
		}
		
		
		
		switch(myState)
		{
			case STATE_IDLE:
				if(tick % 400 == 0)
					myCruiser.setHeading(random.nextGaussian()-0.5+myCruiser.getHeading());
			break;				
			case STATE_RETREAT:
					if(!myTargets.isEmpty())
					{
						myCruiser.setHeading(myTargets.front().getPosition());
						myCruiser.setHeading(myCruiser.getHeading()+Math.PI);
					}
					if(myCruiser.canLaunch(MISSILE))
						myCruiser.launch(MISSILE);
			break;
		}
		
		if(myTargets.size() >0 && myCruiser.canLaunch(MISSILE))
			myCruiser.launch(MISSILE);
		
		myShips.apply(AIFilter.MISSILES, new AIGovernor() {
			public void run(AIShip s)
			{
				if(s instanceof BasicMissile)
				{
					BasicMissile t = (BasicMissile) s;
					if(!t.hasTarget() || isAmmo(t.getTarget()))
					{
						t.setTarget(getTarget());
					}
				}
				s.run();								
			}
		});
		
		
		return myShips.getActionIterator();
	}
		
	public Contact getTarget()
	{
		while(!myTargets.isEmpty() && myTargets.front().getScanTick() < tick-10)
			myTargets.dequeue();
	
		if(myTargets.isEmpty())
			return null;
		
		return myTargets.dequeue();
	}
	
	
	public boolean isAmmo(ShipStatus s)
	{
		return s.getTypeID() == ROCKET_ID || s.getTypeID() == MISSILE_ID;
	}
	
}
