package net.banack.spacerobots;


import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import net.banack.spacerobots.ai.ClientProtocolFactory;
import net.banack.spacerobots.ai.DummyFleet;
import net.banack.spacerobots.ai.SimpleFleet;
import net.banack.util.MethodNotImplementedException;


public class SpaceRobots
{
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	public static final int STARTING_CREDITS=0;
	public static final int CREDIT_INCREMENT=20;
	
	public static void main(String[] args)
	{
		Debug.enableDebug();
		Debug.STD_ERR_MESSAGES=true;
		Debug.STD_ERR_MESSAGES_INFO=true;
		Debug.STD_ERR_MESSAGES_WARN=true;
		Debug.STD_ERR_MESSAGES_ERROR=true;
		Debug.setShowAIWarnings(true);
		Debug.setShowComLog(false);
		
		//SETUP
		Debug.info("Initializing Display...");
		Display d = new GLDisplay();
		
		//setup initial battle state
		Debug.info("Initializing Battle...");
		Battle b = new Battle(DEFAULT_WIDTH,DEFAULT_HEIGHT);

		// load AI's
		Debug.info("Initializing AI's...");
		FleetAI[] ai = new FleetAI[2];
		
		try{
			Debug.info("Initializing pipe set #1...");
			PipedInputStream sIn = new PipedInputStream();
			PipedOutputStream cOut = new PipedOutputStream(sIn);
			PipedInputStream cIn = new PipedInputStream();
			PipedOutputStream sOut = new PipedOutputStream(cIn);
			
			Debug.info("Initializing background thread #1");
			Thread background = new AIThread(new DummyFleet(),cIn,cOut);
			Debug.info("Starting background thread #1");
			background.start();
			Debug.info("Handshaking...");
			AIProtocol aip = ProtocolFactory.doHandshake(sIn,sOut);
			ai[0] = new FleetAI(aip);
			
			Debug.info("Initializing pipe set #2...");
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
			Debug.error("Error initializing AI: IOException!");
			Debug.crash(e);
		}
//		catch(InterruptedException e)
//		{
//			Debug.crash(e,"Error Initializing AI: Interrupted!");
//		}
		   
		
		//setup teams
		Debug.info("Initializing teams...");
		int[] tID = b.createTeams(2);
		
		//setup fleets		
		Debug.info("Initializing fleets...");
		b.addFleet("Fleet 1", ai[0], tID[0],STARTING_CREDITS,CREDIT_INCREMENT);
		b.addFleet("Fleet 2", ai[1], tID[1],STARTING_CREDITS,CREDIT_INCREMENT);
		
		//setup starting ships/locations
		/// (or at least give the specs, and let battle determine them randomly)
		// notify AI's about game setup
		Debug.info("Calling Battle.initialize()");
		try {
			b.initialize();
		}
		catch(java.io.IOException e)
		{
			Debug.crash(e,"Error initializing battle.");
		}
		
		//GAME LOOP
		Debug.info("Starting battle...");
		while(!b.isOver())
		{
			try{
				b.runTick();
			}
			catch(java.io.IOException e)
			{
				Debug.crash(e,"Error running tick!");
			}
			if(b.getTick() % 10 == 0)
			{
				try{ 
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					Debug.crash("interrupted!");
				}
				d.updateDisplay(b);
				System.out.println("Frame!");
			}
		}
		
		Debug.info("Battle over!");
		//END GAME LOOP
		
		// cleanup?
	}
}
