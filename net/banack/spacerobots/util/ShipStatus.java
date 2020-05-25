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

import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;

public interface ShipStatus
{
	public int getID();
	
	public int getTypeID();
	
	public ShipType getType();
	
	public DPoint getPosition();
	
	public double getX();
	
	public double getY();
	
	public DDimension getDimension();
	
	public DQuad getLocation();
	
	public double getHeading();
	
	public double getMaxSpeed();
	
	public boolean getCanStop();
	
	public double getMaxTurningRate();
	
	public int getLife();
	
	public boolean isAlive();
	
	public boolean isDead();
	
	public int getMaxTickCount();
	
	public boolean getCanMoveScanner();
	
	public int getCost();
	
	public boolean isAmmo();
}
