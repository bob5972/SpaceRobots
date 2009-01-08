package net.banack.spacerobots.util;

import java.util.Iterator;

import net.banack.geometry.DArc;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.geometry.NormalRectangle;
import net.banack.geometry.Point;
import net.banack.geometry.Rectangle;
import net.banack.geometry.SkewRectangle;
import net.banack.spacerobots.Battle;
import net.banack.spacerobots.Debug;
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
		else //if (curHeading < desiredHeading)
			return curHeading+maxTurningRate;		
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
		
		
		if(r == 0 || p.equals(center))
			return p;
		
		double xl = p.getX()-center.getX();
		double yl = p.getY()-center.getY();
		double length = Math.sqrt(xl*xl+yl*yl);
		double angle = Math.atan(yl/xl);
		angle+=r;
		
		DPoint oup = new DPoint(Math.cos(angle)*length,Math.sin(angle)*length);
		oup = oup.add(center);
		
		return oup;	
	}
	
	//rotates a DQuad around it's center point
	public static DQuad rotate(DQuad q, double r)
	{
		if(r==0)
			return q;
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
	
	public static DQuad rotate(DQuad q, DPoint c, double r)
	{
		if(r==0)
			return q;
		DPoint ul = q.getP1();
		DPoint ur = q.getP2();
		DPoint bl = q.getP3();
		DPoint br = q.getP4();
		
		ul = rotate(ul, c, r);
		ur = rotate(ur, c, r);
		bl = rotate(bl, c, r);
		br = rotate(br, c, r);
		
		return new DQuad(ul,ur,br,bl);
	}
	
	public static DPoint findCenter(DPoint a, DPoint b, DPoint c, DPoint d)
	{
		DPoint oup = new DPoint((a.getX()+b.getX()+c.getX()+d.getX())/4 , (a.getY()+b.getY()+c.getY()+d.getY())/4);
		return oup;
	}
	
	public static DQuad getDQuad(DPoint center, double width, double height, double heading)
	{
		DPoint c=center;
		DPoint ul = new DPoint(c.getX()-width/2,c.getY()+height/2);
		DPoint ur = new DPoint(c.getX()+width/2,c.getY()+height/2);
		DPoint bl = new DPoint(c.getX()-width/2,c.getY()-height/2);
		DPoint br = new DPoint(c.getX()+width/2,c.getY()-height/2);
		
		
		DQuad r = new DQuad(ul,ur,br,bl);
		return rotate(r,c,heading);
	}
	
	//returns the normal bounding rectangle for the given quad
	//  (ie the bounding rectangle parallel to the axes)
	public static DQuad getNormalBounds(DQuad r)
	{
		DPoint ul,ur,bl,br,p;
		double minX,minY,maxX,maxY;
		Iterator<DPoint> i = r.iterator();
		p = ((DPoint)i.next());
		maxX=minX = p.getX();
		maxY=minY = p.getY();
		
		while(i.hasNext())
		{
			p = ((DPoint)i.next());
			if(p.getX() < minX)
				minX = p.getX();
			if(p.getX() > maxX)
				maxX = p.getX();
			if(p.getY() < minY)
				minY = p.getY();
			if(p.getY() > maxY)
				maxY = p.getY();
		}
		
		ul = new DPoint(minX,maxY);
		ur = new DPoint(maxX,maxY);
		br = new DPoint(maxX,minY);
		bl = new DPoint(minX,minY);
		return new DQuad(ul,ur,br,bl);		
	}
	
	//reorders the points so that P1 is the upper-left, and proceeding clockwise
	public static DQuad reorderToNormal(DQuad r)
	{
		throw new MethodNotImplementedException();
	}
	
	//test if the line segements Seg[a1,a2] intersects Seg[b1,b2]
	public static boolean isIntersection(double a1, double a2, double b1, double b2)
	{
		if(a1 <= b1 && b1 <= a2)
			return true;
		if(a1 <= b2 && b2 <= a2)
			return true;
		return false;
	}
	
	//returns true iff p is in [a1,a2]
	public static boolean containsPoint(double p, double a1, double a2)
	{
		if(a1 <= p && p <= a2)
			return true;
		return false;
	}
	
	//test if the line segements Seg[a1,a2] intersects Seg[b1,b2]
	public static boolean isIntersection(DPoint a1, DPoint a2, DPoint b1, DPoint b2)
	{
		double r = Math.atan(a1.getY()/a1.getX());
		a2 = rotate(a2,a1,r);
		b1 = rotate(b1,a1,r);
		b2 = rotate(b2,a1,r);
		
		double bYMin,bYMax;
				
		double temp;
		
		bYMin = b1.getY();
		bYMax = b2.getY();
		
		if(bYMin > bYMax)
		{
			temp = bYMin;
			bYMin = bYMax;
			bYMax = temp;
		}

		//in theory you really only need to check a1.getY()
		// but I put the average in to check against rounding errors)
		double yAvg = (a1.getY()+a2.getY())/2;
		if(containsPoint(yAvg,bYMin,bYMax))
		{
			double aXMin,aXMax;
			aXMin = a1.getX();
			aXMax = a2.getX();
			
			if(aXMin > aXMax)
			{
				temp =  aXMin;
				aXMin = aXMax;
				aXMax = temp;
			}
			
			double u = (yAvg - b2.getY())/(b1.getY()-b2.getY());
			double Bx = (b1.getX()-b2.getX())*u+b2.getX();
			if(containsPoint(Bx,aXMin,aXMax))
				return true;
			return false;
		}
		
		//B does not cross the y value of A (give or take rounding)		
		return false;
	}
	
	public static double min(double a, double b)
	{
		if(a < b)
			return a;
		return b;
	}
	
	public static double max(double a, double b)
	{
		if(a> b)
			return a;
		return b;
	}
	
	public static boolean containsPoint(DPoint p,DQuad r)
	{
		DPoint ul, br, bl;
		bl = r.getP4();
		double a = Math.atan((r.getP3().getY()-bl.getY())/(r.getP3().getX()-bl.getX()));
		ul = rotate(r.getP1(),bl,a);
		br = rotate(r.getP3(),bl,a);
		p = rotate(p,bl,a);
		
		if(p.getX() > br.getX())
			return false;
		if(p.getY() > ul.getY())
			return false;
		if(p.getX() < bl.getX())
			return false;
		if(p.getY() < bl.getY())
			return false;
		return true;		
	}	
	
	public static boolean isCollision(DQuad r, DQuad s)
	{
		//either all of r is inside s
		// or at least 1 corner of r is outside s
		
		
		DPoint center = s.getP4();
		double a = Math.atan((s.getP3().getY()-center.getY())/(s.getP3().getX()-center.getX()));
		r = rotate(r,center,a);
		s = rotate(s,center,a);
		
		//s is now normal (parallel to axes)
			
		DPoint ul, br, bl;
		ul = s.getP1();
		//ur = s.getP2(); //never actually used
		br = s.getP3();
		bl = s.getP4();
		
		for(int x=1;x<=4;x++)
		{
			DPoint p = r.getVertex(x);
			p = rotate(p,center,a);
			
			if(p.getX() > br.getX())
				return false;
			if(p.getY() > ul.getY())
				return false;
			if(p.getX() < bl.getX())
				return false;
			if(p.getY() < bl.getY())
				return false;
			return true;
		}
		
		//all corners are outside
		//  so let's check if edges cross
		
		
		//If both endpoints of an r-edge are outside the rectangle
		//  then it has to cross 2 s-edges (but it could be any 2)
		//  So, we only need to check 3 s-edges for each r-edge
		for(int ri=1;ri<=4;ri++)
		{
			for(int si=1;si<=3;si++)
			{
				DPoint a1,a2;
				DPoint b1,b2;
				
				//to keep java initializers happy
				a1 = a2 = b1 = b2 = null;
				
				switch(ri)
				{
					case 1:
						a1 = r.getP1();
						a2 = r.getP2();
					break;
					case 2:
						a1 = r.getP2();
						a2 = r.getP3();
					break;
					case 3:
						a1 = r.getP3();
						a2 = r.getP4();
					break;
					case 4:
						a1 = r.getP4();
						a2 = r.getP1();
					break;
				}
				
				switch(si)
				{
					case 1:
						b1 = s.getP1();
						b2 = s.getP2();
					break;
					case 2:
						b1 = s.getP2();
						b2 = s.getP3();
					break;
					case 3:
						b1 = s.getP3();
						b2 = s.getP4();
					break;
				}
				
				if(isIntersection(a1,a2,b1,b2))
					return true;
			}
		}
		
		return false;
	}
	
	public static boolean isCollision(DArc a, DQuad r)
	{
		//these are all speedups
		//  but I havn't actually tested to see if any/how many help
		
		for(int x=1;x<=4;x++)
		{
			if(containsPoint(r.getVertex(x),a))
				return true;
		}
		
		DPoint left = new DPoint(a.getRadius()*Math.cos(a.getAngleEnd()),a.getRadius()*Math.sin(a.getAngleEnd()));
		left = left.add(a.getCenter());
		
		if(containsPoint(left,r))
			return true;
		
		DPoint right = new DPoint(a.getRadius()*Math.cos(a.getAngleStart()),a.getRadius()*Math.sin(a.getAngleStart()));
		right = right.add(a.getCenter());
		
		if(containsPoint(right,r))
			return true;
		
		if(containsPoint(a.getCenter(),r))
			return true;
		
			
		//no luck with fast and loose
		// do real collision detection
		
		//cheat and use the java library!

		//This needs to be verified!
		
		DPoint center = r.getP4();
		double angle = Math.atan((r.getP3().getY()-center.getY())/(r.getP3().getX()-center.getX()));
		r = rotate(r,center,angle);
		right = rotate(right,center,angle);
		a = new DArc(rotate(a.getCenter(),center,angle),a.getRadius(),Math.atan((right.getY()-a.getCenter().getY())/(right.getX()-a.getCenter().getX())),a.getAngleSpan());
		
		
		//r is now normal (parallel to axes)
		
		java.awt.geom.Arc2D.Double ja = new java.awt.geom.Arc2D.Double(a.getCenter().getX(), a.getCenter().getY(), a
				.getRadius(), a.getRadius(), radToDeg(a.getAngleStart()), radToDeg(a.getAngleEnd()),
				java.awt.geom.Arc2D.PIE);
		return ja.intersects(r.getP1().getX(),r.getP1().getY(),r.getP2().getX()-r.getP1().getX(),r.getP1().getY()-r.getP4().getY());
	}
	
	public static boolean containsPoint(DPoint p, DArc a)
	{
		double angle = Math.atan((p.getY()-a.getCenter().getY())/(p.getX()-a.getCenter().getX()));
		double x = p.getX()-a.getCenter().getX();
		double y = p.getY()-a.getCenter().getY();
		double radius = Math.sqrt(x*x+y*y);
		
		return (radius <= a.getRadius() && (a.getAngleStart() <= angle && angle <= a.getAngleEnd()));		
	}
}

