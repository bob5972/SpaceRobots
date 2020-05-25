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

package net.banack.spacerobots.ai;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Contact;
import net.banack.util.Filter;

/**
 * A filter that selects contacts in a given location range.
 * 
 * @author Michael Banack <github@banack.net>
 */
public class RangeFilter extends ContactFilter
{
	private DPoint bl, tr;
	
	public RangeFilter(DPoint bl, DPoint tr)
	{
		this.bl = bl;
		this.tr = tr;
	}
	
	public boolean test(Contact c)
	{
		double x = c.getX();
		double y = c.getY();
		
		if (bl.getX() <= x && x <= tr.getX()) {
			if (bl.getY() <= y && y <= tr.getY())
				return true;
		}
		
		return false;
	}
}
