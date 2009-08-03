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

/** Stores all the pertinent information about a fleet. */
public class Fleet
{
	private int myFleetID;// internal battle ID
	private int myTeamID;
	private boolean isAlive;
	private String myFleetName;
	private String myAIName;
	private String myAIAuthor;
	private String myAIVersion;
	
	public Fleet(int fleetID, int teamID, String fleetName, String aiName, String aiAuthor, String aiVersion)
	{
		myFleetID = fleetID;
		myTeamID = teamID;
		isAlive = true;
		myFleetName = fleetName;
		myAIName = aiName;
		myAIVersion = aiVersion;
		myAIAuthor = aiAuthor;
	}
	
	public Fleet(int fleetID, int teamID, String fleetName, String aiName, String aiAuthor, String aiVersion,
	        boolean alive)
	{
		myFleetID = fleetID;
		myTeamID = teamID;
		isAlive = alive;
		myFleetName = fleetName;
		myAIName = aiName;
		myAIAuthor = aiAuthor;
		myAIVersion = aiVersion;
	}
	
	public String getFleetName()
	{
		return myFleetName;
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public void setAlive(boolean b)
	{
		isAlive = b;
	}
	
	public int getFleetID()
	{
		return myFleetID;
	}
	
	public final int getID()
	{
		return getFleetID();
	}
	
	public int getTeamID()
	{
		return myTeamID;
	}
	
	
	public String getAIName()
	{
		return myAIName;
	}
	
	public String getAIVersion()
	{
		return myAIVersion;
	}
	
	public String getAIAuthor()
	{
		return myAIAuthor;
	}
	
}
