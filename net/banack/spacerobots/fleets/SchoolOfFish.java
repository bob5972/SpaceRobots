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

package net.banack.spacerobots.fleets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.banack.debug.Debug;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIFilter;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.CompositeGovernor;
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;

/**
 * An example fleet that mostly flies in the same direction.
 * @author Michael Banack <bob5972@banack.net>
 */
public class SchoolOfFish extends AIFleet
{
	private int nextMove;
	private double groupHeading;
	
	
	public SchoolOfFish()
	{
		super();
	}
	
	public SchoolOfFish(long seed)
	{
		super(seed);
	}
	
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	public String getVersion()
	{
		return "1.1";
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double width, double height)
	{
		super.initBattle(fleetID, teamID, startingCredits, s, teams, f, width, height);
		
		nextMove=100;
		groupHeading=0;
	}

	public Iterator<ShipAction> runTick()
	{		
		Iterator<AIShip> i;
		
		boolean adjTick = false;
		
		if(tick> nextMove)
		{
			nextMove = tick+50;
			adjTick=true;
			
			if(random.nextInt(10) < 4)
			{
				double ax= 0;
				double ay= 0;
				
				i = myShips.iterator();
				while(i.hasNext())
				{
					AIShip cur = i.next();
					ax+= cur.getX();
					ay+= cur.getY();					
				}
				ax /= myShips.size();
				ay /= myShips.size();
				
				groupHeading = SpaceMath.getAngle(new DPoint(ax,ay), myCruiser.getPosition());
			}
			else
			{
				groupHeading += (random.nextDouble())*Math.PI*2;
				groupHeading = SpaceMath.wrapHeading(groupHeading);
			}
		}
		
		CompositeGovernor govna = new CompositeGovernor();
		
		if(adjTick)
		{
			govna.insert(AIFilter.FIGHTERS, new AIGovernor(){
				public void run(AIShip s)
				{
					AIShip t = (AIShip) s;
					if(random.nextInt(10) < 3)
					{
						t.intercept(myCruiser);
					}
					else
					{
						t.setHeading(groupHeading); 
					}
				};
			});
		}
		
		if(myContacts.size() > 0)
		{
			govna.insert(new AIGovernor(){
				public void run(AIShip s)
				{
					AIShip t = (AIShip) s;
					if(t.canLaunch(ROCKET) && Math.abs(t.getHeading() -groupHeading) < 0.1)
					{
						t.launch(ROCKET);
					}
				}
			});
		}
		
		if(govna.size() > 0)
			myShips.apply(govna);
		
		if(myCruiser.isAlive())
		{
			if(myCruiser.canLaunch(FIGHTER) &&  credits > DefaultShipTypeDefinitions.FIGHTER.getCost()*2)
			{
				myCruiser.launch(FIGHTER);
			}
			
			myCruiser.setScannerHeading(myCruiser.projHeading());			
			
			if(tick % 100 == 0)
			{
				double h = myCruiser.curHeading();
				h += random.nextDouble();
				myCruiser.setHeading(h);
			}
		}
		
		return myShips.getActionIterator();
	}
	
}
