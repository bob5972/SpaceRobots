package net.banack.spacerobots.util;

import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.ServerShip;

public class Contact implements ShipStatus
{
	private int enemyID;
	private int enemyFleetID;
	private int enemyTypeID;
	private int enemyLife;
	private DPoint enemyPosition;
	private double enemyHeading;
	private ShipType enemyType;
	
	public static final int INVALID_FLEET_ID = net.banack.spacerobots.Battle.INVALID_ID;
	
	public Contact(ServerShip enemy)
	{
		this.enemyID = enemy.getID();
		this.enemyFleetID = enemy.getFleetID();
		this.enemyTypeID = enemy.getTypeID();
		this.enemyType = enemy.getType();
		this.enemyPosition = enemy.getPosition();
		this.enemyHeading = enemy.getHeading();
		this.enemyLife = enemy.getLife();
	}
	
	public Contact(int enemyID, int fleetID, int type, double x, double y, double heading, int life)
	{
		this(enemyID,fleetID,type,new DPoint(x,y),heading,life);
	}
	
	public Contact(int enemyID, int fleetID, int typeID, DPoint position, double heading,int life)
	{
		this(enemyID,fleetID, DefaultShipTypeDefinitions.getShipType(typeID), position,heading,life);
	}
	
	public Contact(int enemyID, int fleetID, ShipType type, DPoint position, double heading,int life)
	{
		this.enemyID = enemyID;
		this.enemyFleetID = fleetID;
		this.enemyTypeID = type.getID();
		this.enemyPosition = position;
		this.enemyHeading = heading;
		this.enemyType = type;
		this.enemyLife = life;
	}
	
	public final int getID()
	{
		return enemyID;
	}
	
	public final int getFleetID()
	{
		return enemyFleetID;
	}
	
	public int getTypeID()
	{
		return enemyTypeID;
	}
	
	public DPoint getPosition()
	{
		return enemyPosition;
	}
	
	public double getHeading()
	{
		return enemyHeading;
	}
	
	public int hashCode()
	{
		return enemyID;
	}
	
	public boolean equals(Object rhs)
	{
		if(!(rhs instanceof Contact))
			return false;
		return enemyID == ((Contact)rhs).enemyID;
	}
	
	public ShipType getType()
	{
		return enemyType;
	}
		
	public double getX()
	{
		return enemyPosition.getX();
	}
	
	public double getY()
	{
		return enemyPosition.getY();
	}
	
	public DDimension getDimension()
	{
		return new DDimension(enemyType.getWidth(),enemyType.getHeight());
	}
	public DQuad getLocation()
	{
		return SpaceMath.getDQuad(enemyPosition,enemyType.getWidth(),enemyType.getHeight(),enemyHeading);
	}
	
	public double getMaxSpeed()
	{
		return enemyType.getMaxSpeed();
	}
	
	public boolean getCanStop()
	{
		return enemyType.getCanStop();
	}
	
	public double getMaxTurningRate()
	{
		return enemyType.getMaxTurningRate();
	}
	
	
	public int getLife()
	{
		return enemyLife;
	}
	
	public boolean isAlive()
	{
		return getLife() == 0;
	}
	
	public final boolean isDead()
	{
		return !isAlive();
	}
	
	public int getMaxTickCount()
	{
		return enemyType.getMaxTickCount();
	}
	
	public boolean getCanMoveScanner()
	{
		return enemyType.getCanMoveScanner();
	}
	
	public int getCost()
	{
		return enemyType.getCost();
	}
}
