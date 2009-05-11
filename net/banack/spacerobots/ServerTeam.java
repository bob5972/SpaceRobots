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

package net.banack.spacerobots;

import net.banack.spacerobots.util.Team;

public class ServerTeam extends Team
{
	public ServerTeam(int teamID, int teamIndex, String name)
	{
		super(teamID,name);
		myLiveFleets=0;
		myTeamIndex = teamIndex;
	}
	
	private int myLiveFleets;
	private int myTeamIndex;
	
	public final int getIndex()
	{
		return getTeamIndex();
	}
	
	public int getTeamIndex()
	{
		return myTeamIndex;
	}
	
	public boolean isAlive()
	{
		return myLiveFleets >0;
	}
	

	public int getLiveFleets()
	{
		return myLiveFleets;
	}
	
	public void setLiveFleets(int n)
	{
		myLiveFleets = n;
	}
	
	public void incrementLiveFleets(int n)
	{
		myLiveFleets += n;
	}
	
	public void decrementLiveFleets(int n)
	{
		myLiveFleets-=n;
	}
}

