package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;

public class TargetFilter extends ContactFilter
{
	public boolean test(Contact c)
	{
		if(c.getTypeID() == DefaultShipTypeDefinitions.MISSILE_ID ||
				c.getTypeID() == DefaultShipTypeDefinitions.ROCKET_ID )
		{
			return false;
		}
		
		if(c.isDead())
			return false;
		
		return true;
	}	
}
