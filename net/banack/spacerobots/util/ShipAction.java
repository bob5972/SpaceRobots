package net.banack.spacerobots.util;

import net.banack.spacerobots.Debug;
import net.banack.util.MethodNotImplementedException;

public class ShipAction
{
	private int myShipID;
	private boolean willMove;
	private double newHeading;
	private double newScannerHeading;
	private int launchWhat;
	
	public ShipAction(Ship s)
	{
		myShipID = s.getID();
		willMove = s.willMove();
		newHeading = s.getHeading();
		newScannerHeading = s.getScannerHeading();
		launchWhat = ShipTypeDefinitions.TYPE_INVALID;		
	}
	
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
		return launchWhat != ShipTypeDefinitions.TYPE_INVALID;
	}
	
	public boolean willMove()
	{
		return willMove;
	}
	
	public void setWillMove(boolean b)
	{
		willMove = b;
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
		return myShipID;
	}
	
	public boolean equals(Object rhs)
	{
		if(!(rhs instanceof ShipAction))
			return false;
		return myShipID == ((ShipAction)rhs).myShipID;
	}
	
	public void setLaunchWhat(int t)
	{
		launchWhat = t;
	}
	
	public void setHeading(double h)
	{
		newHeading = h;
	}
	
	public void setScannerHeading(double h)
	{
		newScannerHeading = h;
	}
}
