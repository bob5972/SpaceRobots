package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;


public class SpaceRobots
{
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	public static final int STARTING_CREDITS=0;
	public static final int CREDIT_INCREMENT=1;
	
	public static void main(String[] args)
	{
		//SETUP
		Display d = new ConsoleDisplay();
		
		//setup initial battle state
		Battle b = new Battle(DEFAULT_WIDTH,DEFAULT_HEIGHT);

		// load AI's
		
		//setup teams
		int[] tID = b.createTeams(2);
		
		//setup fleets
		FleetAI[] ai = new FleetAI[2];
		ai[0] = ai[1] = null;
		
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
				throw new MethodNotImplementedException("No error handler");
			}
			d.updateDisplay(b);
		}
		
		//END GAME LOOP
		
		// cleanup?
	}
	
	public static boolean showBadAIWarnings()
	{
		return true;
	}

}
