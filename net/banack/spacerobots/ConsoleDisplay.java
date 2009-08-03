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

import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.Fleet;

public class ConsoleDisplay implements Display
{
	public ConsoleDisplay()
	{
		
	}
	
	public void initDisplay(Battle b)
	{
		return;
	}
	
	public void closeDisplay(Battle b)
	{
		return;
	}
	
	public boolean isVisible()
	{
		return false;
	}
	
	public void updateDisplay(Battle b)
	{
		int cruiser, fighter, destroyer, total;
		cruiser = fighter = destroyer = total = 0;
		
		Iterator<ServerShip> i = b.shipIterator();
		while (i.hasNext()) {
			Ship s = (Ship) i.next();
			switch (s.getTypeID()) {
				case DefaultShipTypeDefinitions.CRUISER_ID:
					cruiser++;
					break;
				case DefaultShipTypeDefinitions.FIGHTER_ID:
					fighter++;
					break;
				case DefaultShipTypeDefinitions.DESTROYER_ID:
					destroyer++;
					break;
			}
			total++;
		}
		
		Iterator<ServerFleet> fi;
		
		if (b.getTick() % 100 == 0) {
			System.out.println("Tick " + b.getTick());
			System.out.println("Ships total=" + total + ", cruiser=" + cruiser + ", destroyer=" + destroyer
			        + ", fighter=" + fighter);
			fi = b.fleetIterator();
			while (i.hasNext()) {
				ServerFleet f = fi.next();
				System.out.println("Fleet " + f.getFleetName() + " ships=" + f.getNumShips() + " credits="
				        + f.getCredits());
			}
		}
		
	}
	
}
