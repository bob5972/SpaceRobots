package net.banack.spacerobots.util;
import java.io.IOException;
import java.io.Reader;

import net.banack.io.Parser;
import net.banack.spacerobots.Debug;

public class SpaceText extends Parser
{
	public SpaceText(Reader r)
	{
		super(r);
	}
	
	
	public static String toString(Fleet f)
	{
		return f.getFleetID()+" "+f.getTeamID()+" "+" \""+f.getFleetName()+"\" \""+f.getAIName()+"\" \""+f.getAIAuthor()+"\" \""+f.getAIVersion()+"\" "+(f.isAlive()?1:0)+" "+((f.getWinOrLose()==Fleet.STATUS_WIN)?1:0);
	}
	
	public Fleet parseFleet(String str)
	{
		return parseFleet(parseWords(str));
	}
	
	public Fleet readFleet() throws IOException
	{
		String temp = readWord();
		String[] words = readWords();
		if(!temp.equals("YOU") && !temp.equals("FLEET"))
		{
			String[] s = new String[words.length+1];
			s[0]=temp;
			for(int x=0;x<words.length;x++)
			{
				s[x+1]=words[x];
			}
			words = s;
		}
		
		return parseFleet(words);		
	}
	
	
	public Fleet parseFleet(String[] words)
	{
		int fleetID = parseInt(words[0]);
		int teamID = parseInt(words[1]);
		
		int sPos = 2;
		int ePos = sPos;
		
		String temp = words[ePos];
		while(!(temp.charAt(temp.length()-1)=='"'))
		{
			ePos++;
			temp = words[ePos];
		}
		
		StringBuffer sbTemp=new StringBuffer();
		for(int x=sPos;x<=ePos;x++)
		{
			sbTemp.append(words[x]);
			sbTemp.append(" ");
		}
		sbTemp.deleteCharAt(0);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		String fName = sbTemp.toString();
		
		sbTemp.setLength(0);
		ePos++;
		sPos=ePos;
		temp = words[ePos];
		while(!(temp.charAt(temp.length()-1)=='"'))
		{
			ePos++;
			temp = words[ePos];
		}
		sbTemp=new StringBuffer();
		for(int x=3;x<=ePos;x++)
		{
			sbTemp.append(words[x]);
			sbTemp.append(" ");
		}
		sbTemp.deleteCharAt(0);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		String aiName = sbTemp.toString();
		
		sbTemp.setLength(0);
		ePos++;
		sPos=ePos;
		temp = words[ePos];
		while(!(temp.charAt(temp.length()-1)=='"'))
		{
			ePos++;
			temp = words[ePos];
		}
		sbTemp=new StringBuffer();
		for(int x=sPos;x<=ePos;x++)
		{
			sbTemp.append(words[x]);
			sbTemp.append(" ");
		}
		sbTemp.deleteCharAt(0);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		String aiAuthor = sbTemp.toString();

		
		sbTemp.setLength(0);
		ePos++;
		sPos=ePos;
		temp = words[ePos];
		while(!(temp.charAt(temp.length()-1)=='"'))
		{
			ePos++;
			temp = words[ePos];
		}
		sbTemp=new StringBuffer();
		for(int x=sPos;x<=ePos;x++)
		{
			sbTemp.append(words[x]);
			sbTemp.append(" ");
		}
		sbTemp.deleteCharAt(0);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		String aiVersion = sbTemp.toString();
		
		boolean isAlive = parseInt(words[ePos+1])!=0;
		boolean winOrLose = parseInt(words[ePos+2])!=0;
		
		
		return new Fleet(fleetID,teamID,fName,aiName, aiAuthor, aiVersion,isAlive,winOrLose);
	}
	
	public ShipAction readAction() throws IOException
	{
		String temp = readWord();
		String[] words = readWords();
		if(!temp.equals("SHIP_ACTION"))
		{
			String[] s = new String[words.length+1];
			s[0]=temp;
			for(int x=0;x<words.length;x++)
			{
				s[x+1]=words[x];
			}
			words = s;
		}
		
		return parseAction(words);		
	}
	
	public static ShipAction parseAction(String s)
	{
		return parseAction(parseWords(s));
	}
	
	public static ShipAction parseAction(String[] inp)
	{
		//<SHIP_ACTION id willMove newHeading newScannerHeading launchWhat
		if(inp.length < 5)
			throw new IllegalArgumentException("Invalid ShipAction String: Expected 6 parameters, received "+inp.length);
		int id = parseInt(inp[0]);
		int willMove = parseInt(inp[1]);
		double newHeading = SpaceMath.degToRad(parseInt(inp[2]));
		double newScannerHeading = SpaceMath.degToRad(parseInt(inp[3]));
		int launchWhat = parseInt(inp[4]);
		return new ShipAction(id,willMove!=0,newHeading,newScannerHeading,launchWhat);
	}
	
	public static String toString(ShipAction a)
	{
		StringBuffer oup = new StringBuffer();
		oup.append(a.getShipID());
		oup.append(" ");
		oup.append(a.willMove()?1:0);
		oup.append(" ");
		oup.append((int)SpaceMath.radToDeg(a.getHeading()));
		oup.append(" ");
		oup.append((int)SpaceMath.radToDeg(a.getScannerHeading()));
		oup.append(" ");
		oup.append(a.getLaunch());
		return oup.toString();		
	}
	
