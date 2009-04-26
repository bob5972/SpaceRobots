package net.banack.spacerobots;

import java.io.InputStream;
import java.io.OutputStream;

import net.banack.spacerobots.ai.AIClientProtocol;
import net.banack.spacerobots.ai.ClientProtocolFactory;

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
		AIClientProtocol p = ClientProtocolFactory.doHandshake(myAI,myInp,myOup);
		p.start();
		Debug.info("Thread ending...");
	}
		
}
