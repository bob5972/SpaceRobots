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
	public abstract String getAuthor();

	public String getName()
	{
		String className = this.getClass().getName();
		String[] oup = className.split("\\.");
		if(oup.length == 0)
			return "null";
		return oup[oup.length-1];
	}
		
	public String getVersion()
	{
		return "0";
	}
	
	public AIShip createShip(Ship s)
	{
		return new AIShip(s);
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose)
	{
		return;
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double width, double height)
	{
		return;
	}
	
	public abstract Iterator<ShipAction> runTick(int tick, int credits, ContactList c, AIShipList s);
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
}
