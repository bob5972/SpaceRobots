package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.Ship;

public interface AIShipFactory
{
	public AIShip createShip(Ship s);
}
