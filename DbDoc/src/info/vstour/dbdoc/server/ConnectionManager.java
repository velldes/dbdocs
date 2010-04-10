package info.vstour.dbdoc.server;

import info.vstour.dbdoc.shared.PropsConstants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	private static ConnectionManager	instance;

	public static String	         propsFileName;
	private String	                 driver;
	private String	                 url;
	private String	                 user;
	private String	                 password;

	/**
	 * Creates a new instance of ConnectionManager
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 */
	private ConnectionManager() throws FileNotFoundException, IOException {
		driver = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.DRIVER);
		url = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.URL);
		user = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.USER);
		password = DbDocRes.get(propsFileName).getProps().getProperty(PropsConstants.PASSWORD);
	}

	public static ConnectionManager get(String propsFileName) throws FileNotFoundException, IOException {
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
