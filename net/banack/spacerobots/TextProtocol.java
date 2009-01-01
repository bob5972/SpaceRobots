package net.banack.spacerobots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.SensorContact;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ContactList;

public class TextProtocol implements AIProtocol
{
	private Parser sIn;
	private PrintWriter sOut;
	
	private int curLevel;
	
	public TextProtocol(Reader in, PrintWriter out)
	{
		sIn = new Parser(in);
		sOut = out;
		curLevel=0;
	}
	
	private void send(String message)
	{
		send("message",curLevel);
	}
	
	//send the message at the given indentation level
	// (really just for debugging purposes, could set a flag to disable)
	private void send(String message, int indentation)
	{
		while(indentation-->0)
			sOut.print("\t");
		sOut.println(message);
	}
	
	private String readLine() throws IOException
	{
		return sIn.readLine();
	}
	
	private String readWord() throws IOException
	{
		return sIn.readWord();
	}
	
	private ShipAction readAction() throws IOException
	{
		return sIn.readAction();
	}
	
	private String[] read(String cmd) throws IOException
	{
		String[] cur = sIn.readWords();
		if(!cur[0].equals(cmd))
			Debug.crash("Bad AI Response: expected "+cmd+", received "+cur[0]);
		return cur;
	}
	
	//check if it has args tokens (including the command)
	private String[] read(String cmd, int args) throws IOException
	{
		String[] cur = read(cmd);
		if(cur.length-1 != args)
			Debug.error("Unable to retrieve requested number of tokens: Got "+(cur.length-1)+", requested "+args);
		return cur;
	}
	
	//Returns {name, author,version}
	public String[] loadInfo() throws IOException
	{
		String[] oup = new String[3];
		String temp;
		
		
		send("REQUEST_INFO");
		
		read("BEGIN_INFO",1);
	
		temp = readWord();
		while(!temp.equals("END_INFO"))
		{
			if(temp.equals("NAME"))
			{
				oup[0] = readLine();
			}
			else if(temp.equals("AUTHOR"))
			{
				oup[1] = readLine();
			}
			else if(temp.equals("VERSION"))
			{
				oup[2] = readLine();
			}
			else
				Debug.aiwarn("Unknown AI Response in BEGIN_INFO: "+temp);
			
			temp = readWord();
		}
		
		//clean up the hanging endl
		readLine();
			
		//>REQUEST_INFO
		//<BEGIN_INFO
		//<NAME Der Uber Fleet
		//<AUTHOR Michael Banack
		//<VERSION 1.0
		//<END_INFO
		
		return oup;
	}
	
