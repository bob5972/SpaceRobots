package net.banack.spacerobots.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.banack.spacerobots.Debug;
import net.banack.util.Stack;

public class ClientProtocolFactory
{
	private static final String PROGRAM_NAME = "SpaceRobots Java Protocol Server version 1";
	
	public static AIClientProtocol doHandshake(FleetAI ai, InputStream inp, OutputStream oup)
	{
		BufferedReader sIn = new BufferedReader(new InputStreamReader(inp));
		PrintWriter sOut = new PrintWriter(oup);
		
		String temp;
		// Greetings
		try{
			//>SERVER_HELLO from PROGRAM_NAME
			Debug.info("Waiting for SERVER_HELLO");
			temp = sIn.readLine();
			Debug.info("Got something...");
			if(!Pattern.matches("SERVER_HELLO\\s+.*",temp))
				Debug.crash("Invalid Server Response: Expected SERVER_HELLO");
			Debug.info("Found SERVER_HELLO");
			
			//<CLIENT_HELLO from AI_NAME
			sOut.println("CLIENT_HELLO "+PROGRAM_NAME);
			sOut.flush();
			
			//<USING_PROTOCOL TEXT_1
			sOut.println("USING_PROTOCOL TEXT_1");
			sOut.flush();

			
			temp = sIn.readLine();
			if(!Pattern.matches("ACK_PROTOCOL TEXT_1",temp))
				Debug.crash("Server does not accept protocol: TEXT_1");
			
			return new TextProtocol(ai,sIn,sOut);
		}
		catch(IOException e)
		{
			Debug.crash(new Exception("Unable to handshake with AI",e));
			return null;//to keep compiler happy
		}
	}
}
