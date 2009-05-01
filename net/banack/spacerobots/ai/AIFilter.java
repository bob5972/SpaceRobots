package net.banack.spacerobots.ai;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.util.Filter;

public abstract class AIFilter implements Filter<AIShip>
{
	public static AIFilter FIGHTERS = new AIFilter(){
		public boolean test(AIShip s)
		{ return s.getTypeID() == DefaultShipTypeDefinitions.FIGHTER_ID; }
	};
	public static AIFilter DESTROYERS = new AIFilter(){
		public boolean test(AIShip s)
		{ return s.getTypeID() == DefaultShipTypeDefinitions.DESTROYER_ID; }
	};
	public static AIFilter CRUISERS = new AIFilter(){
		public boolean test(AIShip s)
		{ return s.getTypeID() == DefaultShipTypeDefinitions.CRUISER_ID; }
	};
	public static AIFilter MISSILES = new AIFilter(){
		public boolean test(AIShip s)
		{ return s.getTypeID() == DefaultShipTypeDefinitions.MISSILE_ID; }
	};
	public static AIFilter ALL = new AIFilter(){
		public boolean test(AIShip s)
		{ return true; }
	};
	public static AIFilter NONE = new AIFilter() {
		public boolean test(AIShip s)
		{ return false; }
	};
	
	private static class JoinByANDFilter extends AIFilter
	{
		private AIFilter a,b;
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
		private AIFilter a,b;
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
	
	public static AIFilter joinAnd(AIFilter f,AIFilter g)
	{
		return new JoinByANDFilter(f,g);
	}
	
	public static AIFilter joinOr(AIFilter f, AIFilter g)
	{
		return new JoinByORFilter(f,g);
	}
	
	
	
	public abstract boolean test(AIShip s);
}
