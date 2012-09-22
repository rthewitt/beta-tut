package com.mpi.playn.core;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.keyboard;
import static com.mpi.playn.core.BetaConstants.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer;
import playn.core.Keyboard;
import playn.core.PlayN;
import playn.core.Surface;

public class Beta implements Game, Keyboard.Listener {
	
	private static final double[] enemyPlacements = {20, 130, 280, 400, 520, 520};
	
	private static final Set<State> drawStates = EnumSet.of(State.ACTIVE); // quick boolean lookups
	private static final Random rand = new Random();
	
	
	
	private int TOP = 0; // may vary, depends on scrolling technique
	private int BOTTOM = GAME_HEIGHT; // will not always be true
	
	private int HORIZON = -40; // TODO change logic
	// strategy taken from cute game - review
	private float frameAlpha;
	private ImmediateLayer gameLayer;
	private Ship player;
	private Image[] imgOn = new Image[3];
	private Image[] imgOff = new Image[3];
	private boolean controlLeft, controlRight, controlUp, controlDown;
	private int updateCounter = -1;
	
	private static final double FRICTION = .07;
	
	private List<Ship> entities = new ArrayList<Ship>(); // use inheritance
	
  @Override
  public void init() {
	  
	  keyboard().setListener(this);
	  
	  graphics().setSize(GAME_WIDTH, GAME_HEIGHT);
    // create and add background image layer
	  
    Image bgImage = assets().getImage("images/incredible-zelda-painting.jpg");
    
    loadPlayerImages();
    
    // TODO consider mutability
    player = new Ship(imgOff, imgOn, 224.0, 710.0);
    player.isPlayer = true;
    player.activate();
    entities.add(player);
    
    initEnemies();
    
    ImageLayer bgLayer = graphics().createImageLayer(bgImage);
    graphics().rootLayer().add(bgLayer);
    
    // going to try to add an immediate layer, but
    // may instead need / wish to replace the layer.  Not sure yet.
    gameLayer = graphics().createImmediateLayer(new ImmediateLayer.Renderer() {
		
		@Override
		public void render(Surface surface) {
			// TODO set origin to match location of ship, or else gradually move it forward. (This allows movement)
			surface.clear();
			paintShipLayer(surface, frameAlpha);
		}
	});
    graphics().rootLayer().add(gameLayer);
    
  }
  
  // currently, placement serves as a mere timer
  private void initEnemies() {
	  for(double d : enemyPlacements ) {
		  Ship enemy = new Ship(new Image[] { assets().getImage("images/smallBlue.png") },
				  (double)randRange(10, GAME_WIDTH-10), -d); // cache image? // TODO change x position setting
		  enemy.setVelocity(0, 1);
		  enemy.setAcceleration(0, 1);
		  entities.add(enemy);
	  }
  }
  
  private void loadPlayerImages() {
	  imgOff[STRAIGHT] = assets().getImage("images/largeRedArmor_off.png");
	  imgOff[LEFT] = assets().getImage("images/largeRedArmor_off_l.png");
	  imgOff[RIGHT] = assets().getImage("images/largeRedArmor_off_r.png");
	  
	  imgOn[STRAIGHT] = assets().getImage("images/largeRedArmor_on.png");
	  imgOn[LEFT] = assets().getImage("images/largeRedArmor_on_l.png");
	  imgOn[RIGHT] = assets().getImage("images/largeRedArmor_on_r.png");
  }
  
  private void paintShipLayer(Surface surf, float alpha) {
	  
	  for(Ship s : entities) 
		  if(drawStates.contains(s.state)) 
			  surf.drawImage(s.getCurrentImage(), (float)s.x, (float)s.y);
  }

  @Override
  public void paint(float alpha) {
    // background autopaints - TODO investigate method
	  
	  frameAlpha = alpha; // use with immediateLayer renderer 
  }

  @Override
  public void update(float delta) {
	  
	  player.setAcceleration(0, 0);
	  
	      ++updateCounter;
  }

  @Override
  public int updateRate() {
    return 25;
  }
  
  @Override
  public void onKeyDown(Keyboard.Event event) {
	  
	  switch (event.key()) {
      case LEFT:
    	  if(!controlRight) {
    		  controlLeft = true;
    	      player.roll = LEFT;  
    	  }
        break;
      case UP:
        controlUp = true;
        player.jetsFiring = true;
        break;
      case RIGHT:
    	  if(!controlLeft) {
    		  controlRight = true;
    	      player.roll = RIGHT;  
    	  }
        break;
      case DOWN:
        controlDown = true;
        break;
    }
  }
  
  @Override
  public void onKeyTyped(Keyboard.TypedEvent event) {
	  // not used
  }
  
  @Override
  public void onKeyUp(Keyboard.Event event) {
	  switch (event.key()) {
      case LEFT:
        controlLeft = false;
        player.roll = STRAIGHT;
        break;
      case UP:
        controlUp = false;
        player.jetsFiring = false;
        break;
      case RIGHT:
        controlRight = false;
        player.roll = STRAIGHT;
        break;
      case DOWN:
        controlDown = false;
        break;
    }
  }
  
  private int randRange(int min, int max) {
	  return rand.nextInt(max - min + 1) + min;
  }
  
  private double randRange(double min, double max) {
	  return (min + (max - min)) * rand.nextDouble();
	  
  }
  
  private void spawnEnemy(Ship enemy) {
	  enemy.activate();
	  double xVelocity = randRange(0, 0.75);
	  enemy.vx = xVelocity;
	  if(enemy.x > GAME_WIDTH/2) enemy.vx = -enemy.vx;
	  
	  // find acceleration unit vector
	  double xAcc = enemy.vx/enemy.vy;
	  double yAcc = xAcc < 0 ? 1.0+xAcc : 1.0-xAcc; // never flies up
//	  enemy.setAcceleration(xAcc, yAcc);
  }
  
  // Will be used to as an indicator that it's time to spawn enemy.
  private boolean isApproaching(Ship enemy) {
	  return enemy.y < TOP && enemy.y > HORIZON; 
  }
  
  // will be needed for all objects
  // notice that objects are not self updating.  Consider
  private void updateShip(Ship s, double delta) {
	  
	  PlayN.log().debug(s.state.toString());
	  
	  switch(s.state) {
	  case REMOVE:
		  return;
	  case INACTIVE:
		  if(isApproaching(s)) spawnEnemy(s); // no break
	  case ACTIVE:
		  if(s.state == State.INACTIVE) {
			    
		  } else if(s.y > BOTTOM) {
			  if(s.isPlayer) s.vy = -s.vy * 0.4; // restrict movement, bounce back
//			  entities.remove(s); // ConcurrentModificationException.  Collection copy workaround?
			  else s.state = State.REMOVE;
			  return;
		  }
		  
		  if (s.lastUpdated == updateCounter) {
		      return;
		    }
		    s.lastUpdated = updateCounter;
		  
		  s.saveOldPos();
		  
		  if (s.isResting()) {
		      s.vx -= s.vx * FRICTION * delta;
		      s.vy -= s.vy * FRICTION * delta;
		    }
		  
		  PlayN.log().debug("x = " + s.x + ", y = " + s.y);
		  
		  s.vx += s.ax * delta;
		  s.vy += s.ay * delta;
		  
		  // added update x,y
		  s.x = s.x + s.vx;
		  s.y = s.y + s.vy;
		  break;
	  default: PlayN.log().debug("Unrecognized State in update");
	  }
  }
  
}
