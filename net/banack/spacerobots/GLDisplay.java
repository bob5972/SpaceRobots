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
    private boolean simulationFinished = false;
    private JFrame frame;
    private GLCanvas canvas;
    private TextRenderer plainRenderer;
    private TextRenderer boldRenderer;
    private DisplayFrame lastFrame;
    private double battleWidth;
    private double battleHeight;

    private AbstractQueue<DisplayFrame> frameQueue;

    private class DisplayFrame {
	public ArrayList<DisplayTeam> teams;
	public ArrayList<DisplayFleet> fleets;
	public ArrayList<DisplayShip> ships;

	public int tick;

	public DisplayFrame(ArrayList<DisplayTeam> teams,
			    ArrayList<DisplayFleet> fleets,
			    ArrayList<DisplayShip> ships,
			    int tick) {
	    this.teams = teams;
	    this.fleets = fleets;
	    this.ships = ships;
	    this.tick = tick;
	}
    }

    private class DisplayTeam {
	int index;

	String name;
    }

    private class DisplayFleet {
	int indexInTeam;
	DisplayTeam team;
	
	int credits;
	int numShips;
	String name;
	String aiName;
	String aiAuthor;
	String aiVersion;
	float red;
	float green;
	float blue;
    }

    private class DisplayShip {
	int indexInFleet;
	DisplayFleet fleet;

	DQuad location;
    }
    
    public GLDisplay() {
	frameQueue = new ConcurrentLinkedQueue<DisplayFrame>();
    }

    private void createFrame(int width, int height) {
    	frame = new JFrame("Space Robots");
	canvas = new GLCanvas();

	canvas.addGLEventListener(this);
	frame.add(canvas);
	final FPSAnimator animator = new FPSAnimator(canvas, 60);

	canvas.setSize(width, height);
	frame.pack();

	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    // Run this on another thread than the AWT event
		    // queue to make sure the call to Animator.stop()
		    // completes before exiting
		    new Thread(new Runnable() {
			    public void run() {
				animator.stop();
				System.exit(0);
			    }
			}).start();
		}
	    });


	frame.setVisible(true);
	animator.start();
    }
	

    static int fleetID1 = 0;
    static int fleetID2 = 0;

    public void updateDisplay(Battle b) {
	if (frame == null) {
	    battleWidth = b.getWidth();
	    battleHeight = b.getHeight();
	    createFrame((int) b.getWidth()*5, (int) b.getHeight()*5);
	    
//  	    Iterator iter = b.teamIterator();
// 	    while(iter.hasNext()) {
// 		teams.add((Team) iter.next());
// 	    }
// 	    iter = b.fleetIterator();
// 	    while(iter.hasNext()) {
// 		fleets.add((ServerFleet) iter.next());
// 	    }
	}

	if (b.isOver()) {
	    simulationFinished = true;
	}


	ArrayList<DisplayTeam> teams = new ArrayList<DisplayTeam>();
	ArrayList<DisplayFleet> fleets = new ArrayList<DisplayFleet>();
	ArrayList<DisplayShip> ships = new ArrayList<DisplayShip>();

	Iterator<ServerTeam> teamIter = b.teamIterator();
	while(teamIter.hasNext()) {
	    ServerTeam team = teamIter.next();
	    DisplayTeam disTeam = new DisplayTeam();

	    disTeam.index = team.getTeamIndex();
	    disTeam.name = team.getName();
	    teams.add(disTeam.index, disTeam);
	}

	Iterator<ServerFleet> fleetIter = b.fleetIterator();
	while(fleetIter.hasNext()) {
	    ServerFleet fleet = fleetIter.next();
	    DisplayFleet disFleet = new DisplayFleet();

	    disFleet.indexInTeam = fleet.getFleetIndex();
	    disFleet.credits = fleet.getCredits();
	    disFleet.name = fleet.getName();
	    disFleet.aiName = fleet.getAIName();
	    disFleet.aiAuthor = fleet.getAIAuthor();
	    disFleet.aiVersion = fleet.getAIVersion();
	    disFleet.red = 1f;
	    disFleet.green = 1f;
	    disFleet.blue = 0f;
	    fleets.add(disFleet);
	}

	Iterator<ServerShip> iter = b.shipIterator();
	while(iter.hasNext()) {
	    ServerShip ship = iter.next();
	    DisplayShip disShip = new DisplayShip();

	    disShip.location = ship.getLocation();
	    ships.add(disShip);
	}

	frameQueue.add(new DisplayFrame(teams, fleets, ships, b.getTick()));
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			       boolean deviceChanged) {}
    
    public void reshape(GLAutoDrawable drawable, int x, int y,
			int width, int height) {
	GL gl = drawable.getGL();

	//This kept giving me warnings...
	//float h = (float)height / (float)width;

	gl.glMatrixMode(GL.GL_PROJECTION);

	Debug.info("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
	Debug.info("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
	Debug.info("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));


	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();

	gl.glMatrixMode(GL.GL_MODELVIEW);
	gl.glLoadIdentity();

	gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
	gl.glOrtho(0, battleWidth, battleHeight, 0, 1, -1);

	gl.glEnable(GL.GL_BLEND);
	gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void display(GLAutoDrawable drawable) {
	DisplayFrame displayFrame;
	GL gl = drawable.getGL();

	if (simulationFinished && frameQueue.isEmpty()) {
	    System.exit(0);
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

	gl.glBegin(GL.GL_QUADS);
	for (int i = 0; i < displayFrame.ships.size(); i++) {
	    DisplayShip ship = displayFrame.ships.get(i);

	    //	    gl.glColor4f(ship.red, ship.green, ship.blue, .2f);
	    gl.glColor4f(1.0f,1.0f,0.0f,.2f);
	    gl.glVertex2f((float) ship.location.getP1().getX(), 
			  (float) ship.location.getP1().getY());
	    gl.glVertex2f((float) ship.location.getP2().getX(), 
			  (float) ship.location.getP2().getY());
	    gl.glVertex2f((float) ship.location.getP3().getX(), 
			  (float) ship.location.getP3().getY());
	    gl.glVertex2f((float) ship.location.getP4().getX(), 
			  (float) ship.location.getP4().getY());
	}
	gl.glEnd();

	plainRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
	plainRenderer.setColor(0.0f, 0.8f, 0.0f, 0.8f);
	plainRenderer.draw("Tick: " + displayFrame.tick,
		      0, 24);
	plainRenderer.draw("Ticks in Queue: " + frameQueue.size(),
		      0, 12);
	plainRenderer.draw("Ship Count: " + displayFrame.ships.size(),
		      0, 0);
	plainRenderer.setColor(1.0f, 0.0f, 0.0f, 0.8f);

	for (int i = 0; i < displayFrame.fleets.size(); i++) {
	    plainRenderer.draw("   " + displayFrame.fleets.get(i).name +
			       " (" + displayFrame.fleets.get(i).aiName + 
			       ":" + displayFrame.fleets.get(i).aiVersion +
			       ") -- " + displayFrame.fleets.get(i).aiAuthor,
			       0, drawable.getHeight() - 12 * (2*i + 2));
	    plainRenderer.draw("       Credits: " + displayFrame.fleets.get(i).credits,
			       0, drawable.getHeight() - 12 * (2*i + 3));
	}

	plainRenderer.endRendering();	


	boldRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
	boldRenderer.setColor(1.0f, 0.0f, 0.0f, 0.8f);
	for (int i = 0; i < displayFrame.teams.size(); i++) {
	    boldRenderer.draw("" + displayFrame.teams.get(i).name,
			      (drawable.getWidth() / displayFrame.teams.size()) * i,
			      drawable.getHeight() - 12);
	}

	boldRenderer.endRendering();

	gl.glPushMatrix();
	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();
	gl.glMatrixMode(GL.GL_MODELVIEW);
	gl.glLoadIdentity();
	gl.glOrtho(0, drawable.getWidth(), drawable.getHeight(), 0, 1, -1);

	gl.glBegin(GL.GL_QUADS);
	gl.glColor4f(1.0f, 0.0f, 0.0f, 0.8f);
	gl.glVertex2f(3, 3+12);
	gl.glVertex2f(3, 9+12);
	gl.glVertex2f(9, 9+12);
	gl.glVertex2f(9, 3+12);

	gl.glColor4f(0.0f, 0.0f, 1.0f, 0.8f);
	gl.glVertex2f(3, 3+36);
	gl.glVertex2f(3, 9+36);
	gl.glVertex2f(9, 9+36);
	gl.glVertex2f(9, 3+36);

	gl.glEnd();

	gl.glPopMatrix();
    }

    public void init(GLAutoDrawable drawable) {
	GL gl = drawable.getGL();

	Debug.info("INIT GL IS: " + gl.getClass().getName());

	Debug.info("Chosen GLCapabilities: " +
			   drawable.getChosenGLCapabilities());

	plainRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
	boldRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));

	gl.setSwapInterval(1);
    }
	
}
