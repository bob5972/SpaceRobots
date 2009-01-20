package net.banack.spacerobots.util;

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.ServerFleet;

public class Ship
{	
	private int myID;
	private DPoint myPosition;
	private int myLife;
	private int myCreationTick;
	private int myDeltaLife;
	private double myHeading;
	private double myScannerHeading;
	private int myTypeID;
	private ShipType myType;
	private boolean willMove;
	
	public Ship(int id, int type,ShipType t, double x, double y, int life,int tick,int deltalife)
	{
		myID=id;
		myTypeID = type;
		myPosition = new DPoint(x,y);
		myLife = life;
		willMove=true;
		myScannerHeading=0;
		myType = t;
		myCreationTick = tick;
		myDeltaLife = deltalife;
	}
	
	public Ship(int id, int type,ShipType t, double x, double y, double heading, double scannerH, int life,int tick,int deltalife)
	{
		myID=id;
		myTypeID = type;
		myPosition = new DPoint(x,y);
		myLife = life;
		willMove=true;
		myScannerHeading=scannerH;
		myHeading = heading;
		myType = t;
		myCreationTick = tick;
		myDeltaLife = deltalife;
	}
	
	public Ship(int id, int type,ShipType t, double x, double y, int life,int tick)
	{
		myID=id;
		myTypeID = type;
		myPosition = new DPoint(x,y);
		myLife = life;
		willMove=true;
		myScannerHeading=0;
		myType = t;
		myCreationTick = tick;
		myDeltaLife=0;
	}
	
	public void decrementLife(int d)
	{
		myDeltaLife += d;
		myLife -= d;
	}
	
	public int getCost()
	{
		return myType.getCost();
	}
	
	public void reset()
	{
		myDeltaLife=0;
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

	public int getFleetID()
	{
		return getFleetID();
	}
	
	public int getDeltaLife()
	{
		return myDeltaLife;
	}
	
	
	
	//Status Functions
	public double getX()
	{
		return myPosition.getX();
	}
	public final double getXPos()
	{
		return getX();
	}
	public double getY()
	{
		return myPosition.getY();
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
		
	public boolean isAlive()
	{
		return getLife()>0;
	}
	
	public int getTypeID()
	{
		return myTypeID;
	}
	
	public ShipType getType()
	{
		return myType;
	}
	
	public double getMaxSpeed()
	{
		return myType.getMaxSpeed();
	}
	
	public boolean getCanStop()
	{
		return myType.getCanStop();
	}
	
	public double getMaxTurningRate()
	{
		return myType.getMaxTurningRate();
	}
	
	public int getMaxTickCount()
	{
		return myType.getMaxTickCount();
	}
	
	public boolean getCanMoveScanner()
	{
		return myType.getCanMoveScanner();
	}
	
	public void setX(double x)
	{
		myPosition = new DPoint(x,myPosition.getY());
	}
	public void setY(double y)
	{
		myPosition = new DPoint(myPosition.getX(),y);
	}
	
	public void setPosition(DPoint p)
	{
		myPosition = p;
	}
	
	public void addPosition(DPoint offset)
	{
		myPosition = myPosition.add(offset);
	}
	
	public DPoint getPosition()
	{
		return myPosition;
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
	
	public DDimension getDimension()
	{
		return new DDimension(myType.getWidth(),myType.getHeight());
	}
	
	public DQuad getLocation()
	{
		return SpaceMath.getDQuad(myPosition,myType.getWidth(),myType.getHeight(),myHeading);
	}
	
	public DArc getScannerArc()
	{
		DArc oup = new DArc(myPosition, myType.getScannerRadius(),myScannerHeading,myType.getScannerAngleSpan());
		oup.rotate(-myType.getScannerAngleSpan()/2);
		return oup;
	}
	
	public String toString()
	{
		return SpaceText.toString(this);
	}
}
