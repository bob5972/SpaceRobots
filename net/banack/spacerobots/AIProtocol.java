package net.banack.spacerobots;

import java.io.IOException;

import net.banack.spacerobots.util.ActionList;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ContactList;

public interface AIProtocol
{
	//Returns {name, author,version}
	String[] loadInfo() throws IOException;
	
	void initBattle(int fleetID,int teamID, int startingCredits, ServerShip[] s, ServerTeam[] t, ServerFleet[] f) throws IOException;
	
	void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException;	

	void writeShip(ServerShip s) throws IOException;
	
	void endFleetStatusUpdate() throws IOException;
	
	ActionList readFleetActions() throws IOException;
	
	void endBattle(ServerFleet me, ServerTeam[] t, ServerFleet[] f) throws IOException;
}
