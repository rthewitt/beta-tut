package com.mpi.playn.core;


public enum State {
		
	// Game States
	INTRO,
	TITLE_SCREEN,
	MAIN_MENU,
	RUNNING,
	PAUSED,
	FROZEN,
	COMPLETE,
	
	// Ship states
	ACTIVE,
	INACTIVE,
	REMOVE,
	COLLIDED
}