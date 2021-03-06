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

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;

/** Stores all pertinent information about a ship. */
public class Ship implements ShipStatus
{
	private int myID;
	private DPoint myPosition;
	private int myLife;
	private int myCreationTick;
	private int myDeltaLife;
	private double myHeading;
	private double myScannerHeading;
	private int myTypeID;
	private ShipType myType;
	private boolean willMove;
	private int myLaunchDelay;// number of ticks until the ship can fire again
	private int myParentID;// ID of the ship that spawned it
	
	public Ship(int id, int parentID, int type, ShipType t, DPoint pos, double heading, double scannerH, int tick,
	        int life, int deltalife, int firingDelay)
	{
		myID = id;
		myParentID = parentID;
		myTypeID = type;
		myPosition = pos;
		myLife = life;
		willMove = true;
		myScannerHeading = scannerH;
		myHeading = heading;
		myType = t;
		myCreationTick = tick;
		myDeltaLife = deltalife;
		myLaunchDelay = firingDelay;
	}
	
	public Ship(Ship s)
	{
		this(s.myID, s.myParentID, s.myTypeID, s.myType, s.myPosition, s.myHeading, s.myScannerHeading,
		        s.myCreationTick, s.myLife, s.myDeltaLife, s.myLaunchDelay);
		this.willMove = s.willMove;
	}
	
	public Ship(int id, int parentID, int type, ShipType t, double x, double y, double heading, double scannerH,
	        int tick, int life, int deltalife, int firingDelay)
	{
		this(id, parentID, type, t, new DPoint(x, y), heading, scannerH, tick, life, deltalife, firingDelay);
	}
	
	public Ship(int id, int parentID, int type, ShipType t, double x, double y, int tick, int life)
	{
		this(id, parentID, type, t, x, y, 0, 0, tick, life, 0, 0);
	}
	
	/** Clobbers this ship with the contents of s. */
	public void update(Ship s)
	{
		myID = s.myID;
		myParentID = s.myParentID;
		myTypeID = s.myTypeID;
		myPosition = s.myPosition;
		myLife = s.myLife;
		willMove = s.willMove;
		myScannerHeading = s.myScannerHeading;
		myHeading = s.myHeading;
		myType = s.myType;
		myCreationTick = s.myCreationTick;
		myDeltaLife = s.myDeltaLife;
		myLaunchDelay = s.myLaunchDelay;
	}
	
	/** Returns the ID of the ship that spawned this ship. */
	public int getParentID()
	{
		return myParentID;
	}
	
	/** Number of ticks until the ship can fire again. */
	public int getLaunchDelay()
	{
		return myLaunchDelay;
	}
	
	public final int getFiringDelay()
	{
		return getLaunchDelay();
	}
	
	public void setLaunchDelay(int d)
	{
		myLaunchDelay = d;
	}
	
	public boolean isReadyToLaunch()
	{
		return myLaunchDelay <= 0;
	}
	
	public final boolean readyToLaunch()
	{
		return isReadyToLaunch();
	}
	
	public void decrementLife(int d)
	{
		myDeltaLife += d;
		myLife -= d;
	}
	
	public void setLife(int L)
	{
		myDeltaLife += myLife - L;
		myLife = L;
	}
	
	public int getCost()
	{
		return myType.getCost();
	}
	
	public void reset()
	{
		if (myLaunchDelay > 0)
			myLaunchDelay--;
		myDeltaLife = 0;
	}
	
	public int getCreationTick()
	{
		return myCreationTick;
	}
	
	public boolean willMove()
	{
		return willMove;
	}
	
	public void setWillMove(boolean willMove)
	{
		this.willMove = willMove;
	}
	
	/** The change in life from last tick. */
	public int getDeltaLife()
	{
		return myDeltaLife;
	}
	
	
	// Status Functions
	public double getX()
	{
		return myPosition.getX();
	}
	
	public final double getXPos()
	{
		return getX();
	}
	
	public double getY()
	{
		return myPosition.getY();
	}
	
	public final double getYPos()
	{
		return getY();
	}
	
	public double getScannerHeading()
	{
		return myScannerHeading;
	}
	
	public void setScannerHeading(double h)
	{
		myScannerHeading = h;
	}
	
	public double getHeading()
	{
		return myHeading;
	}
	
	public void setHeading(double h)
	{
		myHeading = SpaceMath.wrapHeading(h);
	}
	
	public int getLife()
	{
		return myLife;
	}
	
	public boolean isAlive()
	{
		return getLife() > 0;
	}
	
	public final boolean isDead()
	{
		return !isAlive();
	}
	
	public int getTypeID()
	{
		return myTypeID;
	}
	
	public ShipType getType()
	{
		return myType;
	}
	
	public double getMaxSpeed()
	{
		return myType.getMaxSpeed();
	}
	
	public boolean getCanStop()
	{
		return myType.getCanStop();
	}
	
	public double getMaxTurningRate()
	{
		return myType.getMaxTurningRate();
	}
	
	public int getMaxTickCount()
	{
		return myType.getMaxTickCount();
	}
	
	public boolean getCanMoveScanner()
	{
		return myType.getCanMoveScanner();
	}
	
	public void setX(double x)
	{
		myPosition = new DPoint(x, myPosition.getY());
	}
	
	public void setY(double y)
	{
		myPosition = new DPoint(myPosition.getX(), y);
	}
	
	public void setPosition(DPoint p)
	{
		myPosition = p;
	}
	
	public void addPosition(DPoint offset)
	{
		myPosition = myPosition.add(offset);
	}
	
	public DPoint getPosition()
	{
		return myPosition;
	}
	
	
	public final int getID()
	{
		return getShipID();
	}
	
	public int getShipID()
	{
		return myID;
	}
	
	public DDimension getDimension()
	{
		return new DDimension(myType.getWidth(), myType.getHeight());
	}
	
	/** The perhaps poorly named way to get the rectangle of the ships location. */
	public DQuad getLocation()
	{
		return SpaceMath.getDQuad(myPosition, myType.getWidth(), myType.getHeight(), myHeading);
	}
	
	public boolean canMoveScanner()
	{
		return myType.canMoveScanner();
	}
	
	public DArc getScannerArc()
	{
		DArc oup = new DArc(myPosition, myType.getScannerRadius(), myScannerHeading, myType.getScannerAngleSpan());
		oup = oup.rotate(-myType.getScannerAngleSpan() / 2);
		return oup;
	}
	
	public double getScannerRadius()
	{
		return myType.getScannerRadius();
	}
	
	public double getScannerAngleSpan()
	{
		return myType.getScannerAngleSpan();
	}
	
	public String toString()
	{
		return SpaceText.toString(this);
	}
	
	public boolean isAmmo()
	{
		return myType.isAmmo();
	}
}
