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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Iterator;
import java.util.Random;

import net.banack.geometry.DArc;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.comm.ClientProtocolFactory;
import net.banack.spacerobots.comm.ServerProtocolFactory;
import net.banack.spacerobots.comm.ServerAIProtocol;
import net.banack.spacerobots.fleets.*;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;
import net.banack.util.MethodNotImplementedException;


public class SpaceRobots
{
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	public static final int STARTING_CREDITS = 0;
	public static final int CREDIT_INCREMENT = 1;
	public static long RANDOM_SEED = 0;// 0 for random
	
	public static final String RECORD_FILE = "battle.srb";
	public static boolean RECORD_BATTLE = false;
	public static boolean USE_CONSOLE_DISPLAY = true;
	
	public static void main(String[] args)
	{
		// Debug INIT
		Debug.enableDebug();
		Debug.setMessages(true);
		Debug.STD_ERR_MESSAGES = true;
		Debug.STD_ERR_MESSAGES_VERBOSE = false;
		Debug.STD_ERR_MESSAGES_INFO = true;
		Debug.STD_ERR_MESSAGES_WARN = true;
		Debug.STD_ERR_MESSAGES_ERROR = true;
		Debug.setShowAIWarnings(true);
		Debug.setShowComLog(false);// only works for TextProtocol
		Debug.setSlowGraphics(false);
		
		Debug.info("SpaceRobots version 0.9");
		
		Random r;
		OutputStream recordFile = null;
		
		if (RANDOM_SEED == 0) {
			r = new Random();
			RANDOM_SEED = r.nextLong();
		} else {
			r = new Random(RANDOM_SEED);
		}
		
		Debug.info("Random Seed = " + RANDOM_SEED);
		
		// SETUP
		Debug.info("Initializing Display...");
		Display d = (USE_CONSOLE_DISPLAY ? new ConsoleDisplay() : new GLDisplay());
		
		if (RECORD_BATTLE) {
			try {
				recordFile = new FileOutputStream(new File(RECORD_FILE));
				d = new JoinDisplay(d, new RecordingDisplay(recordFile));
			} catch (FileNotFoundException e1) {
				Debug.crash(e1, "Unable to initialize recordfile.");
			}
		}
		
		// setup initial battle state
		Debug.info("Initializing Battle...");
		Battle b = new Battle(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		b.seedRandom(RANDOM_SEED);
		
		// load AI's
		Debug.info("Initializing AI's...");
		AIFleet ai[] = { new BattleCruiserFleet(r.nextLong()), new Cache(r.nextLong()), new Mob(r.nextLong()),
		        new Fortress(r.nextLong()), };
		addAIFleets(b, ai);
		
		// setup starting ships/locations
		// / (or at least give the specs, and let battle determine them randomly)
		// notify AI's about game setup
		Debug.info("Calling Battle.initialize()");
		try {
			b.initialize();
			d.initDisplay(b);
		} catch (java.io.IOException e) {
			Debug.crash(e, "Error initializing battle.");
		}
		
		// GAME LOOP
		Debug.info("Starting battle...");
		while (!b.isOver()) {
			try {
				b.runTick();
				d.updateDisplay(b);
			} catch (java.io.IOException e) {
				Debug.crash(e, "Error running tick!");
			}
		}
		
		Debug.info("Battle over!");
		// END GAME LOOP
		
		Iterator<ServerTeam> it = b.teamIterator();
		while (it.hasNext()) {
			ServerTeam t = it.next();
			if (t.isAlive()) {
				System.out.println("The winner is: " + t.getName());
				Iterator<ServerFleet> itf = b.fleetIterator();
				while (itf.hasNext()) {
					ServerFleet f = itf.next();
					if (f.getTeam() == t)
						System.out.print("               " + f.getName() + ": " + f.getAIName() + " v "
						        + f.getAIVersion() + " by " + f.getAIAuthor());
				}
				System.out.println();
			}
		}
		
		// cleanup
		try {
			b.cleanup();
			d.closeDisplay(b);
			
			if (RECORD_BATTLE && recordFile != null)
				recordFile.close();
		} catch (java.io.IOException e) {
			Debug.crash(e, "Error during cleanup!");
		}
		
		Debug.info("Main Exiting");
	}
	
	private static void addAIFleets(Battle b, AIFleet[] ai)
	{
		Debug.info("Initializing teams...");
		int[] tID = b.createTeams(ai.length);
		
		try {
			for (int x = 0; x < ai.length; x++) {
				
				Debug.info("Initializing pipe set #" + x + "...");
				PipedInputStream sIn = new PipedInputStream();
				PipedOutputStream cOut = new PipedOutputStream(sIn);
				PipedInputStream cIn = new PipedInputStream();
				PipedOutputStream sOut = new PipedOutputStream(cIn);
				
				Debug.info("Initializing background thread #" + x);
				Thread background = new AIThread(ai[x], cIn, cOut);
				Debug.info("Starting background thread #" + x);
				background.start();
				Debug.info("Handshaking...");
				ServerAIProtocol aip = ServerProtocolFactory.doHandshake(sIn, sOut);
				b.addFleet("Fleet " + x, new FleetAI(aip), tID[x], STARTING_CREDITS, CREDIT_INCREMENT);
			}
		} catch (java.io.IOException e) {
			Debug.error("Error initializing AI: IOException!");
			Debug.crash(e);
		}
	}
}
