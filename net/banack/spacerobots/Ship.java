package net.banack.spacerobots;

import net.banack.util.MethodNotImplementedException;

public class Ship {
	//Internal Representation of a Ship
	
	static final int TYPE_INVALID   = -1;
	static final int TYPE_CRUISER   = 1;
	static final int TYPE_DESTROYER = 2;
	static final int TYPE_FIGHTER   = 3;
	static final int TYPE_MISSILE	= 4;
	static final int TYPE_ROCKET	= 5;
	
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
	private int myType;
	
	public Ship(int id, int type,int x, int y, int life)
	{
		myID=id;
		myType = type;
		if(!isValidShipType(myType))
			throw new IllegalArgumentException("Invalid Ship Type!");
		myXPos = x;
		myYPos = y;
		myLife = life;
		deltaLife=0;
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
	
	//Util Functions
	public boolean isValidShipType(int t)
	{
		switch(t)
		{
			case TYPE_CRUISER:
			case TYPE_DESTROYER:
			case TYPE_FIGHTER:
				return true;
		}
		return false;
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
	public int getY()
	{
		return myYPos;
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
		return myType;
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
