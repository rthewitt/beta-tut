package com.mpi.playn.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import com.mpi.playn.core.Beta;

public class BetaJava {

  public static void main(String[] args) {
    JavaPlatform platform = JavaPlatform.register();
    platform.assets().setPathPrefix("com/mpi/playn/resources");
    PlayN.run(new Beta());
  }
}
