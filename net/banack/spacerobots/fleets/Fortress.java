/*
 * Copyright (c)2009 Mark Sheldon
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
import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.Ship;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;

/**
 * Sample fleet.
 * @author Mark Sheldon
 *
 */
public class Fortress extends AIFleet
{
    private DPoint center;
    private Double radius;
    private DPoint destination;
    private double scannerHeading = 0;
    private int timeOutTick;

    private int shipCreateCount = 0;

    private Contact target;

    public Fortress() {
		super();
    }
	
    public Fortress(long seed) {
		super();
    }
	
    public String getAuthor() {
	return "Mark Sheldon";
    }
	
    public String getVersion() {
	return "1.0";
    }
	
    public String getName() {
	return "Fortress";
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
	timeOutTick = 300;
    }

    public Iterator<ShipAction> runTick() {
	Iterator<AIShip> i;

 	if (myContacts.size() > 0) {
 	    Iterator<Integer> ei = myContacts.enemyIterator();
	    Contact enemy = myContacts.get(ei.next());

	    if (!isAmmo(enemy)) {
		target = enemy;
	    }
 	} else {
 	    target = null;
 	}
	
	if (shipCreateCount % 3 == 2) {
	    if(myCruiser.canLaunch(CRUISER) &&
	       credits >= CRUISER.getCost() + myShips.size() * 3 * MISSILE.getCost()) {
		myCruiser.launch(CRUISER);
		shipCreateCount++;
	    }
	} else {
	    if(myCruiser.canLaunch(DESTROYER) &&
	       credits >= DESTROYER.getCost() + myShips.size() * 3 * MISSILE.getCost()) {
		myCruiser.launch(DESTROYER);
		shipCreateCount++;
	    }
	}

	boolean launchedMissleThisTick = false;

	i = myShips.getAliveIterator();
	int shipCount = 0;
	while(i.hasNext()) {
	    AIShip cur = (AIShip) i.next();

	    if (!myCruiser.isAlive() && cur.getTypeID() == CRUISER_ID) {
		myCruiser = cur;
	    }
	    
	    if(cur.canMoveScanner()) {
		if (target != null) {
		    cur.setScannerHeading(target);
		} else {
		    cur.advanceScannerHeading();
		}
	    }
			
	    shipCount++;
	    if (target != null) {
		double distance = distance(cur.getPosition(), target.getPosition());
		if (distance < 60 && cur.canLaunch(MISSILE) &&
		    !launchedMissleThisTick) {
		    cur.launch(MISSILE);
		    launchedMissleThisTick = true;
		}
	    }

	    if (myCruiser.isAlive()) {
		cur.setHeading(shortestAngleToPoint(cur.getPosition(),
						    myCruiser.getPosition().add(
										DPoint.newPolar(CRUISER.getScannerRadius()/3,shipCount * Math.PI * 2 / 5))));
	    } else {
		cur.setHeading(shortestAngleToPoint(cur.getPosition(),
						    destination));
	    }
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


 	myCruiser.setHeading(shortestAngleToPoint(myCruiser.getPosition(),
						  destination));

 	if (((Math.abs(myCruiser.getPosition().getX() - destination.getX()) < 10) &&
 	     (Math.abs(myCruiser.getPosition().getY() - destination.getY()) < 10))  || tick > timeOutTick) {

 	    destination = new DPoint(random.nextDouble() * battleHeight,
 				     random.nextDouble() * battleHeight);
 	    timeOutTick = tick + 300;
 	}

	return myShips.getActionIterator();
    }
	

    private double distance(DPoint p1,
			    DPoint p2) {
	return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
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