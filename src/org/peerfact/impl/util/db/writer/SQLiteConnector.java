/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.peerfact.impl.util.db.writer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.Constants;


/**
 * The Connector for the SQLite database. For a near description see
 * {@link DBMSConnector}.
 * 
 * @author Christoph Muenker
 * @version 02/28/2011
 * 
 */
public class SQLiteConnector implements IDBMSConnector {

	/**
	 * Logger for this class
	 */
	static final Logger logger = Logger.getLogger(SQLiteConnector.class);

	/**
	 * A connection to the database. It will be set on the
	 * {@link SQLiteConnector#getDBConnection(String)}. It is used for all
	 * operations on the DB.
	 */
	private Connection conn;

	@Override
	public Connection getDBConnection(String databaseName) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:./"
					+ Constants.OUTPUTS_DIR + "/" + databaseName + ".db");
			return conn;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassNotFoundException", e);
		}
	}

	@Override
	public boolean existsTable(String tableName) {
		String existsTableSQLQuery = "SELECT name FROM sqlite_master WHERE name='"
				+ tableName + "'";

		try {

			PreparedStatement stat = conn.prepareStatement(existsTableSQLQuery);

			ResultSet rs = stat.executeQuery();
			boolean result = rs.next();
			stat.close();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		}
	}

	@Override
	public void createTable(String tableName) {
		logger.info("create Table");
		String createTableSQL = "CREATE TABLE if not exists  " + tableName
				+ " (" + "id INT AUTO_INCREMENT," + "hostID BIGINT NOT NULL,"
				+ "time BIGINT NOT NULL," + "metric STRING NOT NULL," + "value"
				+ ", PRIMARY KEY (id)); ";

		executeUpdate(createTableSQL);

		logger.warn("create index for table");
		String indexSQL = "CREATE INDEX i1_" + tableName + " ON " + tableName
				+ "(hostID, metric);";

		executeUpdate(indexSQL);

		indexSQL = "CREATE INDEX i2_" + tableName + " ON " + tableName
				+ "(hostID, time, metric);";

		executeUpdate(indexSQL);

		indexSQL = "CREATE INDEX i3_" + tableName + " ON " + tableName
				+ "(metric, time);";

		executeUpdate(indexSQL);

		logger.warn("created table: " + tableName);
	}

	@Override
	public List<String> getColumns(String tableName) {
		List<String> columns = new Vector<String>();

		String columnsSQLQuery = "PRAGMA table_info(statistics);";

		try {
			PreparedStatement stat = conn.prepareStatement(columnsSQLQuery);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				columns.add(rs.getString(2));
			}

			stat.close();

			return columns;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		}
	}

	@Override
	public void executeUpdate(String sqlStatement) {
		Statement stat = null;
		try {
			stat = conn.createStatement();
			stat.executeUpdate(sqlStatement);
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}
	}

	@Override
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
			conn = null;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		}
	}
}
