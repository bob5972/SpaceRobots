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

package net.banack.spacerobots;

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DQuad;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.util.MethodNotImplementedException;

public class ServerShip extends Ship
{
	// Internal Representation of a Ship
	
	private ServerFleet myFleet;
	
	public ServerShip(ServerFleet f, int id, int parentID, int type, ShipType t, double x, double y, int tick, int life)
	{
		super(id, parentID, type, t, x, y, tick, life);
		myFleet = f;
	}
	
	public ServerShip(ServerFleet f, int id, int parentID, int type, ShipType t, double x, double y, double heading,
	        int tick, int life)
	{
		super(id, parentID, type, t, x, y, tick, life);
		myFleet = f;
	}
	
	public ServerFleet getFleet()
	{
		return myFleet;
	}
	
	public int getFleetID()
	{
		return myFleet.getFleetID();
	}
}
