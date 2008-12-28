package net.banack.spacerobots.util;

import net.banack.spacerobots.Battle;
import net.banack.util.MethodNotImplementedException;

public class SpaceMath
{
	public static final int HEADING_MAX = Battle.HEADING_MAX;
	public static final int HEADING_WRAP = Battle.HEADING_WRAP;
	
	public static int calculateAdjustedHeading(int curHeading, int desiredHeading, int maxTurningRate)
	{
		if(Math.abs(curHeading-desiredHeading) <= maxTurningRate)
			return desiredHeading;
		else if (curHeading > desiredHeading)
			return curHeading - maxTurningRate;
		else if (curHeading < desiredHeading)
			return curHeading+maxTurningRate;
		
		throw new MethodNotImplementedException("No error handler");		
	}
	
	public static int calculateXOffset(int heading, int speed)
	{
		if(heading > HEADING_MAX)
			throw new IllegalArgumentException("heading exceeds HEADING_MAX");
		if(heading < 0)
			throw new IllegalArgumentException("heading is less than zero!");
		return (int)(Math.cos(headingToRadians(heading))*speed);
	}
	
	public static double headingToRadians(int heading)
	{
		double oup =heading/((double)HEADING_WRAP)*2*Math.PI;
		while(oup < 0)
			oup+=2*Math.PI;
		while(oup > 2*Math.PI)
			oup-=2*Math.PI;
		return oup;
	}
	
	public static int radiansToHeading(double r)
	{
		int oup= (int)(r/(2*Math.PI)*HEADING_WRAP);
		while(oup < 0)
			oup += HEADING_WRAP;
		while(oup > HEADING_MAX)
			oup-=HEADING_WRAP;
		return oup;
		
	}
	
	public static int calculateYOffset(int heading,int speed)
	{
		if(heading > HEADING_MAX)
			throw new IllegalArgumentException("heading exceeds HEADING_MAX");
		if(heading < 0)
			throw new IllegalArgumentException("heading is less than zero!");
		return (int)(Math.sin(headingToRadians(heading))*speed);
	}
}

