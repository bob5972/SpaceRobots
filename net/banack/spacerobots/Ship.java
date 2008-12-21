package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;

public class Ship {
	//Internal Representation of a Ship
	
	//the maximum heading is HEADING_MAX
	//so HEADING_WRAP is really the mod value
	//(but I wasn't creative enough for a better name)
	static final int HEADING_WRAP = 64;
	static final int HEADING_MAX = HEADING_WRAP-1;
	
	private int myID;
	private int myXPos;
	private int myYPos;
	private int myLife;
	private int deltaLife;
	private int myHeading;
	private int myTypeID;
	private Fleet myFleet;
	
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
		return (0 <= h && h <= HEADING_MAX);
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
		throw new MethodNotImplementedException();
	}
	public int getHeading()
	{
		return myHeading;
	}
	public int getLife()
	{
		return myLife;
	}
	
	public boolean isAlive()
	{
		return getLife()>0;
	}
	
	public int getType()
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
