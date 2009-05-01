package net.banack.spacerobots;

import java.io.IOException;

public class JoinDisplay implements Display
{
	//ideally, this could be more generic, but this works for now
	private Display one, two; 
	
	public JoinDisplay(Display one, Display two)
	{
		this.one = one;
		this.two = two;
	}
	
	public void closeDisplay(Battle b) throws IOException
	{
		one.closeDisplay(b);
		two.closeDisplay(b);
		
	}
	
	public void initDisplay(Battle b) throws IOException
	{
		one.initDisplay(b);
		two.initDisplay(b);
	}
	
	public void updateDisplay(Battle b) throws IOException
	{
		one.updateDisplay(b);
		two.updateDisplay(b);		
	}
	
}
