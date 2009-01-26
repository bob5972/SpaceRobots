package net.banack.spacerobots.util;

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;

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
	private int myLaunchDelay;//number of ticks until the ship can fire again

	public Ship(int id, int type,ShipType t, DPoint pos, double heading, double scannerH, int tick, int life,int deltalife,int firingDelay)
	{
		myID=id;
		myTypeID = type;
		myPosition = pos;
		myLife = life;
		willMove=true;
		myScannerHeading=scannerH;
		myHeading = heading;
		myType = t;
		myCreationTick = tick;
		myDeltaLife = deltalife;
		myLaunchDelay = firingDelay;
	}
	
	public Ship(Ship s)
	{
		this(s.myID,s.myTypeID,s.myType,s.myPosition,s.myHeading,s.myScannerHeading,s.myCreationTick,s.myLife,s.myDeltaLife,s.myLaunchDelay);
		this.willMove=s.willMove;
	}
	
	public Ship(int id, int type,ShipType t, double x, double y, double heading, double scannerH, int tick, int life,int deltalife,int firingDelay)
	{
		this(id,type,t,new DPoint(x,y),heading,scannerH,tick,life,deltalife,firingDelay);
	}
	
	public Ship(int id, int type,ShipType t, double x, double y,int tick, int life, int deltalife)
	{
		this(id,type,t,x,y,0,0,tick,life,deltalife,0);
	}
	
	public Ship(int id, int type,ShipType t, double x, double y, double heading, double scannerH, int tick, int life,int deltalife)
	{
		this(id,type,t,x,y,heading,scannerH,tick,life,deltalife,0);
	}
	
	public Ship(int id, int type,ShipType t, double x, double y, int tick, int life)
	{
		this(id,type,t,x,y,0,0,tick,life,0,0);
	}
	
	//clobbers this ship with the contents of s
	public void update(Ship s)
	{
		myID=s.myID;
		myTypeID = s.myTypeID;
		myPosition = s.myPosition;
		myLife = s.myLife;
		willMove=s.willMove;
		myScannerHeading=s.myScannerHeading;
		myHeading = s.myHeading;
		myType = s.myType;
		myCreationTick = s.myCreationTick;
		myDeltaLife = s.myDeltaLife;
		myLaunchDelay = s.myLaunchDelay;
	}
	
	public int getLaunchDelay()
	{
		return myLaunchDelay;
	}
	
	public void setLaunchDelay(int d)
	{
		myLaunchDelay = d;
	}
	
	public boolean isReadyToLaunch()
	{
		return myLaunchDelay <= 0;
	}
	
	public final boolean readyToLaunch()
	{
		return isReadyToLaunch();
	}
	
	public void decrementLife(int d)
	{
		myDeltaLife += d;
		myLife -= d;
	}
	
	public void setLife(int L)
	{
		myDeltaLife += myLife-L;
		myLife=L;
	}
	
	public int getCost()
	{
		return myType.getCost();
	}
	
	public void reset()
	{
		if(myLaunchDelay>0)
			myLaunchDelay--;
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
	
	public void setHeading(double h)
	{
		myHeading = SpaceMath.wrapHeading(h);
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
		DArc oup = new DArc(myPosition, myType.getScannerRadius(),(myScannerHeading-myType.getScannerAngleSpan())/2,myType.getScannerAngleSpan());
		return oup;
	}
	
	public String toString()
	{
		return SpaceText.toString(this);
	}
}
