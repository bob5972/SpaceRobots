/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <bob5972@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with SpaceRobots. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots.ai;

import net.banack.util.ListNode;

/**
 * Chains several AIGovernors and associated Filters together.
 * 
 * @author Michael Banack <bob5972@banack.net>
 * 
 */
public class CompositeGovernor implements AIGovernor
{
	private ListNode<AIGovernor> myGovernors;
	private ListNode<AIFilter> myFilters;
	
	private ListNode<AIGovernor> gBottom;
	private ListNode<AIFilter> fBottom;
	
	private int mySize;
	
	public CompositeGovernor()
	{
		gBottom = myGovernors = null;
		fBottom = myFilters = null;
		mySize = 0;
	}
	
	public void insert(AIGovernor g)
	{
		insert(null, g);
	}
	
	public void insert(AIFilter f)
	{
		insert(f, null);
	}
	
	public void insert(AIFilter f, AIGovernor g)
	{
		myGovernors = new ListNode<AIGovernor>(g, myGovernors);
		myFilters = new ListNode<AIFilter>(f, myFilters);
		
		if (gBottom == null)
			gBottom = myGovernors;
		if (fBottom == null)
			fBottom = myFilters;
		mySize++;
	}
	
	public void append(AIGovernor g)
	{
		append(null, g);
	}
	
	public void append(AIFilter f)
	{
		append(f, null);
	}
	
	public void append(AIFilter f, AIGovernor g)
	{
		ListNode<AIFilter> newF = new ListNode<AIFilter>(f);
		ListNode<AIGovernor> newG = new ListNode<AIGovernor>(g);
		
		if (gBottom != null)
			gBottom.setNext(newG);
		else
			myGovernors = newG;
		
		if (fBottom != null)
			fBottom.setNext(newF);
		else
			myFilters = newF;
		
		gBottom = newG;
		fBottom = newF;
		mySize++;
	}
	
	public int size()
	{
		return mySize;
	}
	
	public void run(AIShip s)
	{
		ListNode<AIGovernor> gp = myGovernors;
		ListNode<AIFilter> fp = myFilters;
		while (gp != null) {
			AIFilter f = fp.getValue();
			AIGovernor g = gp.getValue();
			
			if (g == null && f != null && !f.test(s))
				return;
			
			if (f == null || f.test(s))
				g.run(s);
			
			gp = gp.next();
			fp = fp.next();
		}
	}
}
