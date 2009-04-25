package net.banack.spacerobots.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.Debug;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;
import net.banack.util.MethodNotImplementedException;

public class TextProtocol implements AIClientProtocol
{
	private FleetAI myAI;
	private SpaceText sIn;
	private PrintWriter sOut;
	private int curLevel;
	private AIShipList myShips;
	
	public TextProtocol(FleetAI ai, BufferedReader sIn, PrintWriter sOut)
	{
		myAI = ai;
		this.sIn = new SpaceText(sIn);
		this.sOut = sOut;
		curLevel=0;
		myShips=new AIShipList();
	}
	
	public void send(String message)
	{
		send(message,curLevel);
	}
	
	//	send the message at the given indentation level
	// (really just for debugging purposes, could set a flag to disable)
	private void send(String message, int indentation)
	{
		if(Debug.isDebug())
		{
			String msg ="";
			for(int x=0;x<indentation;x++)
				msg+="\t";
			Debug.comLog(">"+msg+message);
		}
		
		while(indentation-->0)
			sOut.print("\t");
		sOut.println(message);
		sOut.flush();
	}
	
	
	
	private String[] read(String cmd) throws IOException
	{
		String[] cur = sIn.readWords();
		if(cur.length <= 0)
			Debug.crash("Received bad number of tokens: Received "+cur.length);
		if(!cur[0].equals(cmd))
			Debug.crash("Bad Server Response: expected "+cmd+", received "+cur[0]);
		return cur;
	}
		
	
	//	check if it has args tokens (including the command)
	private String[] read(String cmd, int args) throws IOException
	{
		String[] cur = read(cmd);
		if(cur.length != args)
		{
			String msg = "";
			for(int x=0;x<cur.length;x++)
				msg += cur[x]+" ";
			Debug.error("Unable to retrieve requested number of tokens: Got "+(cur.length-1)+", requested "+args+"\nLine="+msg);
		}
		return cur;
	}
	
	
	private void writeAction(ShipAction a)
	{
		send("SHIP_ACTION "+SpaceText.toString(a));
	}
	
	private void readContact(ContactList c) throws IOException
	{
		//>CONTACT eID fleetID typeID x y heading numSpotters (sId sID sID)
		
		String cmd = sIn.readWord();
		if(!cmd.equals("CONTACT"))
			Debug.crash("Bad Server Response: Expected CONTACT, received "+cmd);
				
		int eID = sIn.readInt();
		int fleetID = sIn.readInt();
		int typeID = sIn.readInt();
		double x = sIn.readInt();
		double y = sIn.readInt();
		double heading = SpaceMath.degToRad(sIn.readInt());
		int numSpotters = sIn.readInt();
		
		Contact ghost = new Contact(eID,fleetID,typeID,x,y,heading);
		
		HashSet<Integer> spotters = new HashSet<Integer>();
		
		char dummy = sIn.readNonWhitespaceChar();
		if(dummy != '(')
		{
			String msg = "";
			msg+="eid="+eID+" fleetID="+fleetID+" typeID="+typeID+" x="+x+" y="+y+" heading="+heading+" numSpotters="+numSpotters;
			Debug.crash("Bad Server Response: Expected (, received "+dummy+"\nline="+msg);
		}
		
		for(int i=0;i<numSpotters;i++)
		{
			int sID = sIn.readInt();
			spotters.add(new Integer(sID));
		}
		
		dummy = sIn.readNonWhitespaceChar();
		if(dummy != ')')
		{
			Debug.crash("Bad Server Response: Expected ), received "+dummy);
		}
		
		//clean up junk endl
		sIn.readLine();
		
		c.addContact(ghost,spotters);
	}
	
	public void start()
	{
		try {
			processChannel();
		}
		catch(IOException e)
		{
		    Debug.crash(e,"Error with TextProtocol");
		}
	}
	
