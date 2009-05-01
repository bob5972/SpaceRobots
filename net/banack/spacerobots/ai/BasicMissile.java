package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipStatus;

public class BasicMissile extends AIShip
{
	private ShipStatus myTarget;
	
	
	public BasicMissile(AIFleet f)
	{
		super(f);
	}
	
	public BasicMissile(AIFleet f, ShipStatus target)
	{
		super(f);
		myTarget= target;
	}
	
	public BasicMissile(Ship s, AIFleet f, ShipStatus target)
	{
		super(s,f);
		myTarget= target;
	}
	
	public void run()
	{
		if(myTarget != null)
			intercept(myTarget);
	}
	
	public ShipStatus getTarget()
	{
		return myTarget;
	}
	
	public void setTarget(ShipStatus t)
	{
		myTarget = t;
	}
	
	public boolean hasTarget()
	{
		return myTarget != null && myTarget.isAlive();
	}
	
}