	public void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f) throws IOException
	{		
		curLevel=0;
		send("BEGIN_BATTLE");
		curLevel++;
		send("BEGIN_INIT_BATTLE");
		curLevel++;
		send("FLEET_ID "+fleetID);
		send("TEAM_ID "+teamID);
		send("STARTING_CREDITS "+startingCredits);
		
		send("BEGIN_STARTING_SHIPS");
		curLevel++;
		for(int x=0;x<s.length;x++)
		{
			writeShip(s[x]);
		}
		curLevel--;
		send("END_STARTING_SHIPS");
		
		send("BEGIN_TEAMS");
		curLevel++;
		for(int x=0;x<t.length;x++)
		{
			writeTeam(t[x]);
		}
		curLevel--;
		send("END_TEAMS");
		
		
		send("BEGIN_FLEETS");
		curLevel++;
		for(int x=0;x<f.length;x++)
		{
			writeFleet(f[x]);
		}
		curLevel--;
		send("END_FLEETS");
		
		send("END_INIT_BATTLE");
		curLevel--;
		
		
		read("BATTLE_READY_BEGIN",1);
		
		//setup teams, starting positions, etc
		//>BEGIN_BATTLE
		//>	BEGIN_INIT_BATTLE
		//>		FLEET_ID 27
		//>		TEAM_ID 2
		//>		STARTING_CREDITS 1000
		//>		BEGIN_STARTING_SHIPS
		//>			SHIP iD type xPos yPos heading scannerHeading life deltaLife
		//>		END_STARTING_SHIPS
		//
		//>		BEGIN_TEAMS
		//>			TEAM teamID "Name"
		//>		END_TEAMS
		//
		//>		BEGIN_FLEETS
		//>			FLEET fleetID teamID "Name" "AIName" "AIVersion" deadOrAlive winOrLose
		//>		END_FLEETS
		//>	END_INIT_BATTLE
		
		//let everyone initialize before starting, so require and acknowledgement
		//<BATTLE_READY_BEGIN
		
		//it would make more sense to use something like "ACK_INIT_BATTLE"
		//	but its way cooler to go "BATTLE_READY"
		
		
		//the END_BATTLE would be after it's all over...
		//this way we can re-use sockets between matches
	}
	
	public void battleStatusUpdate(int teamID, int fleetID, boolean doa) throws IOException
	{
		//we could send these intermittantly to tell fleets when people die...
		//>BATTLE_STATUS_UPDATE teamID deadOrAlive winOrLose
		send("BATTLE_STATUS_UPDATE "+teamID+" "+fleetID+" "+(doa?1:0));
	}
	
	public void beginFleetStatusUpdate(int tick, int credits, ContactList c, int numShips) throws IOException
	{
		send("BEGIN_FLEET_STATUS");
		curLevel++;
		
		send("TICK "+tick);
		send("CREDITS "+credits);
		send("BEGIN_CONTACT_LIST "+c.size());
		curLevel++;
		
		//write contacts
		Iterator i = c.enemyIterator();
		while(i.hasNext())
		{
			Integer eID = (Integer)i.next();
			SensorContact ghost = c.getContact(eID);
			DPoint pos = ghost.getPosition();
			StringBuffer cmd = new StringBuffer();
			cmd.append("CONTACT ");
			cmd.append(ghost.getFleetID());
			cmd.append(" ");
			cmd.append(ghost.getTypeID());
			cmd.append(" ");
			cmd.append(pos.getX());
			cmd.append(" ");
			cmd.append(pos.getY());
			cmd.append(" ");
			cmd.append(ghost.getHeading());
			cmd.append(" (");
			
			HashSet spot = c.getSpotters(eID);
			Iterator spoti = spot.iterator();
			while(spoti.hasNext())
			{
				Integer sID = (Integer)spoti.next();
				cmd.append(sID);
				cmd.append(" ");
			}
			cmd.append(")");
			send(cmd.toString());
		}		
		
		curLevel--;
		send("END_CONTACT_LIST");
			
		send("BEGIN_SHIPS "+numShips);
		curLevel++;
		
		//>BEGIN_FLEET_STATUS
		//>	TICK 803
		//>	CREDITS 100
		//>	BEGIN_CONTACT_LIST 13
		//>		CONTACT fleetID type xPos yPos heading (refiD refiD )
		//>	END_CONTACT_LIST
		//> BEGIN_SHIPS 25
	}
	
	public void writeShip(Ship s) throws IOException
	{
		//write a single ship
		//>		SHIP iD type xPos yPos heading scannerHeading life deltaLife
		
		send("SHIP "+s.getID()+" "+s.getTypeID()+" "+((int)s.getXPos())+" "+((int)s.getYPos())+" "+SpaceMath.radToDeg(s.getHeading())+" "+SpaceMath.radToDeg(s.getScannerHeading())+" "+s.getLife()+" "+s.getDeltaLife());
	}
	
	public void endFleetStatusUpdate()
	{
		send("END_SHIPS");
		curLevel--;
		send("END_FLEET_STATUS");
		curLevel--;
		//>	END_SHIPS
		//>END_FLEET_STATUS
	}
	
	private void writeTeam(Team t)
	{
		//>TEAM teamID "Name"
		send("TEAM "+t.getTeamID()+" \""+t.getName()+"\"");
	}
	
	private void writeFleet(Fleet f) throws IOException
	{
		//>FLEET fleetID teamID "Name" "AIName" "AIVersion" isAlive winOrLose
		send("FLEET "+Parser.toString(f));
	}
	
	public ActionList readFleetActions() throws IOException
	{
		ActionList oup = new ActionList();
		int tick,numA;
		
		read("BEGIN_FLEET_ACTIONS",1);
		tick = Parser.parseInt(read("TICK",2)[1]);
		oup.setTick(tick);
		
		numA = Parser.parseInt(read("BEGIN_SHIP_ACTIONS",2)[1]);
		
		for(int x=0;x<numA;x++)
		{
			oup.add(readAction());
		}
		
		read("END_SHIP_ACTIONS",1);
		read("END_FLEET_ACTIONS",1);
		
		return oup;
		
		//<BEGIN_FLEET_ACTIONS
		//<	TICK 803
		//<	BEGIN_SHIP_ACTIONS
		//<		SHIP_ACTION id willMove newHeading newScannerHeading launchWhat
		//< END_SHIP_ACTIONS
		//<END_FLEET_ACTIONS
	}
	
	public void endBattle(Fleet me, Team[]t, Fleet[] f)  throws IOException
	{
		send("BEGIN_BATTLE_OUTCOME");
		curLevel++;
		send("YOU "+Parser.toString(me));
		
		send("BEGIN_TEAMS "+t.length);
		curLevel++;
		for(int x=0;x<t.length;x++)
			writeTeam(t[x]);
		curLevel--;
		send("BEGIN_FLEETS "+f.length);
		curLevel++;
		for(int x=0;x<f.length;x++)
			writeFleet(f[x]);
		curLevel--;
		send("END_FLEETS");
		curLevel--;
		send("END_BATTLE_OUTCOME");
		curLevel--;
		send("END_BATTLE");
		
		read("BATTLE_READY_END",1);
		
		//>BEGIN_BATTLE_OUTCOME
		//>YOU fleetID teamID deadOrAlive winOrLose
		//>	BEGIN_TEAMS
		//>		TEAM teamID deadOrAlive winOrLose friendOrFoe "Name"
		//>	END_TEAMS
		//>	BEGIN_FLEETS
		//>		FLEET fleetID teamID deadOrAlive winOrLose friendOrFoe "Name" "Version"
		//>	END_FLEETS
		//>END_BATTLE_OUTCOME
		
		//>END_BATTLE
		
		//<BATTLE_READY_END
		//now we know the ai is finished fighting (in it's head?)
	}
	
	public static class Parser extends net.banack.io.Parser
	{
		public Parser(Reader in)
		{
			super(in);
		}
		
		public ShipAction readAction() throws IOException
		{
			String cur = readLine();
			return parseAction(cur);
		}
		
		public static String toString(Fleet f)
		{
			return f.getFleetID()+" "+f.getTeamID()+" "+" \""+f.getName()+"\" \""+f.getAIName()+"\" \""+f.getAIVersion()+"\" "+f.isAlive()+" "+f.getWinOrLose();
		}
		
		public static ShipAction parseAction(String s)
		{

			String[] inp = parseWords(s);
			if(!inp[0].equals("ACTION"))
				Debug.crash("Bad AI Response: Expected ACTION, received "+inp[0]);
			//<		SHIP_ACTION id willMove newHeading newScannerHeading launchWhat
			throw new MethodNotImplementedException();
		}			
	}
}
