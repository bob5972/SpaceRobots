/*
 * This file is part of SpaceRobots.
 * Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SpaceRobots.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;

/**
 * A ContactFilter that selects legitimate targets  (ie alive and not ammo).
 * @author Michael Banack <bob5972@banack.net>
 *
 */
public class TargetFilter extends ContactFilter
{
	public boolean test(Contact c)
	{
		if(c.getTypeID() == DefaultShipTypeDefinitions.MISSILE_ID ||
				c.getTypeID() == DefaultShipTypeDefinitions.ROCKET_ID )
		{
			return false;
		}
		
		if(c.isDead())
			return false;
		
		return true;
	}	
}
