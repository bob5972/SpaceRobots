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
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;


public class Mob extends AIFleet
{
    private DPoint center;
    private Double radius;
    private DPoint destination;
    private double scannerHeading = 0;
    private int timeOutTick;

    private Contact target;

    public Mob() {
		super();
    }
	
    public Mob(long seed) {
		super();
    }
	
    public String getAuthor() {
	return "Mark Sheldon";
    }
	
    public String getVersion() {
	return "1.1";
    }
	
    public String getName() {
	return "Mob";
    }

    public void initBattle(int fleetID,
			   int teamID,
			   int startingCredits,
			   AIShipList s,
			   Team[] teams,
			   Fleet[] f,
			   double w,
			   double h) {
	super.initBattle(fleetID, teamID, startingCredits, s, teams, f, w, h);

	center = myCruiser.getPosition();
	destination = new DPoint(random.nextDouble() * 300,
				 random.nextDouble() * 300);
	timeOutTick = 500;
    }

    public Iterator<ShipAction> runTick() {
	double fighterAngleStep = Math.PI * 2 * 2 / myShips.size();

	if (myContacts.size() > 0) {
	    Iterator<Integer> ei = myContacts.enemyIterator();
	    target = myContacts.getContact(ei.next());
	    while(ei.hasNext() && (target == null || isAmmo(target.getTypeID()))) {
		target = myContacts.getContact(ei.next());
	    }
	} else {
	    target = null;
	}
		
	Iterator<AIShip> iter = myShips.iterator();;

	myCruiser.setHeading(shortestAngleToPoint(myCruiser.getPosition(),
						destination));
	center = myCruiser.getPosition();

	if (((Math.abs(myCruiser.getPosition().getX() - destination.getX()) < 10) &&
	     (Math.abs(myCruiser.getPosition().getY() - destination.getY()) < 10))  || tick > timeOutTick) {
	    
	    destination = new DPoint(random.nextDouble() * battleHeight,
				     random.nextDouble() * battleHeight);
	    timeOutTick = tick + 1000;
	}

	int curShipIndex = 0;
	while(iter.hasNext()) {
	    AIShip cur = iter.next();

	    if (curShipIndex < myShips.size() / 2) {
		radius = (double) curShipIndex;
	    } else {
		radius = (double) curShipIndex * 3;
	    }
	    
	    if(!cur.isAlive()) {
		continue;
	    }

	    if (cur.getTypeID() == FIGHTER_ID ||
		cur.getTypeID() == DESTROYER_ID) {
		if (myContacts.size() == 0) {
		    DPoint heading;
		    heading = center.add(DPoint.newPolar
					 (radius, curShipIndex * fighterAngleStep));
		    cur.setHeading(shortestAngleToPoint(cur.getPosition(),
						heading));
		} else {
		    double distance = distance(cur.getPosition(), target.getPosition());

		    cur.setHeading(shortestAngleToPoint(cur.getPosition(),
							target.getPosition()));

		    if ((distance < 20) && cur.isReadyToLaunch() &&
			credits > DefaultShipTypeDefinitions.ROCKET.getCost()) {
			credits -= DefaultShipTypeDefinitions.ROCKET.getCost();
			cur.setLaunchWhat(DefaultShipTypeDefinitions.ROCKET_ID);
		    }
		}
	    }

	    curShipIndex = (curShipIndex + 1) % myShips.size();
	}

	myShips.apply(AIFilter.MISSILES, new AIGovernor() {
		public void run(AIShip s)
		{
		    if(target != null) {
			s.setHeading(shortestAngleToPoint(s.getPosition(),
							  target.getPosition()));
		    }
		}
	    });
	
	if (target == null) {
	    myCruiser.setScannerHeading(myCruiser.getScannerHeading()+CRUISER.getScannerAngleSpan());

	    if (myCruiser.readyToLaunch()) {
		int amountToSave;
		if (myShips.size() < 5) {
		    amountToSave = 0;
		} else {
		    amountToSave = 150;
		}	    

		if (credits > DefaultShipTypeDefinitions.FIGHTER.getCost() + amountToSave) {
		    credits -= DefaultShipTypeDefinitions.FIGHTER.getCost();
		    myCruiser.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
		}
	    }
	} else {
	    //You weren't using this anymore :-) --bob5972
		//double distance = distance(myCruiser.getPosition(), target.getPosition());
	    myCruiser.setScannerHeading(shortestAngleToPoint(myCruiser.getPosition(),
							     target.getPosition()));

	    if (myCruiser.readyToLaunch()) {
		if (credits > DefaultShipTypeDefinitions.MISSILE.getCost()) {
		    credits -= DefaultShipTypeDefinitions.MISSILE.getCost();
		    myCruiser.setLaunchWhat(DefaultShipTypeDefinitions.MISSILE_ID);
		}
	    }
	}

	return myShips.getActionIterator();
    }
	

    private double distance(DPoint p1,
			    DPoint p2) {
	return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) -
			 (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
    }
    

    private double shortestAngleToPoint(DPoint from,
					DPoint to) {
	double x1 = from.getX();
	double y1 = from.getY();
	double x2 = to.getX();
	double y2 = to.getY();

	if ((x1 < x2) && (x2 - x1 > battleWidth / 2)) {
	    x1 = x1 + battleWidth;
	}
	if ((x2 < x1) && (x1 - x2 > battleWidth / 2)) {
	    x2 = x2 + battleWidth;
	}

	if ((y1 < y2) && (y2 - y1 > battleHeight / 2)) {
	    y1 = y1 + battleHeight;
	}
	if ((y2 < y1) && (y1 - y2 > battleHeight / 2)) {
	    y2 = y2 + battleHeight;
	}

	return SpaceMath.getAngle(new DPoint(x1, y1), new DPoint(x2, y2));
    }
}