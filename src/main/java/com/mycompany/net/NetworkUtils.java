/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NetworkUtils {

  private static final int HTTP_STATUS_CODE_OK = 200;
  private static final int HTTP_STATUS_CODE_BAD_REQUEST = 400;

  private NetworkUtils() {}

  /**
   * @return host addresses for each network interface for this machine
   */
  public static Map<String, List<String>> getInterfaceHostAddresses() {
    Map<NetworkInterface, List<InetAddress>> interfaceInetAddresses = getInterfaceInetAddresses();
    return convertToInterfaceHostAddresses(interfaceInetAddresses);
  }

  /**
   * @return all host addresses for this machine
   */
  public static List<String> getAllHostAddresses() {
    List<InetAddress> inetAddresses = getAllInetAddresses();
    return convertToHostAddresses(inetAddresses);
  }

  private static Map<String, List<String>> convertToInterfaceHostAddresses(
      final Map<NetworkInterface, List<InetAddress>> interfaceInetAddresses) {
    Map<String, List<String>> interfaceHostAddresses = new HashMap<>();
    for (Map.Entry<NetworkInterface, List<InetAddress>> entry : interfaceInetAddresses.entrySet()) {
      interfaceHostAddresses
          .put(entry.getKey().getName(), convertToHostAddresses(entry.getValue()));
    }
    return interfaceHostAddresses;
  }

  private static List<String> convertToHostAddresses(final List<InetAddress> inetAddresses) {
    List<String> hostAddresses = new ArrayList<>();
    for (InetAddress inetAddress : inetAddresses) {
      hostAddresses.add(inetAddress.getHostAddress());
    }
    return hostAddresses;
  }

  private static Map<NetworkInterface, List<InetAddress>> getInterfaceInetAddresses() {
    Map<NetworkInterface, List<InetAddress>> interfaceInetAddresses = new HashMap<>();
    List<NetworkInterface> interfaces = getNetworkInterfaces();
    for (NetworkInterface networkInterface : interfaces) {
      List<InetAddress> inetAddresses = getInetAddresses(networkInterface);
      interfaceInetAddresses.put(networkInterface, inetAddresses);
    }
    return interfaceInetAddresses;
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

  public static boolean isHostAvailable(final String host) {
    try (Socket socket = new Socket(host, 80)) {
      return true;
    } catch (IOException ex) {
      System.err.println("Failed to check if host " + host + " is available.");
      System.err.println("Message: " + ex.getMessage());
    }
    return false;
  }

  public static boolean isHostContentAvailable(final String host) {
    boolean available = false;
    try {
      URL url = getURL(host);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      available = isPositiveResponseCode(responseCode);
      System.out.println("HTTP response message: " + connection.getResponseMessage());
    } catch (IOException ex) {
      System.err.println("Failed to check if host " + host + " content is available.");
      System.err.println("Message: " + ex.getMessage());
    }
    return available;
  }

  private static URL getURL(final String host) throws MalformedURLException {
    String url = host.replaceFirst("^https", "http");
    String validURL = url.startsWith("http") ? url : "http://" + host;
    return new URL(validURL);
  }

  private static boolean isPositiveResponseCode(int responseCode) {
    return HTTP_STATUS_CODE_OK <= responseCode && responseCode < HTTP_STATUS_CODE_BAD_REQUEST;
  }

}
