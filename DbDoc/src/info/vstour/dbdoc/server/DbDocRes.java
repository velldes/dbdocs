package info.vstour.dbdoc.server;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DbDocRes extends Resource {

	private static DbDocRes	instance;
	public static String	propsFileName;

	private DbDocRes(String baseUrl, String propsFileName) throws FileNotFoundException, IOException {
		super(baseUrl, propsFileName);
	}

	public static DbDocRes get(String propsFileName) throws FileNotFoundException, IOException {
		if (DbDocRes.instance == null || !DbDocRes.propsFileName.equals(propsFileName)) {
			DbDocRes.propsFileName = propsFileName;
			String baseUrl = DbDocRes.class.getProtectionDomain().getCodeSource().getLocation().toString();
			DbDocRes.instance = new DbDocRes(baseUrl, propsFileName);
		}
		return DbDocRes.instance;
	}
}
