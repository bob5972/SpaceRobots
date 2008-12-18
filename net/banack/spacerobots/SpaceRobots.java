package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;


public class SpaceRobots
{
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	
	public static void main(String[] args)
	{
		//SETUP
		Display d = new ConsoleDisplay();

		// load AI's
		
		//setup fleets
		
		//setup teams
		
		// setup initial battle state
		Battle b = new Battle(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		//for a in AI's {b.addAI(a);}
		//for f in fleets {b.addFleet(f);}
		//for t in teams {b.addTeam(t);}
		
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
