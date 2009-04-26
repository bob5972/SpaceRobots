package net.banack.spacerobots.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

import net.banack.spacerobots.Debug;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;
import net.banack.spacerobots.util.Team;

public class BinaryProtocol implements AIClientProtocol
{
	private AIFleet myAI;
	private DataInputStream sIn;
	private DataOutputStream sOut;
	private AIShipList myShips;
	
	public static final int REQUEST_INFO = 10;
	public static final int BEGIN_INFO = 11;
	public static final int BEGIN_BATTLE = 20;
	public static final int BATTLE_READY_BEGIN = 21;
	public static final int BATTLE_STATUS_UPDATE = 30;
	public static final int BEGIN_FLEET_ACTIONS = 31;
	public static final int BEGIN_FLEET_STATUS = 40;
	public static final int BEGIN_BATTLE_OUTCOME = 50;
	public static final int END_BATTLE = 51;
	public static final int BATTLE_READY_END = 52;
	
	public static final int SHIPACTION_ID = 100;
	public static final int CONTACT_ID = 200;
	public static final int SHIP_ID = 300;
	public static final int FLEET_ID = 400;
	public static final int TEAM_ID = 500;
	
	
	public BinaryProtocol(AIFleet ai, DataInputStream sIn, DataOutputStream sOut)
	{
		myAI = ai;
		this.sIn = sIn;
		this.sOut = sOut;
		myShips=new AIShipList();
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
		int cmd=0;
		while(true)
		{
			cmd = sIn.readInt();
			
			switch(cmd)
			{
				case REQUEST_INFO:
				{
					Debug.info("Client Received REQUEST_INFO");
					sOut.writeInt(BEGIN_INFO);
					
					sOut.writeInt(myAI.getName().length());
					sOut.writeChars(myAI.getName());
					
					sOut.writeInt(myAI.getAuthor().length());
					sOut.writeChars(myAI.getAuthor());
					
					sOut.writeInt(myAI.getVersion().length());
					sOut.writeChars(myAI.getVersion());
					sOut.flush();
					Debug.verbose("Finished REQUEST_INFO");
				}
				break;
				case BEGIN_BATTLE:
				{
					double width,height;
					int fleetID,teamID,startingCredits,numShips;
					Debug.verbose("Client entering BEGIN_BATTLE");
					
					width = sIn.readDouble();
					height = sIn.readDouble();
					fleetID = sIn.readInt();
					teamID = sIn.readInt();
					startingCredits = sIn.readInt();
					
					numShips = sIn.readInt();
					Ship[] s = new Ship[numShips];
					
					for(int x=0;x<numShips;x++)
					{
						s[x] = readShip();
					}
					Debug.verbose("Client read ships...");
					
					int numTeams = sIn.readInt();
					Team[] t = new Team[numTeams];
					
					for(int x=0;x<numTeams;x++)
					{
						t[x] = readTeam();
					}
					Debug.verbose("Client read teams...");
									
					int numFleets = sIn.readInt();
					Fleet[] f = new Fleet[numFleets];
					for(int x=0;x<f.length;x++)
					{
						f[x] = readFleet();
					}
					Debug.verbose("Client read fleets...");
					
										
					Debug.info("Calling initBattle on client AI");
					myShips.makeEmpty();
					myShips.update(s,myAI);
					myAI.initBattle(fleetID, teamID, startingCredits, myShips, t, f,width,height);
					
					sOut.writeInt(BATTLE_READY_BEGIN);
					sOut.flush();
					Debug.verbose("Client leaving BEGIN_BATTLE");
				}
				break;
				case BATTLE_STATUS_UPDATE:
				{
					int teamID = sIn.readInt();
					int fleetID = sIn.readInt();
					boolean doa = sIn.readBoolean();
					myAI.battleStatusUpdate(teamID,fleetID,doa);
				}
				break;
				case BEGIN_FLEET_STATUS:
				{
					int tick = sIn.readInt();
					
					int credits = sIn.readInt();
					
					int numContacts = sIn.readInt();
					
					//read contacts
					ContactList c = new ContactList();
					for(int x=0;x<numContacts;x++)
					{
						readContact(c);
					}		
					
					int numShips = sIn.readInt();
					for(int x=0;x<numShips;x++)
					{
						Ship s = readShip();
						myShips.update(s,myAI);
					}
					
					Iterator<ShipAction> i = myAI.runTick(tick, credits, c, myShips);
					
					sOut.writeInt(BEGIN_FLEET_ACTIONS);
					sOut.writeInt(tick);
					
					net.banack.util.Queue<ShipAction> qa = new net.banack.util.Queue<ShipAction>(); 
					while(i.hasNext())
						qa.enqueue(i.next());
					
					sOut.writeInt(qa.size());
					
					while(!qa.isEmpty())					
					{
						ShipAction cur = qa.dequeue();
						writeAction(cur);
					}
					sOut.flush();
					myShips.reset();
				}
				break;
				case BEGIN_BATTLE_OUTCOME:
				{
					Debug.verbose("Client received BEGIN_BATTLE_OUTCOME");
					
					Fleet me = readFleet();
					
					int numTeams = sIn.readInt();
					Team[] t = new Team[numTeams];
					for(int x=0;x<t.length;x++)
						t[x] = readTeam();

					int numFleets = sIn.readInt();
					Fleet[] f = new Fleet[numFleets];
					for(int x=0;x<f.length;x++)
						f[x] = readFleet();
					
					int tmp = sIn.readInt();
					if(tmp != END_BATTLE)
						Debug.crash("Expected END_BATTLE ("+END_BATTLE+") but received "+tmp);
					
					myAI.endBattle(me, t, f);
					
					sOut.writeInt(BATTLE_READY_END);
					sOut.flush();
					
					Debug.verbose("Client AI exiting...");
					return;//its all over..
				}				
				//implied break here is unreachable due to return
				//break;
				default:
					Debug.crash("Client Received Unknown Token: "+cmd);
			}
		}
	}
	
