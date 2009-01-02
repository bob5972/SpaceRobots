package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;

public class SimpleFleet extends AbstractFleetAI
{
	private Ship myCruiser;
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
	
	public String getName()
	{
		return "SimpleFleet";
	}
		
	public String getVersion()
	{
		return "1.0";
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose)
	{
		return;
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, Ship[] s, Team[] teams, Fleet[] f)
	{
		for(int x=0;x<s.length;x++)
		{
			if(s[x].getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID)
			{
				myCruiser=s[x];
				break;
			}
		}
	}
	
	public ActionList runTick(int tick, int credits, ContactList c, Ship[] s)
	{
		ActionList oup = new ActionList();
		oup.setTick(tick);
		
		
		for(int x=0;x<s.length;x++)
		{
			ShipAction a = new ShipAction(s[x]);
			if(s[x].getTypeID() == DefaultShipTypeDefinitions.FIGHTER_ID)
			{
				
				if(c.size() > 0 && credits > DefaultShipTypeDefinitions.ROCKET.getCost())
				{
					credits -= DefaultShipTypeDefinitions.ROCKET.getCost();
					a.setLaunchWhat(DefaultShipTypeDefinitions.ROCKET_ID);
				}
			}
			else
			{
				if(credits > DefaultShipTypeDefinitions.FIGHTER.getCost())
				{
					credits-=DefaultShipTypeDefinitions.FIGHTER.getCost();
					a.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
				}
			}
			oup.add(a);
		}
		return oup;		
	}
}
