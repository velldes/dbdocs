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

import info.vstour.dbdoc.shared.Converter;
import info.vstour.dbdoc.shared.PropsConstants;
import info.vstour.dbdoc.shared.SqlConstants;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class DbDoc {

	DbDocRes	          dbDocRes;

	public final String	HREF	= "<a href ='" + SqlConstants.DIR_TOKEN + SqlConstants.NAME_TOKEN + ".html' target ='"
	                             + SqlConstants.DOC_TOKEN + "'>" + SqlConstants.NAME_TOKEN + "</a><br>";

	private String	    owner;
	private String	    filter;
	private String	    objects;
	private int	        viewId;
	private boolean	    allCommentsAsDoc;

	private Statement	  stmt;
	private Statement	  stmtDoc;
	private Statement	  stmtTmp;

	public DbDoc(String propsFileName) throws IOException {

		String baseUrl = DbDocRes.class.getProtectionDomain().getCodeSource().getLocation().toString();
		baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
		dbDocRes = new DbDocRes(baseUrl, propsFileName);

		setOwner(dbDocRes.getProps().getProperty(PropsConstants.OWNER));
		setFilter(dbDocRes.getProps().getProperty(PropsConstants.FILTER));
		setViewId(dbDocRes.getProps().getProperty(PropsConstants.VIEW));
		setAllCommentsAsDoc(dbDocRes.getProps().getProperty(PropsConstants.ALL_COMMENTS_AS_DOC));
		setObjects(dbDocRes.getProps().getProperty(PropsConstants.OBJECTS));

	}

	public void createDoc() throws Exception {
		Connection conn = null;
		ConnectionManager connManager = new ConnectionManager(dbDocRes.getProps());
		conn = connManager.getConnection();
		try {
			if (conn != null) {
				stmt = conn.createStatement();
				stmtDoc = conn.createStatement();
				stmtTmp = conn.createStatement();

				String links = "";
				String[] objects = getObjects().split(",");
				for (int i = 0; i < objects.length; i++) {
					saveDoc(objects[i].trim());

					String link = HREF;
					link = link.replace(SqlConstants.DIR_TOKEN, "");
					link = link.replace(SqlConstants.NAME_TOKEN, objects[i].toLowerCase().trim());
					link = link.replace(SqlConstants.DOC_TOKEN, "detail");
					links = links + "\n" + link;
				}
				String resource = dbDocRes.CONTENTS_HTML;
				resource = resource.replace("#HREF#", links);
				String fileName = "contents.html";
				String file = dbDocRes.getOutPutDir() + "/" + fileName;
				System.out.println("Saving " + file);
				dbDocRes.saveToFile(file, resource);

				resource = dbDocRes.INFO_HTML;
				resource = resource.replace("#DB#", connManager.getUrl());
				resource = resource.replace("#FILTER#", getFilter());
				resource = resource.replace("#DATE#", new Date().toString());
				fileName = "info.html";
				file = dbDocRes.getOutPutDir() + "/" + fileName;
				System.out.println("Saving " + file);
				dbDocRes.saveToFile(file, resource);
			}
		}
		catch (Exception e) {
			throw new IOException("Error while creating documentation = " + e.toString());
		}
		finally {
			try {
				// Close PreparedStatement and Connection
				stmt.close();
				stmtDoc.close();
				stmtTmp.close();
				conn.close();
			}
			catch (SQLException e) { // Catch All exceptions
				throw new SQLException("Error while closing SQL objects = " + e.toString());
			}
		}
	}

	private void saveDoc(String object) throws SQLException, IOException {
		String objectLowerCase = object.toLowerCase();
		String objectUpperCase = object.toUpperCase();
		String docDir = dbDocRes.getOutPutDir() + "/" + objectLowerCase;
		File docFile = new File(docDir);
		boolean status = docFile.exists();
		if (status) {
			System.out.println("Using exiting directory: " + docDir);
		} else {
			System.out.println("Creating directory: " + docDir);
			status = docFile.mkdirs();
		}
		if (status) {
			String query = (String) dbDocRes.getSqlMap().get(SqlConstants.OBJECTS);
			query = query.replace(SqlConstants.NAME_TOKEN, filterToSql());
			query = query.replace(SqlConstants.OWNER_PARAM, "'" + getOwner() + "'");
			query = query.replace(SqlConstants.OBJECT_PARAM, "'" + objectUpperCase + "'");
			ResultSet rs = stmt.executeQuery(query);

			System.out.println("Please wait. Saving " + objectLowerCase + " ...");
			String links = "";
			while (rs.next()) {
				String docHtmlTmp = dbDocRes.DOC_HTML;
				String itemName = rs.getString(1);
				String link = HREF;
				link = link.replace(SqlConstants.DIR_TOKEN, objectLowerCase + "/");
				link = link.replace(SqlConstants.NAME_TOKEN, itemName.toLowerCase());
				link = link.replace(SqlConstants.DOC_TOKEN, "doc");
				links = links + "\n" + link;

				query = (String) dbDocRes.getSqlMap().get(objectUpperCase);
				query = query.replace(SqlConstants.NAME_PARAM, "'" + itemName + "'");
				query = query.replace(SqlConstants.OWNER_PARAM, "'" + getOwner() + "'");
				String doc = getHtml(objectUpperCase, itemName, query);
				if (objectUpperCase.equals(SqlConstants.TABLE_OBJ)) {
					if (dbDocRes.getSqlMap().containsKey(SqlConstants.TABLE_COL_OBJ)) {
						query = (String) dbDocRes.getSqlMap().get(SqlConstants.TABLE_COL_OBJ);
						query = query.replace(SqlConstants.NAME_PARAM, "'" + itemName + "'");
						query = query.replace(SqlConstants.OWNER_PARAM, "'" + getOwner() + "'");
						doc = doc + getHtml(SqlConstants.TABLE_COL_OBJ, itemName, query);
					}
					if (dbDocRes.getSqlMap().containsKey(SqlConstants.TABLE_CON_OBJ)) {
						query = (String) dbDocRes.getSqlMap().get(SqlConstants.TABLE_CON_OBJ);
						query = query.replace(SqlConstants.NAME_PARAM, "'" + itemName + "'");
						query = query.replace(SqlConstants.OWNER_PARAM, "'" + getOwner() + "'");
						doc = doc + getHtml(SqlConstants.TABLE_CON_OBJ, itemName, query);
					}
					if (dbDocRes.getSqlMap().containsKey(SqlConstants.TABLE_IND_OBJ)) {
						query = (String) dbDocRes.getSqlMap().get(SqlConstants.TABLE_IND_OBJ);
						query = query.replace(SqlConstants.NAME_PARAM, "'" + itemName + "'");
						query = query.replace(SqlConstants.OWNER_PARAM, "'" + getOwner() + "'");
						doc = doc + getHtml(SqlConstants.TABLE_IND_OBJ, itemName, query);
					}
				}
				doc = "<h3>" + itemName + "</h3>" + doc + Converter.DBDOC_LINK;
				dbDocRes.saveToFile(docDir + "/" + itemName.toLowerCase() + ".html", docHtmlTmp.replace(SqlConstants.DOC_TOKEN, doc));
			}
			String resource = dbDocRes.CONTENTS_DETAIL_HTML;
			resource = resource.replace("#HREF#", links);
			String fileName = objectLowerCase + ".html";
			String file = dbDocRes.getOutPutDir() + "/" + fileName;
			System.out.println("Saving " + file);
			dbDocRes.saveToFile(file, resource);
		} else {
			System.out.println("Can not create directory " + docDir);
		}
	}

	private String getHtml(String object, String name, String query) throws SQLException {
		String header = "";
		boolean isGetColumns = false;
		if (object.equals(SqlConstants.TABLE_COL_OBJ))
			header = "Columns";
		else if (object.equals(SqlConstants.TABLE_CON_OBJ)) {
			header = "Constraints";
			isGetColumns = true;
		} else if (object.equals(SqlConstants.TABLE_IND_OBJ)) {
			header = "Indexes";
			isGetColumns = true;
		}

		ResultSet rsD = stmtDoc.executeQuery(query);
		ResultSetMetaData rsDmd = rsD.getMetaData();
		int cols = rsDmd.getColumnCount();
		String doc = "";
		if (cols == 1) {
			Converter.init();
			while (rsD.next()) {
				if (isAllCommentsAsDoc())
					doc = doc + Converter.markUpComments(rsD.getString(1));
				else
					doc = doc + Converter.markUp(rsD.getString(1));
			}
			doc = Converter.textToHtml(doc, getViewId(object), "");
		} else {
			String th = "";
			String tr = "";
			int name_index = -1;
			int col_index = -1;
			for (int i = 1; i <= cols; i++) {
				String colName = rsDmd.getColumnName(i);
				colName = colName.substring(0, 1) + colName.substring(1).toLowerCase();
				th = th + "<th>" + colName + "</th>";
				if (isGetColumns) {
					if (colName.toUpperCase().equals("NAME"))
						name_index = i;
					if (colName.toUpperCase().equals("COLUMNS"))
						col_index = i;
				}
			}
			int row = 0;
			while (rsD.next()) {
				row++;
				String td = "";
				String columns = "";
				for (int i = 1; i <= cols; i++) {
					String value = rsD.getString(i);
					if (value == null)
						value = "";
					if (i == name_index) {
						columns = getColumnsHtml(object, name, value);
						name_index = -1;
					}
					if ((i == col_index) && !columns.isEmpty()) {
						value = columns;
						col_index = -1;
						columns = "";
					}
					td = td + "<td>" + value + "</td>";
				}
				if (row % 2 == 0)
					tr = tr + "<tr class='alt'>" + td + "</tr>";
				else
					tr = tr + "<tr>" + td + "</tr>";
			}
			if (!tr.isEmpty())
				doc = "<h3>" + header + "</h3><table id='smTable'><tbody><tr class='smth'>" + th + "</tr>" + tr + "</tbody></table><hr>";
		}
		return doc;
	}

	private String getColumnsHtml(String action, String name, String otherName) throws SQLException {
		String columns = "";
		String query = "";
		if (action.equals(SqlConstants.TABLE_CON_OBJ)) {
			if (dbDocRes.getSqlMap().containsKey(SqlConstants.TABLE_CON_COL_OBJ))
				query = (String) dbDocRes.getSqlMap().get(SqlConstants.TABLE_CON_COL_OBJ);
		} else if (action.equals(SqlConstants.TABLE_IND_OBJ)) {
			if (dbDocRes.getSqlMap().containsKey(SqlConstants.TABLE_IND_COL_OBJ))
				query = (String) dbDocRes.getSqlMap().get(SqlConstants.TABLE_IND_COL_OBJ);
		}

		if (!query.isEmpty()) {
			query = query.replace(SqlConstants.NAME_PARAM, "'" + name + "'");
			query = query.replace(SqlConstants.OWNER_PARAM, "'" + getOwner() + "'");
			query = query.replace(SqlConstants.OTHER_NAME_PARAM, "'" + otherName + "'");
			ResultSet rs = stmtTmp.executeQuery(query);
			while (rs.next()) {
				if (columns.isEmpty())
					columns = columns + rs.getString(1);
				else
					columns = columns + "<br>" + rs.getString(1);
			}
		}
		return columns;
	}

	private String filterToSql() {
		if (getFilter().length() > 0) {
			String like = "";
			String or = "";
			String[] split = getFilter().toUpperCase().split(",");
			for (int i = 0; i < split.length; i++) {
				if (i > 0)
					or = " OR ";
				like = like + or + "( object_name LIKE '%" + split[i].trim() + "%' )";
			}
			return "AND (" + like + ")";
		} else
			return "";
	}

	public String getObjects() {
		return objects;
	}

	private void setObjects(String property) {
		if (property == null || property.trim().isEmpty()) {
			property = "";
		}
		objects = property;
	}

	public String getFilter() {
		return filter;
	}

	private void setOwner(String property) {
		if (property == null || property.trim().isEmpty()) {
			property = "";
		}
		owner = property.toUpperCase();
	}

	private String getOwner() {
		return owner;
	}

	private void setFilter(String property) {
		if (property == null || property.trim().isEmpty()) {
			property = "";
		}
		filter = property;
	}

	public int getViewId(String property) {
		if (viewId == 2 && property.equals(SqlConstants.PACKAGE_OBJ))
			return viewId;
		else
			return 1;
	}

	private void setViewId(String property) {
		if (property == null || property.trim().isEmpty()) {
			property = "2";
		}
		viewId = Integer.valueOf(property.trim()).intValue();
	}

	public boolean isAllCommentsAsDoc() {
		return allCommentsAsDoc;
	}

	private void setAllCommentsAsDoc(String property) {
		if (property == null || property.trim().isEmpty()) {
			property = "0";
		}
		if (property.trim().equals("1"))
			allCommentsAsDoc = true;
		else
			allCommentsAsDoc = Boolean.valueOf(property).booleanValue();;
	}

}
