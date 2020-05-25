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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import net.banack.spacerobots.Debug;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;

/**
 * A fleet that just flies around to be targets.
 * 
 * @author Michael Banack <github@banack.net>
 * 
 */
public class DummyFleet extends AIFleet
{
	public DummyFleet()
	{
		super();
	}
	
	public DummyFleet(long seed)
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
	
	public Iterator<ShipAction> runTick()
	{
		Iterator<AIShip> i = myShips.getAliveIterator();
		
		while (i.hasNext()) {
			AIShip ship = i.next();
			
			if (tick % 100 == 0) {
				ship.setHeading(random.nextDouble() * Math.PI / 2);
			}
		}
		
		if (myCruiser != null && myCruiser.canLaunch(FIGHTER)) {
			myCruiser.launch(FIGHTER);
		}
		
		return myShips.getActionIterator();
	}
	

}
