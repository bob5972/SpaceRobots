package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.Contact;
import net.banack.util.Filter;

public abstract class ContactFilter implements Filter<Contact>
{
	public final static ContactFilter ALL = new ContactFilter(){
		public boolean test(Contact c)
		{
			return true;
		}
	};
	
	private static class JoinByANDFilter extends ContactFilter
	{
		private Filter<Contact> a,b;
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
		private Filter<Contact> a,b;
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
	
	public static ContactFilter joinAnd(Filter<Contact> f,Filter<Contact> g)
	{
		return new JoinByANDFilter(f,g);
	}
	
	public static ContactFilter joinOr(Filter<Contact> f, Filter<Contact> g)
	{
		return new JoinByORFilter(f,g);
	}
	
	
	
	public abstract boolean test(Contact s);
}
