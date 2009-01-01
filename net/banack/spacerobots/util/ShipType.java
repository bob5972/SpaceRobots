package net.banack.spacerobots.util;

public class ShipType
{
	public final static int TYPE_INVALID = ShipTypeDefinitions.TYPE_INVALID;
	
	private String name;
	private int typeID;
	private int cost;
	private int maxLife;
	private double width;
	private double height;	
	private double maxSpeed;
	private boolean canStop;
	private boolean hasAI;//does the Ai get to control it?
	private boolean isShip;//ship or projectile?
	private double maxTurningRate;
	private boolean canMoveScanner;
	private boolean hasScanner;
	private double scannerRadius;
	private double scannerAngleSpan;
	private int maxTickCount;
	
	public ShipType(String name, int id, 
			int cost, int life, double width, double height, 
			boolean canStop, double maxTurningRate, double maxSpeed, 
			boolean hasScanner, boolean canMoveScanner, double scannerRadius, double scannerAngleSpan,
			boolean hasAI, boolean isShip, int maxTickCount)
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
		this.maxTurningRate = maxTurningRate;
		this.canMoveScanner = canMoveScanner;
		this.maxTickCount = maxTickCount;
		this.hasScanner = hasScanner;
		this.scannerAngleSpan = scannerAngleSpan;
		this.scannerRadius = scannerRadius;
	}
	
	//maxTickCount of 0 means no expiry
	public int getMaxTickCount()
	{
		return maxTickCount;
	}
	
	public boolean getCanStop()
	{
		return canStop;
	}
	
	public boolean getAI()
	{
		return hasAI;
	}
	
	public double getMaxSpeed()
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
	
	public double getHeight()
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
	public double getWidth()
	{
		return width;
	}
	public int getCost()
	{
		return cost;
	}

	public double getMaxTurningRate()
	{
		return maxTurningRate;
	}

	public boolean canMoveScanner()
	{
		return canMoveScanner;
	}

	public boolean hasScanner()
	{
		return hasScanner;
	}

	public double getScannerAngleSpan()
	{
		return scannerAngleSpan;
	}

	public double getScannerRadius()
	{
		return scannerRadius;
	}
}
