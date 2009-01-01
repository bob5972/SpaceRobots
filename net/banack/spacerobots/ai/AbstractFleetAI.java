package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.util.MethodNotImplementedException;

public abstract class AbstractFleetAI implements FleetAI
{
	
	public void endBattle()
	{
		return;
	}
	
	public abstract String getAuthor();	
	
	public abstract String getName();
		
	public abstract String getVersion();
	
	public void initBattle(int fleetID, int teamID, int startingCredits, Ship[] s, int[] teams, Fleet[] f)
	{
		return;
	}
	
	public ActionList runTick(int tick, int credits, ContactList c, Ship[] s)
	{
		return new ActionList();
	}
}
