package net.banack.spacerobots.util;

import java.util.Iterator;

import net.banack.geometry.DArc;
import net.banack.geometry.DPoint;
import net.banack.geometry.DQuad;
import net.banack.spacerobots.Battle;
import net.banack.spacerobots.Debug;
import net.banack.util.MethodNotImplementedException;

public class SpaceMath
{
	public static final double HEADING_WRAP = 2*Math.PI;
	public static final DPoint ORIGIN = DPoint.ORIGIN;
	
	public static double getAngle(DPoint origin, DPoint target)
	{
		return (target.subtract(origin)).getTheta();
	}
	
	public static double getRawDistance(DPoint a, DPoint b)
	{
		return (a.subtract(b)).getRadius();
	}
	
	public static DPoint getClosestMirror(DPoint orig, DPoint target, double width, double height)
	{
		DPoint sPos = orig;
		
		double x = target.getX();
		double y = target.getY();
		double mx = x+2*(width-x);
		double my = y+2*(height-y);
		
		double d[] = new double[9];
		DPoint m[] = new DPoint[9];
		
		//Zero Reflections (identity)
		m[0] = target;		
		//Single Reflections
		m[1] = new DPoint(x,my);		
		m[2] = new DPoint(mx,y);		
		m[3] = new DPoint(x,-y);		
		m[4] = new DPoint(-x,y);		
		//Double Reflections
		m[5] = new DPoint(mx,my);
		m[6] = new DPoint(mx,-y);
		m[7] = new DPoint(-x,-y);
		m[8] = new DPoint(-x,my);
		
		//Find min
		int index=0;
		d[0] = getRawDistance(sPos,m[0]);
		for(int i=1;i<d.length;i++)
		{
			d[i] = getRawDistance(sPos,m[i]);
			
			if(d[i] < d[index])
			{
				index = i;
			}
		}
		return m[index];		
	}
	
	//figures out the new heading for a ship, taking into account how far a ship can turn
	public static double calculateAdjustedHeading(double curHeading, double desiredHeading, double maxTurningRate)
	{
		double angle = desiredHeading-curHeading;
		if(angle > Math.PI)
		{
			angle-=2*Math.PI;
		}
		
		if(angle < -Math.PI)
		{
			angle += 2*Math.PI;
		}
		
		if(Math.abs(angle) <= maxTurningRate)
			return desiredHeading;
		else if (angle  > 0)
			return curHeading + maxTurningRate;
		else //if (angle < 0)
			return curHeading-maxTurningRate;		
	}
	
	public static double wrapHeading(double h)
	{
		return wrap(h,HEADING_WRAP);
	}
	
	
	
	public static double wrap(double value, double range)
	{
		value -= Math.floor(value/range)*range;
		
		while(value <0)
			value+=range;
		while(value >= range)
			value -= range;
		
		return value;
	}
	
	public static double wrap(double value, double range, double center)
	{
		double oup =  wrap(value,range);
		double offset = wrap(center,range);
		
		//There's probably a faster way to do this, but I couldn't figure it out in 5 minutes.
		//  and we should be within range of the correct value, so these really shouldn't run more than once
		oup += (center-offset);		
		while(oup < center-range/2)
			oup+=range;
		while(oup>= center+ range/2)
			oup -= range;
		
		return oup;
	}
	
	public static DPoint wrap(DPoint p, double width, double height)
	{
		DPoint oup = new DPoint(wrap(p.getX(),width),wrap(p.getY(),height));
		return oup;
	}
	
	public static DPoint wrap(DPoint p, DPoint center, double width, double height)
	{
		DPoint oup = new DPoint(wrap(p.getX(),width,center.getX()),wrap(p.getY(),height,center.getY()));
		return oup;
	}
	
	public static DQuad wrap(DQuad r, DPoint center, double width,double height)
	{
		DPoint rc = r.getCenter();
		DPoint offset = wrap(rc,center,width,height);
		offset = offset.subtract(rc);
		DPoint p1 = r.getP1().add(offset);
		DPoint p2 = r.getP2().add(offset);
		DPoint p3 = r.getP3().add(offset);
		DPoint p4 = r.getP4().add(offset);
		
		return new DQuad(p1,p2,p3,p4);
	}
	
	//does no wrapping
	public static DPoint calculateNewPos(DPoint orig, double heading, double speed)
	{
		return orig.add(calculateOffset(heading,speed));
	}
	
	public static DPoint calculateOffset(double heading, double speed)
	{
		return new DPoint(calculateXOffset(heading,speed), calculateYOffset(heading,speed));
	}
	
	public static double calculateXOffset(double heading, double speed)
	{
		return (Math.cos(heading)*speed);
	}
	
	public static double calculateYOffset(double heading,double speed)
	{
		return (Math.sin(heading)*speed);
	}
	
	public static double degToRad(double d)
	{
		return (d/360)*2*Math.PI;
	}
	
	public static double radToDeg(double r)
	{
		return (r/(2*Math.PI))*360;
	}
	
	
	
