package net.banack.spacerobots;

import java.util.Iterator;

import net.banack.spacerobots.util.DefaultShipTypeDefinitions;
import net.banack.spacerobots.util.Ship;
import net.banack.util.MethodNotImplementedException;
import net.banack.spacerobots.util.Fleet;
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
    private TextRenderer renderer;
    private DisplayFrame lastFrame;

    private AbstractQueue<DisplayFrame> frameQueue;

    private class DisplayFrame {
	public ArrayList<DisplayShip> ships;
	public int tick;

	public DisplayFrame(ArrayList<DisplayShip> ships, int tick) {
	    this.ships = ships;
	    this.tick = tick;
	}
    }
    
    private class DisplayShip {
	public DQuad location;
	public float red;
	public float green;
	public float blue;
    }


    private int frameWidth;
    private int frameHeight;
    private double battleWidth;
    private double battleHeight;

    public GLDisplay() {
	frameQueue = new ConcurrentLinkedQueue<DisplayFrame>();
    }

    private void createFrame(int width, int height) {
    	frame = new JFrame("Space Robots");
	canvas = new GLCanvas();

	frameWidth = width;
	frameHeight = height;

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


       	frame.setResizable(false);
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
	}
	if (b.isOver()) {
	    simulationFinished = true;
	}
	ArrayList<DisplayShip> ships = new ArrayList<DisplayShip>();

	Iterator<ServerShip> iter = b.shipIterator();
	while(iter.hasNext()) {
	    ServerShip ship = (ServerShip) iter.next();
	    DisplayShip displayShip = new DisplayShip();
	    displayShip.location = ship.getLocation();

	    displayShip.red = 0f;
	    displayShip.green = 0f;
	    displayShip.blue = 0f;

	    int fleetID = ship.getFleetID();
	    
	    if ((fleetID != fleetID1) || (fleetID != fleetID2)) {
		if (fleetID1 == 0) {
		    fleetID1 = fleetID;
		} else if (fleetID2 == 0) {
		    fleetID2 = fleetID;
		}
	    }
	    
	    if (fleetID == fleetID1) {
		displayShip.red = 1f;
	    } else if (fleetID == fleetID2) {
		displayShip.blue = 1f;
	    } else {
		displayShip.red = 1f;
		displayShip.green = 1f;
		displayShip.blue = 1f;
	    }
	    ships.add(displayShip);
	}

	frameQueue.add(new DisplayFrame(ships, b.getTick()));
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			       boolean deviceChanged) {}
    
    public void reshape(GLAutoDrawable drawable, int x, int y,
			int width, int height) {
	GL gl = drawable.getGL();

	float h = (float)height / (float)width;

	gl.glMatrixMode(GL.GL_PROJECTION);

	System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
	System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
	System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));


	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();

	gl.glMatrixMode(GL.GL_MODELVIEW);
	gl.glLoadIdentity();

	gl.glViewport(0, 0, frameWidth, frameHeight);
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

	    gl.glColor4f(ship.red, ship.green, ship.blue, .1f);
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

	renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
	renderer.setColor(0.0f, 0.8f, 0.0f, 0.8f);
	renderer.draw("Tick: " + displayFrame.tick,
		      0, drawable.getHeight()-24);
	renderer.draw("Ticks in Queue: " + frameQueue.size(),
		      0, drawable.getHeight()-48);
	renderer.draw("Ship Count: " + displayFrame.ships.size(),
		      0, drawable.getHeight()-72);
	renderer.endRendering();
    }

    public void init(GLAutoDrawable drawable) {
	GL gl = drawable.getGL();

	System.err.println("INIT GL IS: " + gl.getClass().getName());

	System.err.println("Chosen GLCapabilities: " +
			   drawable.getChosenGLCapabilities());

	renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));

	gl.setSwapInterval(1);
    }
	
}
