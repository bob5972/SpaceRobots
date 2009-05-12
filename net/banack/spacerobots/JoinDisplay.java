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

package net.banack.spacerobots;

import java.io.IOException;

public class JoinDisplay implements Display
{
	//ideally, this could be more generic, but this works for now
	private Display one, two; 
	
	public JoinDisplay(Display one, Display two)
	{
		this.one = one;
		this.two = two;
	}
	
	public void closeDisplay(Battle b) throws IOException
	{
		one.closeDisplay(b);
		two.closeDisplay(b);
		
	}
	
	public void initDisplay(Battle b) throws IOException
	{
		one.initDisplay(b);
		two.initDisplay(b);
	}
	
	public void updateDisplay(Battle b) throws IOException
	{
		one.updateDisplay(b);
		two.updateDisplay(b);		
	}


    public boolean isVisible()
    {
	return false;
    }	
}
