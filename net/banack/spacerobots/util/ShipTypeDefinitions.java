package net.banack.spacerobots.util;

import net.banack.spacerobots.Battle;


public interface ShipTypeDefinitions
{
	public static final int TYPE_INVALID = Battle.TYPE_INVALID;
	java.util.Iterator getShipTypeIterator();
	ShipType[] getShipTypes();
	
	ShipType get(int typeID);	
}
