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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public abstract class Resource {

	public final String	        BASE_URL;
	public final String	        PROPS_RES	= "res/props/";
	public final String	        SQL_RES	  = "res/sql/";

	private Properties	        props;
	private Map<String, String>	sqlmap;

	public Resource(String baseUrl, String propsFileName) throws FileNotFoundException, IOException {
		BASE_URL = baseUrl;
		if (propsFileName == null)
			propsFileName = "";
		if (!propsFileName.isEmpty()) {
			props = this.loadProps(new URL(BASE_URL + PROPS_RES + propsFileName + ".properties"));
			sqlmap = getMap(getResource(new URL(BASE_URL + SQL_RES + props.getProperty("SqlFile"))));
		}
	}

	/**
	 * Returns an array of strings naming the files and directories in the directory that
	 * matches the given regular expression.
	 * 
	 * @param dir
	 * @param regex
	 * @return
	 */
	public String[] getList(File dir, final String regex) {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches(regex);
			}
		};
		return dir.list(filter);
	}
	/**
	 * Reads a properties file and returns Java Properties object.
	 * 
	 * @param url
	 *          Uniform Resource Locator.
	 * 
	 * @return Properties object
	 * @throws IOException
	 */
	private Properties loadProps(URL url) throws IOException {
		Properties prop = new Properties();
		if (url != null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			prop.load(in);
		}

		return prop;
	}

	/**
	 * Returns content of the file.
	 * 
	 * @param url
	 *          Uniform Resource Locator.
	 * 
	 * @return Content of the file
	 * @throws IOException
	 */
	public String getResource(URL url) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		Scanner scanner = new Scanner(in).useDelimiter("\\Z");
		return scanner.next();
	}

	/**
	 * Saves text to a file. The file named by the path name <code>name</code> in the file
	 * system.
	 * 
	 * @param name
	 *          the system-dependent file name.
	 * @param text
	 *          Text
	 * @throws IOException
	 */
	public void saveToFile(String name, String text) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");
		try {
			out.write(text);
		}
		finally {
			out.close();
		}
	}

	/**
	 * Splits the text by following regexp --.* (two hyphens at the beginning of the line )
	 * to get key==value array of strings. Then puts key, value pair into the map.
	 * 
	 * Text must have two hyphens at the beginning of the line before each key, value.
	 * 
	 * @param String
	 *          resource
	 * @return Map<String, String>
	 */
	private Map<String, String> getMap(String resource) {
		Map<String, String> map = new HashMap<String, String>();

		String[] pair = resource.split("--.*");
		for (int i = 0; i < pair.length; i++) {
			if (pair[i].length() > 0) {
				String[] keyValue = pair[i].split("==");
				map.put(keyValue[0].trim(), keyValue[1].trim());
			}
		}
		return map;
	}

	/**
	 * 
	 * @return Properties object
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * 
	 * @return Map of sql statements
	 */
	public Map<String, String> getSqlMap() {
		return sqlmap;
	}

}
