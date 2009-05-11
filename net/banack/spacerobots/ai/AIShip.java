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

import java.util.Iterator;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.SpaceMath;

/** Basic ship class for the AI's.
 * <p>Tracks current status, the new action for next tick, and includes some utility functions.
 * @author Michael Banack <bob5972@banack.net>
 *
 */
public class AIShip extends BasicAIShip
{
	public static final DefaultShipTypeDefinitions TYPE = new DefaultShipTypeDefinitions();

	public static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	public static final int MISSILE_ID = DefaultShipTypeDefinitions.MISSILE_ID;
	
	public static final ShipType CRUISER = TYPE.CRUISER;
	public static final ShipType DESTROYER = TYPE.DESTROYER;
	public static final ShipType FIGHTER = TYPE.FIGHTER;
	public static final ShipType ROCKET = TYPE.ROCKET;
	public static final ShipType MISSILE = TYPE.MISSILE;
	
	protected AIFleet myFleet;
	
	public AIShip(AIFleet f)
	{
		super(-1);
		myFleet = f;
	}
	
	public AIShip(Ship s)
	{
		super(s);
		myFleet = null;
	}
	
	public AIShip(Ship s, AIFleet f)
	{
		super(s);
		myFleet = f;
	}
	
	public void run()
	{
		return;
	}
	
	/** Returns true iff the ship is alive, has no firing delay, and the type is capable of launching anything. */
	public boolean canLaunch()
	{
		return isAlive() && isReadyToLaunch() && getTypeID() != ROCKET_ID && getTypeID() != MISSILE_ID;
	}
	
	/** Returns true iff the ship is capable of launching the specified type right now, and the fleet has enough credits.*/
	public boolean canLaunch(int type)
	{
		return canLaunch(TYPE.get(type));
	}
	
	/** Returns true iff the ship is capable of launching the specified type right now, and the fleet has enough credits.*/
	public boolean canLaunch(ShipType t)
	{
		return canLaunch() && myFleet.credits >= t.getCost();
	}
	
	/** Gets the raw (Euclidean) distance from this ship to p.*/
	public double getDistanceFrom(DPoint p)
	{
		DPoint loc = getPosition();
		return SpaceMath.getRawDistance(loc,wrap(p));
	}
	
	/** Plots an intercept course to target, and returns the new heading. */
	public double intercept(ShipStatus target)
	{
		double oup = SpaceMath.interceptHeading(this,target,myFleet.battleWidth,myFleet.battleHeight);
		setHeading(oup);
		return oup;
	}
	
	public void setHeading(DPoint p)
	{
		DPoint loc = getPosition();
		setHeading(SpaceMath.getAngle(loc, wrap(p)));
	}
	
	public void setScannerHeading(DPoint p)
	{
		DPoint loc = getPosition();
		setScannerHeading(SpaceMath.getAngle(loc,wrap(p)));
	}
	
	public void setScannerHeading(ShipStatus p)
	{
		setScannerHeading(p.getPosition());
	}
	
	/** Moves the scanner heading forward by one scannerAngleSpan. */
	public void advanceScannerHeading()
	{
		setScannerHeading(getScannerHeading()+getScannerAngleSpan());
	}
	
	/** Moves the scanner heading forward by h radians. */
	public void advanceScannerHeading(double h)
	{
		setScannerHeading(getScannerHeading()+h);
	}
	
	/** Wraps a point to within (width,height) of this ship. */
	public DPoint wrap(DPoint p)
	{
		DPoint loc = getPosition();
		return SpaceMath.wrap(p,loc, myFleet.battleWidth,myFleet.battleHeight);
	}
	
	/** Wraps a point to within (width,height) of this ship. */
	public DPoint wrap(double x, double y)
	{
		return wrap(new DPoint(x,y));
	}
	
	/** Sets this ship to launch the given type, and adjusts the fleets credits accordingly.
	 * <p> No check is made if the ship actually can or not. */
	public void launch(int type)
	{
		launch(TYPE.get(type));
	}
	
	/** Sets this ship to launch the given type, and adjusts the fleets credits accordingly.
	 * <p> No check is made if the ship actually can or not. */
	public void launch(ShipType t)
	{
		setLaunchWhat(t.getID());
		myFleet.credits-=t.getCost();
	}
	
	/** Sets this ship to launch a basic missle tracking the target, and adjusts the fleets credits accordingly.
	 * <p> No check is made if the ship actually can or not. */
	public void launchMissile(ShipStatus target)
	{
		launch(MISSILE);
		myFleet.queueSpawnAI(new BasicMissile(myFleet,target),this.getID());
	} 
}
