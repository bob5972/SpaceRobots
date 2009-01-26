package net.banack.spacerobots.ai;

import net.banack.geometry.DArc;
import net.banack.geometry.DDimension;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.ShipTypeDefinitions;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.SpaceText;

public class AIShip extends Ship
{
	private ShipAction myAction;
	
	public AIShip(int id)
	{
		super(id,ShipTypeDefinitions.TYPE_INVALID,null,-1,-1,-1,-1);
		myAction = new ShipAction(id);
	}
	
	public AIShip(Ship s)
	{
		super(s);
		myAction = new ShipAction(s);
	}
	
	public ShipAction getAction()
	{
		//there is a threading issue here if someone decides to muck with the returned action...
		//but I don't really feel like copying it yet
		return myAction;
	}
	
	public void update(Ship s)
	{
		if(getID() != s.getID())
			throw new IllegalArgumentException("Updating with a bad ID!");
		double desiredHeading = myAction.getHeading();
		super.update(s);
		myAction = new ShipAction(s);
		myAction.setHeading(desiredHeading);
	}
	
	public boolean willSpawn()
	{
		return myAction.isSpawn();
	}
	
	public boolean willMove()
	{
		return myAction.willMove();
	}
	
	public final boolean getWillMove()
	{
		return willMove();
	}
	
	public void setWillMove(boolean b)
	{
		myAction.setWillMove(b);
	}
	
	public boolean curWillMove()
	{
		return super.willMove();
	}
	
	public double getHeading()
	{
		return  myAction.getHeading();
	}
	
	public double curHeading()
	{
		return super.getHeading();
	}
	
	public double projHeading()
	{
		return SpaceMath.calculateAdjustedHeading(curHeading(), getHeading(), getMaxTurningRate());
	}
	
	public double getScannerHeading()
	{
		return myAction.getScannerHeading();
	}
	
	public double curScannerHeading()
	{
		return super.getScannerHeading();
	}
	
	public int getLaunch()
	{
		return myAction.getLaunch();
	}
	
	public final int getLaunchWhat()
	{
		return getLaunch();
	}
	
	public void setLaunchWhat(int t)
	{
		myAction.setLaunchWhat(t);
	}
	
	public final void setLaunch(int t)
	{
		setLaunchWhat(t);
	}
	
	public void setHeading(double h)
	{
		myAction.setHeading(h);
	}
	
	public void setHeading(DPoint pos)
	{
		//note this won't wrap right
		setHeading(SpaceMath.getAngle(getPosition(), pos));
	}
	
	public void setScannerHeading(double h)
	{
		myAction.setScannerHeading(h);
	}
}
