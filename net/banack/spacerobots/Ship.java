package net.banack.spacerobots;

import net.banack.geometry.DDimension;
import net.banack.geometry.DQuad;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.util.MethodNotImplementedException;

public class Ship {
	//Internal Representation of a Ship
	
	private int myID;
	private double myXPos;
	private double myYPos;
	private int myLife;
	private int myCreationTick;
	private int deltaLife;
	private double myHeading;
	private double myScannerHeading;
	private int myTypeID;
	private ShipType myType;
	private Fleet myFleet;
	private boolean willMove;
	
	public Ship(Fleet f, int id, int type,ShipType t, double x, double y, int life,int tick)
	{
		myFleet=f;
		myID=id;
		myTypeID = type;
		myXPos = x;
		myYPos = y;
		myLife = life;
		deltaLife=0;
		willMove=true;
		myScannerHeading=0;
		myType = t;
		myCreationTick = tick;
	}
	
	public int getCreationTick()
	{
		return myCreationTick;
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
	}
	
	public int getDeltaLife()
	{
		return deltaLife;
	}
	
	
	
	//Status Functions
	public double getX()
	{
		return myXPos;
	}
	public final double getXPos()
	{
		return getX();
	}
	public double getY()
	{
		return myYPos;
	}
	public final double getYPos()
	{
		return getY();
	}
	public double getScannerHeading()
	{
		return myScannerHeading;
	}
	
	public void setScannerHeading(double h)
	{
		myScannerHeading = h;
	}
	
	public double getHeading()
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
	
	public void setX(double x)
	{
		myXPos = x;
	}
	public void setY(double y)
	{
		myYPos = y;
	}
	public void setHeading(double h)
	{
		myHeading = SpaceMath.wrapHeading(h);
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
	
	public DDimension getDimension()
	{
		return new DDimension(myType.getWidth(),myType.getHeight());
	}
	
	public DQuad getLocation()
	{
		return SpaceMath.getDQuad(new DPoint(myXPos,myYPos),myType.getWidth(),myType.getHeight(),myHeading);
	}
	
}
