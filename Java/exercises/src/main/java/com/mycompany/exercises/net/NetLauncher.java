/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net;

public final class NetLauncher {

  private NetLauncher() {}

  public static void main(final String[] args) {
    System.out.println(NetworkUtils.getAllHostAddresses());
    System.out.println(NetworkUtils.getInterfaceHostAddresses());
    System.out
        .println("google.co.uk is available: " + NetworkUtils.isHostAvailable("google.co.uk"));
    System.out.println("netboss.com content is available: "
        + NetworkUtils.isHostContentAvailable("netboss.com"));
  }

}
