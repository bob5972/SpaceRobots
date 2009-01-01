package net.banack.spacerobots.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.banack.spacerobots.Fleet;
import net.banack.spacerobots.Ship;
import net.banack.spacerobots.Team;
import net.banack.spacerobots.util.ActionList;
import net.banack.spacerobots.util.ContactList;
import net.banack.util.MethodNotImplementedException;

public class TextProtocol implements AIClientProtocol
{
	private FleetAI myAI;
	private BufferedReader sIn;
	private PrintWriter sOut;
	
	public TextProtocol(FleetAI ai, BufferedReader sIn, PrintWriter sOut)
	{
		throw new MethodNotImplementedException();
	}
}
