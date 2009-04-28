package net.banack.spacerobots;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;
import java.util.Set;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceText;

public class BinaryProtocol implements AIProtocol
{
	private DataInputStream sIn;
	private DataOutputStream sOut;
	
	public static final int REQUEST_INFO = net.banack.spacerobots.ai.BinaryProtocol.REQUEST_INFO;
	public static final int BEGIN_INFO = net.banack.spacerobots.ai.BinaryProtocol.BEGIN_INFO;
	public static final int BEGIN_BATTLE = net.banack.spacerobots.ai.BinaryProtocol.BEGIN_BATTLE;
	public static final int BATTLE_READY_BEGIN = net.banack.spacerobots.ai.BinaryProtocol.BATTLE_READY_BEGIN;
	public static final int BATTLE_STATUS_UPDATE = net.banack.spacerobots.ai.BinaryProtocol.BATTLE_STATUS_UPDATE;
	public static final int BEGIN_FLEET_ACTIONS = net.banack.spacerobots.ai.BinaryProtocol.BEGIN_FLEET_ACTIONS;
	public static final int BEGIN_FLEET_STATUS = net.banack.spacerobots.ai.BinaryProtocol.BEGIN_FLEET_STATUS;
	public static final int BEGIN_BATTLE_OUTCOME = net.banack.spacerobots.ai.BinaryProtocol.BEGIN_BATTLE_OUTCOME;
	public static final int END_BATTLE = net.banack.spacerobots.ai.BinaryProtocol.END_BATTLE;
	public static final int BATTLE_READY_END = net.banack.spacerobots.ai.BinaryProtocol.BATTLE_READY_END;
	
	public static final int SHIPACTION_ID = net.banack.spacerobots.ai.BinaryProtocol.SHIPACTION_ID;
	public static final int CONTACT_ID = net.banack.spacerobots.ai.BinaryProtocol.CONTACT_ID;
	public static final int SHIP_ID = net.banack.spacerobots.ai.BinaryProtocol.SHIP_ID;
	public static final int FLEET_ID = net.banack.spacerobots.ai.BinaryProtocol.FLEET_ID;
	public static final int TEAM_ID = net.banack.spacerobots.ai.BinaryProtocol.TEAM_ID;
	
	public BinaryProtocol(DataInputStream in, DataOutputStream out)
	{
		sIn = in;
		sOut = out;
	}
	
	private String readString(int length) throws IOException
	{
		StringBuffer oup = new StringBuffer(length);
		for(int x=0;x<length;x++)
		{
			oup.append(sIn.readChar());
		}
		return oup.toString();
	}
	
	//Returns {name, author,version}
	public String[] loadInfo() throws IOException
	{
		String[] oup = new String[3];
		String name;
		String author;
		String version;
		
		Debug.info("Server sending REQUEST_INFO");
		sOut.writeInt(REQUEST_INFO);
		sOut.flush();
		
		Debug.verbose("Server reading BEGIN_INFO");
		int cmd = sIn.readInt();
		if(cmd != BEGIN_INFO)
			Debug.crash("Bad Client Response: Expected BEGIN_INFO ("+BEGIN_INFO+"), received "+cmd);
		Debug.verbose("Server got valid BEGIN_INFO");
	
		int len;
		
		len = sIn.readInt();
		name = readString(len);
		len = sIn.readInt();
		author = readString(len);
		len = sIn.readInt();
		version = readString(len);
		
		if(Debug.isDebug())
		{
			if(name == null || author == null || version == null)
				Debug.warn("We've got null pointers in loadInfo()!");
		}
		
		oup[0] = name;
		oup[1] = author;
		oup[2] = version;
		
		return oup;
	}
	
	public void initBattle(int fleetID,int teamID, int startingCredits, ServerShip[] s, ServerTeam[] t, ServerFleet[] f, double width, double height) throws IOException
	{		
		Debug.verbose("Server entering BEGIN_BATTLE");
		sOut.writeInt(BEGIN_BATTLE);
		sOut.writeDouble(width);
		sOut.writeDouble(height);
		sOut.writeInt(fleetID);
		sOut.writeInt(teamID);
		sOut.writeInt(startingCredits);
		sOut.writeInt(s.length);
		
		for(int x=0;x<s.length;x++)
		{
			writeShip(s[x]);
		}
		sOut.flush();
		
		sOut.writeInt(t.length);
		for(int x=0;x<t.length;x++)
		{
			writeTeam(t[x]);
		}
		sOut.flush();
		
		sOut.writeInt(f.length);
		for(int x=0;x<f.length;x++)
		{
			writeFleet(f[x]);
		}
		sOut.flush();
		
		int cmd = sIn.readInt();
		if(cmd != BATTLE_READY_BEGIN)
			Debug.crash("Bad Client Response: Expected BATTLE_READY_BEGIN ("+BATTLE_READY_BEGIN+"), received "+cmd);
		Debug.verbose("Leaving Server::BinaryProtocol.initBattle");
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa) throws IOException
	{
		Debug.verbose("Server entering BEGIN_STATUS_UPDATE");
		sOut.writeInt(BATTLE_STATUS_UPDATE);
		sOut.writeInt(teamID);
		sOut.writeInt(fleetID);
		sOut.writeBoolean(doa);
		sOut.flush();
	}
	
