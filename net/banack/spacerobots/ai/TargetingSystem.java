package net.banack.spacerobots.ai;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.banack.geometry.DPoint;
import net.banack.spacerobots.Debug;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.ShipStatus;
import net.banack.spacerobots.util.ShipType;
import net.banack.util.FilteredIterator;
import net.banack.util.IntMap;
import net.banack.util.MethodNotImplementedException;
import net.banack.util.Queue;
import net.banack.util.SkipList;
import net.banack.util.Filter;
import net.banack.spacerobots.util.SpaceMath;

public class TargetingSystem
{
	public static final DefaultShipTypeDefinitions TYPE = new DefaultShipTypeDefinitions();
	
	public static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
	public static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
	public static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
	public static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;
	public static final int MISSILE_ID = DefaultShipTypeDefinitions.MISSILE_ID;
	
	public static final ShipType CRUISER = TYPE.CRUISER;
	public static final ShipType DESTROYER = TYPE.DESTROYER;
	public static final ShipType FIGHTER = TYPE.FIGHTER;
	public static final ShipType ROCKET = TYPE.ROCKET;
	public static final ShipType MISSILE = TYPE.MISSILE;
	
	private ContactList myContactList;
	private Set<Integer> myActiveContacts;
	private Queue<Set<Integer>> expiryCache;
	private SkipList<Contact> byPos;
	private int discardTime;
	private IntMap allocations;
	private AIFleet myFleet;
	private Comparator<Contact> defaultMissilePriority;
	
	private static final Filter<Contact> targetFilter = new TargetFilter();
	
	public TargetingSystem(AIFleet fleet)
	{
		myFleet = fleet;
		discardTime = 10;
		myActiveContacts = new HashSet<Integer>();
		expiryCache = new Queue<Set<Integer>>();
		allocations = new IntMap();
		allocations.setUseElementDefaults(true);
		
		HashSet<Integer> empty = new HashSet<Integer>();		
		for(int x=0;x<discardTime+1;x++)
		{
			expiryCache.add(empty);
		}
	}
	
	public void clearAllocations()
	{
		allocations.clear();
	}
	
	public void setDefaultMissilePriority(Comparator<Contact> c)
	{
		defaultMissilePriority = c;
	}
	
	public int getDiscardTime()
	{
		return discardTime;
	}
	
	public void setDiscardTime(int t)
	{
		discardTime = t;
		throw new MethodNotImplementedException("You totally gotta redo the expiryCache if you wanna do that man!");		
	}
	
	public void update()
	{
		myContactList = myFleet.myContacts;
		byPos = null;
		
		myActiveContacts.addAll(myContactList.getIDSet());
		
		Set<Integer> toDie =  new HashSet<Integer>();
		Iterator<Integer> i = myContactList.enemyIterator();
		while(i.hasNext())
		{
			Integer eid = i.next();
			toDie.add(eid);
		}
		
		expiryCache.enqueue(toDie);
		
		toDie = expiryCache.dequeue();
		i = toDie.iterator();
		
		while(i.hasNext())
		{
			Integer eid = i.next();
			if(get(eid).getScanTick() < myFleet.tick - discardTime)
			{
				myActiveContacts.remove(eid);
			}
		}
	}
	
	//Don't use byPos w/o it!
	private void initialize()
	{
		if(byPos == null)
		{
			byPos = new SkipList<Contact>(new Comparator<Contact>() {
				public int compare(Contact left, Contact right)
				{
					if(left.getX() < right.getX())
						return -1;
					if(left.getX() > right.getX())
						return 1;
					if(left.getID() < right.getID())
						return -1;
					if(left.getID() > right.getID())
						return 1;
					
					return 0;
				}
				
			});
		
			Iterator<Integer> i = myActiveContacts.iterator();
			while(i.hasNext())
			{
				byPos.add(get(i.next()));
			}
		}
	}
	
	//Iterator over Integers of active enemyID's
	public Iterator<Integer> iterator()
	{
		return myActiveContacts.iterator();
	}
	
	public final Contact get(int enemyID)
	{
		return getContact(enemyID);
	}
	
	public final Contact get(Integer enemyID)
	{
		return getContact(enemyID);
	}
	
	public final Contact getContact(int enemyID)
	{
		return getContact(new Integer(enemyID));
	}
	
	public Contact getContact(Integer enemyID)
	{
		return myContactList.getOld(enemyID);
	}
	
	//number of enemies listed
	public int size()
	{
		return myActiveContacts.size();
	}
	
	public final boolean containsEnemy(int eID)
	{
		return containsEnemy(new Integer(eID));
	}
	
	public boolean containsEnemy(Integer eID)
	{
		return myActiveContacts.contains(eID);
	}
	
	public int allocate(int eID)
	{
		return allocations.increment(eID);
	}
	
	public int allocate(ShipStatus c)
	{
		if(c == null)
			return 0;
		return allocate(c.getID());
	}
	
	public int deallocate(int eID)
	{
		return allocations.decrement(eID);
	}
	
	public int deallocate(ShipStatus c)
	{
		if(c == null)
			return  0;
		return deallocate(c.getID());
	}
	
	public int getAllocationCount(int eID)
	{
		return allocations.get(eID);
	}
	
	public int getAllocationCount(ShipStatus s)
	{
		return allocations.get(s.getID());
	}
	
