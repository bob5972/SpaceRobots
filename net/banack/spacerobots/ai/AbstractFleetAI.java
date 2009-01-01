package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.Team;
import net.banack.util.MethodNotImplementedException;

public abstract class AbstractFleetAI implements FleetAI
{
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
	
	public String getAuthor()
	{
		return "Anonymous";
	}
	
	public String getName()
	{
		return this.getClass().getName();
	}
		
	public String getVersion()
	{
		return "0";
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose)
	{
		return;
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, Ship[] s, int[] teams, Fleet[] f)
	{
		return;
	}
	
	public ActionList runTick(int tick, int credits, ContactList c, Ship[] s)
	{
		return new ActionList();
	}
}