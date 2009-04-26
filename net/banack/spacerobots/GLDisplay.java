package net.banack.spacerobots;

import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.ServerShip;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.ServerFleet;
import net.banack.spacerobots.ServerTeam;
import net.banack.geometry.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;

import javax.media.opengl.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.j2d.*;

public class GLDisplay implements GLEventListener, Display
{
    private static final float SHIP_TRANSPARENCY = .5f;
    private static final int MAX_QUEUED_TICKS = 4000;
    private static final int FRAMES_PER_SECOND = 60;

    private static final int SENSOR_ARC_POINTS = 5;

    private static final int OBJECT_SENSOR_ARCS = 1;
    private static final int OBJECT_SHIPS = 2;
	
    private boolean simulationFinished = false;
    private JFrame frame;
    private GLCanvas canvas;
    private TextRenderer plainRenderer;
    private TextRenderer boldRenderer;
    private DisplayFrame lastFrame;
    private double battleWidth;
    private double battleHeight;

    private AbstractQueue<DisplayFrame> frameQueue;
	
    private class DisplayFrame
    {
	DisplayTeam teams[];
	DisplayFleet fleets[];
		
	public int tick;
		
	public DisplayFrame(DisplayTeam teams[],
			    DisplayFleet fleets[],
			    int tick)
	{
	    this.teams = teams;
	    this.fleets = fleets;
	    this.tick = tick;
	}
    }
	
    private class DisplayTeam
    {
	int index;
	int numFleets;
	
	String name;
    }
	
    private class DisplayFleet
    {
	int index;
	DisplayTeam team;
	int indexInTeam;
	
	int credits;
	int numShips;
	String name;
	String aiName;
	String aiAuthor;
	String aiVersion;
	float red;
	float green;
	float blue;

 	public ArrayList<DisplayShip> ships;
    }
	
    private class DisplayShip
    {
	int indexInFleet;
	DisplayFleet fleet;
	
	DQuad location;
	DArc scanner;
    }
	
    private FPSAnimator animator;
	
    private static class ClosingThread extends Thread
    {
	private FPSAnimator myAnimator;
		
	public ClosingThread(FPSAnimator a)
	{
	    myAnimator=a;
	}
		
	public void run()
	{
	    myAnimator.stop();
	    Debug.info("Exiting from GLDisplay");
	    System.exit(0);
	} 	
    }
	
    public GLDisplay()
    {
	frameQueue = new ConcurrentLinkedQueue<DisplayFrame>();
    }
	