	private void writeAction(ShipAction a) throws IOException
	{
		sOut.writeInt(SHIPACTION_ID);
		sOut.writeInt(a.getShipID());
		sOut.writeBoolean(a.willMove());
		sOut.writeDouble(a.getHeading());
		sOut.writeDouble(a.getScannerHeading());
		sOut.writeInt(a.getLaunch());	
	}
	
	private void readContact(ContactList c) throws IOException
	{		
		int cmd = sIn.readInt();
		if(cmd != CONTACT_ID)
			Debug.crash("Bad Server Response: Expected CONTACT_ID ("+CONTACT_ID+"), received "+cmd);
				
		int eID = sIn.readInt();
		int fleetID = sIn.readInt();
		int typeID = sIn.readInt();
		double x = sIn.readDouble();
		double y = sIn.readDouble();
		double heading = sIn.readDouble();
		int numSpotters = sIn.readInt();
		
		Contact ghost = new Contact(eID,fleetID,typeID,x,y,heading);
		
		HashSet<Integer> spotters = new HashSet<Integer>();
		
		for(int i=0;i<numSpotters;i++)
		{
			int sID = sIn.readInt();
			spotters.add(new Integer(sID));
		}
		
		c.addContact(ghost,spotters);
	}
	
	private Ship readShip() throws IOException
	{
		int cmd = sIn.readInt();
		if(cmd != SHIP_ID)
			Debug.crash("Bad Server Response: Expected SHIP_ID ("+SHIP_ID+"), received "+cmd);
		int id = sIn.readInt();
		int type = sIn.readInt();
		double x = sIn.readDouble();
		double y = sIn.readDouble();
		double heading = sIn.readDouble();
		double scannerHeading = sIn.readDouble();
		int creationTick = sIn.readInt();
		int life = sIn.readInt();
		int deltaLife = sIn.readInt();
		int firingDelay = sIn.readInt();

		Ship s = new Ship(id,type,DefaultShipTypeDefinitions.getShipType(type),x,y,heading,scannerHeading,creationTick,life,deltaLife);
		s.setLaunchDelay(firingDelay);
		return s;
	}
	
	private Team readTeam() throws IOException
	{
		int cmd= sIn.readInt();
		if(cmd != TEAM_ID)
			Debug.crash("Bad Server Response: Expected TEAM_ID ("+TEAM_ID+"), received "+cmd);
		
		int teamID = sIn.readInt();
		int nameLength = sIn.readInt();
		StringBuffer name = new StringBuffer(nameLength);
		for(int x=0;x<nameLength;x++)
		{
			name.append(sIn.readChar());
		}
		
		return new Team(teamID,name.toString());		
	}
	
	private Fleet readFleet() throws IOException
	{
		int cmd = sIn.readInt();
		if(cmd != FLEET_ID)
			Debug.crash("Bad Server Response: Expected FLEET_ID ("+FLEET_ID+"), received "+cmd);
		
		int fleetID = sIn.readInt();
		int teamID = sIn.readInt();
		
		int fNameL = sIn.readInt();
		Debug.verbose("Client fleetNameLength = "+fNameL);
		StringBuffer fName = new StringBuffer(fNameL);
		for(int x=0;x<fNameL;x++)
		{
			fName.append(sIn.readChar());
		}
		
		
		int aiNameL = sIn.readInt();
		StringBuffer aiName = new StringBuffer(aiNameL);
		for(int x=0;x<aiNameL;x++)
		{
			aiName.append(sIn.readChar());
		}
		
		int aiAuthorL = sIn.readInt();
		StringBuffer aiAuthor = new StringBuffer(aiAuthorL);
		for(int x=0;x<aiAuthorL;x++)
		{
			aiAuthor.append(sIn.readChar());
		}
		
		int aiVersionL = sIn.readInt();
		StringBuffer aiVersion = new StringBuffer(aiVersionL);
		for(int x=0;x<aiVersionL;x++)
		{
			aiVersion.append(sIn.readChar());
		}
		
		boolean isAlive = sIn.readBoolean();
		
		return new Fleet(fleetID,teamID, fName.toString(),aiName.toString(),aiAuthor.toString(),aiVersion.toString(),isAlive);
	}
}
