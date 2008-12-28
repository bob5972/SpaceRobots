package net.banack.spacerobots.util;

import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.geometry.NormalRectangle;
import net.banack.geometry.Point;
import net.banack.geometry.Rectangle;
import net.banack.geometry.SkewRectangle;
import net.banack.spacerobots.Battle;
import net.banack.util.MethodNotImplementedException;

public class SpaceMath
{
	public static final double HEADING_WRAP = 2*Math.PI;
	
	public static double calculateAdjustedHeading(double curHeading, double desiredHeading, double maxTurningRate)
	{
		if(Math.abs(curHeading-desiredHeading) <= maxTurningRate)
			return desiredHeading;
		else if (curHeading > desiredHeading)
			return curHeading - maxTurningRate;
		else if (curHeading < desiredHeading)
			return curHeading+maxTurningRate;
		
		throw new MethodNotImplementedException("No error handler");		
	}
	
	public static double wrapHeading(double h)
	{
		while(h<0)
			h+=HEADING_WRAP;
		while(h>HEADING_WRAP)
			h-=HEADING_WRAP;
		return h;
	}
	
	public static double calculateXOffset(double heading, double speed)
	{
		return (Math.cos(heading)*speed);
	}
	
	public static double degToRad(double d)
	{
		return (d/360)*2*Math.PI;
	}
	
	public static double radToDeg(double r)
	{
		return (r/(2*Math.PI))*360;
	}
	
	public static double calculateYOffset(double heading,double speed)
	{
		return (Math.sin(heading)*speed);
	}
	
	public static DPoint rotate(DPoint p, DPoint center, double r)
	{
		double xl = p.x-center.x;
		double yl = p.y-center.y;
		double length = Math.sqrt(xl*xl+yl*yl);
		double angle = Math.atan(yl/xl);
		angle+=r;
		
		DPoint oup = new DPoint(center.x+Math.cos(angle)*length,center.y+Math.sin(angle)*length);
		return oup;	
	}
	
	
	public static DQuad rotate(DQuad q, double r)
	{
		DPoint ul = q.getP1();
		DPoint ur = q.getP2();
		DPoint bl = q.getP3();
		DPoint br = q.getP4();
		DPoint c = findCenter(ul,ur,bl,br);
		
		ul = rotate(ul, c, r);
		ur = rotate(ur, c, r);
		bl = rotate(bl, c, r);
		br = rotate(br, c, r);
		
		return new DQuad(ul,ur,br,bl);		
	}
	
	public static DPoint findCenter(DPoint a, DPoint b, DPoint c, DPoint d)
	{
		DPoint oup = new DPoint();
		oup.x = (a.x+b.x+c.x+d.x)/4;
		oup.y = (a.y+b.y+c.y+d.y)/4;
		return oup;
	}
	
	public static DQuad getDQuad(DPoint center, double width, double height, double heading)
	{
		DPoint c=center;
		DPoint ul = new DPoint(c.x-width/2,c.y+height/2);
		DPoint ur = new DPoint(c.x+width/2,c.y+height/2);
		DPoint bl = new DPoint(c.x-width/2,c.y-height/2);
		DPoint br = new DPoint(c.x+width/2,c.y-height/2);
		
		
		DQuad r = new DQuad(ul,ur,br,bl);
		return rotate(r,heading);		
	}
	
	public static boolean isCollision(DQuad r, DQuad s)
	{
		throw new MethodNotImplementedException();		
	}
}

