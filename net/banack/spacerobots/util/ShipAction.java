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

package net.banack.spacerobots.util;

import net.banack.spacerobots.Debug;
import net.banack.util.MethodNotImplementedException;

/** Represents the assigned actions a ship will take next tick. */
public class ShipAction
{
	private int myShipID;
	private boolean willMove;
	private double newHeading;
	private double newScannerHeading;
	private int launchWhat;
	
	/** Copies all the current status of the Ship. */
	public ShipAction(Ship s)
	{
		myShipID = s.getID();
		willMove = s.willMove();
		newHeading = s.getHeading();
		newScannerHeading = s.getScannerHeading();
		launchWhat = ShipTypeDefinitions.TYPE_INVALID;
	}
	
	/** Constructs a ShipAction with the specified data. */
	public ShipAction(int id, boolean move, double heading, double scannerHeading, int launch)
	{
		myShipID = id;
		willMove = move;
		newHeading = heading;
		newScannerHeading = scannerHeading;
		launchWhat = launch;
	}
	
	/** Constructs an empty ShipAction with the specified ID. */
	public ShipAction(int id)
	{
		myShipID = id;
		willMove = true;
		newHeading = -1;
		newScannerHeading = -1;
		launchWhat = ShipTypeDefinitions.TYPE_INVALID;
	}
	
	public int getShipID()
	{
		return myShipID;
	}
	
	/**
	 * 
	 * @return true if the ship is attempting to launch something.
	 */
	public boolean isSpawn()
	{
		return launchWhat != ShipTypeDefinitions.TYPE_INVALID;
	}
	
	/**
	 * 
	 * @return true if the ship is trying to move next tick.
	 * @see setWillMove(boolean)
	 */
	public boolean willMove()
	{
		return willMove;
	}
	
	/** Same as willMove(). */
	public final boolean getWillMove()
	{
		return willMove();
	}
	
	/**
	 * Sets whether the ship will attempt to move next tick.
	 * <p>
	 * WARNING: This does not check if the ship type is actually capable of stopping.
	 */
	public void setWillMove(boolean b)
	{
		willMove = b;
	}
	
	public double getHeading()
	{
		return newHeading;
	}
	
	public double getScannerHeading()
	{
		return newScannerHeading;
	}
	
	public int getLaunchWhat()
	{
		return launchWhat;
	}
	
	public int hashCode()
	{
		return myShipID;
	}
	
	/** Checks if the ID's are equal. */
	public boolean equals(Object rhs)
	{
		if (!(rhs instanceof ShipAction))
			return false;
		return myShipID == ((ShipAction) rhs).myShipID;
	}
	
	public void setLaunchWhat(int t)
	{
		launchWhat = t;
	}
	
	public void setHeading(double h)
	{
		newHeading = SpaceMath.wrapHeading(h);
	}
	
	public void setScannerHeading(double h)
	{
		newScannerHeading = h;
	}
}