	public static DPoint rotate(DPoint p, DPoint center, double r)
	{
		if(r == 0 || p.equals(center))
			return p;
		
		DPoint diff = p.subtract(center);
		double length = diff.getRadius();
		double angle = diff.getTheta();
		angle+=r;
		
		DPoint oup = DPoint.newPolar(length,angle);
		oup = oup.add(center);
		
		return oup;	
	}
	
	public static DPoint findCenter(DPoint a, DPoint b, DPoint c, DPoint d)
	{
		DPoint oup = new DPoint((a.getX()+b.getX()+c.getX()+d.getX())/4 , (a.getY()+b.getY()+c.getY()+d.getY())/4);
		return oup;
	}
	
	//rotates a DQuad around it's center point
	public static DQuad rotate(DQuad q, double r)
	{
		if(r==0)
			return q;
		DPoint ul = q.getP1();
		DPoint ur = q.getP2();
		DPoint br = q.getP3();
		DPoint bl = q.getP4();
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
		DPoint br = q.getP3();
		DPoint bl = q.getP4();
		
		ul = rotate(ul, c, r);
		ur = rotate(ur, c, r);
		bl = rotate(bl, c, r);
		br = rotate(br, c, r);
		
		return new DQuad(ul,ur,br,bl);
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
		//Write a unit test whenever this gets done.
		throw new MethodNotImplementedException();
	}
	
