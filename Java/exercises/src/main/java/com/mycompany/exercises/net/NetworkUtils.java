/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NetworkUtils {

  private NetworkUtils() {}

  public static List<String> getAllHostAddresses() {
    List<String> hostAddresses = new ArrayList<>();
    List<InetAddress> inetAddresses = getAllInetAddresses();
    for (InetAddress inetAddress : inetAddresses) {
      hostAddresses.add(inetAddress.getHostAddress());
    }
    return hostAddresses;
  }

  private static List<InetAddress> getAllInetAddresses() {
    List<InetAddress> allInetAddresses = new ArrayList<>();
    List<NetworkInterface> interfaces = getNetworkInterfaces();
    for (NetworkInterface networkInterface : interfaces) {
      List<InetAddress> inetAddresses = getInetAddresses(networkInterface);
      allInetAddresses.addAll(inetAddresses);
    }
    return allInetAddresses;
  }

  private static List<NetworkInterface> getNetworkInterfaces() {
    List<NetworkInterface> interfaces = new ArrayList<>();
    try {
      interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
    } catch (SocketException ex) {
      System.err.println("Failed to get all the interfaces on this machine.");
      System.err.println("Message: " + ex.getMessage());
    }
    return interfaces;
  }

  private static List<InetAddress> getInetAddresses(final NetworkInterface networkInterface) {
    return Collections.list(networkInterface.getInetAddresses());
  }

}
