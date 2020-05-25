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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.Debug;
import net.banack.spacerobots.ServerFleet;
import net.banack.spacerobots.ServerShip;
import net.banack.spacerobots.ServerTeam;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ContactList;

public class TextProtocolServer implements ServerAIProtocol
{
	private SpaceText sIn;
	private PrintWriter sOut;
	
	private int curLevel;
	
	public TextProtocolServer(Reader in, PrintWriter out)
	{
		sIn = new SpaceText(in);
		sOut = out;
		curLevel = 0;
	}
	
	private void send(String message)
	{
		send(message, curLevel);
	}
	
	// send the message at the given indentation level
	// (really just for debugging purposes, could set a flag to disable)
	private void send(String message, int indentation)
	{
		if (Debug.isDebug()) {
			String msg = "";
			for (int x = 0; x < indentation; x++)
				msg += "\t";
			Debug.comLog(">" + msg + message);
		}
		
		while (indentation-- > 0)
			sOut.print("\t");
		sOut.println(message);
		sOut.flush();
		
	}
	
	private String readLine() throws IOException
	{
		return sIn.readLine();
	}
	
	private String readWord() throws IOException
	{
		return sIn.readWord();
	}
	
	private String[] readWords() throws IOException
	{
		return sIn.readWords();
	}
	
	private ShipAction readAction() throws IOException
	{
		return sIn.readAction();
	}
	
	private String[] read(String cmd) throws IOException
	{
		String[] cur = sIn.readWords();
		if (!cur[0].equals(cmd))
			Debug.crash("Bad AI Response: expected " + cmd + ", received " + cur[0]);
		return cur;
	}
	
	// check if it has args tokens (including the command)
	private String[] read(String cmd, int args) throws IOException
	{
		String[] cur = read(cmd);
		if (cur.length != args) {
			String msg = "";
			for (int x = 0; x < cur.length; x++)
				msg += cur[x] + " ";
			Debug.error("Unable to retrieve requested number of tokens: Got " + (cur.length) + ", requested " + args
			        + "\nCmd=" + msg);
		}
		return cur;
	}
	
