package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;

public class ConsoleDisplay implements Display
{
	public ConsoleDisplay()
	{
		
	}
	
	public void updateDisplay(Battle b)
	{
		System.out.println("numShips = "+b.getNumShips());		
	}
	
}
