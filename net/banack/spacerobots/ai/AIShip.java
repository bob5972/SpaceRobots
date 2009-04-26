package net.banack.spacerobots.ai;

import java.util.Iterator;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.SpaceMath;

public class AIShip extends BasicAIShip
{
	public static final DefaultShipTypeDefinitions TYPE = new DefaultShipTypeDefinitions();

	public static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	public static final int MISSILE_ID = DefaultShipTypeDefinitions.MISSILE_ID;
	
	public static final ShipType CRUISER = TYPE.CRUISER;
	public static final ShipType DESTROYER = TYPE.DESTROYER;
	public static final ShipType FIGHTER = TYPE.FIGHTER;
	public static final ShipType ROCKET = TYPE.ROCKET;
	public static final ShipType MISSILE = TYPE.MISSILE;
	
	protected FleetAI myFleet;
	
	public AIShip(Ship s)
	{
		super(s);
		myFleet = null;
	}
	
	public AIShip(Ship s, FleetAI f)
	{
		super(s);
		myFleet = f;
	}
	
	public boolean canLaunch()
	{
		return isAlive() && isReadyToLaunch() && getTypeID() != ROCKET_ID && getTypeID() != MISSILE_ID;
	}
	
	public boolean canLaunch(int type)
	{
		return canLaunch(TYPE.get(type));
	}
	
	public boolean canLaunch(ShipType t)
	{
		return canLaunch() && myFleet.myCredits >= t.getCost();
	}
	
	public double getDistanceFrom(DPoint p)
	{
		DPoint loc = getPosition();
		return SpaceMath.getRawDistance(loc,wrap(p));
	}
	
	public double intercept(Ship target)
	{
		double oup = SpaceMath.interceptHeading(this,target,myFleet.battleWidth,myFleet.battleHeight);
		setHeading(oup);
		return oup;
	}
	
	public double intercept(Contact target)
	{
		double oup = SpaceMath.interceptHeading(this,target.getPosition(),target.getHeading(),TYPE.getShipType(target.getTypeID()).getMaxSpeed(),myFleet.battleWidth,myFleet.battleHeight);
		setHeading(oup);
		return oup;
	}
	
	public void setHeading(DPoint p)
	{
		DPoint loc = getPosition();
		setHeading(SpaceMath.getAngle(loc, wrap(p)));
	}
	
	public void setScannerHeading(DPoint p)
	{
		DPoint loc = getPosition();
		setScannerHeading(SpaceMath.getAngle(loc,wrap(p)));
	}
	
	public DPoint wrap(DPoint p)
	{
		DPoint loc = getPosition();
		return SpaceMath.wrap(p,loc, myFleet.battleWidth,myFleet.battleHeight);
	}
	
	public DPoint wrap(double x, double y)
	{
		return wrap(new DPoint(x,y));
	}
	
	public void launch(int type)
	{
		launch(TYPE.get(type));
	}
	
	public void launch(ShipType t)
	{
		setLaunchWhat(t.getID());
		myFleet.myCredits-=t.getCost();
	}
}