	// Returns {name, author,version}
	public String[] loadInfo() throws IOException
	{
		String[] oup = new String[3];
		StringBuffer name = new StringBuffer();
		StringBuffer author = new StringBuffer();
		StringBuffer version = new StringBuffer();
		String temp;
		

		send("REQUEST_INFO");
		
		read("BEGIN_INFO", 1);
		
		temp = readWord();
		while (!temp.equals("END_INFO")) {
			if (temp.equals("NAME")) {
				temp = readLine();
				name.append(temp);
				while (Character.isWhitespace(name.charAt(0)))
					name.deleteCharAt(0);
			} else if (temp.equals("AUTHOR")) {
				temp = readLine();
				author.append(temp);
				while (Character.isWhitespace(author.charAt(0)))
					author.deleteCharAt(0);
			} else if (temp.equals("VERSION")) {
				temp = readLine();
				version.append(temp);
				while (Character.isWhitespace(version.charAt(0)))
					version.deleteCharAt(0);
			} else
				net.banack.spacerobots.Debug.aiwarn("Unknown AI Response in BEGIN_INFO: " + temp);
			
			temp = readWord();
		}
		
		// clean up the hanging endl
		readLine();
		
		// >REQUEST_INFO
		// <BEGIN_INFO
		// <NAME Der Uber Fleet
		// <AUTHOR Michael Banack
		// <VERSION 1.0
		// <END_INFO
		
		oup[0] = name.toString();
		oup[1] = author.toString();
		oup[2] = version.toString();
		
		return oup;
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, ServerShip[] s, ServerTeam[] t,
	        ServerFleet[] f, double width, double height) throws IOException
	{
		curLevel = 0;
		send("BEGIN_BATTLE");
		curLevel++;
		send("BEGIN_INIT_BATTLE " + ((int) width) + " " + ((int) height));
		curLevel++;
		send("FLEET_ID " + fleetID);
		send("TEAM_ID " + teamID);
		send("STARTING_CREDITS " + startingCredits);
		
		send("BEGIN_STARTING_SHIPS " + s.length);
		curLevel++;
		for (int x = 0; x < s.length; x++) {
			writeShip(s[x]);
		}
		curLevel--;
		send("END_STARTING_SHIPS");
		
		send("BEGIN_TEAMS " + t.length);
		curLevel++;
		for (int x = 0; x < t.length; x++) {
			writeTeam(t[x]);
		}
		curLevel--;
		send("END_TEAMS");
		

		send("BEGIN_FLEETS " + f.length);
		curLevel++;
		for (int x = 0; x < f.length; x++) {
			writeFleet(f[x]);
		}
		curLevel--;
		send("END_FLEETS");
		
		send("END_INIT_BATTLE");
		curLevel--;
		

		read("BATTLE_READY_BEGIN", 1);
		
		// setup teams, starting positions, etc
		// >BEGIN_BATTLE
		// > BEGIN_INIT_BATTLE width height
		// > FLEET_ID 27
		// > TEAM_ID 2
		// > STARTING_CREDITS 1000
		// > BEGIN_STARTING_SHIPS
		// > SHIP iD type xPos yPos heading scannerHeading creationTick life deltaLife launchDelay
		// > END_STARTING_SHIPS
		//
		// > BEGIN_TEAMS
		// > TEAM teamID "Name"
		// > END_TEAMS
		//
		// > BEGIN_FLEETS
		// > FLEET fleetID teamID "Name" "AIName" "AIVersion" deadOrAlive winOrLose
		// > END_FLEETS
		// > END_INIT_BATTLE
		
		// let everyone initialize before starting, so require and acknowledgment
		// <BATTLE_READY_BEGIN
		
		// it would make more sense to use something like "ACK_INIT_BATTLE"
		// but its way cooler to go "BATTLE_READY"
		

		// the END_BATTLE would be after it's all over...
		// this way we can re-use sockets between matches
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa) throws IOException
	{
		// we could send these intermittently to tell fleets when people die...
		// >BATTLE_STATUS_UPDATE teamID deadOrAlive winOrLose
		send("BATTLE_STATUS_UPDATE " + teamID + " " + fleetID + " " + (doa ? 1 : 0));
	}
	
	public void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException
	{
		send("BEGIN_FLEET_STATUS");
		curLevel++;
		
		send("TICK " + tick);
		send("CREDITS " + credits);
		send("BEGIN_CONTACT_LIST " + c.size());
		curLevel++;
		
		// write contacts
		Iterator<Integer> i = c.enemyIterator();
		while (i.hasNext()) {
			Integer eID = (Integer) i.next();
			Contact ghost = c.getContact(eID);
			DPoint pos = ghost.getPosition();
			StringBuffer cmd = new StringBuffer();
			cmd.append("CONTACT ");
			cmd.append(ghost.getID());
			cmd.append(" ");
			cmd.append(ghost.getFleetID());
			cmd.append(" ");
			cmd.append(ghost.getTypeID());
			cmd.append(" ");
			cmd.append(((int) pos.getX()));
			cmd.append(" ");
			cmd.append(((int) pos.getY()));
			cmd.append(" ");
			cmd.append(((int) ghost.getHeading()));
			cmd.append(" ");
			cmd.append(ghost.getLife());
			cmd.append(" ");
			Set<Integer> spot = c.getSpotters(eID);
			cmd.append(spot.size());
			cmd.append(" ( ");
			
			Iterator<Integer> spoti = spot.iterator();
			while (spoti.hasNext()) {
				Integer sID = (Integer) spoti.next();
				cmd.append(sID);
				cmd.append(" ");
			}
			cmd.append(")");
			send(cmd.toString());
		}
		
		curLevel--;
		send("END_CONTACT_LIST");
		
		send("BEGIN_SHIPS " + numShips);
		curLevel++;
		
		// >BEGIN_FLEET_STATUS
		// > TICK 803
		// > CREDITS 100
		// > BEGIN_CONTACT_LIST 13
		// > CONTACT eID fleetID typeID x y heading numSpotters (sId sID sID)
		// > END_CONTACT_LIST
		// > BEGIN_SHIPS 25
	}
	
