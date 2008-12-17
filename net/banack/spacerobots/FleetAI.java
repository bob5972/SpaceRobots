package net.banack.spacerobots;

import java.io.InputStream;
import java.io.OutputStream;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ActionList;

public class FleetAI
{
	private AIProtocol myCom;

	private String myAuthor;
	private String myVersion;
	private String myName;
	
	public FleetAI(AIProtocol p)
	{
		myCom = p;
		String[] s = myCom.loadInfo();
		
		myName = s[0];
		myAuthor = s[1];
		myVersion = s[2];
	}
	
	public FleetAI(InputStream inp,OutputStream oup)
	{
		myCom = ProtocolFactory.doHandshake(inp,oup);
		
		String[] s = myCom.loadInfo();
		myName = s[0];
		myAuthor = s[1];
		myVersion = s[2];
	}
	
	public void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f)
	{		
		myCom.initBattle(fleetID,teamID,startingCredits,s,t,f);
	}
	
	public void beginFleetStatusUpdate(int tick, int credits, SensorContact[] c, int numShips)
	{
		myCom.beginFleetStatusUpdate(tick,credits,c,numShips);
	}
	
	public void writeShip(Ship s)
	{
		myCom.writeShip(s);
	}
	
	public void endFleetStatusUpdate()
	{
		myCom.endFleetStatusUpdate();
	}
	
	public ActionList readFleetActions()
	{
		return myCom.readFleetActions();
	}
	
	public void endBattle(Fleet me, Fleet[] f)
	{
		myCom.endBattle(me, f);
	}

	public String getAuthor()
	{
		return myAuthor;
	}

	public String getName()
	{
		return myName;
	}

	public String getVersion()
	{
		return myVersion;
	}
}
