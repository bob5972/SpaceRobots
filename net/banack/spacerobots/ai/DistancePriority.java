package net.banack.spacerobots.ai;

import java.util.Comparator;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.SpaceMath;

public class DistancePriority implements Comparator<Contact>
{
	private DPoint center;
	private double width,height;
	
	public DistancePriority(DPoint center, double width, double height)
	{
		this.center = center;
		this.width = width;
		this.height = height;
	}
	
	public int compare(Contact lhs, Contact rhs)
	{
		double ld = SpaceMath.getDistance(center,lhs.getPosition(),width,height);
		double rd = SpaceMath.getDistance(center,rhs.getPosition(),width,height);
		
		if(ld < rd)
			return -1;
		if(ld > rd)
			return 1;
		
		return 0;		
	}
}
