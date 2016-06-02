/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.jdbc;

public final class DatabaseParameters {

  private final String url;
  private final String username;
  private final String password;

  public static class Builder {

    // Required parameters
    private final String url;
    private final String username;
    private final String password;

    public Builder(final String url, final String username, final String password) {
      this.url = url;
      this.username = username;
      this.password = password;
    }

    public DatabaseParameters build() {
      return new DatabaseParameters(this);
    }
  }

  private DatabaseParameters(final Builder builder) {
    url = builder.url;
    username = builder.username;
    password = builder.password;
  }

  public String getURL() {
    return url;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

}
