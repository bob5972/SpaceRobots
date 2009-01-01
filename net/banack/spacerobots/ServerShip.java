package net.banack.spacerobots;

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DQuad;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.util.MethodNotImplementedException;

public class ServerShip extends Ship
{
	//Internal Representation of a Ship
	
	private ServerFleet myFleet;
	
	public ServerShip(ServerFleet f, int id, int type,ShipType t, double x, double y, int life,int tick)
	{
		super(id,type,t,x,y,life,tick);
		myFleet=f;
	}
	
	public ServerFleet getFleet()
	{
		return myFleet;
	}
	
	public int getFleetID()
	{
		return myFleet.getFleetID();
	}
}