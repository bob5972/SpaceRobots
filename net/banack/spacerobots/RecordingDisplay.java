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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;

public class RecordingDisplay implements Display
{
	private OutputStream myRaw;
	private DataOutputStream sOut;
	private GZIPOutputStream myZip;
	private boolean initialized;
	
	public static final int BATTLE_ID = 0x01f2d945;//this is basically a random number
	public static final int SHIPACTION_ID = net.banack.spacerobots.comm.BinaryProtocolClient.SHIPACTION_ID;
	public static final int CONTACT_ID = net.banack.spacerobots.comm.BinaryProtocolClient.CONTACT_ID;
	public static final int SHIP_ID = net.banack.spacerobots.comm.BinaryProtocolClient.SHIP_ID;
	public static final int FLEET_ID = net.banack.spacerobots.comm.BinaryProtocolClient.FLEET_ID;
	public static final int TEAM_ID = net.banack.spacerobots.comm.BinaryProtocolClient.TEAM_ID;
	
	private TreeSet<ServerShip> myShips;
	private TreeSet<ServerFleet> myFleets;
	
	public RecordingDisplay(OutputStream out)
	{
		myRaw = out;
		initialized=false;
		
		myShips = new TreeSet<ServerShip>(new Comparator<Ship>() {
			public int compare(Ship lhs, Ship rhs)
			{
				if(lhs.getID() < rhs.getID())
					return -1;
				if(lhs.getID() > rhs.getID())
					return 1;
				return 0;
			}
		});
		
		myFleets = new TreeSet<ServerFleet>(new Comparator<Fleet>() {
			public int compare(Fleet lhs, Fleet rhs)
			{
				if(lhs.getID() < rhs.getID())
					return -1;
				if(lhs.getID() > rhs.getID())
					return 1;
				return 0;
			}
		});
	}
	
	public void initDisplay(Battle b) throws IOException
	{
		myZip = new GZIPOutputStream(myRaw);
		sOut  = new DataOutputStream(myZip);
		myRaw = null;//don't tempt fate
		initialized=true;
		
		writeBattleHeader(b);
		writeTeams(b);
		writeFleets(b);
		writeShips(b);
	}
	
	public void closeDisplay(Battle b) throws IOException
	{
		sOut.flush();
		myZip.finish();
		
		//we're done here
		myZip = null;
		sOut=null;
	}
	
	public void updateDisplay(Battle b) throws IOException
	{
		sOut.writeInt(b.getTick());
		writeBreifFleets(b);
		writeShips(b);
	}
	
	public void writeBattleHeader(Battle b) throws IOException
	{
		sOut.writeInt(BATTLE_ID);
		sOut.writeDouble(b.getWidth());
		sOut.writeDouble(b.getHeight());		
	}
	
	
	
	private void writeTeams(Battle b) throws IOException
	{
		sOut.writeInt(b.getNumTeams());
		Iterator<ServerTeam> i = b.teamIterator();
		while(i.hasNext())
		{
			ServerTeam t = i.next();
			writeTeam(t);
		}		
	}
	
	private void writeFleets(Battle b) throws IOException
	{
		sOut.writeInt(b.getNumFleets());
		Iterator<ServerFleet> i = b.fleetIterator();
		while(i.hasNext())
		{
			myFleets.add(i.next());
		}
		
		i = myFleets.iterator();
		while(i.hasNext())
		{
			ServerFleet f = i.next();
			writeFleet(f);
		}
	}
	
	
	
	private void writeShips(Battle b) throws IOException
	{
		sOut.writeInt(b.getNumShipsBorn());
		Iterator<ServerShip> i = b.birthIterator();
		while(i.hasNext())
		{
			ServerShip s = i.next();
			myShips.add(s);
			writeFullShip(s);
		}
		
		sOut.writeInt(b.getNumShipsDied());
		i = b.deathIterator();
		while(i.hasNext())
		{
			ServerShip s = i.next();
			myShips.remove(s);
			writeFullShip(s);
		}
		
		sOut.writeInt(myShips.size());
		i = myShips.iterator();
		while(i.hasNext())
		{
			ServerShip s = i.next();
			writeBriefShip(s);
		}
	}
	
	private void writeTeam(ServerTeam t) throws IOException
	{
		sOut.writeInt(TEAM_ID);
		sOut.writeInt(t.getID());
		sOut.writeInt(t.getName().length());
		sOut.writeChars(t.getName());
	}
	
	private void writeBreifFleets(Battle b) throws IOException
	{
		Iterator<ServerFleet> i = myFleets.iterator();
		while(i.hasNext())
		{
			ServerFleet f = i.next();
			sOut.writeInt(f.getCredits());
		}
		
	}
	
	private void writeFleet(ServerFleet f) throws IOException
	{
		sOut.writeInt(FLEET_ID);
		sOut.writeInt(f.getID());
		sOut.writeInt(f.getTeamID());
		
		if(f.getName()==null)
		{
			sOut.writeInt(0);
		}
		else
		{
			Debug.verbose("Server fleet length = "+f.getName().length()+" name = "+f.getName());
			sOut.writeInt(f.getName().length());
			sOut.writeChars(f.getName());
		}
		if(f.getAIName() == null)
		{
			sOut.writeInt(0);
		}
		else
		{
			sOut.writeInt(f.getAIName().length());
			sOut.writeChars(f.getAIName());
		}
		if(f.getAIAuthor() == null)
		{
			sOut.writeInt(0);
		}
		else
		{
			sOut.writeInt(f.getAIAuthor().length());
			sOut.writeChars(f.getAIAuthor());
		}
		if(f.getAIVersion() == null)
		{
			sOut.writeInt(0);
		}
		else
		{
			sOut.writeInt(f.getAIVersion().length());
			sOut.writeChars(f.getAIVersion());
		}
		sOut.writeBoolean(f.isAlive());
		sOut.flush();
	}
	
	public void writeFullShip(ServerShip s) throws IOException
	{
		sOut.writeInt(SHIP_ID);
		
		sOut.writeInt(s.getID());
		sOut.writeInt(s.getParentID());
		sOut.writeInt(s.getTypeID());
		sOut.writeDouble(s.getXPos());
		sOut.writeDouble(s.getYPos());
		sOut.writeDouble(s.getHeading());
		sOut.writeDouble(s.getScannerHeading());
		sOut.writeInt(s.getCreationTick());
		sOut.writeInt(s.getLife());
		sOut.writeInt(s.getDeltaLife());
		sOut.writeInt(s.getFiringDelay());
	}
	
	public void writeBriefShip(ServerShip s) throws IOException
	{
		sOut.writeDouble(s.getXPos());
		sOut.writeDouble(s.getYPos());
		sOut.writeDouble(s.getHeading());
		if(s.getType().canMoveScanner())
			sOut.writeDouble(s.getScannerHeading());
		sOut.writeInt(s.getDeltaLife());
		sOut.writeInt(s.getFiringDelay());
	}
	
	
}
