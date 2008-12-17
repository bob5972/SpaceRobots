package net.banack.spacerobots;

import net.banack.spacerobots.util.ActionList;
import net.banack.util.MethodNotImplementedException;

public interface AIProtocol
{
	//Returns {name, author,version}
	String[] loadInfo();
	
	void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f);
	
	void beginFleetStatusUpdate(int tick, int credits, SensorContact[] c, int numShips);	

	void writeShip(Ship s);
	
	void endFleetStatusUpdate();
	
	ActionList readFleetActions();
	
	void endBattle(Fleet me, Fleet[] f);
}
