package net.banack.spacerobots.ai;

import java.util.Iterator;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.Team;
import net.banack.util.MethodNotImplementedException;

public abstract class AbstractFleetAI implements FleetAI
{
	public static final DefaultShipTypeDefinitions TYPE = new DefaultShipTypeDefinitions();
	
	public static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	public static final int MISSILE_ID = DefaultShipTypeDefinitions.MISSILE_ID;
	
	public static final ShipType CRUISER = TYPE.CRUISER;
	public static final ShipType DESTROYER = TYPE.DESTROYER;
	public static final ShipType FIGHTER = TYPE.FIGHTER;
	public static final ShipType ROCKET = TYPE.ROCKET;
	public static final ShipType MISSILE = TYPE.MISSILE;
	
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
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa)
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
