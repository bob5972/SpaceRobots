package net.banack.spacerobots.fleets;

import java.util.Iterator;

import net.banack.spacerobots.ai.AIFilter;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.MBFleet;
import net.banack.spacerobots.ai.MBShip;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;

public class BattleCruiserFleet extends MBFleet
{
	private final int STATE_IDLE = 1;
	private final int STATE_ATTACK = 2;
	private final int STATE_RETREAT =3;
	
	private int myState;
	private int stateTimer;
	private Contact myTarget;
	
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
		
		myState = 1;
	}
		
	
	
	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s)
	{
		myCredits = credits;
		if(stateTimer > 0)
			stateTimer--;
		
		if(c.size() == 0 && stateTimer <= 0)
			myState = STATE_IDLE;
		else if(c.size() > 0 && myState == STATE_IDLE)
			myState = STATE_ATTACK;
				
		if(c.size() > 5)
		{
			myState=STATE_RETREAT;
			stateTimer=100;
		}
			
		
		switch(myState)
		{
			case STATE_IDLE:
				myTarget=null;
				if(tick % 400 == 0)
					myCruiser.setHeading(myRandom.nextGaussian()-0.5+myCruiser.getHeading());
				myCruiser.setScannerHeading(myCruiser.getScannerHeading()+CRUISER.getScannerAngleSpan());
			break;
			case STATE_ATTACK:
				if(myCruiser.canLaunch(MISSILE))
					myCruiser.launch(MISSILE);
				Iterator<Integer> ei = c.enemyIterator();
				myTarget = c.getContact(ei.next());
				while(ei.hasNext() && (myTarget == null || isAmmo(myTarget.getTypeID())))
					myTarget = c.getContact(ei.next());
				myCruiser.intercept(myTarget);
				myCruiser.setScannerHeading(myTarget.getPosition());
			break;
			case STATE_RETREAT:
				if(c.size() >0)
					myTarget = c.getContact(c.enemyIterator().next());
				if(myTarget != null)
				{
					myCruiser.setHeading(myTarget.getPosition());
					myCruiser.setHeading(myCruiser.getHeading()+Math.PI);
					if(myCruiser.canLaunch(MISSILE))
						myCruiser.launch(MISSILE);
				}
			break;
		}
		
		s.apply(AIFilter.MISSILES, new AIGovernor() {
			public void run(AIShip s)
			{
				MBShip t = (MBShip) s;
				if(myTarget != null)
					t.intercept(myTarget);
				
			}
		});
		
		
		return s.getActionIterator();
	}
	
}
