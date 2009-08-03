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

package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipStatus;

/**
 * A basic AI for a missile class that homes in on a target.
 * <p>
 * If given a stable ShipStatus (such as a Contact or AIShip from one of the lists) this will track them as they move.
 * 
 * @author Michael Banack <bob5972@banack.net>
 * 
 */
public class BasicMissile extends AIShip
{
	private ShipStatus myTarget;
	
	
	public BasicMissile(AIFleet f)
	{
		super(f);
	}
	
	public BasicMissile(AIFleet f, ShipStatus target)
	{
		super(f);
		myTarget = target;
	}
	
	public BasicMissile(Ship s, AIFleet f, ShipStatus target)
	{
		super(s, f);
		myTarget = target;
	}
	
	public void run()
	{
		if (myTarget != null)
			intercept(myTarget);
	}
	
	public ShipStatus getTarget()
	{
		return myTarget;
	}
	
	public void setTarget(ShipStatus t)
	{
		myTarget = t;
	}
	
	public boolean hasTarget()
	{
		return myTarget != null && myTarget.isAlive();
	}
	
}
