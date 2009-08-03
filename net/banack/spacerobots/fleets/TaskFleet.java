package net.banack.spacerobots.fleets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import net.banack.spacerobots.ai.AIFleet;
import net.banack.spacerobots.ai.AIGovernor;
import net.banack.spacerobots.ai.AIShip;
import net.banack.spacerobots.ai.AIShipList;
import net.banack.spacerobots.ai.TargetingSystem;
import net.banack.spacerobots.ai.TaskForce;
import net.banack.spacerobots.util.Contact;
import net.banack.spacerobots.util.Fleet;
import net.banack.spacerobots.util.ShipAction;
import net.banack.spacerobots.util.ShipType;
import net.banack.spacerobots.util.SpaceMath;
import net.banack.spacerobots.util.Team;

public class TaskFleet extends AIFleet
{
	private ArrayList<AttackForce> taskList;
	private IdleForce idleTask;
	
	private class IdleForce extends TaskForce
	{
		public boolean isIdle()
		{
			return true;
		}
		
		public void run()
		{
			myShips.apply(new AIGovernor() {
				public void run(AIShip s)
				{
					if (random.nextDouble() < 0.05)
						s.intercept(myCruiser);
				}
			});
		}
		
	}
	
	private class AttackForce extends TaskForce
	{
		private Contact myTarget;
		
		public AttackForce()
		{
			
		}
		
		public void setTarget(Contact t)
		{
			myTarget = t;
		}
		
		public int getTargetScanTick()
		{
			return myTarget.getScanTick();
		}
		
		public Contact getTarget()
		{
			return myTarget;
		}
		
		public void run()
		{
			if (isIdle())
				return;
			
			myShips.apply(new AIGovernor() {
				public void run(AIShip s)
				{
					double head = s.intercept(myTarget);
					if (s.isInMissileRange(myTarget) && s.canLaunchMissile()) {
						s.fire(myTarget);
					} else if (s.isInRocketRange(myTarget) && s.canLaunchRocket()) {
						double low = head - SpaceMath.degToRad(45.0 / 2);
						double high = head + SpaceMath.degToRad(45.0 / 2);
						low = SpaceMath.wrapHeading(low);
						high = SpaceMath.wrapHeading(high);
						if (low <= s.projHeading() && s.projHeading() <= high)
							s.fire();
					}
					
				}
			});
		}
		
		public boolean isIdle()
		{
			return myTarget == null || myTarget.isDead();
		}
		
	}
	
	public TaskFleet()
	{
		super();
		
	}
	
	public TaskFleet(long seed)
	{
		super(seed);
	}
	
	
	@Override
	public String getAuthor()
	{
		return "Michael Banack";
	}
	
	@Override
	public String getVersion()
	{
		return "1.0";
	}
	
	public void initBattle(int fleetID, int teamID, int startingCredits, AIShipList s, Team[] teams, Fleet[] f,
	        double width, double height)
	{
		super.initBattle(fleetID, teamID, startingCredits, s, teams, f, width, height);
		
		taskList = new ArrayList<AttackForce>();
		idleTask = new IdleForce();
		
		for (int x = 0; x < 4; x++) {
			taskList.add(new AttackForce());
		}
	}
	
	@Override
	public Iterator<ShipAction> runTick()
	{
		// Reorganize Task Forces
		Iterator<AIShip> si = myShips.getNewIterator();
		while (si.hasNext()) {
			AIShip s = si.next();
			idleTask.add(s);
		}
		
		Iterator<AttackForce> tfi = taskList.iterator();
		Iterator<Integer> ci = myContacts.enemyShipIterator();
		while (tfi.hasNext()) {
			AttackForce t = tfi.next();
			if (t.getTarget() != null && t.getTargetScanTick() < tick - 10) {
				t.setTarget(null);
			}
			
			if (t.isIdle()) {
				if (ci.hasNext()) {
					t.setTarget(myContacts.get(ci.next()));
				}
			} else {
				if (myCruiser.isInMissileRange(t.getTarget())) {
					myCruiser.fire(t.getTarget());
				}
			}
		}
		
		rebalanceTaskForces();
		
		// Run Cruiser
		if (myCruiser.isAlive()) {
			ShipType toLaunch = FIGHTER;
			if (myCruiser.canLaunch(toLaunch) && credits >= toLaunch.getCost() + MISSILE.getCost() * 10) {
				myCruiser.launch(toLaunch);
			}
			
			if (tick % 100 == 0) {
				double h = random.nextDouble();
				myCruiser.setHeading(h);
			}
			myCruiser.advanceScannerHeading();
		}
		
		// Run Task Forces
		tfi = taskList.iterator();
		while (tfi.hasNext()) {
			AttackForce t = tfi.next();
			if (t.isIdle()) {
				idleTask.transferAll(t);
			} else {
				tfi.next().run();
			}
		}
		
		idleTask.run();
		
		return myShips.getActionIterator();
	}
	
	public void rebalanceTaskForces()
	{
		int working = 0;
		Iterator<AttackForce> it = taskList.iterator();
		
		while (it.hasNext()) {
			AttackForce t = it.next();
			if (!t.isIdle())
				working++;
		}
		
		int num = (idleTask.size()) / (working + 1);
		
		it = taskList.iterator();
		
		while (it.hasNext()) {
			AttackForce t = it.next();
			if (!t.isIdle()) {
				for (int x = 0; x < num; x++) {
					t.transferOne(idleTask);
				}
			}
		}
	}
	

}