	public void writeShip(ServerShip s) throws IOException
	{
		// write a single ship
		// > SHIP iD type xPos yPos heading scannerHeading creationTick life deltaLife launchDelay
		
		send("SHIP " + SpaceText.toString(s));
	}
	
	public void endFleetStatusUpdate()
	{
		send("END_SHIPS");
		curLevel--;
		send("END_FLEET_STATUS");
		curLevel--;
		// > END_SHIPS
		// >END_FLEET_STATUS
	}
	
	private void writeTeam(ServerTeam t)
	{
		// >TEAM teamID "Name"
		send("TEAM " + t.getTeamID() + " \"" + t.getName() + "\"");
	}
	
	private void writeFleet(ServerFleet f) throws IOException
	{
		// >FLEET fleetID teamID "Name" "AIName" "AIVersion" isAlive winOrLose
		send("FLEET " + SpaceText.toString(f));
	}
	
	public ActionList readFleetActions() throws IOException
	{
		ActionList oup = new ActionList();
		int tick, numA;
		
		read("BEGIN_FLEET_ACTIONS", 1);
		tick = SpaceText.parseInt(read("TICK", 2)[1]);
		oup.setTick(tick);
		
		numA = SpaceText.parseInt(read("BEGIN_SHIP_ACTIONS", 2)[1]);
		
		if (numA == -1) {
			boolean done = false;
			String[] words;
			
			while (!done) {
				words = readWords();
				if (words.length >= 1 && words[0].equals("SHIP_ACTION"))
					oup.add(SpaceText.parseAction(words));
				else if (words.length >= 1 && words[0].equals("END_SHIP_ACTIONS"))
					done = true;
				else
					Debug.error("Bad Protocol reponse during fleet actions: " + SpaceText.toString(words));
			}
		} else {
			for (int x = 0; x < numA; x++) {
				oup.add(readAction());
			}
			read("END_SHIP_ACTIONS", 1);
		}
		

		read("END_FLEET_ACTIONS", 1);
		
		return oup;
		
		// <BEGIN_FLEET_ACTIONS
		// < TICK 803
		// < BEGIN_SHIP_ACTIONS
		// < SHIP_ACTION id willMove newHeading newScannerHeading launchWhat
		// < END_SHIP_ACTIONS
		// <END_FLEET_ACTIONS
	}
	
	public void endBattle(ServerFleet me, ServerTeam[] t, ServerFleet[] f) throws IOException
	{
		send("BEGIN_BATTLE_OUTCOME");
		curLevel++;
		send("YOU " + SpaceText.toString(me));
		
		send("BEGIN_TEAMS " + t.length);
		curLevel++;
		for (int x = 0; x < t.length; x++)
			writeTeam(t[x]);
		curLevel--;
		send("BEGIN_FLEETS " + f.length);
		curLevel++;
		for (int x = 0; x < f.length; x++)
			writeFleet(f[x]);
		curLevel--;
		send("END_FLEETS");
		curLevel--;
		send("END_BATTLE_OUTCOME");
		curLevel--;
		send("END_BATTLE");
		
		read("BATTLE_READY_END", 1);
		
		// >BEGIN_BATTLE_OUTCOME
		// >YOU fleetID teamID deadOrAlive winOrLose
		// > BEGIN_TEAMS
		// > TEAM teamID deadOrAlive winOrLose friendOrFoe "Name"
		// > END_TEAMS
		// > BEGIN_FLEETS
		// > FLEET fleetID teamID deadOrAlive winOrLose friendOrFoe "Name" "Version"
		// > END_FLEETS
		// >END_BATTLE_OUTCOME
		
		// >END_BATTLE
		
		// <BATTLE_READY_END
		// now we know the ai is finished fighting (in it's head?)
	}
}
