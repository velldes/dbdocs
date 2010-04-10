package info.vstour.dbdoc.server;

import info.vstour.dbdoc.client.DocService;
import info.vstour.dbdoc.shared.Converter;
import info.vstour.dbdoc.shared.PropsConstants;
import info.vstour.dbdoc.shared.SqlConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DocServiceImpl extends RemoteServiceServlet implements DocService {

	public List<String> getTreeItems(String connName, String itemName, String search) {
		List<String> items = new ArrayList<String>();
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = ConnectionManager.get(connName).getConnection();
			if (connection != null) {
				stmt = connection.createStatement();
				String query = DbDocRes.get(connName).getSqlMap().get(SqlConstants.OBJECTS);
				query = query.replace(SqlConstants.OBJECT_PARAM, "'" + itemName + "'");
				query = query.replace(SqlConstants.NAME_TOKEN, filterToSql(search));
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					items.add(rs.getString(1).toLowerCase());
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return items;
	}
	public String getDoc(String connName, String parent, String child) {
		String doc = "";
		parent = parent.toUpperCase();
		child = child.toUpperCase();
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = ConnectionManager.get(connName).getConnection();
			if (connection != null) {
				stmt = connection.createStatement();
				String query = DbDocRes.get(connName).getSqlMap().get(parent.toUpperCase());
				query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
				doc = getHtml(stmt, parent, child, query);
				if (parent.equals(SqlConstants.TABLE_OBJ)) {
					if (DbDocRes.get(connName).getSqlMap().containsKey(SqlConstants.TABLE_COL_OBJ)) {
						query = (String) DbDocRes.get(connName).getSqlMap().get(SqlConstants.TABLE_COL_OBJ);
						query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
						doc = doc + getHtml(stmt, SqlConstants.TABLE_COL_OBJ, child, query);
					}
					if (DbDocRes.get(connName).getSqlMap().containsKey(SqlConstants.TABLE_CON_OBJ)) {
						query = (String) DbDocRes.get(connName).getSqlMap().get(SqlConstants.TABLE_CON_OBJ);
						query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
						doc = doc + getHtml(stmt, SqlConstants.TABLE_CON_OBJ, child, query);
					}
					if (DbDocRes.get(connName).getSqlMap().containsKey(SqlConstants.TABLE_IND_OBJ)) {
						query = (String) DbDocRes.get(connName).getSqlMap().get(SqlConstants.TABLE_IND_OBJ);
						query = query.replace(SqlConstants.NAME_PARAM, "'" + child + "'");
						doc = doc + getHtml(stmt, SqlConstants.TABLE_IND_OBJ, child, query);
					}
				}
				doc = "<h3>" + child + "</h3>" + doc;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				stmt.close();
				connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return doc;
	}
	@Override
	public String[] getObjects(String name) {
		String objects = "";
		try {
			objects = DbDocRes.get(name).getProps().getProperty(PropsConstants.OBJECTS);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objects.split(",");
	}

	@Override
	public String[] getPropsList() {
		String[] props = null;
		try {
			URL dir = new URL(DbDocRes.get("").BASE_URL + DbDocRes.get("").PROPS_RES);
			props = DbDocRes.get("").getList(new File(dir.toURI()), ".*");
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}

	private String getHtml(Statement stmt, String object, String name, String query) throws Exception {
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
		String acad = DbDocRes.get(DbDocRes.propsFileName).getProps().getProperty(PropsConstants.ALL_COMMENTS_AS_DOC);
		boolean isAllCommentsAsDoc = Boolean.valueOf(acad).booleanValue();
		if (cols == 1) {
			Converter.init();
			while (rsD.next()) {
				if (isAllCommentsAsDoc)
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
			Statement stmtCol = ConnectionManager.get(ConnectionManager.propsFileName).getConnection().createStatement();
			while (rsD.next()) {
				row++;
				String td = "";
				String columns = "";
				for (int i = 1; i <= cols; i++) {
					String value = rsD.getString(i);
					if (value == null)
						value = "";
					if (i == name_index) {
						columns = getColumnsHtml(stmtCol, object, name, value);
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
				doc = "<h3>" + header + "</h3><table id='smTable'><tbody><tr class='smth'>" + th + "</tr>" + tr
				        + "</tbody></table><hr>";
		}
		return doc;
	}

	private String getColumnsHtml(Statement stmtCol, String action, String name, String otherName) throws Exception {
		String columns = "";
		String query = "";
		if (action.equals(SqlConstants.TABLE_CON_OBJ)) {
			if (DbDocRes.get(DbDocRes.propsFileName).getSqlMap().containsKey(SqlConstants.TABLE_CON_COL_OBJ))
				query = (String) DbDocRes.get(DbDocRes.propsFileName).getSqlMap().get(SqlConstants.TABLE_CON_COL_OBJ);
		} else if (action.equals(SqlConstants.TABLE_IND_OBJ)) {
			if (DbDocRes.get(DbDocRes.propsFileName).getSqlMap().containsKey(SqlConstants.TABLE_IND_COL_OBJ))
				query = (String) DbDocRes.get(DbDocRes.propsFileName).getSqlMap().get(SqlConstants.TABLE_IND_COL_OBJ);
		}

		if (!query.isEmpty()) {
			query = query.replace(SqlConstants.NAME_PARAM, "'" + name + "'");
			query = query.replace(SqlConstants.OTHER_NAME_PARAM, "'" + otherName + "'");
			ResultSet rs = stmtCol.executeQuery(query);
			while (rs.next()) {
				if (columns.isEmpty())
					columns = columns + rs.getString(1);
				else
					columns = columns + "<br>" + rs.getString(1);
			}
		}
		return columns;
	}

	private String filterToSql(String filter) {
		if (filter.length() > 0) {
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

	public int getViewId(String property) {
		String view;
		try {
			view = DbDocRes.get(DbDocRes.propsFileName).getProps().getProperty(PropsConstants.VIEW).trim();
		}
		catch (Exception e) {
			view = "2";
		}
		int viewId = Integer.valueOf(view).intValue();
		if (viewId == 2 && property.equals(SqlConstants.PACKAGE_OBJ))
			return viewId;
		else
			return 1;
	}

}
