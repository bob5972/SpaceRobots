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

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.comm.ServerProtocolFactory;
import net.banack.spacerobots.comm.ServerAIProtocol;
import net.banack.spacerobots.fleets.*;

public class SpaceRobotsUI
{
	private static JFrame mainWindowFrame;
	private static JPanel mainWindowSouth;
	private static JButton runBattleButton;
	private static JPanel mainWindowEast;
	private static JPanel mainWindowEastRow1;
	private static JPanel mainWindowEastRow2;
	private static JPanel mainWindowEastRow3;
	
	private static JLabel widthLabel;
	private static JTextField widthTextField;
	private static JLabel heightLabel;
	private static JTextField heightTextField;
	private static JLabel startingCreditsLabel;
	private static JTextField startingCreditsField;
	private static JLabel creditsPerTurnLabel;
	private static JTextField creditsPerTurnField;
	
	
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
		

		mainWindowFrame = new JFrame("Space Robots");
		mainWindowFrame.getContentPane().setLayout(new BorderLayout());
		runBattleButton = new JButton("Battle!");
		runBattleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				final Random r;
				
				r = new Random();
				
				final AIFleet ai[] = {
				        new BattleCruiserFleet(r.nextLong()),
				        new Cache(r.nextLong()),
				        //new TaskFleet(r.nextLong()), new TaskFleet(r.nextLong()), new Mob(r.nextLong()),
				        new Mob(r.nextLong()),
				        // new Fortress(r.nextLong()),
				        new Beeline(r.nextLong()),
				};
				
				final int width;
				final int height;
				final int startingCredits;
				final int creditsPerTurn;
				
				try {
					width = Integer.parseInt(widthTextField.getText());
					height = Integer.parseInt(heightTextField.getText());
					startingCredits = Integer.parseInt(startingCreditsField.getText());
					creditsPerTurn = Integer.parseInt(creditsPerTurnField.getText());
				} catch (NumberFormatException exception) {
					return;
				}
				
				new Thread(new Runnable() {
					public void run()
					{
						runBattle(width, height, ai, startingCredits, creditsPerTurn, r.nextLong(), new GLDisplay());
					}
				}).start();
			}
		});
		

		mainWindowSouth = new JPanel();
		mainWindowEast = new JPanel();
		mainWindowEastRow1 = new JPanel();
		mainWindowEastRow2 = new JPanel();
		mainWindowEastRow3 = new JPanel();
		mainWindowFrame.add(mainWindowSouth, BorderLayout.SOUTH);
		mainWindowFrame.add(mainWindowEast, BorderLayout.EAST);
		mainWindowSouth.setLayout(new FlowLayout());
		mainWindowEast.setLayout(new GridLayout(3, 1));
		mainWindowEast.add(mainWindowEastRow1);
		mainWindowEast.add(mainWindowEastRow2);
		mainWindowEast.add(mainWindowEastRow3);
		mainWindowEastRow1.setLayout(new FlowLayout());
		mainWindowEastRow2.setLayout(new FlowLayout());
		mainWindowEastRow3.setLayout(new FlowLayout());
		
		mainWindowSouth.add(runBattleButton);
		mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		widthTextField = new JTextField("500");
		widthTextField.setColumns(4);
		widthLabel = new JLabel("Width");
		
		heightTextField = new JTextField("500");
		heightTextField.setColumns(4);
		heightLabel = new JLabel("Height");
		
		startingCreditsField = new JTextField("0");
		startingCreditsField.setColumns(4);
		startingCreditsLabel = new JLabel("Starting Credits");
		
		creditsPerTurnField = new JTextField("1");
		creditsPerTurnField.setColumns(4);
		creditsPerTurnLabel = new JLabel("Credits Per Turn");
		
		mainWindowEastRow1.add(widthLabel);
		mainWindowEastRow1.add(widthTextField);
		
		mainWindowEastRow1.add(heightLabel);
		mainWindowEastRow1.add(heightTextField);
		
		mainWindowEastRow2.add(startingCreditsLabel);
		mainWindowEastRow2.add(startingCreditsField);
		
		mainWindowEastRow3.add(creditsPerTurnLabel);
		mainWindowEastRow3.add(creditsPerTurnField);
		
		mainWindowFrame.pack();
		mainWindowFrame.setVisible(true);
		

	}
	
	public static void runBattle(int width, int height, AIFleet ai[], int startingCredits, int creditIncrement,
	        long randomSeed, Display display)
	{
		
		Battle b = new Battle(width, height);
		b.seedRandom(randomSeed);
		
		addAIFleets(b, ai, startingCredits, creditIncrement);
		
		// setup starting ships/locations
		// / (or at least give the specs, and let battle determine them randomly)
		// notify AI's about game setup
		Debug.info("Calling Battle.initialize()");
		try {
			b.initialize();
			display.initDisplay(b);
		} catch (java.io.IOException e) {
			Debug.crash(e, "Error initializing battle.");
		}
		
		// GAME LOOP
		Debug.info("Starting battle...");
		while (!b.isOver() && display.isVisible()) {
			try {
				b.runTick();
				display.updateDisplay(b);
			} catch (java.io.IOException e) {
				Debug.crash(e, "Error running tick!");
			}
		}
		
		Debug.info("Battle over!");
		// END GAME LOOP
		
		// cleanup
		try {
			b.cleanup();
			display.closeDisplay(b);
		} catch (java.io.IOException e) {
			Debug.crash(e, "Error during cleanup!");
		}
		Debug.info("Main Exiting");
	}
	
	private static void addAIFleets(Battle b, AIFleet[] ai, int startingCredits, int creditIncrement)
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
				b.addFleet("Fleet " + x, new FleetAI(aip), tID[x], startingCredits, creditIncrement);
			}
		} catch (java.io.IOException e) {
			Debug.error("Error initializing AI: IOException!");
			Debug.crash(e);
		}
	}
}
