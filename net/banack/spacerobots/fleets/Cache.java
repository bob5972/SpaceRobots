/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <bob5972@banack.net>
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;

/**
 * A sample fleet that sticks together.
 * 
 * @author Michael Banack <bob5972@banack.net>
 * 
 */
public class Cache extends AIFleet
{
	public Cache()
	{
		super();
	}
	
	public Cache(long seed)
	{
		super(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getVersion()
	{
		return "1.2";
	}
	
	private Contact myTarget;
	private boolean launchFighter = true;
	
	public Iterator<ShipAction> runTick()
	{
		
		Iterator<AIShip> i;
		
		Iterator<Integer> ci;
		
		ci = myContacts.enemyShipIterator();
		while (ci.hasNext()) {
			Contact enemy = myContacts.get(ci.next());
			myTarget = enemy;
			break;
		}
		

		i = myShips.getAliveIterator();
		while (i.hasNext()) {
			AIShip cur = (AIShip) i.next();
			
			if (cur.canMoveScanner())
				cur.advanceScannerHeading();
			
			if (cur == myCruiser)
				continue;
			
			if (myTarget != null) {
				cur.intercept(myTarget);
			} else {
				if (tick % 40 == 0)
					cur.intercept(myCruiser);
			}
			
		}
		
		if (myTarget != null && myTarget.getScanTick() < tick - 10)
			myTarget = null;
		

		if (myCruiser.isAlive()) {
			ShipType toLaunch = (launchFighter ? FIGHTER : DESTROYER);
			if (myCruiser.canLaunch(toLaunch) && credits >= toLaunch.getCost() + MISSILE.getCost() * 10) {
				myCruiser.launch(toLaunch);
				// launchFighter = !launchFighter;
			}
			
			if (tick % 100 == 0) {
				double h = random.nextDouble();
				myCruiser.setHeading(h);
			}
		}
		
		ci = myContacts.enemyShipIterator();
		while (ci.hasNext()) {
			Integer eid = ci.next();
			Iterator<Integer> si = myContacts.getSpotters(eid).iterator();
			while (si.hasNext()) {
				AIShip s = (AIShip) myShips.get(si.next());
				if (s.getTypeID() != FIGHTER_ID) {
					Contact enemy = myContacts.get(eid);
					s.setScannerHeading(enemy);
					if (s.canLaunch(MISSILE))
						s.launchMissile(enemy);
				} else {
					if (s.canLaunch(ROCKET))
						s.launch(ROCKET);
				}
			}
		}
		
		return myShips.getActionIterator();
	}
	

}
