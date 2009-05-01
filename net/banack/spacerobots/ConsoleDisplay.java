package net.banack.spacerobots;

import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.Fleet;

public class ConsoleDisplay implements Display
{
	public ConsoleDisplay()
	{
		
	}
	
	public void initDisplay(Battle b)
	{
		return;
	}
	
	public void closeDisplay(Battle b)
	{
		return;
	}
	
	public void updateDisplay(Battle b)
	{
		int cruiser,fighter,destroyer,total;
		cruiser = fighter = destroyer = total = 0;
		
		Iterator<ServerShip> i = b.shipIterator();
		while(i.hasNext())
		{
			Ship s = (Ship)i.next();
			switch(s.getTypeID())
			{
				case DefaultShipTypeDefinitions.CRUISER_ID:
					cruiser++;
					break;
				case DefaultShipTypeDefinitions.FIGHTER_ID:
					fighter++;
					break;
				case DefaultShipTypeDefinitions.DESTROYER_ID:
					destroyer++;
					break;
			}
			total++;
		}
		
		Iterator<ServerFleet> fi;
		
		if(b.getTick()%100==0)
		{
			System.out.println("Tick "+b.getTick());
			System.out.println("Ships total="+total+", cruiser="+cruiser+", destroyer="+destroyer+", fighter="+fighter);
			fi = b.fleetIterator();
			while(i.hasNext())
			{
				ServerFleet f = fi.next();
				System.out.println("Fleet "+f.getFleetName()+" ships="+f.getNumShips()+" credits="+f.getCredits());
			}
		}
		
	}
	
}
