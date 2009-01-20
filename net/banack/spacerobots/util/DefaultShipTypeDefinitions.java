package net.banack.spacerobots.util;

import java.util.Iterator;

public class DefaultShipTypeDefinitions implements ShipTypeDefinitions
{
	public final static int ROCKET_ID = 1;
	public final static int MISSILE_ID = 2;
	public final static int FIGHTER_ID = 3;
	public final static int DESTROYER_ID = 4;
	public final static int CRUISER_ID = 5;
	
	public final static ShipType ROCKET = new ShipType(
			"Rocket",ROCKET_ID,
			5,1,2, 2,
			false, 0, 1,
			false, false, 0,0,
			false, false,20
	);
	
	public final static ShipType MISSILE = new ShipType(
			"Missile",MISSILE_ID,
			10,1,5,5,
			true, 1, .01,
			false, false,0,0,
			true, false,20
	);
	
	public final static ShipType FIGHTER = new ShipType(
			"Fighter",FIGHTER_ID,
			100, 1, 5, 5,
			false, 1, 0.5,
			true, false,10,1,
			true,true,0
	);
	
	public final static ShipType DESTROYER = new ShipType(
			"Destroyer",DESTROYER_ID,
			400,5,10,10,
			true, 1, .01,
			true, true,10,1,
			true,true,0
	);
	
	public final static ShipType CRUISER = new ShipType(
			"Cruiser",CRUISER_ID,
			1000,10,20,20,
			true, 0.5, 0.3,
			true, true,10,1,
			true,true,0
	);
	
	private final static ShipType[] myTypes={ROCKET,MISSILE,FIGHTER,DESTROYER,CRUISER};
	
	public static ShipType getShipType(int typeID)
	{
		switch(typeID)
		{
			case ROCKET_ID:
				return ROCKET;
			case MISSILE_ID:
				return MISSILE;
			case FIGHTER_ID:
				return FIGHTER;
			case DESTROYER_ID:
				return DESTROYER;
			case CRUISER_ID:
				return CRUISER;
		}
		return null;
	}
	

	
	public ShipType get(int typeID)
	{
		return getShipType(typeID);
	}
	
	
	public Iterator<ShipType> getShipTypeIterator()
	{
		return new Iterator<ShipType>(){
			private int x=0;
			public boolean hasNext(){
				return x < myTypes.length;
			}
			public ShipType next(){
				return myTypes[x++];
			}
			
			public void remove(){
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public ShipType[] getShipTypes()
	{
		return (ShipType[])myTypes.clone();
	}
	
}
