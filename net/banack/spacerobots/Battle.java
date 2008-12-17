package net.banack.spacerobots;

import java.util.Collection;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ActionList;
import java.util.Iterator;
import net.banack.spacerobots.util.ShipAction;

public class Battle
{
	private Collection myShips;
	private Collection myFleets;
	private Collection myTeams;
	
	//this way we can re-use it
	//saves on (de)allocation
	private ActionList aggregate;
	
	private int myWidth;
	private int myHeight;
	
	public Battle()
	{
		aggregate = new ActionList();
		throw new MethodNotImplementedException();
	}
	
	
	public void initialize()
	{
		//setup stuff...
		//notify the AI's a battle is starting
		// etc
		throw new MethodNotImplementedException();
	}
	
	public boolean isOver()
	{
		//check if anyone won
		throw new MethodNotImplementedException();
	}
	
	public void runTick()
	{
		Fleet f;
		Ship s;
		ShipAction a;
		Iterator i;
		
		aggregate.makeEmpty();	
		
		
		i=myFleets.iterator();
		while(i.hasNext())
		{
			f=(Fleet)i.next();
			FleetAI ai = f.getAI();
			
			Iterator si = myShips.iterator();
			
			//Write ships to AI sockets
			ai.beginFleetStatusUpdate();			
			
			while(si.hasNext())
			{
				s=(Ship)si.next();
				ai.writeShip(s);
			}				
			ai.endFleetStatusUpdate();
			
			ActionList AL = ai.readFleetActions();
			aggregate.add(AL);
			
			//add credits to fleets
			int credit = f.getCreditIncrement();
			f.incrementCredits(credit);			
		}		
		
		//do spawns
		//	No guarantee made as to order of spawns
		//	ie if you try to spawn more than you can, some get through, some don't
		
		i = aggregate.spawnIterator();
		while(i.hasNext())
		{
			a=(ShipAction)i.next();
			
			if (canSpawn(a))
			{
				doSpawn(a);
			}
			else
			{
				//error message
				if(SpaceRobots.showBadAIWarnings())
					System.err.println("Illegal Spawn attempted by: ");
			}
		}
		
		i = myShips.iterator();
		while(i.hasNext())
		{
			s=(Ship)i.next();
			s.reset();//tick damage counters and the like
			a = aggregate.get(s.getShipID());
			
			//apply a to s;
			//move s;
		}

//		//check collisions
//		//blow stuff up
		
		throw new MethodNotImplementedException();
	}
	
	//canSpawn checks if s.ship is alive, is of the correct type, and s.ship.fleet has the correct credits, etc
	public boolean canSpawn(ShipAction a)
	{
		throw new MethodNotImplementedException();
	}
	
	public void doSpawn(ShipAction a)
	{
		throw new MethodNotImplementedException();
	}
}
