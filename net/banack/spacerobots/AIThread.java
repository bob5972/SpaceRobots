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

package net.banack.spacerobots;

import java.io.InputStream;
import java.io.OutputStream;

import net.banack.spacerobots.comm.ClientAIProtocol;
import net.banack.spacerobots.comm.ClientProtocolFactory;

public class AIThread extends Thread
{
	private net.banack.spacerobots.ai.AIFleet myAI;
	private InputStream myInp;
	private OutputStream myOup;
	
	public AIThread(net.banack.spacerobots.ai.AIFleet ai, InputStream inp, OutputStream oup)
	{
		super();
		myAI = ai;
		myInp = inp;
		myOup = oup;
		setDaemon(true);
	}
	
	public void run()
	{
		Debug.info("Thread starting...");
		ClientAIProtocol p = ClientProtocolFactory.doHandshake(myAI, myInp, myOup);
		p.start();
		Debug.info("Thread ending...");
	}
	
}
