package net.banack.spacerobots;

import java.io.IOException;

public interface Display
{
	void initDisplay(Battle b) throws IOException;
	
	void closeDisplay(Battle b) throws IOException;
	
	//Updates the display with the current status of b
	void updateDisplay(Battle b) throws IOException;
}
