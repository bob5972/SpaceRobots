package net.banack.spacerobots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.banack.io.Parser;
import net.banack.spacerobots.util.ActionList;
import net.banack.util.MethodNotImplementedException;

public class TextProtocol implements AIProtocol
{
	private Parser sIn;
	private PrintWriter sOut;
	
	public TextProtocol(Reader in, PrintWriter out)
	{
		sIn = new Parser(in);
		sOut = out;
	}
	
	//Returns {name, author,version}
	public String[] loadInfo()
	{
		String[] oup = new String[3];
		String cur[];
		String temp;
		
		//>REQUEST_INFO
		sOut.println("REQUEST_INFO");
		
		try{
			cur = sIn.readWords();
			
			//<BEGIN_INFO
			if(cur.length != 1 || !cur[0].equals("BEGIN_INFO"))
				throw new MethodNotImplementedException("No error handler");
		
			temp = sIn.parseWord();
			while(!temp.equals("END_INFO"))
			{
				if(temp.equals("NAME"))
				{//<NAME Der Uber Fleet
					cur[0] = sIn.readLine();
				}
				else if(temp.equals("AUTHOR"))
				{//<AUTHOR Michael Banack
					cur[1] = sIn.readLine();
				}
				else if(temp.equals("VERSION"))
				{//<VERSION 1.0
					cur[2] = sIn.readLine();
				}
				else
					throw new MethodNotImplementedException("No error handler");
				
				temp = sIn.parseWord();
			}
				
			//<END_INFO
			return oup;
		}
		catch(IOException e)
		{
			throw new MethodNotImplementedException("No error handler",e);
		}
	}
	
	public void initBattle(int fleetID,int teamID, int startingCredits, Ship[] s, Team[] t, Fleet[] f)
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
	
	public void beginFleetStatusUpdate(int tick, int credits, SensorContact[] c, int numShips)
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
	
	public void endBattle(Fleet me, Fleet[] f)
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
}
