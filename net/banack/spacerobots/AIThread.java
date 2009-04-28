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
		ClientAIProtocol p = ClientProtocolFactory.doHandshake(myAI,myInp,myOup);
		p.start();
		Debug.info("Thread ending...");
	}
		
}
