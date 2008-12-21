package net.banack.spacerobots.util;


public interface ShipTypeDefinitions
{
	java.util.Iterator getShipTypeIterator();
	ShipType[] getShipTypes();
	
	ShipType get(int typeID);	
}
