/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <github@banack.net>
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

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.util.Filter;

/** Abstract class for filtering AIShips. */
public abstract class AIFilter implements Filter<AIShip>
{
	public static AIFilter FIGHTERS = new AIFilter() {
		public boolean test(AIShip s)
		{
			return s.getTypeID() == DefaultShipTypeDefinitions.FIGHTER_ID;
		}
	};
	public static AIFilter DESTROYERS = new AIFilter() {
		public boolean test(AIShip s)
		{
			return s.getTypeID() == DefaultShipTypeDefinitions.DESTROYER_ID;
		}
	};
	public static AIFilter CRUISERS = new AIFilter() {
		public boolean test(AIShip s)
		{
			return s.getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID;
		}
	};
	public static AIFilter MISSILES = new AIFilter() {
		public boolean test(AIShip s)
		{
			return s.getTypeID() == DefaultShipTypeDefinitions.MISSILE_ID;
		}
	};
	public static AIFilter ALL = new AIFilter() {
		public boolean test(AIShip s)
		{
			return true;
		}
	};
	public static AIFilter NONE = new AIFilter() {
		public boolean test(AIShip s)
		{
			return false;
		}
	};
	
	private static class JoinByANDFilter extends AIFilter
	{
		private AIFilter a, b;
		
		public JoinByANDFilter(AIFilter f, AIFilter g)
		{
			a = f;
			b = g;
		}
		
		public boolean test(AIShip s)
		{
			return a.test(s) && b.test(s);
		}
	}
	
	private static class JoinByORFilter extends AIFilter
	{
		private AIFilter a, b;
		
		public JoinByORFilter(AIFilter f, AIFilter g)
		{
			a = f;
			b = g;
		}
		
		public boolean test(AIShip s)
		{
			return a.test(s) || b.test(s);
		}
	}
	
	public static AIFilter joinAnd(AIFilter f, AIFilter g)
	{
		return new JoinByANDFilter(f, g);
	}
	
	public static AIFilter joinOr(AIFilter f, AIFilter g)
	{
		return new JoinByORFilter(f, g);
	}
	
	
	public abstract boolean test(AIShip s);
}
