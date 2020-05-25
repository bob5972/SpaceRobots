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

import java.util.Iterator;

/** The standard collection of ShipTypes. */
public class DefaultShipTypeDefinitions implements ShipTypeDefinitions
{
	public final static int ROCKET_ID = 1;
	public final static int MISSILE_ID = 2;
	public final static int FIGHTER_ID = 3;
	public final static int DESTROYER_ID = 4;
	public final static int CRUISER_ID = 5;
	
	// public ShipType(String name, int id,
	// int cost, int life, double width, double height,
	// boolean canStop, double maxTurningRate, double maxSpeed,
	// boolean hasScanner, boolean canMoveScanner, double scannerRadius, double scannerAngleSpan,
	// boolean hasAI, boolean isShip, int maxTickCount,
	// int launchDelay, int launchCost)
	
	public final static ShipType ROCKET = new ShipType("Rocket", ROCKET_ID, 5, 1, 2, 2, false, 0, 1.5, false, false, 0,
	        0, false, false, 20, -1, 0);
	
	public final static ShipType MISSILE = new ShipType("Missile", MISSILE_ID, 10, 1, 2, 2, true, 1, 1.5, false, false,
	        0, 0, true, false, 40, -1, 0);
	
	public final static ShipType FIGHTER = new ShipType("Fighter", FIGHTER_ID, 100, 1, 5, 5, false, 0.1, 0.5, true,
	        false, 20, 0.7, true, true, 0, 10, 10);
	
	public final static ShipType DESTROYER = new ShipType("Destroyer", DESTROYER_ID, 400, 5, 10, 10, true, 0.05, 0.35,
	        true, true, 30, 0.6, true, true, 0, 10, 20);
	
	public final static ShipType CRUISER = new ShipType("Cruiser", CRUISER_ID, 1000, 10, 30, 20, true, 0.01, 0.3, true,
	        true, 60, 0.7, true, true, 0, 10, 30);
	
	private final static ShipType[] myTypes = { ROCKET, MISSILE, FIGHTER, DESTROYER, CRUISER };
	
	public static ShipType getShipType(int typeID)
	{
		switch (typeID) {
			case ROCKET_ID:
				return ROCKET;
			case MISSILE_ID:
				return MISSILE;
			case FIGHTER_ID:
				return FIGHTER;
			case DESTROYER_ID:
				return DESTROYER;
			case CRUISER_ID:
				return CRUISER;
		}
		return null;
	}
	
	
	public ShipType get(int typeID)
	{
		return getShipType(typeID);
	}
	
	
	public Iterator<ShipType> getShipTypeIterator()
	{
		return new Iterator<ShipType>() {
			private int x = 0;
			
			public boolean hasNext()
			{
				return x < myTypes.length;
			}
			
			public ShipType next()
			{
				return myTypes[x++];
			}
			
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public ShipType[] getShipTypes()
	{
		return (ShipType[]) myTypes.clone();
	}
	
}
