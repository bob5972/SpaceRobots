package net.banack.spacerobots;

import net.banack.spacerobots.util.ShipType;
import net.banack.util.MethodNotImplementedException;

public class Ship {
	//Internal Representation of a Ship
	
	private int myID;
	private int myXPos;
	private int myYPos;
	private int myLife;
	private int deltaLife;
	private int myHeading;
	private int myScannerHeading;
	private int myTypeID;
	private ShipType myType;
	private Fleet myFleet;
	private boolean willMove;
	
	public Ship(Fleet f, int id, int type,int x, int y, int life)
	{
		myFleet=f;
		myID=id;
		myTypeID = type;
//		if(!isValidShipType(myTypeID))
//			throw new IllegalArgumentException("Invalid Ship Type!");
		myXPos = x;
		myYPos = y;
		myLife = life;
		deltaLife=0;
		willMove=true;
		myScannerHeading=0;
	}
	
	public boolean willMove()
	{
		return willMove;
	}

	public void setWillMove(boolean willMove)
	{
		this.willMove = willMove;
	}

	public Fleet getFleet()
	{
		return myFleet;
	}
	
	public int getFleetID()
	{
		return myFleet.getFleetID();
	}
	
	public void reset()
	{
		deltaLife=0;
		throw new MethodNotImplementedException();
	}
	
	public int getDeltaLife()
	{
		return deltaLife;
	}
	
	
	public boolean isValidHeading(int h)
	{
		return (0 <= h && h <= Battle.HEADING_MAX);
	}
	
	
	
	//Status Functions
	public int getX()
	{
		return myXPos;
	}
	public final int getXPos()
	{
		return getX();
	}
	public int getY()
	{
		return myYPos;
	}
	public final int getYPos()
	{
		return getY();
	}
	public int getScannerHeading()
	{
		return myScannerHeading;
	}
	
	public void setScannerHeading(int h)
	{
		myScannerHeading = h;
	}
	
	public int getHeading()
	{
		return myHeading;
	}
	public int getLife()
	{
		return myLife;
	}
	
	public void decrementLife(int d)
	{
		deltaLife += d;
		myLife -= d;
	}
	
	public boolean isAlive()
	{
		return getLife()>0;
	}
	
	public int getTypeID()
	{
		return myTypeID;
	}
	
	public void setX(int x)
	{
		myXPos = x;
	}
	public void setY(int y)
	{
		myYPos = y;
	}
	public void setHeading(int h)
	{
		if(!isValidHeading(h))
			throw new IllegalArgumentException("Invalid Heading!");
		myHeading = h;
	}
	
	public final int getID()
	{
		return getShipID();
	}
	
	public int getShipID()
	{
		return myID;
	}
		
	public void printStatus(java.io.PrintStream oup)
	{
		oup.println(getClass().getName()+" - ");
		oup.println("----(x,y)=("+myXPos+","+myYPos+")");
		oup.println("----Life="+myLife);
		oup.println("----Heading="+myHeading);
	}
}
