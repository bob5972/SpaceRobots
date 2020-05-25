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

package net.banack.spacerobots.comm;

import java.io.IOException;

import net.banack.spacerobots.ServerFleet;
import net.banack.spacerobots.ServerShip;
import net.banack.spacerobots.ServerTeam;
import net.banack.spacerobots.util.ActionList;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ContactList;

public interface ServerAIProtocol
{
	// Returns {name, author,version}
	String[] loadInfo() throws IOException;
	
	void initBattle(int fleetID, int teamID, int startingCredits, ServerShip[] s, ServerTeam[] t, ServerFleet[] f,
	        double width, double height) throws IOException;
	
	void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException;
	
	void writeShip(ServerShip s) throws IOException;
	
	void endFleetStatusUpdate() throws IOException;
	
	ActionList readFleetActions() throws IOException;
	
	void endBattle(ServerFleet me, ServerTeam[] t, ServerFleet[] f) throws IOException;
}