	public Ship readShip() throws IOException
	{
		//>		SHIP iD type xPos yPos heading scannerHeading creationTick life deltaLife launchDelay
		String temp = readWord();
		String[] words = readWords();
		if(!temp.equals("SHIP"))
		{
			String[] s = new String[words.length+1];
			s[0]=temp;
			for(int x=0;x<words.length;x++)
			{
				s[x+1]=words[x];
			}
			words = s;
		}
		
		return parseShip(words);
	}
	
	public static String toString(Ship s)
	{
		//>		SHIP iD type xPos yPos heading scannerHeading creationTick life deltaLife launchDelay
		StringBuffer oup = new StringBuffer();
		
		oup.append(s.getID());
		oup.append(" ");
		oup.append(s.getTypeID());
		oup.append(" ");
		oup.append(((int)s.getX()));
		oup.append(" ");
		oup.append(((int)s.getY()));
		oup.append(" ");
		oup.append(((int)SpaceMath.radToDeg(s.getHeading())));
		oup.append(" ");
		oup.append(((int)SpaceMath.radToDeg(s.getScannerHeading())));
		oup.append(" ");
		oup.append(s.getCreationTick());
		oup.append(" ");
		oup.append(s.getLife());
		oup.append(" ");
		oup.append(s.getDeltaLife());
		oup.append(" ");
		oup.append(s.getLaunchDelay());
		return oup.toString();
	}
	
	public static String prettyPrint(Ship s)
	{
		StringBuffer oup = new StringBuffer();
		
		oup.append("SHIP id=");
		oup.append(s.getID());
		oup.append(" type=");
		oup.append(prettyPrintType(s.getTypeID()));
		oup.append(" pos=(");
		oup.append(((int)s.getX()));
		oup.append(",");
		oup.append(((int)s.getY()));
		oup.append(") h=");
		oup.append(((int)SpaceMath.radToDeg(s.getHeading())));
		oup.append(" sh=");
		oup.append(((int)SpaceMath.radToDeg(s.getScannerHeading())));
		oup.append(" tick=");
		oup.append(s.getCreationTick());
		oup.append(" life=");
		oup.append(s.getLife());
		oup.append(" dl=");
		oup.append(s.getDeltaLife());
		oup.append(" delay=");
		oup.append(s.getLaunchDelay());
		return oup.toString();		
	}
	
	public static String prettyPrintType(int t)
	{
		switch(t)
		{
			case DefaultShipTypeDefinitions.CRUISER_ID:
				return "Cruiser";
			case DefaultShipTypeDefinitions.FIGHTER_ID:
				return "Fighter";
			case DefaultShipTypeDefinitions.DESTROYER_ID:
				return "Destroyer";
			case DefaultShipTypeDefinitions.ROCKET_ID:
				return "Rocket";
			case DefaultShipTypeDefinitions.MISSILE_ID:
				return "Missile";
		}
		return Integer.toString(t);
	}
	
	public static Ship parseShip(String[] words)
	{
		//>		SHIP iD type xPos yPos heading scannerHeading creationTick life deltaLife launchDelay
		int id = parseInt(words[0]);
		int type = parseInt(words[1]);
		double x = parseInt(words[2]);
		double y = parseInt(words[3]);
		double heading = SpaceMath.degToRad(parseInt(words[4]));
		double scannerHeading = SpaceMath.degToRad(parseInt(words[5]));
		int creationTick = parseInt(words[6]);
		int life = parseInt(words[7]);
		int deltaLife = parseInt(words[8]);
		int firingDelay = parseInt(words[9]);
		
		Ship s = new Ship(id,type,DefaultShipTypeDefinitions.getShipType(type),x,y,heading,scannerHeading,creationTick,life,deltaLife);
		s.setLaunchDelay(firingDelay);
		return s;
	}
	
	public Team readTeam() throws IOException
	{
		String temp = readWord();
		String[] words = readWords();
		if(!temp.equals("TEAM"))
		{
			String[] s = new String[words.length+1];
			s[0]=temp;
			for(int x=0;x<words.length;x++)
			{
				s[x+1]=words[x];
			}
			words = s;
		}
		
		return parseTeam(words);	
	}
	
	public static Team parseTeam(String[] words)
	{
		int teamID = parseInt(words[0]);
		
		int sPos = 1;
		int ePos = sPos;
		
		String temp = words[ePos];
		while(!(temp.charAt(temp.length()-1)=='"'))
		{
			ePos++;
			temp = words[ePos];
		}
		
		StringBuffer sbTemp=new StringBuffer();
		for(int x=sPos;x<=ePos;x++)
		{
			sbTemp.append(words[x]);
			sbTemp.append(" ");
		}
		sbTemp.deleteCharAt(0);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		sbTemp.deleteCharAt(sbTemp.length()-1);
		String teamName = sbTemp.toString();
		
		return new Team(teamID,teamName);
	}
	
}