	//test if the interval [a1,a2] intersects the interval [b1,b2]
	//Precondition: a1 <= a2, b1<= b2
	public static boolean isIntersection(double a1, double a2, double b1, double b2)
	{
		if(a1 <= b1 && b1 <= a2)
			return true;
		if(a1 <= b2 && b2 <= a2)
			return true;
		if(b1 <= a1 && a1 <= b2)
			return true;
		if(b1 <= a2 && a2 <= b2)
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
	
	//test if the line segments Seg[a1,a2] intersects Seg[b1,b2]
	public static boolean isIntersection(DPoint a1, DPoint a2, DPoint b1, DPoint b2)
	{
		DPoint offset = a2.subtract(a1);
		double t = -offset.getTheta();
		a2 = rotate(a2,a1,t);
		b1 = rotate(b1,a1,t);
		b2 = rotate(b2,a1,t);
		
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
			
			double xVal;
			
			double bSlope = (b2.getY()-b1.getY())/(b2.getX()-b1.getX());
			
			if(bSlope == Double.POSITIVE_INFINITY || bSlope==Double.NEGATIVE_INFINITY)
			{
				xVal = (b1.getX()+b2.getX())/2;
			}
			else
			{
				if(bSlope == 0)
				{
					double bXMin = min(b1.getX(),b2.getX());
					double bXMax = max(b1.getX(),b2.getX());
					return isIntersection(bXMin,bXMax,aXMin,aXMax);
				}
				
				double bIntercept = (b1.getY())/(bSlope*b1.getX());				
				
				xVal = (yAvg-bIntercept)/bSlope;
			}
			
			if(containsPoint(xVal,aXMin,aXMax))
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
		DPoint center = s.getP4();
		DPoint offset = s.getP3().subtract(center);
		double a = -offset.getTheta();
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
			
			if(p.getX() > br.getX()  ||
			  (p.getY() > ul.getY()) ||
			  (p.getX() < bl.getX()) ||
			  (p.getY() < bl.getY())  )
			{
				continue;
			}
			return true;
		}
		
		for(int x=1;x<=4;x++)
		{
			DPoint p = s.getVertex(x);
			p = rotate(p,center,a);
			
			if(containsPoint(p,r))
				return true;
		}
		
		
		
		//all corners are outside
		//  so let's check if edges cross
		
		
		//If both end points of an r-edge are outside the rectangle
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
	
	public static boolean isCollision(DArc arc, DQuad r)
	{		
		//Debug.print("Entering isCollision: arc="+arc+", r="+r);
		
		double radius = arc.getRadius();
		r = r.subtract(arc.getCenter());
		//Debug.print("Recentering: arc="+arc+", r="+r);
		r = rotate(r,ORIGIN,-arc.getAngleStart());
		arc = new DArc(ORIGIN, radius,0,arc.getAngleSpan());
		
		//Debug.print("Rotating: arc="+arc+", r="+r);
		
		//There is an odd case where the rectangle completely encloses the arc, and thus there are no intersections
		if(containsPoint(ORIGIN,r))
		{
			//Debug.print("Returning True: Contains Origin!");
			return true;
		}
		
		//for each edge y=mx+intercept
		double m,a,intercept,b,c,minX,maxX,minY,maxY;		
		
		DPoint p1,p2;
		p1 = p2 = null;
		
		for(int x=0;x<4;x++)
		{
			switch(x)
			{
				case 0:
					p1 = r.getP1();
					p2 = r.getP2();
					break;
				case 1:
					p1 = r.getP2();
					p2 = r.getP3();
					break;
				case 2:
					p1 = r.getP3();
					p2 = r.getP4();
					break;
				case 3:
					p1 = r.getP4();
					p2 = r.getP1();
					break;
				default:
					Debug.crash("Somebody changed the loop without fixing the switch statement!");
			}
			
			double x1,x2,y1,y2,discriminant;
			DPoint pp1,pp2;
						
			m = (p2.getY()-p1.getY())/(p2.getX()-p1.getX());
			intercept = p2.getY()-m*p2.getX();
			
			//if there exist x and y (in the right range) such that sqrt(x^2+y^2) <= r
			//  then we're shiny
			// So if we're not vertical this is equivalent to sqrt(x^2+(mx+intercept)^2) <= r
			//   x^2+m^2*x^2+2*m*intercept*x+intercept^2 <= r^2
			//   x^2+m^2*x^2+2*m*intercept*x+intercept^2 -r^2<= 0
			// which is all nice and quadratic
			a = (m*m+1);
			b = 2*m*intercept;
			c = intercept*intercept-radius*radius;
			
			if(Math.abs(p2.getX()-p1.getX()) >= 0.001)
			{
				//make sure we're not vertical 

				discriminant = b*b-4*a*c;
				if(discriminant < 0)
					continue;
				
				x1 = (-b - Math.sqrt(discriminant))/(2*a);
				x2 = (-b + Math.sqrt(discriminant))/(2*a);
				
				//so now we need to get the intersection of these points with our line segment
				minX = min(p1.getX(),p2.getX());
				maxX = max(p1.getX(),p2.getX());
				if(!isIntersection(x1,x2,minX,maxX))
					continue;
				if(minX > x1)
					x1 = minX;
				if(maxX < x2)
					x2 = maxX;
				
				y1 = m*x1+intercept;
				y2 = m*x2+intercept;
			}
			else
			{
				//we've got a vertical line...
				
				// so switch x and y here
				//if there exist x and y (in the right range) such that sqrt(x^2+y^2) <= r
				//  then we're shiny
				//So this is equivalent to sqrt(y^2+(0*y+intercept)^2) <= r
				//   x^2+intercept^2 <= r^2
				//   x^2+intercept^2 -r^2<= 0
				// which is all nice and quadratic
				a = 1;
				b = 0;
				m=0;
				intercept = (p1.getX()+p2.getX())/2;
				c = intercept*intercept-radius*radius;
			
				discriminant = b*b-4*a*c;
				if(discriminant < 0)
					continue;
				
				y1 = (-b - Math.sqrt(discriminant))/(2*a);
				y2 = (-b + Math.sqrt(discriminant))/(2*a);
				
				//so now we need to get the intersection of these points with our line segment
				minY = min(p1.getY(),p2.getY());
				maxY = max(p1.getY(),p2.getY());
				if(!isIntersection(y1,y2,minY,maxY))
					continue;
				if(minY > y1)
					y1 = minY;
				if(maxY < y2)
					y2 = maxY;
				
				x1 = intercept;
				x2 = intercept;			
			}
			
			pp1 = new DPoint(x1,y1);
			pp2 = new DPoint(x2,y2);
			
			if(Math.abs(discriminant) < 0.001)
			{
				if(containsPoint(pp1,arc))
				{
//					Debug.print("m="+m);
//					Debug.print("intercept="+intercept);
//					Debug.print("a="+a);
//					Debug.print("b="+b);
//					Debug.print("c="+c);
//					Debug.print("p1.x="+p1.getX()+", p1.y="+p1.getY());
//					Debug.print("p2.x="+p2.getX()+", p2.y="+p2.getY());
//					Debug.print("x1="+x1+", y1="+y1);
//					Debug.print("x2="+x2+", y2="+y2);
//					Debug.print("Returning True at x="+x);
					return true;
				}
			}
			
			if(isIntersection(0,arc.getAngleSpan(),pp1.getTheta(),pp2.getTheta()))
			{
//				Debug.print("m="+m);
//				Debug.print("intercept="+intercept);
//				Debug.print("a="+a);
//				Debug.print("b="+b);
//				Debug.print("c="+c);
//				Debug.print("p1.x="+p1.getX()+", p1.y="+p1.getY());
//				Debug.print("p2.x="+p2.getX()+", p2.y="+p2.getY());
//				Debug.print("x1="+x1+", y1="+y1);
//				Debug.print("x2="+x2+", y2="+y2);
//				Debug.print("Returning True at x="+x);
				return true;
			}
			
		}
		
		//Debug.print("Returning False");
		return false;		
	}
	
	public static boolean containsPoint(DPoint p, DArc a)
	{
		DPoint offset = p.subtract(a.getCenter());
		double radius = offset.getRadius();
		
		if(radius > a.getRadius())
			return false;
		
		double angle = offset.getTheta();
		
		
		return (a.getAngleStart() <= angle && angle <= a.getAngleEnd());		
	}
}

