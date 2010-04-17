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
