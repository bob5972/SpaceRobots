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

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;

/**
 * Extension of Ship that tracks both the new ShipAction and the current status.
 * 
 * @author Michael Banack <bob5972@banack.net>
 * 
 */
public class BasicAIShip extends Ship
{
	private ShipAction myAction;
	
	public BasicAIShip(int id)
	{
		super(id, -1, ShipTypeDefinitions.TYPE_INVALID, null, -1, -1, -1, -1);
		myAction = new ShipAction(id);
	}
	
	public BasicAIShip(Ship s)
	{
		super(s);
		myAction = new ShipAction(s);
	}
	
	public ShipAction getAction()
	{
		// there is a threading issue here if someone decides to muck with the returned action...
		// but I don't really feel like copying it yet
		return myAction;
	}
	
	/** Clobbers this one with s. */
	public void update(Ship s)
	{
		if (getID() != -1 && getID() != s.getID())
			throw new IllegalArgumentException("Updating with a bad ID!");
		double desiredHeading = myAction.getHeading();
		super.update(s);
		myAction = new ShipAction(s);
		myAction.setHeading(desiredHeading);
	}
	
	public boolean willSpawn()
	{
		return myAction.isSpawn();
	}
	
	public boolean willMove()
	{
		return myAction.willMove();
	}
	
	/** The desired setting of willMove. */
	public final boolean getWillMove()
	{
		return willMove();
	}
	
	public void setWillMove(boolean b)
	{
		myAction.setWillMove(b);
	}
	
	/** The current setting of willMove. */
	public boolean curWillMove()
	{
		return super.willMove();
	}
	
	/** The new desired heading. */
	public double getHeading()
	{
		return myAction.getHeading();
	}
	
	/** The current heading. */
	public double curHeading()
	{
		return super.getHeading();
	}
	
	/** The projected heading next tick. */
	public double projHeading()
	{
		return SpaceMath.calculateAdjustedHeading(curHeading(), getHeading(), getMaxTurningRate());
	}
	
	/** The desired scanner heading. */
	public double getScannerHeading()
	{
		return myAction.getScannerHeading();
	}
	
	/** The current scanner heading. */
	public double curScannerHeading()
	{
		return super.getScannerHeading();
	}
	
	public int getLaunchWhat()
	{
		return myAction.getLaunchWhat();
	}
	
	public void setLaunchWhat(int t)
	{
		myAction.setLaunchWhat(t);
	}
	
	public void setHeading(double h)
	{
		myAction.setHeading(h);
	}
	
	public void setScannerHeading(double h)
	{
		myAction.setScannerHeading(h);
	}
}
