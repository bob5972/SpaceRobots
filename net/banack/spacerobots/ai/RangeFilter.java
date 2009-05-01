package net.banack.spacerobots.ai;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Contact;
import net.banack.util.Filter;

public class RangeFilter extends ContactFilter
{
	private DPoint bl,tr;
	
	public RangeFilter(DPoint bl, DPoint tr)
	{
		this.bl = bl;
		this.tr = tr;
	}
	
	public boolean test(Contact c)
	{
		double x = c.getX();
		double y = c.getY();
		
		if(bl.getX() <= x && x <= tr.getX())
		{
			if(bl.getY() <= y && y <= tr.getY())
				return true;
		}
		
		return false;
	}
}
