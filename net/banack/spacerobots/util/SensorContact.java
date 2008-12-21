package net.banack.spacerobots.util;

public class SensorContact
{
	private int enemyID;
	private int spotterID;
	private int fleetID;
	
	public static final int INVALID_FLEET_ID = -1;
	
	public SensorContact(int enemyID, int spotterID)
	{
		this.enemyID = enemyID;
		this.spotterID = spotterID;
		this.fleetID = INVALID_FLEET_ID;
	}
	
	public SensorContact(int enemyID, int fleetID, int spotterID)
	{
		this.enemyID = enemyID;
		this.spotterID = spotterID;
		this.fleetID = fleetID;
	}

	public int getEnemyID()
	{
		return enemyID;
	}

	public void setEnemyID(int enemyID)
	{
		this.enemyID = enemyID;
	}

	public int getFleetID()
	{
		return fleetID;
	}

	public void setFleetID(int fleetID)
	{
		this.fleetID = fleetID;
	}

	public int getSpotterID()
	{
		return spotterID;
	}

	public void setSpotterID(int spotterID)
	{
		this.spotterID = spotterID;
	} 
}
