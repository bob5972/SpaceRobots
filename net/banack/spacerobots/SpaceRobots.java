package net.banack.spacerobots;


public class SpaceRobots
{
	public static void main(String[] args)
	{
		//SETUP
		// create display
		//Display d = new Display();
		// load AI's
		
		//setup fleets
		
		//setup teams
		
		// setup initial battle state
		Battle b = new Battle();
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
			b.runTick();
			//update display
//			d.displayStatus(b);
		}
		
		//END GAME LOOP
		
		// cleanup?
	}
	
	public static boolean showBadAIWarnings()
	{
		return true;
	}

}
