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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * The Connector for the MySQL database. For a near description see
 * {@link IDBMSConnector}.
 * 
 * @author Christoph Muenker (extended by Dominik Stingl)
 * @version 03/09/2011
 * 
 */
public class MySQLConnector implements IDBMSConnector {

	private static Logger logger = SimLogger.getLogger(MySQLConnector.class);

	/**
	 * A connection to the database. It will be set on the
	 * {@link MySQLConnector#getDBConnection(String)}. It is used for all
	 * operations on the DB.
	 */
	private Connection conn;

	/**
	 * User name for the connection to the MySQL database.
	 */
	private String username = "root";

	/**
	 * The password to the associated user, for the connection to the MySQL
	 * database.
	 */
	private String password = "root";

	public MySQLConnector() {
		logger.warn("Called the constructor");
	}

	/**
	 * Sets the user name for the user of the database.
	 * 
	 * @param username
	 *            the user name
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets the password to the associated user for the database.
	 * 
	 * @param password
	 *            the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Connection getDBConnection(String databaseName) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connTemp = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/", username, password);
			Statement st = connTemp.createStatement();

			st.executeUpdate("DROP DATABASE IF EXISTS `" + databaseName);

			st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + databaseName
					+ "` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");

			connTemp.close();

			this.conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + databaseName
							+ "?rewriteBatchedStatements=true", username,
					password);
			return this.conn;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassNotFoundException", e);
		}
	}

	@Override
	public boolean existsTable(String tableName) {

		try {

			ResultSet rs = conn.getMetaData().getTables(null, null, tableName,
					null);
			boolean result = rs.next();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		}
	}

	@Override
	public void createTable(String tableName) {
		String createTableSQL = "CREATE TABLE if not exists  " + tableName
				+ " (" + "id INT AUTO_INCREMENT," + "hostID BIGINT NOT NULL,"
				+ "time BIGINT NOT NULL," + "metric VARCHAR(100) NOT NULL,"
				+ "value BLOB" + ", PRIMARY KEY (id)); ";
		logger.debug("Create Table " + tableName + " with statement "
				+ createTableSQL);
		executeUpdate(createTableSQL);

		// logger.debug("create index for table");
		// String indexSQL = "CREATE INDEX i1 ON " + tableName
		// + "(hostID, metric);";
		//
		// executeUpdate(indexSQL);
		//
		// indexSQL = "CREATE INDEX i2 ON " + tableName
		// + "(hostID, time, metric);";
		//
		// executeUpdate(indexSQL);
		//
		// indexSQL = "CREATE INDEX i3 ON " + tableName + "(metric, time);";
		//
		// executeUpdate(indexSQL);

		logger.warn("created table: " + tableName);
	}

	@Override
	public List<String> getColumns(String tableName) {
		try {
			List<String> result = new Vector<String>();
			ResultSet rs = conn.getMetaData().getColumns(null, null, tableName,
					null);
			while (rs.next()) {
				result.add(rs.getString("COLUMN_NAME"));
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
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

}
