/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Simple Music Equalizer.
 */
public class MusicEqualizer extends Application {

  @Override
  public void start(final Stage stage) throws Exception {
    stage.initStyle(StageStyle.TRANSPARENT);
    Group root = new Group();
    Scene scene = new Scene(root, 400, 200, Color.TRANSPARENT);
    stage.setScene(scene);
  }

}
