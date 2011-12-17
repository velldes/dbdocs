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

import info.vstour.dbdoc.client.DocService;
import info.vstour.dbdoc.shared.Converter;
import info.vstour.dbdoc.shared.PropsConstants;
import info.vstour.dbdoc.shared.SqlConstants;
import info.vstour.dbdoc.shared.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DocServiceImpl extends RemoteServiceServlet implements DocService {

  public List<String> getTreeItems(String connName, String owner, String itemName, String search) {
    List<String> items = new ArrayList<String>();
    Connection connection = null;
    try {
      connection = ConnectionManager.get(connName).getConnection();
    }
    catch (ClassNotFoundException e) {
      System.err.println("Class Not Found while getting Connection");
      e.printStackTrace();
    }
    catch (SQLException e) {
      System.err.println("SQL Error while getting Connection");
      e.printStackTrace();
    }
    Statement stmt = null;
    if (connection != null) {
      try {
        stmt = connection.createStatement();
      }
      catch (SQLException e) {
        System.err.println("DocServiceImpl.getTreeItems: SQL, Create Statement");
        e.printStackTrace();
      }
    }
    if (stmt != null) {
      try {
        owner = owner.toUpperCase();
        String query = DbDocRes.get(connName).getSqlMap().get(SqlConstants.OBJECTS);
        query = query.replace(SqlConstants.OBJECT_PARAM, "'" + itemName + "'");
        query = query.replace(SqlConstants.OWNER_PARAM, "'" + owner + "'");
        query = query.replace(SqlConstants.NAME_TOKEN, filterToSql(search));
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
          items.add(rs.getString(1).toLowerCase());
        }
      }
      catch (SQLException ex) {
        System.err.println("DocServiceImpl.getTreeItems: SQL, Execute Query");
        ex.printStackTrace();
      }
      finally {
        try {
          stmt.close();
          connection.close();
        }
        catch (SQLException e) {
          System.err.println("DocServiceImpl.getTreeItems: SQL, close");
          e.printStackTrace();
        }
      }
    }
    return items;
  }
  public String getDoc(final String connName, String owner, String parent, String child) {
    String doc = "";
    parent = parent.toUpperCase();
    child = child.toUpperCase();
    Connection connection = null;
    Statement stmt = null;
    try {
      connection = ConnectionManager.get(connName).getConnection();
    }
    catch (ClassNotFoundException e) {
      System.err.println("Class Not Found while getting Connection");
      e.printStackTrace();
    }
    catch (SQLException e) {
      System.err.println("SQL Error while getting Connection");
      e.printStackTrace();
    }
    if (connection != null) {
      try {
        stmt = connection.createStatement();
      }
      catch (SQLException e) {
        System.err.println("DocServiceImpl.getDoc: SQL, Create Statement");
        e.printStackTrace();
      }
    }
    if (stmt != null) {
      try {
        owner = owner.toUpperCase();
        stmt = connection.createStatement();
        Map<String, String> sqlMap = DbDocRes.get(connName).getSqlMap();

        String query = sqlMap.get(parent.toUpperCase());
        query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
        query = query.replace(SqlConstants.OWNER_PARAM, "'" + owner + "'");
        doc = getHtml(connName, owner, stmt, parent, child, query);
        if (parent.equals(SqlConstants.TABLE_OBJ)) {
          if (sqlMap.containsKey(SqlConstants.TABLE_COL_OBJ)) {
            query = sqlMap.get(SqlConstants.TABLE_COL_OBJ);
            query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
            query = query.replace(SqlConstants.OWNER_PARAM, "'" + owner + "'");
            doc = doc + getHtml(connName, owner, stmt, SqlConstants.TABLE_COL_OBJ, child, query);
          }
          if (sqlMap.containsKey(SqlConstants.TABLE_CON_OBJ)) {
            query = sqlMap.get(SqlConstants.TABLE_CON_OBJ);
            query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
            query = query.replace(SqlConstants.OWNER_PARAM, "'" + owner + "'");
            doc = doc + getHtml(connName, owner, stmt, SqlConstants.TABLE_CON_OBJ, child, query);
          }
          if (sqlMap.containsKey(SqlConstants.TABLE_IND_OBJ)) {
            query = sqlMap.get(SqlConstants.TABLE_IND_OBJ);
            query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
            query = query.replace(SqlConstants.OWNER_PARAM, "'" + owner + "'");
            doc = doc + getHtml(connName, owner, stmt, SqlConstants.TABLE_IND_OBJ, child, query);
          }
        }
        doc = "<h3>" + child + "</h3>" + doc;
      }
      catch (SQLException ex) {
        System.err.println("DocServiceImpl.getDoc");
        ex.printStackTrace();
      }
      finally {
        try {
          stmt.close();
          connection.close();
        }
        catch (SQLException e) {
          System.err.println("DocServiceImpl.getDoc: SQL, close");
          e.printStackTrace();
        }
      }
    }
    return doc + Converter.DBDOC_LINK;
  }

  @Override
  public String[] getOwners(String connName) {
    String owners = "";
    Properties props = DbDocRes.get(connName).getProps();

    if (props == null) {
      System.err.println("DocServiceImpl.getOwners: Resource Properties is null");
    } else {
      owners = props.getProperty(PropsConstants.OWNER);
      if (Utils.isEmpty(owners)) {
        owners = props.getProperty(PropsConstants.USER);
      }
    }
    return owners.split(",");
  }

  @Override
  public String[] getObjects(String connName) {
    String objects = "";
    Properties props = DbDocRes.get(connName).getProps();
    if (props == null) {
      System.err.println("DocServiceImpl.getObjects: Resource Properties is null");
    } else {
      objects = props.getProperty(PropsConstants.OBJECTS);
    }
    return objects.split(",");
  }
  @Override
  public String[] getPropsList() {
    String[] props = null;
    URL dir = null;
    try {
      dir = new URL(DbDocRes.get("").BASE_URL + DbDocRes.get("").PROPS_RES);
      props = DbDocRes.get("").getList(new File(dir.toURI()), ".*properties");
    }
    catch (MalformedURLException e) {
      System.err.println("Malformed URL");
      e.printStackTrace();
    }
    catch (URISyntaxException e) {
      System.err.println("URI Syntax");
      e.printStackTrace();
    }
    return props;
  }

  private String getHtml(final String connName, final String owner, Statement stmt, String object, String name, String query)
      throws SQLException {
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

    ResultSet rsD = stmt.executeQuery(query);
    ResultSetMetaData rsDmd = rsD.getMetaData();
    int cols = rsDmd.getColumnCount();
    String doc = "";
    Properties props = DbDocRes.get(connName).getProps();
    String acad = props.getProperty(PropsConstants.ALL_COMMENTS_AS_DOC);
    if (acad.equals("1"))
      acad = "true";
    boolean isAllCommentsAsDoc = Boolean.valueOf(acad).booleanValue();
    if (cols == 1) {
      Converter.init();
      while (rsD.next()) {
        if (isAllCommentsAsDoc)
          doc = doc + Converter.markUpComments(rsD.getString(1));
        else
          doc = doc + Converter.markUp(rsD.getString(1));
      }
      String defaultViewId = props.getProperty(PropsConstants.VIEW);
      doc = Converter.textToHtml(doc, getViewId(object, defaultViewId), "");
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
      Statement stmtCol = null;
      try {
        stmtCol = ConnectionManager.get(connName).getConnection().createStatement();
      }
      catch (ClassNotFoundException e) {
        System.err.println("Class Not Found while getting Connection");
        e.printStackTrace();
      }
      while (rsD.next()) {
        row++;
        String td = "";
        String columns = "";
        for (int i = 1; i <= cols; i++) {
          String value = rsD.getString(i);
          if (value == null)
            value = "";
          if (i == name_index) {
            columns = getColumnsHtml(connName, owner, stmtCol, object, name, value);
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
      stmtCol.close();

      if (!tr.isEmpty())
        doc = "<h3>" + header + "</h3><table id='smTable'><tbody><tr class='smth'>" + th + "</tr>" + tr + "</tbody></table><hr>";
    }
    return doc;
  }

  private String getColumnsHtml(final String connName, final String owner, Statement stmtCol, String action, String name,
      String otherName) throws SQLException {
    String columns = "";
    if (stmtCol != null) {
      String query = "";
      Map<String, String> sqlMap = DbDocRes.get(connName).getSqlMap();
      if (action.equals(SqlConstants.TABLE_CON_OBJ)) {
        query = Utils.getMapValue(SqlConstants.TABLE_CON_COL_OBJ, sqlMap);
      } else if (action.equals(SqlConstants.TABLE_IND_OBJ)) {
        query = Utils.getMapValue(SqlConstants.TABLE_IND_COL_OBJ, sqlMap);
      }

      if (!query.isEmpty()) {
        query = query.replace(SqlConstants.NAME_PARAM, "'" + name + "'");
        query = query.replace(SqlConstants.OWNER_PARAM, "'" + owner + "'");
        query = query.replace(SqlConstants.OTHER_NAME_PARAM, "'" + otherName + "'");
        ResultSet rs = stmtCol.executeQuery(query);
        while (rs.next()) {
          if (columns.isEmpty())
            columns = columns + rs.getString(1);
          else
            columns = columns + "<br>" + rs.getString(1);
        }
      }
    }
    return columns;
  }

  private String filterToSql(String filter) {
    if (filter != null && filter.length() > 0) {
      String like = "";
      String or = "";
      String[] split = filter.toUpperCase().split(",");
      for (int i = 0; i < split.length; i++) {
        if (i > 0)
          or = " OR ";
        like = like + or + "( object_name LIKE '%" + split[i].trim() + "%' )";
      }
      return "AND (" + like + ")";
    } else
      return "";
  }

  public int getViewId(String property, String defaultViewId) {
    int viewId = Integer.valueOf(defaultViewId.trim()).intValue();
    if (viewId == 2 && property.equals(SqlConstants.PACKAGE_OBJ))
      return viewId;
    else
      return 1;
  }
}
