package net.banack.spacerobots;

import java.io.InputStream;
import java.io.OutputStream;

import net.banack.spacerobots.ai.AIClientProtocol;
import net.banack.spacerobots.ai.ClientProtocolFactory;

public class AIThread extends Thread
{
	private net.banack.spacerobots.ai.FleetAI myAI;
	private InputStream myInp;
	private OutputStream myOup;
	
	public AIThread(net.banack.spacerobots.ai.FleetAI ai, InputStream inp, OutputStream oup)
	{
		super();
		myAI = ai;
		myInp = inp;
		myOup = oup;
		setDaemon(true);
	}
	
	public void start()
	{
		AIClientProtocol p = ClientProtocolFactory.doHandshake(myAI,myInp,myOup);
		p.start();
	}
		
}