	//Please don't modify this!  (Actually, I'm not sure if you can...)
	private Collection<Contact> getContactsInRange(double left, double right)
	{
		initialize();
		DPoint bl = new DPoint(left,0);
		DPoint tr = new DPoint(right,0);
		
		Contact leftC = new Contact(-1,-1,-1,bl,-1,-1);
		Contact rightC = new Contact(-1,-1,-1,tr,-1,-1);
		
		if(leftC.getX() > rightC.getX())
		{
			Contact temp = leftC;
			leftC = rightC;
			rightC = temp;
		}
		
		return byPos.subCollection(leftC,rightC);
	}
	
	private Iterator<Contact> getContactRangeIterator(DPoint bl, DPoint tr)
	{
		Collection<Contact> list = getContactsInRange(bl.getX(),tr.getX());
		Iterator<Contact> i = list.iterator();
		return new FilteredIterator<Contact>(i,new RangeFilter(bl,tr));
	}
	
	public Contact getContactInRange(DPoint bl, DPoint tr)
	{
		Iterator<Contact> i = getContactRangeIterator(bl,tr);
		
		if(!i.hasNext())
			return null;
		
		return i.next();
	}
	
	public Contact getTargetInRange(DPoint bl, DPoint tr)
	{
		return getContactInRange(bl,tr,targetFilter);
	}
	
	public Contact getTargetInRange(DPoint bl, DPoint tr, Filter<Contact> f)
	{
		return getContactInRange(bl,tr,ContactFilter.joinAnd(targetFilter,f));
	}
	
	public Contact getTargetInRange(DPoint bl, DPoint tr, Filter<Contact> f, Comparator<Contact> p)
	{
		return getContactInRange(bl,tr,ContactFilter.joinAnd(targetFilter,f),p);
	}
	
	public Contact getContactInRange(DPoint bl, DPoint tr, Filter<Contact> f, Comparator<Contact> p)
	{
		Iterator<Contact> i = new FilteredIterator<Contact>(getContactRangeIterator(bl,tr),f);
		if(!i.hasNext())
			return null;
		
		Contact max = i.next();
		while(i.hasNext())
		{
			Contact next = i.next();
			if(p.compare(max,next) < 0)
				max = next;
		}
		
		return max;
	}
	
	public Contact getContactInRange(DPoint bl, DPoint tr, Filter<Contact> f)
	{
		Iterator<Contact> i = new FilteredIterator<Contact>(getContactRangeIterator(bl,tr),f);
		if(!i.hasNext())
			return null;
		return i.next();
	}
	
	//the range makes this WAY faster
	// if you really wanted the closest period, you could write one that iterates ranges
	// but I'm too lazy right now.
	//Actually: better idea, locate the given xCoor in the list, and then iterate out (alternating)
	//  but you'd have to keep going until the x_distance exceeded the current distance
	public Contact getClosestTarget(DPoint bl, DPoint tr, DPoint center)
	{
		return getContactInRange(bl,tr,targetFilter, new DistancePriority(center,myFleet.battleWidth,myFleet.battleHeight));
	}
	
	public Contact getClosestTarget(AIShip s, double radius)
	{		
		DPoint bl = s.getPosition().subtract(radius);
		DPoint tr = s.getPosition().add(radius);
		
		return getContactInRange(bl,tr,targetFilter, new DistancePriority(s.getPosition(),myFleet.battleWidth,myFleet.battleHeight));
	}
	
	public Contact getContactInRange(DPoint bl, DPoint tr, Comparator<Contact> p)
	{	
		Iterator<Contact> i = getContactRangeIterator(bl,tr);
		
		if(!i.hasNext())
			return null;
		
		Contact max = i.next();
		while(i.hasNext())
		{
			Contact next = i.next();
			if(p.compare(max,next) < 0)
				max = next;
		}
		
		return max;
	}
	
	//gets a target within missile range (accounting for number of ticks remaining if this is a missile)
	//not entirely accurate (doens't account for turning radius), but its not so bad
	//IDEALLY:
	//	this should account for turning radii
	//  not use a bounding square
	//  account for firing delay on the part of the firing ship
	//  account for time to intercept of the said missile
	//BUT: I iz lazy.
	public Contact getMissileTarget(AIShip s)
	{		
		return getMissileTarget(s,ContactFilter.ALL,defaultMissilePriority);
	}
	
	//see restrictions above
	public Contact getClosestMissileTarget(AIShip s)
	{		
		return getMissileTarget(s,ContactFilter.ALL,new DistancePriority(s.getPosition(),myFleet.battleWidth,myFleet.battleHeight));
	}
	
	//see restrictions above
	public Contact getMissileTarget(AIShip s, Filter<Contact> f)
	{		
		int missileTicks= MISSILE.getMaxTickCount();
		if(s.getTypeID()==MISSILE_ID)
			missileTicks -= (myFleet.tick - s.getCreationTick());
		
		double radius = MISSILE.getMaxSpeed()*missileTicks;
		
		DPoint bl = s.getPosition().subtract(radius);
		DPoint tr = s.getPosition().add(radius);
		
		return getTargetInRange(bl,tr,f,defaultMissilePriority);		
	}
	
	//see restrictions above
	public Contact getMissileTarget(AIShip s, Filter<Contact> f,Comparator<Contact> p)
	{		
		int missileTicks= MISSILE.getMaxTickCount();
		if(s.getTypeID()==MISSILE_ID)
			missileTicks -= (myFleet.tick - s.getCreationTick());
		
		double radius = MISSILE.getMaxSpeed()*missileTicks;
		
		DPoint bl = s.getPosition().subtract(radius);
		DPoint tr = s.getPosition().add(radius);
		
		return getTargetInRange(bl,tr,f,p);		
	}
}
