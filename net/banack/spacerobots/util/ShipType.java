package net.banack.spacerobots.util;

public class ShipType
{
	private String name;
	private int typeID;
	private int cost;
	private int maxLife;
	private int width;
	private int height;	
	private int maxSpeed;
	private boolean canStop;
	private boolean hasAI;//does the Ai get to control it?
	private boolean isShip;//ship or projectile?
	
	
	public ShipType(String name, int id, 
			int cost, int life, int width, int height,
			boolean canStop, int maxSpeed,
			boolean hasAI, boolean isShip)
	{
		this.name = name;
		typeID = id;
		maxLife = life;
		this.width = width;
		this.height = height;
		this.cost = cost;
		this.canStop = canStop;
		this.hasAI = hasAI;
		this.isShip = isShip;
		this.maxSpeed = maxSpeed;
	}
	
	public boolean getCanStop()
	{
		return canStop;
	}
	
	public boolean getAI()
	{
		return hasAI;
	}
	
	public int getMaxSpeed()
	{
		return maxSpeed;
	}
	
	public boolean getIsShip()
	{
		return isShip;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getHeight()
	{
		return height;
	}
	public int getMaxLife()
	{
		return maxLife;
	}
	public int getTypeID()
	{
		return typeID;
	}
	public int getWidth()
	{
		return width;
	}
	public int getCost()
	{
		return cost;
	}
}
