/*
 * Copyright 2010 Roman Mishchenko
 * 
 * This file is part of DbDoc. Project web is http://code.google.com/p/dbdocs/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package info.vstour.dbdoc.server;

import info.vstour.dbdoc.shared.PropsConstants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

  private static ConnectionManager instance;

  private static String            propsFileName;
  private String                   driver;
  private String                   url;
  private String                   user;
  private String                   password;

  /**
   * Creates a new instance of ConnectionManager
   * 
   * @throws IOException
   * @throws FileNotFoundException
   * 
   */
  private ConnectionManager() {
    driver = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.DRIVER).trim();
    url = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.URL).trim();
    user = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.USER).trim();
    password = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.PASSWORD).trim();
  }

  public static ConnectionManager get(String propsFileName) {
    if (ConnectionManager.instance == null || !ConnectionManager.propsFileName.equals(propsFileName)) {
      ConnectionManager.propsFileName = propsFileName;
      DbDocRes.get(propsFileName);
      ConnectionManager.instance = new ConnectionManager();
    }
    return ConnectionManager.instance;
  }

  public Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName(driver);
    return DriverManager.getConnection(getUrl(), getUser(), getPassword());
  }

  public String getDriver() {
    return driver;
  }

  public String getUrl() {
    return url;
  }

  public String getUser() {
    return user;
  }

  private String getPassword() {
    return password;
  }
}
