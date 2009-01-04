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

public class GLDisplay implements GLEventListener, Display
{
    private boolean simulationFinished = false;

    private JFrame frame;
    private GLCanvas canvas;

    private AbstractQueue<DisplayFrame> frameQueue;

    private class DisplayFrame {
	public ArrayList<DisplayShip> ships;

	public DisplayFrame(ArrayList<DisplayShip> ships) {
	    this.ships = ships;
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
	final Animator animator = new Animator(canvas);

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
	frame.show();
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

	Iterator iter = b.shipIterator();
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

	frameQueue.add(new DisplayFrame(ships));
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
  }

  public void display(GLAutoDrawable drawable) {
      GL gl = drawable.getGL();

      if (simulationFinished && frameQueue.isEmpty()) {
	  System.exit(0);
      }

      if (frameQueue.isEmpty()) {
	  return;
      }

      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

      DisplayFrame displayFrame = frameQueue.poll();

      gl.glBegin(GL.GL_QUADS);
      for (int i = 0; i < displayFrame.ships.size(); i++) {
	  DisplayShip ship = displayFrame.ships.get(i);

	  gl.glColor3f(ship.red, ship.green, ship.blue);
	  gl.glVertex2f((float) ship.location.getP1().getX()-1, 
			(float) ship.location.getP1().getY()-1);
	  gl.glVertex2f((float) ship.location.getP2().getX()+1, 
			(float) ship.location.getP2().getY()-1);
	  gl.glVertex2f((float) ship.location.getP3().getX()+1, 
			(float) ship.location.getP3().getY()+1);
	  gl.glVertex2f((float) ship.location.getP4().getX()-1, 
			(float) ship.location.getP4().getY()+1);
      }
      gl.glEnd();
  }

  public void init(GLAutoDrawable drawable) {
      GL gl = drawable.getGL();

      System.err.println("INIT GL IS: " + gl.getClass().getName());

      System.err.println("Chosen GLCapabilities: " +
			 drawable.getChosenGLCapabilities());

      gl.setSwapInterval(1);
  }
	
}
