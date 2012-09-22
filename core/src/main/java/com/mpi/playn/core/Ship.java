package com.mpi.playn.core;

import playn.core.Image;
import static com.mpi.playn.core.BetaConstants.*;

public class Ship {
	// TODO handle permissions
	  
	  State state = State.INACTIVE; 
	  
	  boolean isPlayer = false;
	  boolean jetsFiring = false;
	  int roll = STRAIGHT;
	  
	  Image[] offImages = {};
	  Image[] onImages = {};
	  double oldx, oldy;
	  double x, y;
	  double vx, vy;
	  double ax, ay;
	  int lastUpdated;
	  
	  boolean resting;
	  
	  Ship(Image[] img, double x, double y) {
		  this.offImages = img;
		  this.x = x;
		  this.y = y;
		  this.resting = true; // this does not come into play in 2 dimensions
	  }
	  
	  Ship(Image[] offImg, Image[] onImg, double x, double y) {
		  this(offImg, x, y);
		  this.onImages = onImg;
	  }
	  
	  void setVelocity(double vx, double vy) {
		    this.vx = vx;
		    this.vy = vy;
		  }
	  
	  void setAcceleration(double ax, double ay) {
		  this.ax = ax;
		  this.ay = ay;
	  }
	  
	  void saveOldPos() {
		  this.oldx = x;
		  this.oldy = y;
	  }
	  
	  boolean isResting() {
		  return resting;
	  }
	  
	  double x(double alpha) {
		    return x * alpha + oldx * (1.0f - alpha);
		  }

	double y(double alpha) {
		    return y * alpha + oldy * (1.0f - alpha);
		  }
	
	public void activate() {
		this.state = State.ACTIVE;
	}
	
	public void deactivate() {
		this.state = State.INACTIVE;
	}
	
	public Image getCurrentImage() {
		return jetsFiring ? this.onImages[roll] : this.offImages[roll];
	}
	  
}