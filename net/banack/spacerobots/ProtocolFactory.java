package net.banack.spacerobots;

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

import net.banack.util.MethodNotImplementedException;
import net.banack.util.Stack;

public class ProtocolFactory
{
	private static final String PROGRAM_NAME = "SpaceRobots Server version 1";
	
	public static AIProtocol doHandshake(InputStream inp, OutputStream oup)
	{
		DataInputStream sIn = new DataInputStream(inp);
		DataOutputStream sOut = new DataOutputStream(oup);
		
		String temp;
		StringBuffer b = new StringBuffer();
		char c;
		Pattern p;
		Matcher m;
		
		// Greetings
		try{
			//>SERVER_HELLO from PROGRAM_NAME
			Debug.info("Sending SERVER_HELLO");
			sOut.writeChars("SERVER_HELLO from "+PROGRAM_NAME+"\n");
			sOut.flush();
			
			b.setLength(0);
			c = sIn.readChar();
			while(c!= '\n')
			{
				b.append(c);
				c=sIn.readChar();
			}
			temp = b.toString();
			
			//<CLIENT_HELLO from AI_NAME
			if(!Pattern.matches("CLIENT_HELLO\\s+.*",temp))
			{
				Debug.crash("Invalid AI Response: Expected CLIENT_HELLO, Received "+temp);
			}
			Debug.info("Received CLIENT_HELLO");
			
			b.setLength(0);
			c = sIn.readChar();
			while(c!= '\n')
			{
				b.append(c);
				c=sIn.readChar();
			}
			temp = b.toString();
			
			//<USING_PROTOCOL TEXT_1
			p = Pattern.compile("USING_PROTOCOL\\s+(\\w*)");
			m = p.matcher(temp);
			if(!m.matches())
			{
				Debug.crash("Invalid AI Response: Expected USING_PROTOCOL");
			}
			
			temp = m.group(1);
			AIProtocol ai = matchProtocol(temp,sIn,sOut);
			
			if(ai != null)
				return ai;
			
			//>LIST_PROTOCOLS
			sOut.writeChars("LIST_PROTOCOLS\n");
			sOut.flush();
			b.setLength(0);
			c = sIn.readChar();
			while(c!= '\n')
			{
				b.append(c);
				c=sIn.readChar();
			}
			temp = b.toString();
			p = Pattern.compile("HAVE_PROTOCOLS\\s+(.*)");
			m = p.matcher(temp);
			if(!m.matches())
			{
				Debug.crash("Invalid AI Response: Expected HAVE_PROTOCOLS");
			}
			temp = m.group(1);
			p = Pattern.compile("\\s*(\\w+)\\s*(.*)");
			m=p.matcher(temp);
			Stack<String> protList = new Stack<String>();
			while(m.matches())
			{
				protList.push(m.group(1));
				temp = m.group(2);
				m=p.matcher(temp);
			}
			while(!protList.isEmpty())
			{
				ai = matchProtocol((String)protList.pop(),sIn,sOut);
				if(ai != null)
					return ai;
			}
			
			Debug.crash("Unable to handshake with AI");
			return null;//to keep compiler happy
		}
		catch(IOException e)
		{
			Debug.crash(new Exception("Unable to handshake with AI",e));
			return null;//to keep compiler happy
		}
	}
		
	private static AIProtocol matchProtocol(String p,DataInputStream sIn, DataOutputStream sOut) throws IOException
	{
		if(p.equals("TEXT_1"))
		{
			//>ACK_PROTOCOL TEXT_1
			sOut.writeChars("ACK_PROTOCOL TEXT_1\n");
			Debug.info("Using Protocol TEXT_1");
			return new TextProtocol(new BufferedReader(new InputStreamReader(sIn)),new PrintWriter(sOut));
		}
		else if(p.equals("BINARY_1"))
		{
			sOut.writeChars("ACK_PROTOCOL BINARY_1\n");
			sOut.flush();
			Debug.info("Using Protocol BINARY_1");
			return new BinaryProtocol(sIn,sOut);
		}
		
		return null;
	}
}
