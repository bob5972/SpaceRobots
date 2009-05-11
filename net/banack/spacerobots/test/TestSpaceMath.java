/*
 * This file is part of SpaceRobots.
 * Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SpaceRobots.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots.test;

import static org.junit.Assert.*;



import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.banack.spacerobots.util.SpaceMath;
import net.banack.geometry.*;

public class TestSpaceMath
{
	public static final double TOLERANCE = 0.0001;

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#calculateAdjustedHeading(double, double, double)}.
	 */
	@Test
	public void testCalculateAdjustedHeading()
	{
		Assert.assertEquals(0.1,SpaceMath.calculateAdjustedHeading(0, 1, 0.1),TOLERANCE);
		Assert.assertEquals(0.2,SpaceMath.calculateAdjustedHeading(0, 1, 0.2),TOLERANCE);
		Assert.assertEquals(0.8,SpaceMath.calculateAdjustedHeading(1, 0, 0.2),TOLERANCE);
		Assert.assertEquals(0.7,SpaceMath.calculateAdjustedHeading(1, 0, 0.3),TOLERANCE);
		Assert.assertEquals(0.5,SpaceMath.calculateAdjustedHeading(1, 0.5, 0.6),TOLERANCE);
		Assert.assertEquals(1,SpaceMath.calculateAdjustedHeading(0, 1,1.5 ),TOLERANCE);
		Assert.assertEquals(0.5,SpaceMath.calculateAdjustedHeading(1, 0.5, 0.6),TOLERANCE);
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#wrapHeading(double)}.
	 */
	@Test
	public void testWrapHeading()
	{
		Assert.assertEquals(1,SpaceMath.wrapHeading(Math.PI*2+1),TOLERANCE);
		Assert.assertEquals(2,SpaceMath.wrapHeading(Math.PI*2+2),TOLERANCE);
		Assert.assertEquals(0,SpaceMath.wrapHeading(Math.PI*2),TOLERANCE);
		Assert.assertEquals(0,SpaceMath.wrapHeading(-Math.PI*2),TOLERANCE);
		Assert.assertEquals(1,SpaceMath.wrapHeading(-Math.PI*2+1),TOLERANCE);
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#calculateXOffset(double, double)}.
	 */
	@Test
	public void testCalculateXOffset()
	{
		Assert.assertEquals(0.5403023058, SpaceMath.calculateXOffset(1,1),TOLERANCE);
		Assert.assertEquals(2*0.5403023058, SpaceMath.calculateXOffset(1,2),TOLERANCE);
		Assert.assertEquals(2, SpaceMath.calculateXOffset(0,2),TOLERANCE);
		Assert.assertEquals(0, SpaceMath.calculateXOffset(Math.PI/2,100),TOLERANCE);
		Assert.assertEquals(-1, SpaceMath.calculateXOffset(Math.PI,1),TOLERANCE);
		Assert.assertEquals(-2, SpaceMath.calculateXOffset(Math.PI,2),TOLERANCE);
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#calculateYOffset(double, double)}.
	 */
	@Test
	public void testCalculateYOffset()
	{
		Assert.assertEquals(0.8414709848, SpaceMath.calculateYOffset(1,1),TOLERANCE);
		Assert.assertEquals(2*0.8414709848, SpaceMath.calculateYOffset(1,2),TOLERANCE);
		Assert.assertEquals(2, SpaceMath.calculateYOffset(Math.PI/2,2),TOLERANCE);
		Assert.assertEquals(0, SpaceMath.calculateYOffset(0,100),TOLERANCE);
		Assert.assertEquals(0, SpaceMath.calculateYOffset(Math.PI,100),TOLERANCE);
		Assert.assertEquals(-1, SpaceMath.calculateYOffset(3*Math.PI/2,1),TOLERANCE);
		Assert.assertEquals(-2, SpaceMath.calculateYOffset(3*Math.PI/2,2),TOLERANCE);
		Assert.assertEquals(3, SpaceMath.calculateYOffset(Math.PI/2,3),TOLERANCE);
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#degToRad(double)}.
	 */
	@Test
	public void testDegToRad()
	{
		Assert.assertEquals(0,SpaceMath.degToRad(0),TOLERANCE);
		Assert.assertEquals(Math.PI/2,SpaceMath.degToRad(90),TOLERANCE);
		Assert.assertEquals(Math.PI,SpaceMath.degToRad(180),TOLERANCE);
		Assert.assertEquals(3*Math.PI/2,SpaceMath.degToRad(270),TOLERANCE);
		Assert.assertEquals(Math.PI*2,SpaceMath.degToRad(360),TOLERANCE);
		
		Assert.assertEquals(Math.PI/4,SpaceMath.degToRad(45),TOLERANCE);
		Assert.assertEquals(Math.PI/4+Math.PI/2,SpaceMath.degToRad(45+90),TOLERANCE);
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#radToDeg(double)}.
	 */
	@Test
	public void testRadToDeg()
	{
		Assert.assertEquals(0,SpaceMath.radToDeg(0),TOLERANCE);
		Assert.assertEquals(90,SpaceMath.radToDeg(Math.PI/2),TOLERANCE);
		Assert.assertEquals(180,SpaceMath.radToDeg(Math.PI),TOLERANCE);
		Assert.assertEquals(270,SpaceMath.radToDeg(3*Math.PI/2),TOLERANCE);
		Assert.assertEquals(360,SpaceMath.radToDeg(Math.PI*2),TOLERANCE);
		
		Assert.assertEquals(45,SpaceMath.radToDeg(Math.PI/4),TOLERANCE);
		Assert.assertEquals(45+90,SpaceMath.radToDeg(Math.PI/4+Math.PI/2),TOLERANCE);
	}
	
	
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#rotate(net.banack.geometry.DPoint, net.banack.geometry.DPoint, double)}.
	 */
	@Test
	public void testRotateDPointDPointDouble()
	{
		DPoint center,origin,point,result;
		double angle,x,y;
		
		origin = center = new DPoint(0,0);
		point = new DPoint(1,0);		
		angle = 0;
		
		Assert.assertEquals(1,SpaceMath.rotate(point,center,0).getX(),TOLERANCE);
		Assert.assertEquals(0,SpaceMath.rotate(point,center,0).getY(),TOLERANCE);
		Assert.assertEquals(0,SpaceMath.rotate(point,center,Math.PI/2).getX(),TOLERANCE);
		Assert.assertEquals(1,SpaceMath.rotate(point,center,Math.PI/2).getY(),TOLERANCE);
		Assert.assertEquals(Math.cos(1),SpaceMath.rotate(point,center,1).getX(),TOLERANCE);
		Assert.assertEquals(Math.sin(1),SpaceMath.rotate(point,center,1).getY(),TOLERANCE);
		
		point = new DPoint(1,-1);
		result=SpaceMath.rotate(point,origin,1);
		Assert.assertEquals(point.getRadius(),result.getRadius(),TOLERANCE);
		Assert.assertEquals(-Math.PI/4+1,result.getTheta(),TOLERANCE);
		Assert.assertEquals(Math.sqrt(2)*Math.cos(-Math.PI/4+1),result.getX(),TOLERANCE);
		Assert.assertEquals(Math.sqrt(2)*Math.sin(-Math.PI/4+1),result.getY(),TOLERANCE);
		
		point = new DPoint(3,10);
		result=SpaceMath.rotate(point,origin,2);
		result=SpaceMath.rotate(result,origin,-2);
		Assert.assertEquals(point.getRadius(),result.getRadius(),TOLERANCE);
		Assert.assertEquals(point.getTheta(),result.getTheta(),TOLERANCE);
		Assert.assertEquals(point.getX(),result.getX(),TOLERANCE);
		Assert.assertEquals(point.getY(),result.getY(),TOLERANCE);
		
		
		point=new DPoint(1,0);
		center = origin;
		while(angle < 2*Math.PI)
		{
			result = SpaceMath.rotate(point,center,angle);
			x=result.getX();
			y=result.getY();
			Assert.assertEquals(point.getRadius(),result.getRadius(),TOLERANCE);
			Assert.assertEquals(Math.cos(angle),x,TOLERANCE);
			Assert.assertEquals(Math.sin(angle),y,TOLERANCE);
			Assert.assertEquals(1,x*x+y*y,TOLERANCE);
		
			angle += Math.PI/64;
		}
		
		center = new DPoint(1,1);
		point = new DPoint(2,1);		
		angle = 0;
		while(angle < 2*Math.PI)
		{
			result = SpaceMath.rotate(point,center,angle);
			x = result.getX()-1;
			y = result.getY()-1;
			Assert.assertEquals(Math.cos(angle),x,TOLERANCE);
			Assert.assertEquals(Math.sin(angle),y,TOLERANCE);
			Assert.assertEquals(1,x*x+y*y,TOLERANCE);
		
			angle += Math.PI/64;
		}
		
		center = origin;
		point = new DPoint(2,0);		
		angle = 0;
		while(angle < 2*Math.PI)
		{
			result = SpaceMath.rotate(point,center,angle);
			x=result.getX();
			y=result.getY();
			Assert.assertEquals(point.getRadius(),result.getRadius(),TOLERANCE);
			Assert.assertEquals(2*Math.cos(angle),x,TOLERANCE);
			Assert.assertEquals(2*Math.sin(angle),y,TOLERANCE);
			Assert.assertEquals(2,Math.sqrt(x*x+y*y),TOLERANCE);
		
			angle += Math.PI/64;
		}
		
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#findCenter(net.banack.geometry.DPoint, net.banack.geometry.DPoint, net.banack.geometry.DPoint, net.banack.geometry.DPoint)}.
	 */
	@Test
	public void testFindCenter()
	{
		DPoint center;
		DPoint p1 = new DPoint(-1,1);
		DPoint p2 = new DPoint(1,1);
		DPoint p3 = new DPoint(1,-1);
		DPoint p4 = new DPoint(-1,-1);
		
		center = SpaceMath.findCenter(p1,p2,p3,p4);
		
		Assert.assertEquals(0,center.getX(),TOLERANCE);
		Assert.assertEquals(0,center.getY(),TOLERANCE);
		
		p1 = new DPoint(5,5);
		p2 = new DPoint(1,1);
		p3 = new DPoint(-1,-1);
		p4 = new DPoint(-5,-5);
		
		center = SpaceMath.findCenter(p1,p2,p3,p4);
		
		Assert.assertEquals(0,center.getX(),TOLERANCE);
		Assert.assertEquals(0,center.getY(),TOLERANCE);	
		
		p1 = new DPoint(5,1);
		p2 = new DPoint(3,2);
		p3 = new DPoint(0,3);
		p4 = new DPoint(-8,-6);
		
		center = SpaceMath.findCenter(p1,p2,p3,p4);
		
		Assert.assertEquals(0,center.getX(),TOLERANCE);
		Assert.assertEquals(0,center.getY(),TOLERANCE);	
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#rotate(net.banack.geometry.DQuad, double)}.
	 */
	@Test
	public void testRotateDQuadDouble()
	{
		DPoint p1 = new DPoint(-1,1);
		DPoint p2 = new DPoint(1,1);
		DPoint p3 = new DPoint(1,-1);
		DPoint p4 = new DPoint(-1,-1);
		DPoint pred1,pred2,pred3,pred4;
		
		DQuad q = new DQuad(p1,p2,p3,p4);
		DQuad oup;
		double angle;
		DPoint center = new DPoint(0,0);
		Assert.assertTrue(center.equals(SpaceMath.findCenter(p1,p2,p3,p4),TOLERANCE));
		
		oup=SpaceMath.rotate(q,1);
		oup=SpaceMath.rotate(oup,-1);
		Assert.assertEquals(q.getP3().getRadius(),oup.getP3().getRadius(),TOLERANCE);
		Assert.assertEquals(q.getP3().getX(),oup.getP3().getX(),TOLERANCE);
		Assert.assertEquals(q.getP3().getY(),oup.getP3().getY(),TOLERANCE);
		
		
		oup=SpaceMath.rotate(q,1);
		Assert.assertEquals(p3.getRadius(),oup.getP3().getRadius(),TOLERANCE);
		Assert.assertEquals(p3.getRadius()*Math.cos(-Math.PI/4+1),oup.getP3().getX(),TOLERANCE);
		Assert.assertEquals(p3.getRadius()*Math.sin(-Math.PI/4+1),oup.getP3().getY(),TOLERANCE);

		angle=0;
		while(angle < 2*Math.PI)
		{
			oup = SpaceMath.rotate(q, angle);
			
			Assert.assertNotNull(oup.getP1());
			Assert.assertNotNull(oup.getP2());
			Assert.assertNotNull(oup.getP3());
			Assert.assertNotNull(oup.getP4());
			
			pred1 = SpaceMath.rotate(p1,center,angle);
			pred2 = SpaceMath.rotate(p2,center,angle);
			pred3 = SpaceMath.rotate(p3,center,angle);
			pred4 = SpaceMath.rotate(p4,center,angle);
			
			Assert.assertTrue(oup.getP1().equals(pred1,TOLERANCE));
			Assert.assertTrue(oup.getP2().equals(pred2,TOLERANCE));
			Assert.assertEquals(pred3.getX(),oup.getP3().getX(),TOLERANCE);
			Assert.assertEquals(pred3.getY(),oup.getP3().getY(),TOLERANCE);
			Assert.assertTrue(oup.getP4().equals(pred4,TOLERANCE));
			
			oup = SpaceMath.rotate(oup, -angle);
			
			Assert.assertNotNull(oup.getP1());
			Assert.assertNotNull(oup.getP2());
			Assert.assertNotNull(oup.getP3());
			Assert.assertNotNull(oup.getP4());
			
			pred1 = q.getP1();
			pred2 = q.getP2();
			pred3 = q.getP3();
			pred4 = q.getP4();
			
			Assert.assertTrue(oup.getP1().equals(pred1,TOLERANCE));
			Assert.assertTrue(oup.getP2().equals(pred2,TOLERANCE));
			Assert.assertEquals(pred3.getX(),oup.getP3().getX(),TOLERANCE);
			Assert.assertEquals(pred3.getY(),oup.getP3().getY(),TOLERANCE);
			Assert.assertTrue(oup.getP4().equals(pred4,TOLERANCE));
			
			angle+=Math.PI/64;
		}
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#rotate(net.banack.geometry.DQuad, net.banack.geometry.DPoint, double)}.
	 */
	@Test
	public void testRotateDQuadDPointDouble()
	{
		DPoint a = new DPoint(-1,2);
		DPoint b = new DPoint(5,7);
		DPoint c = new DPoint(10,-2);
		DPoint d = new DPoint(-10,-20);
		DPoint origin = new DPoint(0,0);
		
		DQuad q = new DQuad(a,b,c,d);
		DQuad oup = SpaceMath.rotate(q,origin,1);
		
		DPoint pa = SpaceMath.rotate(a,origin,1);
		DPoint pb = SpaceMath.rotate(b,origin,1);
		DPoint pc = SpaceMath.rotate(c,origin,1);
		DPoint pd = SpaceMath.rotate(d,origin,1);
		
		Assert.assertEquals(pa.getX(),oup.getP1().getX(),TOLERANCE);
		Assert.assertEquals(pb.getX(),oup.getP2().getX(),TOLERANCE);
		Assert.assertEquals(pc.getX(),oup.getP3().getX(),TOLERANCE);
		Assert.assertEquals(pd.getX(),oup.getP4().getX(),TOLERANCE);
		
		Assert.assertEquals(pa.getY(),oup.getP1().getY(),TOLERANCE);
		Assert.assertEquals(pb.getY(),oup.getP2().getY(),TOLERANCE);
		Assert.assertEquals(pc.getY(),oup.getP3().getY(),TOLERANCE);
		Assert.assertEquals(pd.getY(),oup.getP4().getY(),TOLERANCE);
	}
	
	
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#getDQuad(net.banack.geometry.DPoint, double, double, double)}.
	 */
	@Test
	public void testGetDQuad()
	{
		//fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#getNormalBounds(net.banack.geometry.DQuad)}.
	 */
	@Test
	public void testGetNormalBounds()
	{
		DPoint origin = new DPoint(0,0);
		DQuad q = SpaceMath.getDQuad(origin,10,7,0);
		
		Assert.assertEquals(-5,q.getP1().getX(),TOLERANCE);
		Assert.assertEquals( 5,q.getP2().getX(),TOLERANCE);
		Assert.assertEquals( 5,q.getP3().getX(),TOLERANCE);
		Assert.assertEquals(-5,q.getP4().getX(),TOLERANCE);
		
		Assert.assertEquals( 3.5,q.getP1().getY(),TOLERANCE);
		Assert.assertEquals( 3.5,q.getP2().getY(),TOLERANCE);
		Assert.assertEquals(-3.5,q.getP3().getY(),TOLERANCE);
		Assert.assertEquals(-3.5,q.getP4().getY(),TOLERANCE);
		
		
		DQuad q2 = SpaceMath.getDQuad(origin,10,7,1);
		DQuad r = SpaceMath.rotate(q,origin, 1);
		
		
		Assert.assertEquals(r.getP1().getX(),q2.getP1().getX(),TOLERANCE);
		Assert.assertEquals(r.getP2().getX(),q2.getP2().getX(),TOLERANCE);
		Assert.assertEquals(r.getP3().getX(),q2.getP3().getX(),TOLERANCE);
		Assert.assertEquals(r.getP4().getX(),q2.getP4().getX(),TOLERANCE);
		
		Assert.assertEquals(r.getP1().getY(),q2.getP1().getY(),TOLERANCE);
		Assert.assertEquals(r.getP2().getY(),q2.getP2().getY(),TOLERANCE);
		Assert.assertEquals(r.getP3().getY(),q2.getP3().getY(),TOLERANCE);
		Assert.assertEquals(r.getP4().getY(),q2.getP4().getY(),TOLERANCE);
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#reorderToNormal(net.banack.geometry.DQuad)}.
	 */
	@Test
	public void testReorderToNormal()
	{
		//fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#isIntersection(double, double, double, double)}.
	 */
	@Test
	public void testIsIntersectionDoubleDoubleDoubleDouble()
	{
		double a,b,c,d,e;
		a = 0;
		b = 0.5;
		c = 1;
		d = 2;
		e = 3;
		
		Assert.assertTrue(SpaceMath.isIntersection(a,c,b,d));
		Assert.assertFalse(SpaceMath.isIntersection(a,b,c,d));
		Assert.assertTrue(SpaceMath.isIntersection(a,e,c,d));
		Assert.assertTrue(SpaceMath.isIntersection(b,c,a,d));
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#containsPoint(double, double, double)}.
	 */
	@Test
	public void testContainsPointDoubleDoubleDouble()
	{
		double a,b,c,d,e;
		a = 0;
		b = 0.5;
		c = 1;
		d = 2;
		e = 3;
		
		Assert.assertTrue(SpaceMath.containsPoint(b,a,c));
		Assert.assertFalse(SpaceMath.containsPoint(a,b,c));
		Assert.assertTrue(SpaceMath.containsPoint(a,a,c));
		Assert.assertTrue(SpaceMath.containsPoint(d,a,e));
		
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#isIntersection(net.banack.geometry.DPoint, net.banack.geometry.DPoint, net.banack.geometry.DPoint, net.banack.geometry.DPoint)}.
	 */
	@Test
	public void testIsIntersectionDPointDPointDPointDPoint()
	{
		DPoint a,b,c,d,e,f,g,h;
		DPoint origin = new DPoint(0,0);
		a = new DPoint(0,1);
		b = new DPoint(1,0);
		c = new DPoint(0,-1);
		d = new DPoint(-1,0);
		
		e = new DPoint(1,1);
		f = new DPoint(-1,-1);
		g = new DPoint(-1,1);
		h = new DPoint(1,-1);
		
		Assert.assertTrue(SpaceMath.isIntersection(origin,e,a,b));
		Assert.assertTrue(SpaceMath.isIntersection(f,e,g,h));		
		Assert.assertTrue(SpaceMath.isIntersection(origin,e,b,a));
		Assert.assertTrue(SpaceMath.isIntersection(e,origin,b,a));
		Assert.assertFalse(SpaceMath.isIntersection(origin,h,b,e));
		Assert.assertFalse(SpaceMath.isIntersection(b,e,origin,a));		
		Assert.assertFalse(SpaceMath.isIntersection(a,b,c,d));
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#min(double, double)}.
	 */
	@Test
	public void testMin()
	{
		Assert.assertEquals(1.0,SpaceMath.min(1,2));
		Assert.assertEquals(2.0,SpaceMath.min(2,3));
		Assert.assertEquals(Math.PI,SpaceMath.min(Math.PI+1,Math.PI));
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#max(double, double)}.
	 */
	@Test
	public void testMax()
	{
		Assert.assertEquals(2.0,SpaceMath.max(1,2));
		Assert.assertEquals(3.0,SpaceMath.max(2,3));
		Assert.assertEquals(Math.PI+1,SpaceMath.max(Math.PI+1,Math.PI));
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#containsPoint(net.banack.geometry.DPoint, net.banack.geometry.DQuad)}.
	 */
	@Test
	public void testContainsPointDPointDQuad()
	{
		DPoint a,b,c,d,p;
		a = new DPoint(-1,1);
		b = new DPoint(1,1);
		c = new DPoint(1,-1);
		d = new DPoint(-1,-1);
		DQuad q = new DQuad(a,b,c,d);
		
		p = new DPoint(0,0);
		Assert.assertTrue(SpaceMath.containsPoint(p,q));
		p = new DPoint(1,0);
		Assert.assertTrue(SpaceMath.containsPoint(p,q));
		p = new DPoint(0.5,0.5);
		Assert.assertTrue(SpaceMath.containsPoint(p,q));
		
		p = new DPoint(10,10);
		Assert.assertFalse(SpaceMath.containsPoint(p,q));
		p = new DPoint(-10,10);
		Assert.assertFalse(SpaceMath.containsPoint(p,q));
		p = new DPoint(10,-10);
		Assert.assertFalse(SpaceMath.containsPoint(p,q));
		p = new DPoint(-10,-10);
		Assert.assertFalse(SpaceMath.containsPoint(p,q));
		
		Assert.assertTrue(SpaceMath.containsPoint(a,q));
		Assert.assertTrue(SpaceMath.containsPoint(b,q));
		Assert.assertTrue(SpaceMath.containsPoint(c,q));
		Assert.assertTrue(SpaceMath.containsPoint(d,q));
		
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#isCollision(net.banack.geometry.DQuad, net.banack.geometry.DQuad)}.
	 */
	@Test
	public void testIsCollisionDQuadDQuad()
	{
		DPoint a,b,c,d,w,x,y,z;
		a = new DPoint(-1,1);
		b = new DPoint(1,1);
		c = new DPoint(1,-1);
		d = new DPoint(-1,-1);
		DQuad q = new DQuad(a,b,c,d);
		
		w = new DPoint(-2,2);
		x = new DPoint(2,2);
		y = new DPoint(2,-2);
		z = new DPoint(-2,-2);
		DQuad p = new DQuad(w,x,y,z);
		
		Assert.assertTrue(SpaceMath.isCollision(q,p));
		Assert.assertTrue(SpaceMath.isCollision(p,q));
		DQuad r = p.add(b);
		Assert.assertTrue(SpaceMath.isCollision(q,r));
		Assert.assertTrue(SpaceMath.isCollision(r,q));
		Assert.assertTrue(SpaceMath.isCollision(p,r));
		Assert.assertTrue(SpaceMath.isCollision(r,p));
		r = q.add(b);
		Assert.assertTrue(SpaceMath.isCollision(q,r));
		Assert.assertTrue(SpaceMath.isCollision(r,q));
		Assert.assertTrue(SpaceMath.isCollision(p,r));
		Assert.assertTrue(SpaceMath.isCollision(r,p));
		r = q.add(new DPoint(10,10));
		Assert.assertFalse(SpaceMath.isCollision(q,r));
		Assert.assertFalse(SpaceMath.isCollision(r,q));
		Assert.assertFalse(SpaceMath.isCollision(p,r));
		Assert.assertFalse(SpaceMath.isCollision(r,p));
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#containsPoint(net.banack.geometry.DPoint, net.banack.geometry.DArc)}.
	 */
	@Test
	public void testContainsPointDPointDArc()
	{
		DArc a = new DArc(SpaceMath.ORIGIN,1,0,Math.PI);
		Assert.assertTrue(SpaceMath.containsPoint(SpaceMath.ORIGIN, a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(0,0.5), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(2,2), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(-2,-2), a));
		
		Assert.assertTrue(SpaceMath.containsPoint(DPoint.newPolar(0.5,1),a));
		Assert.assertTrue(SpaceMath.containsPoint(DPoint.newPolar(0.5,2),a));
		Assert.assertTrue(SpaceMath.containsPoint(DPoint.newPolar(0.5,3),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(0.5,4),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(0.5,5),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(0.5,6),a));
		
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(2.5,1),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(2.5,2),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(2.5,3),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(2.5,4),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(2.5,5),a));
		Assert.assertFalse(SpaceMath.containsPoint(DPoint.newPolar(2.5,6),a));
		
		a = new DArc(SpaceMath.ORIGIN, 10,0,Math.PI/2);
		Assert.assertTrue(SpaceMath.containsPoint(SpaceMath.ORIGIN, a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(0,0.5), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(2,2), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(1,2), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(3,1), a));
		
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(10,11), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(-1,-1), a));
		
		a = new DArc(SpaceMath.ORIGIN, 10,Math.PI,3*Math.PI/2);
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(0,0.5), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(2,2), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(1,2), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(3,1), a));
		Assert.assertFalse(SpaceMath.containsPoint(new DPoint(10,11), a));
		
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(-1,-1), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(0,-0.5), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(-2,-2), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(-1,-2), a));
		Assert.assertTrue(SpaceMath.containsPoint(new DPoint(-3,-1), a));
		
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#wrap(double, double, double)}
	 */
	@Test
	public void testWrap()
	{
		Assert.assertEquals(SpaceMath.wrap(10.7,10,10),10.7);
		Assert.assertEquals(SpaceMath.wrap(19,10,10),9.0);
		Assert.assertEquals(SpaceMath.wrap(19,10,20),19.0);
		Assert.assertEquals(SpaceMath.wrap(19,10,30),29.0);
		Assert.assertEquals(SpaceMath.wrap(19,10,50),49.0);		
	}
	
	/**
	 * Test method for {@link net.banack.spacerobots.util.SpaceMath#isCollision(net.banack.geometry.DArc, net.banack.geometry.DQuad)}.
	 */
	@Test
	public void testIsCollisionDArcDQuad()
	{
		DArc arc;
		DQuad r;
		
		arc = new DArc(SpaceMath.ORIGIN,1, 0,Math.PI/2);
		r = new DQuad(new DPoint(0,1), new DPoint(1,1), new DPoint(1,0), new DPoint(0,0));
		Assert.assertTrue(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(SpaceMath.ORIGIN,1, 0,Math.PI/2);
		r = new DQuad(new DPoint(0,1), new DPoint(1,1), new DPoint(1,0), new DPoint(0.1,0.1));
		Assert.assertTrue(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(SpaceMath.ORIGIN,1, 0,Math.PI/2);
		r = new DQuad(new DPoint(2,2), new DPoint(3,2), new DPoint(3,0), new DPoint(2,0));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(SpaceMath.ORIGIN,1, 0,Math.PI/2);
		r = new DQuad(new DPoint(-2,2), new DPoint(-1.1,2), new DPoint(-1.1,0), new DPoint(-2,0));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(SpaceMath.ORIGIN,1, Math.PI/2,Math.PI/2);
		r = new DQuad(new DPoint(2,2), new DPoint(3,2), new DPoint(3,0), new DPoint(2,0));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(SpaceMath.ORIGIN,1, Math.PI/2,Math.PI/2);
		r = new DQuad(new DPoint(-0.2,0.2), new DPoint(-0.1,0.2), new DPoint(-0.1,0.1), new DPoint(-0.2,0.1));
		Assert.assertTrue(SpaceMath.isCollision(arc,r));			
		
		arc = new DArc(new DPoint(100.0,50.0),40,-0.35,0.7);
		r = new DQuad(new DPoint(98,50+16), new DPoint(99,50+16), new DPoint(99,50+15), new DPoint(98,50+15));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(new DPoint(0,0),10,0,Math.PI/4);
		r = new DQuad(new DPoint(0,30), new DPoint(25,30), new DPoint(25,20), new DPoint(0,20));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));

		arc = new DArc(new DPoint(0,0),40,0,0.7);
		r = new DQuad(new DPoint(50.18700890282735,133.76241449674657), new DPoint(-128.12627167394078,-94.49152127906225), new DPoint(65.93818143705835,124.993117843258), new DPoint(50.986155842519054,123.7943974337204));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));
		
		arc = new DArc(new DPoint(283.3,58.0),40,-0.35,0.7);
		r = new DQuad(new DPoint(284.1980199960244,203.29457416972397), new DPoint(294.8617230662086,-86.15623454226986), new DPoint(301.89451725821266,206.73463007760736), new DPoint(291.2308141880285,196.1854387896012));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));

		arc = new DArc(new DPoint(0,0),40,0,0.7);
		r = new DQuad(new DPoint(46.523960398395516,-141.61749566123808), new DPoint(51.522081884704996,-141.75454166844565), new DPoint(51.385035877497444,-146.7526631547551), new DPoint(46.386914391187965,-146.61561714754754));
		Assert.assertFalse(SpaceMath.isCollision(arc,r));
	}
}

