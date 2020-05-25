/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <github@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with SpaceRobots. If not, see
 * <http://www.gnu.org/licenses/>.
 */

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
	private final int STATE_RETREAT = 3;
	
	private int myState;
	private int stateTimer;
	private double scannerStart;
	private int myShipCount;
	private int shootCounter;
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
		return "2.0";
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f,
	        double width, double height)
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
				
				if (ltype == CRUISER_ID && rtype != CRUISER_ID)
					return -1;
				if (rtype == CRUISER_ID && ltype != CRUISER_ID)
					return 1;
				if (left < right)
					return -1;
				if (left > right)
					return 1;
				return 0;
			}
		});
		
		myShipCount = 1;
		myState = 1;
	}
	
	public AIShip createShip(Ship s)
	{
		if (s.getTypeID() == MISSILE_ID) {
			BasicMissile oup;
			oup = new BasicMissile(this);
			oup.update(s);
			Contact target = myTargets.getMissileTarget(oup);
			oup.setTarget(target);
			myTargets.allocate(target);
			if (target == null)
				oup.setHeading(myCruiser.getScannerHeading() + (MISSILE.getMaxTickCount() - 5)
				        * myCruiser.getScannerAngleSpan());
			return oup;
		}
		if (s.getTypeID() == CRUISER_ID)
			myShipCount++;
		
		return new AIShip(s, this);
	}
	
	public void initTick()
	{
		myTargets.update();
	}
	
	public Iterator<ShipAction> runTick()
	{
		if (stateTimer > 0)
			stateTimer--;
		
		if (myContacts.size() == 0 && stateTimer <= 0)
			myState = STATE_IDLE;
		
		Iterator<Integer> ci = myContacts.enemyShipIterator();
		shootCounter = 0;
		while (ci.hasNext()) {
			int eID = ci.next();
			Contact enemy = myContacts.get(eID);
			int alloc = myTargets.getAllocationCount(eID);
			int life = enemy.getLife();
			if (alloc < life) {
				shootCounter += life - alloc;
			}
		}
		if (shootCounter > 0)
			shootCounter++;// one more for good luck
			
		if (myContacts.size() > 3 * myShipCount) {
			myState = STATE_RETREAT;
			stateTimer = 100;
		}
		
		myCruiser.advanceScannerHeading();
		scannerStart = myCruiser.curScannerHeading();
		myShipCount = 0;
		myShips.apply(AIFilter.CRUISERS, new AIGovernor() {
			public void run(AIShip s)
			{
				if (!myCruiser.isAlive())
					myCruiser = s;
				myShipCount++;// I'm too lazy to update this on deaths
				
				if (s != myCruiser) {
					s.intercept(myCruiser);
					scannerStart += (Math.PI * 2) / myShipCount;
					s.setScannerHeading(scannerStart);
					if (shootCounter > 0 && s.canLaunch(MISSILE)) {
						shootCounter--;
						s.launch(MISSILE);
					}
				}
			}
		});
		
		switch (myState) {
			case STATE_IDLE:
				if (tick % 400 == 0)
					myCruiser.setHeading(random.nextGaussian() - 0.5 + myCruiser.getHeading());
				if (myCruiser.canLaunch(CRUISER) && credits > CRUISER.getCost() + 50 * myShipCount)
					myCruiser.launch(CRUISER);
				break;
			case STATE_RETREAT:
				Contact target = myTargets.getClosestTarget(myCruiser, myCruiser.getScannerRadius());
				if (target != null) {
					myCruiser.setHeading(target.getPosition());
					myCruiser.setHeading(myCruiser.getHeading() + Math.PI);
				}
				break;
		}
		
		if ((shootCounter > 0) && myCruiser.canLaunch(MISSILE)) {
			myCruiser.launch(MISSILE);
			shootCounter--;
		}
		
		myShips.apply(AIFilter.MISSILES, new AIGovernor() {
			public void run(AIShip s)
			{
				if (s instanceof BasicMissile) {
					BasicMissile t = (BasicMissile) s;
					if (t.isDead()) {
						// this should only run once, the tick after the missile dies
						myTargets.deallocate(t.getTarget());
					} else {
						if (!t.hasTarget()) {
							t.setTarget(myTargets.getMissileTarget(t));
						}
					}
				}
				s.run();
			}
		});
		

		return myShips.getActionIterator();
	}
	
}
