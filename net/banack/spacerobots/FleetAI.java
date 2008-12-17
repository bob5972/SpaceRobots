package net.banack.spacerobots;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.ActionList;

public class FleetAI
{
	private static final String PROGRAM_NAME = "SpaceRobots Server version 1";
	private static final String PROTOCOL_VERSION = "1";
	//may want to switch to protocol names? like TEXT_1, BIN_1 ... I dunno
	//I'm also thinking it would be nice to abstract protocols into it's own class
	//	It's a little more work, but it would make new protocols seamless
	//	and allow switching from "text" to "binary" for debug a pinch
	
	
	private BufferedReader myInp;
	private PrintWriter myOup;

	private String myAuthor;
	private String myVersion;
	private String myName;
	
	
	public FleetAI(InputStream inp,OutputStream oup)
	{
		myInp = new BufferedReader(new InputStreamReader(inp));
		myOup= new PrintWriter(oup);
		
		doHandshake();
		
		loadInfo();
	}
	
	private void doHandshake()
	{
		//TODO: Write communication code
		
		//Greetings
		//>SERVER_HELLO from PROGRAM_NAME
		//<CLIENT_HELLO from AI_NAME
		
		//Agree on a protocol
		//>USING_PROTOCOL 1
		//>ACK_PROTOCOL 1
		
		//If the client says this, then do something else
		//	(i'm too lazy to write it now, and it doesn't matter til we get more protocols)
		//<REJECT_PROTOCOL 1
		
		throw new MethodNotImplementedException();
	}
	
	private void loadInfo()
	{
		//>REQUEST_INFO
		//<BEGIN_INFO
		//<	AUTHOR Michael Banack
		//<	NAME Der Uber Fleet
		//<	VERSION 1.0
		//<END_INFO
		
		//store info in FleetAI
		throw new MethodNotImplementedException();
	}
	
	public void initBattle()
	{
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
		//>			TEAM teamID friendOrFoe "Name"
		//>		END_TEAMS
		//
		//>		BEGIN_FLEETS
		//>			BEGIN_FLEET fleetID teamID friendOrFoe "Name" "Version"
		//>				STARTING_CREDITS 1000
		//>				BEGIN_STARTING_SHIPS
		//>					SHIP iD? type xPos? yPos?
		//>				END_STARTING_SHIPS
		//>			END_FLEET
		//>		END_FLEETS
		//>	END_INIT_BATTLE
		
		//let everyone initialize before starting, so require and acknowledgement
		//<BATTLE_READY_BEGIN
		
		//it would make more sense to use something like "ACK_INIT_BATTLE"
		//	but its way cooler to go "BATTLE_READY"
		
		
		//the END_BATTLE would be after it's all over...
		//this way we can re-use sockets between matches
		
		throw new MethodNotImplementedException();
	}
	
	public void beginFleetStatusUpdate()
	{
//		we could send these intermittantly to tell fleets when people die...
		//>BATTLE_STATUS_UPDATE teamID deadOrAlive winOrLose
		
		
		//>BEGIN_FLEET_STATUS
		//>	TICK 803
		//>	CREDITS 100
		//> NUM_CONTACTS 13
		//>	BEGIN_CONTACT_LIST
		//>		CONTACT fleetID type xPos yPos heading (refiD refiD )
		//>	END_CONTACT_LIST
		//>	NUM_SHIPS 25
		//> BEGIN_SHIPS
		throw new MethodNotImplementedException();
	}
	
	public void writeShip(Ship s)
	{
		//write a single ship
		//>		SHIP iD type xPos yPos heading scannerHeading life deltaLife
		throw new MethodNotImplementedException();
	}
	
	public void endFleetStatusUpdate()
	{
		//>	END_SHIPS
		//>END_FLEET_STATUS
		throw new MethodNotImplementedException();
	}
	
	public ActionList readFleetActions()
	{
		//<BEGIN_FLEET_ACTIONS
		//<	TICK 803
		//<	BEGIN_SHIPS
		//<		SHIP id willMove newHeading newScannerHeading launchWhat
		//< END_SHIPS
		//<END_FLEET_ACTIONS
		throw new MethodNotImplementedException();
	}
	
	public ActionList processAI(Ship[] s)
	{
		
		beginFleetStatusUpdate();
		for(int x=0;x<s.length;x++)
		{
			writeShip(s[x]);
		}
		endFleetStatusUpdate();
		
		return readFleetActions();
	}
	
	public void endBattle()
	{
		//notify ai's of outcome?
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
		
		
		throw new MethodNotImplementedException();
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
