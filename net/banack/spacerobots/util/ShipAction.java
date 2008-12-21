package net.banack.spacerobots.util;

import net.banack.util.MethodNotImplementedException;

public class ShipAction
{
	private int myShipID;
	private boolean willMove;
	private int newHeading;
	private int newScannerHeading;
	private int launchWhat;
	
	public ShipAction(int id, boolean move, int heading, int scannerHeading, int launch)
	{
		myShipID = id;
		willMove = move;
		newHeading = heading;
		newScannerHeading = scannerHeading;
		launchWhat=launch;
	}
	
	public int getShipID()
	{
		return myShipID;
	}
	
	public boolean isSpawn()
	{
		throw new MethodNotImplementedException();
	}
	
	public boolean willMove()
	{
		return willMove;
	}
	
	public int getHeading()
	{
		return  newHeading;
	}
	
	public int getScannerHeading()
	{
		return newScannerHeading;
	}
	
	public int getLaunch()
	{
		return launchWhat;
	}
	
	public int hashCode()
	{
		throw new MethodNotImplementedException();
	}
}
