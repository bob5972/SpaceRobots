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

package net.banack.spacerobots.comm;

import java.io.IOException;

import net.banack.spacerobots.ServerFleet;
import net.banack.spacerobots.ServerShip;
import net.banack.spacerobots.ServerTeam;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;

public interface ClientAIProtocol
{
	public void start();
	
}
