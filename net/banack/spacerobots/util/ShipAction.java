package net.banack.spacerobots.util;

import net.banack.util.MethodNotImplementedException;

public class ShipAction
{
	private int myShipID;
	private boolean willMove;
	private double newHeading;
	private double newScannerHeading;
	private int launchWhat;
	
	public ShipAction(int id, boolean move, double heading, double scannerHeading, int launch)
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
	
	public double getHeading()
	{
		return  newHeading;
	}
	
	public double getScannerHeading()
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
