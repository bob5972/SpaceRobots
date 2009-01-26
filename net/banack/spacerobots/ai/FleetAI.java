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

public interface FleetAI
{	
	public void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f);
	
	public Iterator<ShipAction> runTick(int tick, int credits, ContactList c, Ship[] s);
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose);
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f);

	public String getAuthor();

	public String getName();

	public String getVersion();
}