	private void processChannel() throws IOException
	{
		String[] words;
		
		while(true)
		{
			curLevel=0;
			words = sIn.readWords();
			String cmd = words[0];
			
			if(cmd.equals("REQUEST_INFO"))
			{
				send("BEGIN_INFO");
				send("NAME "+myAI.getName());
				send("AUTHOR "+myAI.getAuthor());
				send("VERSION "+myAI.getVersion());
				send("END_INFO");
				
				//>REQUEST_INFO
				//<BEGIN_INFO
				//<NAME Der Uber Fleet
				//<AUTHOR Michael Banack
				//<VERSION 1.0
				//<END_INFO
			}
			else if(cmd.equals("BEGIN_BATTLE"))
			{
				double width,height;
				curLevel++;
				words = read("BEGIN_INIT_BATTLE");
				width = SpaceText.parseInt(words[1]);
				height = SpaceText.parseInt(words[2]);
				curLevel++;
				words = read("FLEET_ID",2);
				int fleetID = SpaceText.parseInt(words[1]);
				words = read("TEAM_ID",2);
				int teamID = SpaceText.parseInt(words[1]);
				words = read("STARTING_CREDITS",2);
				int startingCredits = SpaceText.parseInt(words[1]);;
				
				words = read("BEGIN_STARTING_SHIPS",2);
				int numShips = SpaceText.parseInt(words[1]);
				curLevel++;
				Ship[] s = new Ship[numShips];
				for(int x=0;x<numShips;x++)
				{
					s[x] = sIn.readShip();
				}
				
				curLevel--;
				words = read("END_STARTING_SHIPS");
				
				words = read("BEGIN_TEAMS");
				int numTeams = SpaceText.parseInt(words[1]);
				curLevel++;
				Team[] t = new Team[numTeams];
				for(int x=0;x<numTeams;x++)
				{
					t[x] = sIn.readTeam();
				}
				curLevel--;
				words = read("END_TEAMS");
				
				
				words = read("BEGIN_FLEETS");
				int numFleets = SpaceText.parseInt(words[1]);				
				curLevel++;
				Fleet[] f = new Fleet[numFleets];
				for(int x=0;x<f.length;x++)
				{
					f[x] = sIn.readFleet();
				}
				curLevel--;
				read("END_FLEETS");
				
				read("END_INIT_BATTLE");
				curLevel--;
				
				Debug.info("Calling initBattle on client AI");
				myShips.makeEmpty();
				myShips.update(s,myAI);
				myAI.initBattle(fleetID, teamID, startingCredits, myShips, t, f,width,height);
				
				send("BATTLE_READY_BEGIN");
				
				//setup teams, starting positions, etc
				//>BEGIN_BATTLE
				//>	BEGIN_INIT_BATTLE width height
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
			else if(cmd.equals("BATTLE_STATUS_UPDATE"))
			{
				int teamID = SpaceText.parseInt(words[1]);
				int fleetID = SpaceText.parseInt(words[2]);
				boolean doa = SpaceText.parseInt(words[3])!= 0;
				//we could send these intermittently to tell fleets when people die...
				//>BATTLE_STATUS_UPDATE teamID deadOrAlive
				myAI.battleStatusUpdate(teamID,fleetID,doa);
			}
			else if(cmd.equals("BEGIN_FLEET_STATUS"))
			{
				curLevel++;
		
				words = read("TICK",2);
				int tick = SpaceText.parseInt(words[1]);
				
				words = read("CREDITS",2);
				int credits = SpaceText.parseInt(words[1]);
				
				words = read("BEGIN_CONTACT_LIST",2);
				int numContacts = SpaceText.parseInt(words[1]);
				curLevel++;
				
				//read contacts
				ContactList c = new ContactList();
				for(int x=0;x<numContacts;x++)
				{
					readContact(c);
				}		
				
				curLevel--;
				read("END_CONTACT_LIST");
				
				words = read("BEGIN_SHIPS",2);;
				int numShips = SpaceText.parseInt(words[1]);
				curLevel++;
				
				for(int x=0;x<numShips;x++)
				{
					Ship s = sIn.readShip();
					myShips.update(s,myAI);
				}
				
				read("END_SHIPS");
				curLevel--;
				read("END_FLEET_STATUS");
				curLevel--;
				
				Iterator<ShipAction> i = myAI.runTick(tick, credits, c, myShips);
				
				send("BEGIN_FLEET_ACTIONS");
				send("TICK "+tick);
				curLevel++;
				//the -1 signaling unknown size
				send("BEGIN_SHIP_ACTIONS "+-1);
				curLevel++;
				
				while(i.hasNext())					
				{
					ShipAction cur = i.next();
					writeAction(cur);
				}
				
				myShips.reset();
				
				send("END_SHIP_ACTIONS");
				curLevel--;
				send("END_FLEET_ACTIONS");
				curLevel--;
				
				
				
				//<BEGIN_FLEET_ACTIONS
				//<	TICK 803
				//<	BEGIN_SHIP_ACTIONS
				//<		SHIP_ACTION id willMove newHeading newScannerHeading launchWhat
				//< END_SHIP_ACTIONS
				//<END_FLEET_ACTIONS
			}
			else if(cmd.equals("BEGIN_BATTLE_OUTCOME"))
			{
				curLevel++;
				Fleet me = sIn.readFleet();
				
				words = read("BEGIN_TEAMS",2);
				int numTeams = SpaceText.parseInt(words[1]);
				curLevel++;
				Team[] t = new Team[numTeams];
				for(int x=0;x<t.length;x++)
					t[x] = sIn.readTeam();
				curLevel--;
				words = read("BEGIN_FLEETS",2);
				int numFleets = SpaceText.parseInt(words[1]);
				Fleet[] f = new Fleet[numFleets];
				curLevel++;
				for(int x=0;x<f.length;x++)
					f[x] = sIn.readFleet();
				curLevel--;
				read("END_FLEETS");
				curLevel--;
				read("END_BATTLE_OUTCOME");
				curLevel--;
				read("END_BATTLE");
				
				myAI.endBattle(me, t, f);
				
				send("BATTLE_READY_END");
				
				return;//its all over..
				
				//>BEGIN_BATTLE_OUTCOME
				//>YOU fleetID teamID deadOrAlive winOrLose
				//>	BEGIN_TEAMS
				//>		TEAM teamID deadOrAlive winOrLose "Name"
				//>	END_TEAMS
				//>	BEGIN_FLEETS
				//>		FLEET fleetID teamID deadOrAlive winOrLose "Name" "Version"
				//>	END_FLEETS
				//>END_BATTLE_OUTCOME
				
				//>END_BATTLE
				
				//<BATTLE_READY_END
				//now we know the ai is finished fighting (in it's head?)
			}
		}
	}
}
