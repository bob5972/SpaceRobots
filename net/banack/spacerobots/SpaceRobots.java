package net.banack.spacerobots;


import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import net.banack.spacerobots.ai.ClientProtocolFactory;
import net.banack.spacerobots.ai.DummyFleet;
import net.banack.util.MethodNotImplementedException;


public class SpaceRobots
{
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	public static final int STARTING_CREDITS=0;
	public static final int CREDIT_INCREMENT=1;
	
	public static void main(String[] args)
	{
		Debug.enableDebug();
		Debug.STD_ERR_MESSAGES=true;
		Debug.setShowAIWarnings(true);
		
		//SETUP
		Display d = new ConsoleDisplay();
		
		//setup initial battle state
		Battle b = new Battle(DEFAULT_WIDTH,DEFAULT_HEIGHT);

		// load AI's
		FleetAI[] ai = new FleetAI[2];
		
		try{
			PipedInputStream sIn = new PipedInputStream();
			PipedOutputStream cOut = new PipedOutputStream(sIn);
			PipedInputStream cIn = new PipedInputStream();
			PipedOutputStream sOut = new PipedOutputStream(cIn);
			
			Thread background = new AIThread(new DummyFleet(),cIn,cOut);
			background.start();
			AIProtocol aip = ProtocolFactory.doHandshake(sIn,sOut);
			ai[0] = new FleetAI(aip);
			
			sIn = new PipedInputStream();
			cOut = new PipedOutputStream(sIn);
			cIn = new PipedInputStream();
			sOut = new PipedOutputStream(cIn);
			
			background = new AIThread(new DummyFleet(),cIn,cOut);
			background.start();
			ai[1] = new FleetAI(sIn,sOut);
		}
		catch(java.io.IOException e)
		{
			Debug.error("Error initializing AI!");
			Debug.crash(e);
		}
		   
		
		//setup teams
		int[] tID = b.createTeams(2);
		
		//setup fleets		
		b.addFleet("Fleet 1", ai[0], tID[0],STARTING_CREDITS,CREDIT_INCREMENT);
		b.addFleet("Fleet 2", ai[1], tID[1],STARTING_CREDITS,CREDIT_INCREMENT);
		
		//setup starting ships/locations
		/// (or at least give the specs, and let battle determine them randomly)
		// notify AI's about game setup
		b.initialize();
		
		//GAME LOOP
		while(!b.isOver())
		{
			try{
				b.runTick();
			}
			catch(java.io.IOException e)
			{
				Debug.error("Error running tick!");
				Debug.crash(e);
			}
			d.updateDisplay(b);
		}
		
		//END GAME LOOP
		
		// cleanup?
	}
}
