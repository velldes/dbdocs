package info.vstour.dbdoc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Roman Mishchenko
 */
public class ConnectionManager {

	private String	driver;
	private String	url;
	private String	user;
	private String	password;

	public ConnectionManager(Properties props) {
		driver = props.getProperty("Driver");
		url = props.getProperty("Url");
		user = props.getProperty("User");
		password = props.getProperty("Password");
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
