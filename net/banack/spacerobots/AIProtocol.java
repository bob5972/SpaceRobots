package net.banack.spacerobots;

import java.io.IOException;

import net.banack.spacerobots.util.ActionList;
import net.banack.util.MethodNotImplementedException;

public interface AIProtocol
{
	//Returns {name, author,version}
	String[] loadInfo() throws IOException;
	
	void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f) throws IOException;
	
	void beginFleetStatusUpdate(int tick, int credits, SensorContact[] c, int numShips) throws IOException;	

	void writeShip(Ship s) throws IOException;
	
	void endFleetStatusUpdate() throws IOException;
	
	ActionList readFleetActions() throws IOException;
	
	void endBattle(Fleet me, Team[] t, Fleet[] f) throws IOException;
}
