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

import net.banack.spacerobots.util.Contact;
import net.banack.util.Filter;

/**
 * Generic interface for filtering contacts.
 * 
 * @author Michael Banack <bob5972@banack.net>
 * 
 */
public abstract class ContactFilter implements Filter<Contact>
{
	public final static ContactFilter ALL = new ContactFilter() {
		public boolean test(Contact c)
		{
			return true;
		}
	};
	
	private static class JoinByANDFilter extends ContactFilter
	{
		private Filter<Contact> a, b;
		
		public JoinByANDFilter(Filter<Contact> f, Filter<Contact> g)
		{
			a = f;
			b = g;
		}
		
		public boolean test(Contact s)
		{
			return a.test(s) && b.test(s);
		}
	}
	
	private static class JoinByORFilter extends ContactFilter
	{
		private Filter<Contact> a, b;
		
		public JoinByORFilter(Filter<Contact> f, Filter<Contact> g)
		{
			a = f;
			b = g;
		}
		
		public boolean test(Contact s)
		{
			return a.test(s) || b.test(s);
		}
	}
	
	public static ContactFilter joinAnd(Filter<Contact> f, Filter<Contact> g)
	{
		return new JoinByANDFilter(f, g);
	}
	
	public static ContactFilter joinOr(Filter<Contact> f, Filter<Contact> g)
	{
		return new JoinByORFilter(f, g);
	}
	
	
	public abstract boolean test(Contact s);
}
