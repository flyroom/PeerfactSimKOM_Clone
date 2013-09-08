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
import java.sql.SQLException;
import java.util.List;

/**
 * This interface describes a additive interface to the JDBC interface. In many
 * JDBC are not implemented many functions or the DBMS has a other set of SQL
 * commands. Hence, this interface provides the missed functionality.
 * 
 * @author Christoph Muenker
 * @version 02/27/2011
 * 
 */
public interface IDBMSConnector {
	/**
	 * Gets a connection to the database, with the given database name. If the
	 * database not exists, the database will be created.
	 * 
	 * @param databaseName
	 *            The database name, for that the connection should be
	 *            established.
	 * @return A connection to the database.
	 * @throws SQLException
	 */
	public Connection getDBConnection(String databaseName) throws SQLException;

	/**
	 * Check the existence of a table.
	 * 
	 * @param tableName
	 *            The table name.
	 * @return <code>true</code> if the table exists, otherwise
	 *         <code>false</code>.
	 */
	public boolean existsTable(String tableName);

	/**
	 * Create a table with the given table name. The schema of the table is the
	 * following:<br>
	 * <ul>
	 * <li>id: a unique ID for every entry (integer)</li>
	 * <li>hostID: An unique identifier of a host for a run. (integer)</li>
	 * <li>time: The simulationTime (BigInt)</li>
	 * <li>metric: A description of the metric, that is logged out. (String)</li>
	 * <li>value: The value, that is associated to the metric. (BLOB)</li>
	 * </ul>
	 * The id is the primary key.
	 * 
	 * @param tableName
	 *            The table name of the table, which should be created.
	 */
	public void createTable(String tableName);

	/**
	 * Abstracts the handling of SQL update commands. It executes the SQL update
	 * command and does the handling of the Exceptions.
	 * 
	 * @param sqlStatement
	 *            The sql update statement.
	 */
	public void executeUpdate(String sqlStatement);

	/**
	 * Gets the column names of the existing table back.
	 * 
	 * @param tableName
	 *            The table name
	 * 
	 * @return A list of column names of the table with the given table name.
	 */
	public List<String> getColumns(String tableName);

	/**
	 * Close the connection to the database.
	 */
	public void closeConnection();
}
