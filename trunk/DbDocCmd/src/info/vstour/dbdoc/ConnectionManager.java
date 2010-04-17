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
package info.vstour.dbdoc;

import info.vstour.dbdoc.shared.PropsConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

	private String	driver;
	private String	url;
	private String	user;
	private String	password;

	public ConnectionManager(Properties props) {
		driver = props.getProperty(PropsConstants.DRIVER);
		url = props.getProperty(PropsConstants.URL);
		user = props.getProperty(PropsConstants.USER);
		password = props.getProperty(PropsConstants.PASSWORD);
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
