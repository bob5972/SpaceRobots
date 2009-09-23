package net.banack.spacerobots.fleets;

import java.util.Collection;
import java.util.Iterator;

import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.SpaceMath;

public class Beeline extends AIFleet
{
	private class BeeGov implements AIGovernor
	{
		public ShipStatus getClosestTarget(AIShip s)
		{
			Collection<Contact> targets = myContacts.getShipContacts();
			double minDistance;
			double tempDistance;
			ShipStatus oup;
			ShipStatus tempShip;
			
			Iterator<Contact> it = targets.iterator();
			
			if(!it.hasNext()) {
				return null;
			}
			
			oup = it.next();
			minDistance = SpaceMath.getDistance(s.getPosition(), oup.getPosition(), battleWidth, battleHeight);
			
			while(it.hasNext()) {
				tempShip = it.next();
				tempDistance = SpaceMath.getDistance(s.getPosition(), tempShip.getPosition(), battleWidth, battleHeight);
				
				if(tempDistance < minDistance) {
					minDistance = tempDistance;
					oup = tempShip;
				}
			}
			
			return oup;				
		}

        public void run(AIShip s)
        {
	        if(s.getTypeID() == CRUISER_ID) {
	        	return;
	        }
        	
        	ShipStatus target = getClosestTarget(s);
	        
	        if(target == null) {
	        	if(random.nextDouble() > 0.99) {
	        		s.intercept(myCruiser);
	        	}
	        	return;
	        }
	        
	        s.intercept(target);
	        
	        if(s.isInRocketRange(target)) {
	        	System.out.println("Firing");
	        	s.fire(target);
	        }
        }
		
	}
	
	public Beeline()
	{
		super();
	}
	
	public Beeline(long seed)
	{
		super(seed);
	}
	
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public Iterator<ShipAction> runTick()
	{
		myShips.apply(new BeeGov());
		
		myCruiser.advanceScannerHeading();
		
		if(myCruiser.canLaunch(FIGHTER) && credits > FIGHTER.getCost()*2) {
			myCruiser.launch(FIGHTER_ID);
		}
		
		return myShips.getActionIterator();
	}
	
}
