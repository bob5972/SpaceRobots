package net.banack.spacerobots.ai;

import net.banack.util.ListNode;

public class CompositeGovernor implements AIGovernor
{
	private ListNode<AIGovernor> myGovernors;
	private ListNode<AIFilter> myFilters;
	
	private ListNode<AIGovernor> gBottom;
	private ListNode<AIFilter> fBottom;
	
	public CompositeGovernor()
	{
		gBottom = myGovernors=null;
		fBottom = myFilters  =null;
	}
	
	public void insert(AIGovernor g)
	{
		insert(null,g);
	}
	
	public void insert(AIFilter f)
	{
		insert(f,null);
	}
	
	public void insert(AIFilter f, AIGovernor g)
	{
		myGovernors = new ListNode<AIGovernor>(g,myGovernors);
		myFilters = new ListNode<AIFilter>(f,myFilters);
		
		if(gBottom == null)
			gBottom = myGovernors;
		if(fBottom == null)
			fBottom = myFilters;
	}
	
	public void append(AIGovernor g)
	{
		append(null,g);
	}
	
	public void append(AIFilter f)
	{
		append(f,null);
	}
	
	public void append(AIFilter f, AIGovernor g)
	{
		ListNode<AIFilter> newF = new ListNode<AIFilter>(f);
		ListNode<AIGovernor> newG = new ListNode<AIGovernor>(g);
		
		if(gBottom != null) 
			gBottom.setNext(newG);
		else
			myGovernors = newG;
		
		if(fBottom != null)
			fBottom.setNext(newF);
		else
			myFilters = newF;
		
		gBottom = newG;
		fBottom = newF;
	}
	
	public void run(AIShip s)
	{
		ListNode<AIGovernor> gp = myGovernors;
		ListNode<AIFilter> fp = myFilters;
		while(gp != null)
		{
			AIFilter f = fp.getValue();
			AIGovernor g = gp.getValue();
			
			if(g == null && f != null && !f.test(s))
				return;
			
			if(f == null || f.test(s))
				g.run(s);
			
			gp = gp.next();
			fp = fp.next();
		}		
	}	
}
