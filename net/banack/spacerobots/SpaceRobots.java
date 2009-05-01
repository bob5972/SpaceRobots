package net.banack.spacerobots;


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
		Debug.setShowComLog(false);//only works for TextProtocol
		Debug.setSlowGraphics(false);
		final boolean USE_CONSOLE_DISPLAY = false;
		
		Random r;
		
		if(RANDOM_SEED ==0)
		{
			r = new Random();
			RANDOM_SEED=r.nextLong();
		}
		else
		{
			r = new Random(RANDOM_SEED);
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
		AIFleet ai[] = {
			new BattleCruiserFleet(r.nextLong()),
			new Cache(r.nextLong()),
			new DummyFleet(r.nextLong()),
			new Mob(r.nextLong()),
		};
		addAIFleets(b,ai);
		
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
	
	private static void addAIFleets(Battle b, AIFleet[] ai)
	{		
		Debug.info("Initializing teams...");
		int[] tID = b.createTeams(ai.length);
		
		try{
			for(int x=0;x<ai.length;x++)
			{
				
				Debug.info("Initializing pipe set #"+x+"...");
				PipedInputStream sIn = new PipedInputStream();
				PipedOutputStream cOut = new PipedOutputStream(sIn);
				PipedInputStream cIn = new PipedInputStream();
				PipedOutputStream sOut = new PipedOutputStream(cIn);
				
				Debug.info("Initializing background thread #"+x);
				Thread background = new AIThread(ai[x],cIn,cOut);
				Debug.info("Starting background thread #"+x);
				background.start();
				Debug.info("Handshaking...");
				ServerAIProtocol aip = ServerProtocolFactory.doHandshake(sIn,sOut);
				b.addFleet("Fleet "+x, new FleetAI(aip), tID[x],STARTING_CREDITS,CREDIT_INCREMENT);
			}
		}
		catch(java.io.IOException e)
		{
			Debug.error("Error initializing AI: IOException!");
			Debug.crash(e);
		}		
	}
}