    private void createFrame(int width, int height)
    {
	frame = new JFrame("Space Robots");
	canvas = new GLCanvas();
		
	canvas.addGLEventListener(this);
	frame.add(canvas);
	animator = new FPSAnimator(canvas, FRAMES_PER_SECOND);
		
	canvas.setSize(width, height);
	frame.pack();
		
	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    closeAndExit();
		}
	    });
	
	
	frame.setVisible(true);
	animator.start();
    }
	
    public void closeAndExit()
    {
	// Run this on another thread than the AWT event
	// queue to make sure the call to Animator.stop()
	// completes before exiting
	(new ClosingThread(animator)).start();
    }
	
    public void updateDisplay(Battle b)
    {
	if (frame == null) {
	    battleWidth = b.getWidth();
	    battleHeight = b.getHeight();
	    //createFrame((int) b.getWidth(), (int) b.getHeight());
	    createFrame(700, 700);
	}
		
	if (b.isOver()) {
	    simulationFinished = true;
	}

	DisplayTeam teams[] = new DisplayTeam[b.getNumTeams()];
	DisplayFleet fleets[] = new DisplayFleet[b.getNumFleets()];
		
	Iterator<ServerTeam> teamIter = b.teamIterator();
	while(teamIter.hasNext()) {
	    ServerTeam team = teamIter.next();
	    DisplayTeam disTeam = new DisplayTeam();

	    disTeam.index = team.getTeamIndex();
	    disTeam.name = team.getName();
	    disTeam.numFleets = 0;
	    teams[disTeam.index] = disTeam;
	}
		
	Iterator<ServerFleet> fleetIter = b.fleetIterator();
	while(fleetIter.hasNext()) {
	    ServerFleet fleet = fleetIter.next();
	    DisplayFleet disFleet = new DisplayFleet();
		
	    disFleet.index = fleet.getFleetIndex();
	    disFleet.credits = fleet.getCredits();
	    disFleet.name = fleet.getName();
	    disFleet.aiName = fleet.getAIName();
	    disFleet.aiAuthor = fleet.getAIAuthor();
	    disFleet.aiVersion = fleet.getAIVersion();
	    disFleet.red = 0f;
	    disFleet.green = 0f;
	    disFleet.blue = 0f;
	    switch (disFleet.index) {
	    case 0:
		disFleet.red = 1f;
		break;
	    case 1:
		disFleet.blue = 1f;
		break;
	    case 2:
		disFleet.green = .8f;
		break;
	    case 3:
		disFleet.green = .8f;
		disFleet.blue = .8f;
		break;
	    default:
		Debug.info("GLDisplay: To many fleets, using default" +
			   " fleet color");
		disFleet.red = 1f;
		disFleet.green = 1f;
		disFleet.blue = 1f;
	    }
	    disFleet.team = teams[fleet.getTeam().getTeamIndex()];
	    disFleet.indexInTeam = disFleet.team.numFleets;
	    disFleet.team.numFleets++;
	    disFleet.ships = new ArrayList<DisplayShip>();

	    fleets[fleet.getIndex()] = disFleet;
	}

	Iterator<ServerShip> iter = b.shipIterator();
	while(iter.hasNext()) {
	    ServerShip ship = iter.next();
	    DisplayShip disShip = new DisplayShip();

		
	    disShip.location = ship.getLocation();
	    disShip.scanner = ship.getScannerArc();
	    disShip.fleet = fleets[ship.getFleet().getFleetIndex()];

	    fleets[ship.getFleet().getFleetIndex()].ships.add(disShip);
	}

	while (frameQueue.size() > MAX_QUEUED_TICKS || (Debug.isSlowGraphics() && frameQueue.size()>0)) {
	    try {
		Thread.sleep(1000/FRAMES_PER_SECOND+1);
	    }
	    catch (InterruptedException e) { 
	    }    
	}
		
	frameQueue.add(new DisplayFrame(teams, fleets, b.getTick()));
    }
	
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			       boolean deviceChanged) {}
	
    public void reshape(GLAutoDrawable drawable, int x, int y,
			int width, int height)
    {
	GL gl = drawable.getGL();
		
	gl.glMatrixMode(GL.GL_PROJECTION);
		
	Debug.info("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
	Debug.info("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
	Debug.info("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
		
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();
		
	gl.glMatrixMode(GL.GL_MODELVIEW);
	gl.glLoadIdentity();
		
	gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
	gl.glOrtho(0, battleWidth, 0, battleHeight, 1, -1);
		
	gl.glEnable(GL.GL_BLEND);
	gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }
	
    public void display(GLAutoDrawable drawable)
    {
 	DisplayFrame displayFrame;
 	GL gl = drawable.getGL();
		
 	if (simulationFinished && frameQueue.isEmpty()) {
 	    try {
 		Debug.info("Pausing for 3 seconds...");
 		Thread.sleep(3000);
 	    }
 	    catch (InterruptedException e)
 		{
				
 		}
 	    closeAndExit();
 	}
		
 	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
 	if (frameQueue.isEmpty()) {
 	    displayFrame = lastFrame;
 	} else {
 	    displayFrame = frameQueue.poll();
 	}
 	lastFrame = displayFrame;
		
 	if (displayFrame == null) {
 	    return;
 	}

	for (int i = 0; i < displayFrame.fleets.length; i++) {
	    renderFleetObjects(drawable, displayFrame.fleets[i], OBJECT_SENSOR_ARCS);
	}
	for (int i = 0; i < displayFrame.fleets.length; i++) {
	    renderFleetObjects(drawable, displayFrame.fleets[i], OBJECT_SHIPS);
	}
		
 	renderTeamText(drawable, displayFrame);
 	renderFrameStatsText(drawable, displayFrame);
    }

    private void renderFleetObjects(GLAutoDrawable drawable,
				    DisplayFleet fleet,
				    int type) {
	for (int i = 0; i < fleet.ships.size(); i++) {
	    DisplayShip ship = fleet.ships.get(i);
	    double xOffset;
	    double yOffset;
	    if (ship.location.getP1().getX() > battleWidth / 2) {
		xOffset = -battleWidth;
	    } else {
		xOffset = battleWidth;
	    }

	    if (ship.location.getP1().getY() > battleHeight / 2) {
		yOffset = -battleHeight;
	    } else {
		yOffset = battleHeight;
	    }

	    switch (type) {
	    case OBJECT_SENSOR_ARCS:
		renderSensorArc(drawable, ship, 0, 0);
		break;
	    case OBJECT_SHIPS:
		renderShip(drawable, ship, 0, 0);
		renderShip(drawable, ship, 0, yOffset);
		renderShip(drawable, ship, xOffset, yOffset);
		renderShip(drawable, ship, xOffset, 0);
		break;
	    }
	}
    }

    private void renderTeamText(GLAutoDrawable drawable,
				DisplayFrame displayFrame) {
	GL gl = drawable.getGL();

	/*
	 * Team Names
	 */
	boldRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
	boldRenderer.setColor(1.0f, 0.0f, 0.0f, 0.8f);
	for (int i = 0; i < displayFrame.teams.length; i++) {
	    boldRenderer.draw("" + displayFrame.teams[i].name,
			      (drawable.getWidth() / displayFrame.teams.length) * i,
			      drawable.getHeight() - 12);
	}
	boldRenderer.endRendering();

	/*
	 * Fleet Names
	 */
	plainRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
	for (int i = 0; i < displayFrame.fleets.length; i++) {
	    plainRenderer.draw("   " + displayFrame.fleets[i].name,
			       displayFrame.fleets[i].team.index * (drawable.getWidth() / displayFrame.teams.length), drawable.getHeight() - 12 * (5*displayFrame.fleets[i].indexInTeam + 2));

	    plainRenderer.draw("   " + displayFrame.fleets[i].aiName +
			       " (" + displayFrame.fleets[i].aiVersion +
			       ")",
			       displayFrame.fleets[i].team.index * (drawable.getWidth() / displayFrame.teams.length), drawable.getHeight() - 12 * (5*displayFrame.fleets[i].indexInTeam + 3));

	    plainRenderer.draw("   " + displayFrame.fleets[i].aiAuthor,
			       displayFrame.fleets[i].team.index * (drawable.getWidth() / displayFrame.teams.length), drawable.getHeight() - 12 * (5*displayFrame.fleets[i].indexInTeam + 4));
	    plainRenderer.draw("       Credits: " +
			       displayFrame.fleets[i].credits,
			       displayFrame.fleets[i].team.index * (drawable.getWidth() / displayFrame.teams.length), drawable.getHeight() - 12 * (5*displayFrame.fleets[i].indexInTeam + 5));
	    plainRenderer.draw("       Ships: " +
			       displayFrame.fleets[i].ships.size(),
			       displayFrame.fleets[i].team.index * (drawable.getWidth() / displayFrame.teams.length), drawable.getHeight() - 12 * (5*displayFrame.fleets[i].indexInTeam + 6));
	}
	plainRenderer.endRendering();

	/*
	 * Fleet Color Squares
	 */
	gl.glPushMatrix();
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();
	gl.glMatrixMode(GL.GL_MODELVIEW);
	gl.glLoadIdentity();
	gl.glOrtho(0, drawable.getWidth(), drawable.getHeight(), 0, 1, -1);
		
	gl.glBegin(GL.GL_QUADS);
	for (int i = 0; i < displayFrame.fleets.length; i++) {
	    gl.glColor4f(displayFrame.fleets[i].red, displayFrame.fleets[i].green, displayFrame.fleets[i].blue, 0.8f);
	    float x = displayFrame.fleets[i].team.index * (drawable.getWidth() / displayFrame.teams.length);
	    float y = 12 + 24 * displayFrame.fleets[i].indexInTeam;

	    gl.glVertex2f(3 + x, 3 + y);
	    gl.glVertex2f(3 + x, 9 + y);
	    gl.glVertex2f(9 + x, 9 + y);
	    gl.glVertex2f(9 + x, 3 + y);
	}
	gl.glEnd();
		
	gl.glPopMatrix();


    }

    private void renderFrameStatsText(GLAutoDrawable drawable,
				      DisplayFrame displayFrame) {
	plainRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
	plainRenderer.setColor(0.0f, 0.8f, 0.0f, 0.8f);
	plainRenderer.draw("Tick: " + displayFrame.tick,
			   0, 24);
	plainRenderer.draw("Ticks in Queue: " + frameQueue.size(),
			   0, 12);
	//	plainRenderer.draw("Ship Count: " + displayFrame.ships.size(),
	//			   0, 0);
	plainRenderer.endRendering();
    }

    private void renderSensorArc(GLAutoDrawable drawable,
			    DisplayShip ship,
			    double xOffset,
			    double yOffset) {
	GL gl = drawable.getGL();

	gl.glBegin(GL.GL_TRIANGLE_FAN);
	gl.glColor4f(ship.fleet.red/5, ship.fleet.green/5, ship.fleet.blue/5, 1f);
	gl.glVertex2f((float) (ship.scanner.getCenter().getX() + xOffset),
		      (float) (ship.scanner.getCenter().getY() + yOffset));
	gl.glVertex2f((float) (ship.scanner.getCenter().getX() +
			       Math.cos(ship.scanner.getAngleStart()) *
			       ship.scanner.getRadius()
			       + xOffset),
		      (float) (ship.scanner.getCenter().getY() +
			       Math.sin(ship.scanner.getAngleStart()) *
			       ship.scanner.getRadius()
			       + yOffset));
	for (int i = 0; i <= SENSOR_ARC_POINTS; i++) {
	    float angle = ((SENSOR_ARC_POINTS - i) *
			   (float) ship.scanner.getAngleStart() +
			    i * (float) ship.scanner.getAngleEnd()) /
		SENSOR_ARC_POINTS;
	    gl.glVertex2f((float) (ship.scanner.getCenter().getX() +
				   Math.cos(angle) *
				   ship.scanner.getRadius()
				   + xOffset),
			  (float) (ship.scanner.getCenter().getY() +
				   Math.sin(angle) *
				   ship.scanner.getRadius()
				   + yOffset));
	}
	gl.glEnd();
    }

    private void renderShip(GLAutoDrawable drawable,
			    DisplayShip ship,
			    double xOffset,
			    double yOffset) {
	GL gl = drawable.getGL();

	gl.glBegin(GL.GL_QUADS);
	gl.glColor4f(ship.fleet.red, ship.fleet.green, ship.fleet.blue, SHIP_TRANSPARENCY);
	gl.glVertex2f((float) (ship.location.getP1().getX() + xOffset), 
		      (float) (ship.location.getP1().getY() + yOffset));
	gl.glVertex2f((float) (ship.location.getP2().getX() + xOffset), 
		      (float) (ship.location.getP2().getY() + yOffset));
	gl.glVertex2f((float) (ship.location.getP3().getX() + xOffset), 
		      (float) (ship.location.getP3().getY() + yOffset));
	gl.glVertex2f((float) (ship.location.getP4().getX() + xOffset), 
		      (float) (ship.location.getP4().getY() + yOffset));	
	gl.glEnd();


    }
	
    public void init(GLAutoDrawable drawable)
    {
	GL gl = drawable.getGL();
		
	Debug.info("INIT GL IS: " + gl.getClass().getName());
		
	Debug.info("Chosen GLCapabilities: " +
		   drawable.getChosenGLCapabilities());
		
	plainRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
	boldRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));
		
	gl.setSwapInterval(1);
    }
}
