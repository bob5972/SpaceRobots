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

package net.banack.spacerobots;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.comm.ServerProtocolFactory;
import net.banack.spacerobots.comm.ServerAIProtocol;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;

public class FleetAI
{
	private ServerAIProtocol myCom;
	
	private String myAuthor;
	private String myVersion;
	private String myName;
	
	// must loadInfo prior to using
	public FleetAI(ServerAIProtocol p)
	{
		myCom = p;
	}
	
	// must loadInfo prior to using
	public FleetAI(InputStream inp, OutputStream oup)
	{
		myCom = ServerProtocolFactory.doHandshake(inp, oup);
	}
	
	// returns an array of the form {name, author, version}
	public String[] loadInfo() throws IOException
	{
		String[] s = myCom.loadInfo();
		myName = s[0];
		myAuthor = s[1];
		myVersion = s[2];
		return s;
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, ServerShip[] s, ServerTeam[] t,
	        ServerFleet[] f, double width, double height) throws IOException
	{
		myCom.initBattle(fleetID, teamID, startingCredits, s, t, f, width, height);
	}
	
	public void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException
	{
		myCom.beginFleetStatusUpdate(tick, credits, c, numShips);
	}
	
	public void writeShip(ServerShip s) throws IOException
	{
		myCom.writeShip(s);
	}
	
	public void endFleetStatusUpdate() throws IOException
	{
		myCom.endFleetStatusUpdate();
	}
	
	public ActionList readFleetActions() throws IOException
	{
		return myCom.readFleetActions();
	}
	
	public void endBattle(ServerFleet me, ServerTeam[] t, ServerFleet[] f) throws IOException
	{
		myCom.endBattle(me, t, f);
	}
	
	public String getAuthor()
	{
		if (myAuthor == null)
			return "";
		return myAuthor;
	}
	
	public String getName()
	{
		if (myName == null)
			return "";
		else
			return myName;
	}
	
	public String getVersion()
	{
		if (myVersion == null)
			return "";
		return myVersion;
	}
}
