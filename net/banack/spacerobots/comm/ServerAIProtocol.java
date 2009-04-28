package net.banack.spacerobots.comm;

import java.io.IOException;

import net.banack.spacerobots.ServerFleet;
import net.banack.spacerobots.ServerShip;
import net.banack.spacerobots.ServerTeam;
import net.banack.spacerobots.util.ActionList;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ContactList;

public interface ServerAIProtocol
{
	//Returns {name, author,version}
	String[] loadInfo() throws IOException;
	
	void initBattle(int fleetID,int teamID, int startingCredits, ServerShip[] s, ServerTeam[] t, ServerFleet[] f, double width, double height) throws IOException;
	
	void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException;	

	void writeShip(ServerShip s) throws IOException;
	
	void endFleetStatusUpdate() throws IOException;
	
	ActionList readFleetActions() throws IOException;
	
	void endBattle(ServerFleet me, ServerTeam[] t, ServerFleet[] f) throws IOException;
}
