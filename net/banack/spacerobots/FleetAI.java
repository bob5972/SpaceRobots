package net.banack.spacerobots;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;

public class FleetAI
{
	private AIProtocol myCom;

	private String myAuthor;
	private String myVersion;
	private String myName;
	
	//must loadInfo prior to using
	public FleetAI(AIProtocol p)
	{
		myCom = p;
	}
	
	//must loadInfo prior to using
	public FleetAI(InputStream inp,OutputStream oup)
	{
		myCom = ProtocolFactory.doHandshake(inp,oup);	
	}
	
	public void loadInfo()  throws IOException
	{
		String[] s = myCom.loadInfo();
		myName = s[0];
		myAuthor = s[1];
		myVersion = s[2];
	}
	
	public void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f) throws IOException
	{		
		myCom.initBattle(fleetID,teamID,startingCredits,s,t,f);
	}
	
	public void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips)throws IOException
	{
		myCom.beginFleetStatusUpdate(tick,credits,c,numShips);
	}
	
	public void writeShip(Ship s) throws IOException
	{
		myCom.writeShip(s);
	}
	
	public void endFleetStatusUpdate()throws IOException
	{
		myCom.endFleetStatusUpdate();
	}
	
	public ActionList readFleetActions()throws IOException
	{
		return myCom.readFleetActions();
	}
	
	public void endBattle(Fleet me, Team[]t, Fleet[] f)throws IOException
	{
		myCom.endBattle(me, t, f);
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
