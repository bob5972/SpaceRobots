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

import java.util.Comparator;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.SpaceMath;

/**
 * Prioritizes contacts based on distance from a center point.
 * 
 * @author Michael Banack <bob5972@banack.net>
 * 
 */
public class DistancePriority implements Comparator<Contact>
{
	private DPoint center;
	private double width, height;
	
	public DistancePriority(DPoint center, double width, double height)
	{
		this.center = center;
		this.width = width;
		this.height = height;
	}
	
	public int compare(Contact lhs, Contact rhs)
	{
		double ld = SpaceMath.getDistance(center, lhs.getPosition(), width, height);
		double rd = SpaceMath.getDistance(center, rhs.getPosition(), width, height);
		
		if (ld < rd)
			return -1;
		if (ld > rd)
			return 1;
		
		return 0;
	}
}
