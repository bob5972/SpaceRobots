package net.banack.spacerobots.util;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.ServerShip;

public class Contact
{
	private int enemyID;
	private int enemyFleetID;
	private int enemyType;
	private DPoint enemyPosition;
	private double enemyHeading;
	
	public static final int INVALID_FLEET_ID = net.banack.spacerobots.Battle.INVALID_ID;
	
	public Contact(ServerShip enemy)
	{
		this.enemyID = enemy.getID();
		this.enemyFleetID = enemy.getFleetID();
		this.enemyType = enemy.getTypeID();
		this.enemyPosition = enemy.getPosition();
		this.enemyHeading = enemy.getHeading();
	}
	
	public Contact(int enemyID, int fleetID, int type, double x, double y, double heading)
	{
		this(enemyID,fleetID,type,new DPoint(x,y),heading);
	}
	
	public Contact(int enemyID, int fleetID, int type, DPoint position, double heading)
	{
		this.enemyID = enemyID;
		this.enemyFleetID = fleetID;
		this.enemyType = type;
		this.enemyPosition = position;
		this.enemyHeading = heading;
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
		return enemyType;
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
}
