package net.banack.spacerobots.ai;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
		DataInputStream sIn = new DataInputStream(inp);
		DataOutputStream sOut = new DataOutputStream(oup);
		
		String temp;
		StringBuffer b = new StringBuffer();
		char c;
		
		
		// Greetings
		try{
			//>SERVER_HELLO from PROGRAM_NAME
			Debug.info("Waiting for SERVER_HELLO");
			b.setLength(0);
			c = sIn.readChar();
			while(c!= '\n')
			{
				b.append(c);
				c=sIn.readChar();
			}
			temp = b.toString();
			Debug.info("Got something...");
			
			if(!Pattern.matches("\\s*SERVER_HELLO\\s+.*",temp))
				Debug.crash("Invalid Server Response: Expected SERVER_HELLO, got "+temp);
			Debug.info("Found SERVER_HELLO");
			
			//<CLIENT_HELLO from AI_NAME
			sOut.writeChars("CLIENT_HELLO "+PROGRAM_NAME+"\n");
			sOut.flush();
			
			//<USING_PROTOCOL BINARY_1
			sOut.writeChars("USING_PROTOCOL BINARY_1\n");
			sOut.flush();

			b.setLength(0);
			c = sIn.readChar();
			while(c!= '\n')
			{
				b.append(c);
				c=sIn.readChar();
			}
			temp = b.toString();
			
			if(!Pattern.matches("ACK_PROTOCOL BINARY_1",temp))
			{
				if(Pattern.matches("LIST_PROTOCOLS",temp))
				{
					sOut.writeChars("HAVE_PROTOCOLS BINARY_1 TEXT_1\n");
				}
				
				b.setLength(0);
				c = sIn.readChar();
				while(c!= '\n')
				{
					b.append(c);
					c=sIn.readChar();
				}
				temp = b.toString();
				
				if(Pattern.matches("ACK_PROTOCOL BINARY_1",temp))
				{
					sOut.flush();
					return new BinaryProtocol(ai,sIn,sOut);
				}
				else if(Pattern.matches("ACK_PROTOCOL TEXT_1",temp))
				{
					return new TextProtocol(ai,new BufferedReader(new InputStreamReader(sIn)),new PrintWriter(sOut));
				}
				
				Debug.crash("Server does not accept protocols.");
			}
			else
			{
				sOut.flush();
				return new BinaryProtocol(ai,sIn,sOut);
			}
		}
		catch(IOException e)
		{
			Debug.crash(new Exception("Unable to handshake with Server",e));
		}
		return null;//to keep compiler happy
	}
}
