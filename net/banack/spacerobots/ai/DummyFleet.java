package net.banack.spacerobots.ai;

import java.util.Random;

import net.banack.spacerobots.Debug;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Team;

public class DummyFleet extends AbstractFleetAI
{
	private Ship myCruiser;
	private Random myRandom;
	
	public DummyFleet()
	{
		myRandom = new Random();
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getName()
	{
		return "DummyFleet";
	}
	
	public String getVersion()
	{
		return "1.0";
	}
	
	public void endBattle(Fleet me, Team[] t, Fleet[] f)
	{
		return;
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa, boolean winOrLose)
	{
		return;
	}
		
	public void initBattle(int fleetID, int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f)
	{
		for(int x=0;x<s.length;x++)
		{
			if(s[x].getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID)
			{
				myCruiser=s[x];
				break;
			}
		}
		return;
	}
	
	public ActionList runTick(int tick, int credits, ContactList c, Ship[] s)
	{
		ActionList oup = new ActionList();
		oup.setTick(tick);
		ShipAction a;

		for(int x=0;x<s.length;x++)
		{
			if(Debug.isDebug())
			{
				if(s[x] == null)
					Debug.warn("Null pointer at s["+x+"]");
			}
			
			a = new ShipAction(s[x]);
			
			if(myCruiser != null && s[x].getID() == myCruiser.getID())
			{
				if(credits > DefaultShipTypeDefinitions.FIGHTER.getCost())
				{
					a = new ShipAction(s[x]);
					a.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
				}
			}
			a.setHeading(myRandom.nextDouble()*Math.PI*2);
			oup.add(a);
			
		}
		return oup;
	}

	
}
