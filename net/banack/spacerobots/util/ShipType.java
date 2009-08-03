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

package net.banack.spacerobots.util;

/** All of the constant information about a given type of ship. */
public class ShipType
{
	public final static int TYPE_INVALID = ShipTypeDefinitions.TYPE_INVALID;
	
	private String name;
	private int typeID;
	private int cost;
	private int maxLife;
	private double width;
	private double height;
	private double maxSpeed;
	private boolean canStop;
	private boolean hasAI;// does the Ai get to control it?
	private boolean isShip;// ship or projectile?
	private double maxTurningRate;
	private boolean canMoveScanner;
	private boolean hasScanner;
	private double scannerRadius;
	private double scannerAngleSpan;
	private int maxTickCount;
	private int stdLaunchDelay;// the standard launch delay after this ship fires
	private int extraLaunchCost;// the extra delay after launching a ship of this type
	
	public ShipType(String name, int id, int cost, int life, double width, double height, boolean canStop,
	        double maxTurningRate, double maxSpeed, boolean hasScanner, boolean canMoveScanner, double scannerRadius,
	        double scannerAngleSpan, boolean hasAI, boolean isShip, int maxTickCount, int launchDelay, int launchCost)
	{
		this.name = name;
		this.typeID = id;
		this.maxLife = life;
		this.width = width;
		this.height = height;
		this.cost = cost;
		this.stdLaunchDelay = launchDelay;
		this.extraLaunchCost = launchCost;
		this.canStop = canStop;
		this.hasAI = hasAI;
		this.isShip = isShip;
		this.maxSpeed = maxSpeed;
		this.maxTurningRate = maxTurningRate;
		this.canMoveScanner = canMoveScanner;
		this.maxTickCount = maxTickCount;
		this.hasScanner = hasScanner;
		this.scannerAngleSpan = scannerAngleSpan;
		this.scannerRadius = scannerRadius;
	}
	
	public boolean equals(Object rhs)
	{
		if (!(rhs instanceof ShipType))
			return false;
		if (rhs == this)
			return true;
		
		ShipType t = (ShipType) rhs;
		
		if (this.name.equals(t.name) && this.typeID == t.typeID && this.cost == t.cost
		        && this.stdLaunchDelay == t.stdLaunchDelay && this.extraLaunchCost == t.extraLaunchCost
		        && this.maxLife == t.maxLife && this.width == t.width && this.height == t.height
		        && this.maxSpeed == t.maxSpeed && this.canStop == t.canStop && this.hasAI == t.hasAI
		        && this.isShip == t.isShip && this.maxTurningRate == t.maxTurningRate
		        && this.canMoveScanner == t.canMoveScanner && this.hasScanner == t.hasScanner
		        && this.scannerRadius == t.scannerRadius && this.scannerAngleSpan == t.scannerAngleSpan
		        && this.maxTickCount == t.maxTickCount) {
			return true;
		}
		return false;
	}
	
	/** The base delay of this ship type to launch anything. */
	public int getStdLaunchDelay()
	{
		return stdLaunchDelay;
	}
	
	/** The extra delay a ship faces after launching a ship of <i>this</i> type. */
	public int getExtraLaunchCost()
	{
		return extraLaunchCost;
	}
	
	
	/**
	 * The maximum number of ticks before this ship type expires.
	 * <p>
	 * A maxTickCount of 0 means no expiry.
	 */
	public int getMaxTickCount()
	{
		return maxTickCount;
	}
	
	/** Returns true if the ship is capable of stopping. */
	public boolean getCanStop()
	{
		return canStop;
	}
	
	/** Returns true if the ship type gets to run an AI. */
	public boolean getAI()
	{
		return hasAI;
	}
	
	/** Returns the maximum speed of the ship type. */
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	/** Returns true iff this is a ship (as opposed to projectile ammo). */
	public boolean getIsShip()
	{
		return isShip;
	}
	
	/** Returns true iff this is a ship (as opposed to projectile ammo). */
	public final boolean isShip()
	{
		return getIsShip();
	}
	
	/** Returns true iff this is projectile ammo (as opposed to a ship). */
	public final boolean isAmmo()
	{
		return !isShip();
	}
	
	/** Returns the name of this type of ship. */
	public String getName()
	{
		return name;
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public int getMaxLife()
	{
		return maxLife;
	}
	
	public int getTypeID()
	{
		return typeID;
	}
	
	/** The typeID of this ship type. */
	public final int getID()
	{
		return getTypeID();
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public double getMaxTurningRate()
	{
		return maxTurningRate;
	}
	
	public boolean getCanMoveScanner()
	{
		return canMoveScanner;
	}
	
	public final boolean canMoveScanner()
	{
		return getCanMoveScanner();
	}
	
	public boolean hasScanner()
	{
		return hasScanner;
	}
	
	public double getScannerAngleSpan()
	{
		return scannerAngleSpan;
	}
	
	public double getScannerRadius()
	{
		return scannerRadius;
	}
}