	public void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException
	{
		Debug.verbose("Server entering BEGIN_FLEET_STATUS");
		sOut.writeInt(BEGIN_FLEET_STATUS);

		sOut.writeInt(tick);
		sOut.writeInt(credits);
		sOut.writeInt(c.size());
		
		//write contacts
		Iterator<Integer> i = c.enemyIterator();
		while(i.hasNext())
		{
			Contact sc = c.getContact(i.next());
			

			sOut.writeInt(CONTACT_ID);
			
			sOut.writeInt(sc.getID());
			sOut.writeInt(sc.getFleetID());
			sOut.writeInt(sc.getTypeID());
			
			sOut.writeDouble(sc.getPosition().getX());
			sOut.writeDouble(sc.getPosition().getY());
			sOut.writeDouble(sc.getHeading());
			sOut.writeInt(sc.getLife());
			
			Set<Integer> spot = c.getSpotters(sc.getID());
			sOut.writeInt(spot.size());
			Iterator<Integer> si = spot.iterator();
			while(si.hasNext())
			{
				sOut.writeInt(si.next());
			}		
		}
		sOut.flush();
		
		sOut.writeInt(numShips);
	}
	
	public void writeShip(ServerShip s) throws IOException
	{
		sOut.writeInt(SHIP_ID);
		
		sOut.writeInt(s.getID());
		sOut.writeInt(s.getTypeID());
		sOut.writeDouble(s.getXPos());
		sOut.writeDouble(s.getYPos());
		sOut.writeDouble(s.getHeading());
		sOut.writeDouble(s.getScannerHeading());
		sOut.writeInt(s.getCreationTick());
		sOut.writeInt(s.getLife());
		sOut.writeInt(s.getDeltaLife());
		sOut.writeInt(s.getFiringDelay());
	}
	
	public void endFleetStatusUpdate() throws IOException
	{
		sOut.flush();
	}
	
	private void writeTeam(ServerTeam t) throws IOException
	{
		sOut.writeInt(TEAM_ID);
		sOut.writeInt(t.getID());
		sOut.writeInt(t.getName().length());
		sOut.writeChars(t.getName());
	}
	
	private void writeFleet(ServerFleet f) throws IOException
	{
		sOut.writeInt(FLEET_ID);
		sOut.writeInt(f.getID());
		sOut.writeInt(f.getTeamID());
		
		if(f.getName()==null)
		{
			sOut.writeInt(0);
		}
		else
		{
			Debug.verbose("Server fleet length = "+f.getName().length()+" name = "+f.getName());
			sOut.writeInt(f.getName().length());
			sOut.writeChars(f.getName());
		}
		if(f.getAIName() == null)
		{
			sOut.writeInt(0);
		}
		else
		{
			sOut.writeInt(f.getAIName().length());
			sOut.writeChars(f.getAIName());
		}
		if(f.getAIAuthor() == null)
		{
			sOut.writeInt(0);
		}
		else
		{
			sOut.writeInt(f.getAIAuthor().length());
			sOut.writeChars(f.getAIAuthor());
		}
		if(f.getAIVersion() == null)
		{
			sOut.writeInt(0);
		}
		else
		{
			sOut.writeInt(f.getAIVersion().length());
			sOut.writeChars(f.getAIVersion());
		}
		sOut.writeBoolean(f.isAlive());
		sOut.flush();
	}
	
	public ActionList readFleetActions() throws IOException
	{
		Debug.verbose("Server entering BEGIN_FLEET_ACTIONS");
		ActionList oup = new ActionList();
		int tick,numA;
		
		int cmd = sIn.readInt();
		if(cmd != BEGIN_FLEET_ACTIONS)
			Debug.crash("Bad Client Response: Expected BEGIN_FLEET_ACTIONS ("+BEGIN_FLEET_ACTIONS+"), received "+cmd);
		
		tick = sIn.readInt();
		oup.setTick(tick);
		
		numA = sIn.readInt();
		
		for(int x=0;x<numA;x++)
		{
			oup.add(readAction());
		}
		
		return oup;
	}
	
	public void endBattle(ServerFleet me, ServerTeam[]t, ServerFleet[] f)  throws IOException
	{
		Debug.verbose("Server entering BEGIN_BATTLE_OUTCOME");
		sOut.writeInt(BEGIN_BATTLE_OUTCOME);
		
		
		writeFleet(me);
		
		sOut.writeInt(t.length);
		for(int x=0;x<t.length;x++)
			writeTeam(t[x]);
		sOut.flush();
		sOut.writeInt(f.length);
		for(int x=0;x<f.length;x++)
			writeFleet(f[x]);
		
		sOut.writeInt(END_BATTLE);
		
		sOut.flush();
		
		Debug.verbose("Waiting for Client Response to endBattle...");
		int cmd = sIn.readInt();
		if(cmd != BATTLE_READY_END)
			Debug.crash("Bad Client Response: Expected BATTLE_READY_END ("+BATTLE_READY_END+"), received "+cmd);
	}
	
	public ShipAction readAction() throws IOException
	{
		int cmd = sIn.readInt();
		if(cmd != SHIPACTION_ID)
			Debug.crash("Bad Client Response: Expected SHIPACTION_ID ("+SHIPACTION_ID+"), received "+cmd);
		
		int shipID = sIn.readInt();
		boolean willMove = sIn.readBoolean();
		double heading = sIn.readDouble();
		double scannerHeading = sIn.readDouble();
		int launchWhat = sIn.readInt();
		
		return new ShipAction(shipID,willMove,heading,scannerHeading,launchWhat);
	}
}
