package net.banack.spacerobots.ai;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;

public interface FleetAI
{	
	public void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, int[] teams, Fleet[] f);
	
	public ActionList runTick(int tick, int credits, ContactList c, Ship[] s);
	
	public void endBattle();

	public String getAuthor();

	public String getName();

	public String getVersion();
}
