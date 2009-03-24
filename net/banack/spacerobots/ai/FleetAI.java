package net.banack.spacerobots.ai;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;

public interface FleetAI extends AIShipFactory
{	
	public AIShip createShip(Ship s);
	
	public void initBattle(int fleetID,int teamID, int startingCredits, AIShipList s, Team[] t, Fleet[] f, double width, double height);
	
	//The references to AIShipList and AIShips can be reused, but don't modify them outside this thread!
	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s);
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa);
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f);

	public String getAuthor();

	public String getName();

	public String getVersion();
}
