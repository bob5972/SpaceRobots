/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with SpaceRobots. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots.comm;

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
import net.banack.spacerobots.ai.AIFleet;
import net.banack.util.Stack;

public class ClientProtocolFactory
{
	private static final String PROGRAM_NAME = "SpaceRobots Java Protocol Server version 1";
	
	public static ClientAIProtocol doHandshake(AIFleet ai, InputStream inp, OutputStream oup)
	{
		DataInputStream sIn = new DataInputStream(inp);
		DataOutputStream sOut = new DataOutputStream(oup);
		
		String temp;
		StringBuffer b = new StringBuffer();
		char c;
		

		// Greetings
		try {
			// >SERVER_HELLO from PROGRAM_NAME
			Debug.verbose("Waiting for SERVER_HELLO");
			b.setLength(0);
			c = sIn.readChar();
			while (c != '\n') {
				b.append(c);
				c = sIn.readChar();
			}
			temp = b.toString();
			Debug.verbose("\tGot something...");
			
			if (!Pattern.matches("\\s*SERVER_HELLO\\s+.*", temp))
				Debug.crash("Invalid Server Response: Expected SERVER_HELLO, got " + temp);
			Debug.verbose("Found SERVER_HELLO");
			
			// <CLIENT_HELLO from AI_NAME
			sOut.writeChars("CLIENT_HELLO " + PROGRAM_NAME + "\n");
			sOut.flush();
			
			// <USING_PROTOCOL BINARY_1
			sOut.writeChars("USING_PROTOCOL BINARY_1\n");
			sOut.flush();
			
			b.setLength(0);
			c = sIn.readChar();
			while (c != '\n') {
				b.append(c);
				c = sIn.readChar();
			}
			temp = b.toString();
			
			if (!Pattern.matches("ACK_PROTOCOL\\s+.*", temp)) {
				if (Pattern.matches("LIST_PROTOCOLS", temp)) {
					sOut.writeChars("HAVE_PROTOCOLS BINARY_1 TEXT_1\n");
				}
				
				b.setLength(0);
				c = sIn.readChar();
				while (c != '\n') {
					b.append(c);
					c = sIn.readChar();
				}
				temp = b.toString();
			}
			
			if (Pattern.matches("\\s*ACK_PROTOCOL\\s+BINARY_1", temp)) {
				sOut.flush();
				return new BinaryProtocolClient(ai, sIn, sOut);
			} else if (Pattern.matches("\\s*ACK_PROTOCOL\\s+TEXT_1", temp)) {
				return new TextProtocolClient(ai, new BufferedReader(new InputStreamReader(sIn)), new PrintWriter(sOut));
			}
			
			Debug.crash("Server does not accept protocols.");
		} catch (IOException e) {
			Debug.crash(new Exception("Unable to handshake with Server", e));
		}
		return null;// to keep compiler happy
	}
}
