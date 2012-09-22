package com.mpi.playn.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import com.mpi.playn.core.Beta;

public class BetaHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform platform = HtmlPlatform.register();
    platform.assets().setPathPrefix("betaport/");
    PlayN.run(new Beta());
  }
}
