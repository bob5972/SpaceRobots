package net.banack.spacerobots.ai;

import java.util.Iterator;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;
import net.banack.util.MethodNotImplementedException;

public abstract class AbstractFleetAI implements FleetAI
{
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
	
	public abstract String getAuthor();

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
	
	public void initBattle(int fleetID, int teamID, int startingCredits, Ship[] s, Team[] teams, Fleet[] f)
	{
		return;
	}
	
	public abstract Iterator<ShipAction> runTick(int tick, int credits, ContactList c, Ship[] s);
}
