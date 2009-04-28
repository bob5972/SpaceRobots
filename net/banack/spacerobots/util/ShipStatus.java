package net.banack.spacerobots.util;

import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;

public interface ShipStatus
{
	public int getID();
	
	public int getTypeID();	
	public ShipType getType();
		
	public DPoint getPosition();
	public double getX();	
	public double getY();
	public DDimension getDimension();	
	public DQuad getLocation();
	
	public double getHeading();
	public double getMaxSpeed();	
	public boolean getCanStop();	
	public double getMaxTurningRate();
	
	public int getLife();	
	public boolean isAlive();	
	public boolean isDead();
	
	public int getMaxTickCount();
	public boolean getCanMoveScanner();	
	public int getCost();
	
}
