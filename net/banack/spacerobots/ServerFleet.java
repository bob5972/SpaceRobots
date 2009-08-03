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

import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.Fleet;

public class ServerFleet extends Fleet
{
	// Internal Representation of an entire Fleet
	
	private FleetAI myAI;
	private int myCredits;
	private int myCreditIncrement;
	private int myNextCreditBonus;
	private int myNumShips;
	private int myFleetIndex;
	private ServerTeam myTeam;
	private FleetStats myStats;
	
	
	public ServerFleet(int fleetID, int fleetIndex, int teamID, ServerTeam t, FleetAI ai)
	{
		super(fleetID, teamID, "Fleet " + fleetID, null, null, null);
		myFleetIndex = fleetIndex;
		myAI = ai;
		setAlive(true);
		myTeam = t;
		myStats = new FleetStats();
		myNextCreditBonus = 0;
	}
	
	public ServerFleet(String name, int fleetID, int fleetIndex, int teamID, ServerTeam t, FleetAI ai)
	{
		super(fleetID, teamID, name, ai.getName(), ai.getAuthor(), ai.getVersion());
		myFleetIndex = fleetIndex;
		myAI = ai;
		setAlive(true);
		myTeam = t;
		myStats = new FleetStats();
		myNextCreditBonus = 0;
	}
	
	
	public String getAIName()
	{
		return myAI.getName();
	}
	
	public String getAIVersion()
	{
		return myAI.getVersion();
	}
	
	public String getAIAuthor()
	{
		return myAI.getAuthor();
	}
	
	
	public ServerTeam getTeam()
	{
		return myTeam;
	}
	
	public final int getIndex()
	{
		return getFleetIndex();
	}
	
	public int getFleetIndex()
	{
		return myFleetIndex;
	}
	
	public final String getName()
	{
		return getFleetName();
	}
	
	
	public int getCreditIncrement()
	{
		return myCreditIncrement;
	}
	
	public void setCreditIncrement(int c)
	{
		myCreditIncrement = c;
	}
	
	public int getCredits()
	{
		return myCredits;
	}
	
	public void setCredits(int c)
	{
		myCredits = c;
	}
	
	public void incrementCredits(int c)
	{
		myCredits += c;
	}
	
	public void decrementCredits(int c)
	{
		incrementCredits(-c);
	}
	
	public FleetAI getAI()
	{
		return myAI;
	}
	
	public int getNumShips()
	{
		return myNumShips;
	}
	
	public void setNumShips(int n)
	{
		myNumShips = n;
	}
	
	public void addKill(ServerShip dead)
	{
		myStats.add(dead);
	}
	
	public FleetStats getStats()
	{
		return myStats;
	}
	
	public void addBonus(int d)
	{
		myNextCreditBonus += d;
	}
	
	public int getBonus()
	{
		return myNextCreditBonus;
	}
	
	public void resetBonus()
	{
		myNextCreditBonus = 0;
	}
}
