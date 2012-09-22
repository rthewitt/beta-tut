package com.mpi.playn.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import com.mpi.playn.core.Beta;

public class BetaActivity extends GameActivity {

  @Override
  public void main(){
    platform().assets().setPathPrefix("com/mpi/playn/resources");
    PlayN.run(new Beta());
  }
}
