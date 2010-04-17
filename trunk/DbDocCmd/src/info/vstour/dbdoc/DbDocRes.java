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

import info.vstour.dbdoc.server.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class DbDocRes extends Resource {

	public final String	HTML_RES	= "res/html/";

	public final String	CONTENTS_HTML;
	public final String	CONTENTS_DETAIL_HTML;
	public final String	DOC_HTML;
	public final String	INFO_HTML;

	private String	    outPutDir;

	public DbDocRes(String baseUrl, String propsFileName) throws IOException {
		super(baseUrl, propsFileName);
		CONTENTS_HTML = getResource(new URL(BASE_URL + HTML_RES + "contents.html"));
		CONTENTS_DETAIL_HTML = getResource(new URL(BASE_URL + HTML_RES + "contents_detail.html"));
		INFO_HTML = getResource(new URL(BASE_URL + HTML_RES + "info.html"));
		DOC_HTML = getResource(new URL(BASE_URL + HTML_RES + "doc.html"));

		setOutPutDir(getProps().getProperty("OutputDirectory"));
		if (!saveResources(getOutPutDir()))
			throw new IOException("Can not save resources.");
	}
	/**
	 * Creates directories and saves resource files
	 * 
	 * @param dir
	 *            output directory
	 * @return true on success, otherwise false
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private boolean saveResources(String dir) throws FileNotFoundException, IOException {
		String resDir = dir;
		File docFile = new File(resDir);
		boolean status = docFile.exists();
		if (status) {
			System.out.println("Using exiting directory: " + resDir);
		} else {
			System.out.println("Creating directory: " + resDir);
			status = docFile.mkdirs();
		}
		if (status) {
			iniResources(dir + "/");
			return true;
		} else {
			System.out.println("Can not create directory " + resDir);
			return false;
		}
	}

	private void iniResources(String path) throws FileNotFoundException, IOException {
		String fileName = "index.html";
		saveToFile(path + "/" + fileName, getResource(new URL(BASE_URL + HTML_RES + fileName)));
		fileName = "blank.html";
		saveToFile(path + "/" + fileName, getResource(new URL(BASE_URL + HTML_RES + fileName)));
		fileName = "DbDoc.css";
		saveToFile(path + "/" + fileName, getResource(new URL(BASE_URL + HTML_RES + fileName)));
	}

	public String getOutPutDir() {
		return outPutDir;
	}
	private void setOutPutDir(String property) {
		if (property == null || property.trim().isEmpty()) {
			property = ".";
		}
		outPutDir = property;
	}
}
