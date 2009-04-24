package net.banack.spacerobots;


import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Iterator;
import java.util.Random;

import net.banack.geometry.DArc;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.ai.ClientProtocolFactory;
import net.banack.spacerobots.fleets.Cache;
import net.banack.spacerobots.fleets.DummyFleet;
import net.banack.spacerobots.fleets.SchoolOfFish;
import net.banack.spacerobots.fleets.SimpleFleet;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;
import net.banack.util.MethodNotImplementedException;


public class SpaceRobots
{
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	public static final int STARTING_CREDITS=0;
	public static final int CREDIT_INCREMENT=1;
	public static long RANDOM_SEED=0;//0 for random
	
	public static void main(String[] args)
	{
		//Debug INIT
		Debug.enableDebug();
		Debug.setMessages(true);
		Debug.STD_ERR_MESSAGES=true;
		Debug.STD_ERR_MESSAGES_VERBOSE=false;
		Debug.STD_ERR_MESSAGES_INFO=true;
		Debug.STD_ERR_MESSAGES_WARN=true;
		Debug.STD_ERR_MESSAGES_ERROR=true;		
		Debug.setShowAIWarnings(true);
		Debug.setShowComLog(false);
		Debug.setSlowGraphics(false);
		final boolean USE_CONSOLE_DISPLAY = false;
		
		if(RANDOM_SEED ==0)
		{
			Random r = new Random();
			RANDOM_SEED=r.nextLong();
		}
		Debug.info("Random Seed = "+RANDOM_SEED);
		
		//SETUP
		Debug.info("Initializing Display...");
		Display d = (USE_CONSOLE_DISPLAY? new ConsoleDisplay(): new GLDisplay());
		
		//setup initial battle state
		Debug.info("Initializing Battle...");
		Battle b = new Battle(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		b.seedRandom(RANDOM_SEED);

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
			Thread background = new AIThread(new DummyFleet(RANDOM_SEED),cIn,cOut);
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
			
			Debug.info("Initializing background thread #2");
			background = new AIThread(new SimpleFleet(RANDOM_SEED+1),cIn,cOut);
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
			d.updateDisplay(b);
			Debug.verbose("tick #"+b.getTick());
		}
		
		Debug.info("Battle over!");
		//END GAME LOOP
		
		Iterator<ServerTeam> it = b.teamIterator();
		while(it.hasNext())
		{
			ServerTeam t = it.next();
			if(t.isAlive())
			{
				System.out.println("The winner is: "+t.getName());
				Iterator<ServerFleet> itf = b.fleetIterator();
				while(itf.hasNext())
				{
					ServerFleet f = itf.next();
					if(f.getTeam() == t)
						System.out.print("               "+f.getName()+": "+f.getAIName()+" v "+f.getAIVersion()+ " by "+f.getAIAuthor());
				}
				System.out.println();
			}
		}
		
		// cleanup
		try {
			
			
			b.cleanup();
		}
		catch(java.io.IOException e)
		{
			Debug.crash(e,"Error during cleanup!");
		}
		
		Debug.info("Main Exiting");
	}
}
