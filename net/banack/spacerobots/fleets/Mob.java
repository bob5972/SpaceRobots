package net.banack.spacerobots.fleets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.banack.debug.Debug;
import net.banack.geometry.DPoint;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.AbstractFleetAI;
import net.banack.spacerobots.util.ContactList;
import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;


public class Mob extends AbstractFleetAI
{
    private AIShipList ships;
    private Random random;
    private AIShip cruiser;
    
    private DPoint center;
    private Double radius;
    private DPoint destination;
    private double scannerHeading = 0;
    private int timeOutTick;

    private double width;
    private double height;

    private static final int CRUISER_ID = DefaultShipTypeDefinitions.CRUISER_ID;
    private static final int DESTROYER_ID = DefaultShipTypeDefinitions.DESTROYER_ID;
    private static final int FIGHTER_ID = DefaultShipTypeDefinitions.FIGHTER_ID;
    private static final int ROCKET_ID = DefaultShipTypeDefinitions.ROCKET_ID;

    public Mob() {
	random = new Random();
    }
	
    public Mob(long seed) {
	random = new Random(seed);
    }
	
    public String getAuthor() {
	return "Mark Sheldon";
    }
	
    public String getVersion() {
	return "1.0";
    }
	
    public String getName() {
	return "Mob";
    }

    public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f, double w, double h) {
	super.initBattle(fleetID, teamID, startingCredits, s, teams, f, width, height);

	ships = s;
	Iterator<AIShip> iter = ships.iterator();
		
	while(iter.hasNext()) {
	    AIShip cur = iter.next();
	    if(cur.getTypeID() == CRUISER_ID) {
		cruiser=cur;
		break;
	    }
	}

	width = w;
	height = h;
	center = cruiser.getPosition();
	destination = new DPoint(random.nextDouble() * 300,
				 random.nextDouble() * 300);
	timeOutTick = 500;
    }

    public Iterator<ShipAction> runTick(int tick,
					int credits,
					ContactList c,
					AIShipList s) {

	double fighterAngleStep = Math.PI * 2 * 2 / s.size();
	
	scannerHeading += .2;
	if (scannerHeading >= Math.PI * 2) {
	    scannerHeading = 0;
	}

	ships = s;
		
	Iterator<AIShip> iter = ships.iterator();;

	cruiser.setHeading(shortestAngleToPoint(cruiser.getPosition(),
						destination));
	//	center = cruiser.getPosition().add(DPoint.newPolar(10, cruiser.getHeading()));
	center = cruiser.getPosition();

	if (((Math.abs(cruiser.getPosition().getX() - destination.getX()) < 10) &&
	     (Math.abs(cruiser.getPosition().getY() - destination.getY()) < 10))  || tick > timeOutTick) {
	    
	    destination = new DPoint(random.nextDouble() * width,
				     random.nextDouble() * height);
	    timeOutTick = tick + 1000;
	}

	int curShipIndex = 0;
	while(iter.hasNext()) {
	    AIShip cur = iter.next();

	    if (curShipIndex < ships.size() / 2) {
		radius = (double) curShipIndex / 2;
	    } else {
		radius = (double) curShipIndex;
	    }
	    
	    if(!cur.isAlive()) {
		continue;
	    }

	    if (cur.getTypeID() == FIGHTER_ID || cur.getTypeID() == DESTROYER_ID) {
		if (c.size() == 0 || curShipIndex < ships.size() / 2) {
		    DPoint heading;
		    heading = center.add(DPoint.newPolar
					 (radius, curShipIndex * fighterAngleStep));
		    cur.setHeading(shortestAngleToPoint(cur.getPosition(),
						heading));
		} else {
		    DPoint targetPos = c.getContact(c.enemyIterator().next()).getPosition();
		    double distance = distance(cur.getPosition(), targetPos);

		    cur.setHeading(shortestAngleToPoint(cur.getPosition(),
							targetPos));

		    if ((distance < 20) && cur.isReadyToLaunch() && credits > DefaultShipTypeDefinitions.ROCKET.getCost()) {
			credits -= DefaultShipTypeDefinitions.ROCKET.getCost();
			cur.setLaunchWhat(DefaultShipTypeDefinitions.ROCKET_ID);
		    }
		}
	    }

	    curShipIndex = (curShipIndex + 1) % ships.size();
	}



	cruiser.setScannerHeading(scannerHeading);

	if (cruiser.readyToLaunch()) {
	    if (credits > DefaultShipTypeDefinitions.FIGHTER.getCost()*2) {
		credits -= DefaultShipTypeDefinitions.FIGHTER.getCost();
		cruiser.setLaunchWhat(DefaultShipTypeDefinitions.FIGHTER_ID);
	    }
	}
	    return ships.getActionIterator();
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

	if ((x1 < x2) && (x2 - x1 > width / 2)) {
	    x1 = x1 + width;
	}
	if ((x2 < x1) && (x1 - x2 > width / 2)) {
	    x2 = x2 + width;
	}

	if ((y1 < y2) && (y2 - y1 > height / 2)) {
	    y1 = y1 + height;
	}
	if ((y2 < y1) && (y1 - y2 > height / 2)) {
	    y2 = y2 + height;
	}

	return SpaceMath.getAngle(new DPoint(x1, y1), new DPoint(x2, y2));
    }
}