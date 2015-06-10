/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net;

public final class NetLauncher {

  private NetLauncher() {}

  public static void main(final String[] args) {
    System.out.println(NetworkUtils.getAllHostAddresses());
  }

}
