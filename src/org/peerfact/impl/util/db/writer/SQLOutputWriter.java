package org.peerfact.impl.util.db.writer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.dbevaluation.AnalyzerOutputEntry;
import org.peerfact.impl.analyzer.dbevaluation.IAnalyzerOutputWriter;
import org.peerfact.impl.util.logging.SimLogger;


//TODO: class comment is not up to date
/**
 * This class provides the function to write out in a database. For that, it
 * exists the following table scheme: <br>
 * <ul>
 * <li>id: a unique ID for every entry (database create this unique ID)</li>
 * <li>hostID: An unique identifier of a host for a run. You can it set to -1,
 * if you measure a metric for the whole network</li>
 * <li>time: The simulationTime, for this sample.</li>
 * <li>metric: A description of the metric, that is logged out.</li>
 * <li>value: The value, that is associated to the metric.</li>
 * </ul>
 * 
 * <br>
 * One row in the table corresponds to one measure of a metric! <br>
 * 
 * <p>
 * To set the SQLDBWriter in the XML-Configuration, you must add this line to
 * your Analyzer (assumption: it exists a setter in your Analyzer for an
 * IAnalyzerOutputWriter):<br>
 * <code>			<IAnalyzerOutputWriter class="org.peerfact.impl.util.dbWriter.SQLOutputWriter"
            	rootName="databaseName">
            	<DBMSConnector class="org.peerfact.impl.util.dbWriter.MySQLConnector"/>
            	</IAnalyzerOutputWriter>  </code> <br>
 * 
 * You can change the name of the database. The DBMSConnector is an example.
 * 
 * You can create new tables with initialize(...). You can call this method more
 * than one time, to create new tables.
 * 
 * 
 * @author Christoph Muenker
 * @version 03/13/2011
 * 
 */
public class SQLOutputWriter implements IAnalyzerOutputWriter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = SimLogger
			.getLogger(SQLOutputWriter.class);

	/**
	 * The table name
	 */
	private String databaseName;

	/**
	 * The names of created tables
	 */
	private List<String> dataEntityNames;

	/**
	 * The DBMSConnector
	 */
	private IDBMSConnector dbmsConnector;

	/**
	 * A connection to the database.
	 */
	private Connection conn;

	/**
	 * A counter of inserts. It is used to execute a commit to the database.
	 */
	private int counter;

	private boolean accessable;

	private static SQLOutputWriter instance = null;

	private Map<String, List<Map<String, Object>>> batch = new LinkedHashMap<String, List<Map<String, Object>>>();

	/**
	 * The number of inserts before commit to the DBMS
	 */
	private static final int BATCH_SIZE = 100000;

	private SQLOutputWriter() {
		logger.warn("Called constructor");
		this.dataEntityNames = new Vector<String>();
		this.counter = 0;
		this.accessable = false;
	}

	public static SQLOutputWriter getInstance() {
		if (instance == null) {
			instance = new SQLOutputWriter();
		}
		return instance;
	}

	/**
	 * Sets in this case the databaseName. The databaseName is folderName +
	 * actually real time in millis
	 */
	@Override
	public void setRootName(String folderName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");

		this.databaseName = folderName + "_" + sdf.format(new Date());
		// this.databaseName = folderName; COmmented out because of: Double
		// Assignment
		logger.warn("Set name of the DB to " + this.databaseName);
	}

	/**
	 * Sets the DBMSConnector
	 * 
	 * @param dbmsConnector
	 *            An instance of the DBMSConnector
	 */
	public void setDBMSConnector(IDBMSConnector dbmsConnector) {
		logger.warn("Set the DBMSConntector " + dbmsConnector);
		this.dbmsConnector = dbmsConnector;
	}

	@Override
	public void persist(String entityName, AnalyzerOutputEntry entry) {
		if (accessable) {
			addBatch(entityName, entry.getEntry());
			counter++;
			if (counter % BATCH_SIZE == 0) {
				writeBatch();
				counter = 0;
			}
		}
	}

	@Override
	public void initialize(String[] entityNames) {
		for (String entityName : entityNames) {
			initialize(entityName);
		}
	}

	@Override
	public void initialize(String dataEntityName) {
		logger.warn("Initializing database " + dataEntityName);
		if (conn == null) {
			initializeConnection(databaseName);
		}

		if (dataEntityName != null) {

			if (!dbmsConnector.existsTable(dataEntityName)) {
				dbmsConnector.createTable(dataEntityName);
			}
			if (!this.dataEntityNames.contains(dataEntityName)) {
				this.dataEntityNames.add(dataEntityName);
			}
		}
	}

	/**
	 * Initialize the connection to the database with the given database name.
	 * 
	 * @param databasename
	 *            The name of the database.
	 */
	private void initializeConnection(String databasename) {
		logger.warn("Initialize connection to DB " + databasename);
		try {
			this.conn = dbmsConnector.getDBConnection(databasename);
			this.conn.setAutoCommit(false);
			accessable = true;
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		}
	}

	/**
	 * Delete all tables, which are write to the database in this session.
	 */
	@Override
	public void reset() {
		resetBatch();
		for (String dataEntity : dataEntityNames) {
			deleteTable(dataEntity);
		}
	}

	/**
	 * Delete the uncompleted transaction from the database.
	 */
	private void resetBatch() {
		if (conn != null) {
			try {
				batch.clear();
				conn.rollback();
			} catch (SQLException e) {
				throw new RuntimeException("SQLException", e);
			}
		}
	}

	/**
	 * Delete the given table from the database.
	 * 
	 * @param tableName
	 *            The name of the table
	 */
	private void deleteTable(String tableName) {
		String deleteSQL = "DROP TABLE " + tableName;
		dbmsConnector.executeUpdate(deleteSQL);

	}

	@Override
	public void flush() {
		if (conn == null) {
			logger.warn("Cannot flush. It exists no connection to the DB!");
			return;
		}
		try {
			this.writeBatch();
			this.conn.commit();
		} catch (SQLException e) {
			throw new RuntimeException("SQLException", e);
		}

	}

	@Override
	public void close() {
		flush();
		dbmsConnector.closeConnection();
		conn = null;
		accessable = false;
		logger.warn("Closed connection to DB");
	}

	/**
	 * Add data to the batch.
	 * 
	 * @param entityName
	 *            The table name in the database
	 * @param entry
	 *            The map with columnnames and the associated values.
	 */
	private void addBatch(String entityName, Map<String, Object> entry) {
		List<Map<String, Object>> batchListForTable = batch.get(entityName);
		if (batchListForTable == null) {
			batchListForTable = new Vector<Map<String, Object>>();
			batch.put(entityName, batchListForTable);
		}
		batchListForTable.add(entry);
	}

	/**
	 * Write the batch into the database.
	 * 
	 */
	private void writeBatch() {
		try {

			for (String tableName : batch.keySet()) {
				List<Map<String, Object>> batchListForTable = batch
						.get(tableName);
				if (batchListForTable == null || batchListForTable.size() == 0) {
					continue;
				}
				Set<String> columnSet = batchListForTable.get(0).keySet();

				// create statement for adding to the table
				StringBuffer buf = new StringBuffer();
				buf.append("INSERT INTO " + tableName + " (");
				Iterator<String> iter = columnSet.iterator();

				for (int i = 0; i < columnSet.size() - 1; i++) {
					buf.append(iter.next() + ", ");
				}
				buf.append(iter.next() + ") VALUES (");

				for (int i = 0; i < columnSet.size() - 1; i++) {
					buf.append("?, ");
				}
				buf.append("?)");

				PreparedStatement prep = conn.prepareStatement(buf.toString());
				int j = 0;
				for (Map<String, Object> entry : batchListForTable) {
					iter = columnSet.iterator();
					int i = 0;
					while (iter.hasNext()) {

						Object o = entry.get(iter.next());

						if (o instanceof Double) {
							Double d = (Double) o;
							if (d < 0.001d || d.isNaN() || d.isInfinite()) {
								prep.setString(i + 1, d.toString());
							} else {
								prep.setDouble(i + 1, d);
							}
						} else if (o instanceof Integer) {
							prep.setInt(i + 1, (Integer) o);
						} else if (o instanceof Long) {
							prep.setLong(i + 1, (Long) o);
						} else if (o instanceof String) {
							prep.setString(i + 1, (String) o);
						} else if (o == null) {
							prep.setNull(i + 1, java.sql.Types.BLOB);
						} else {
							throw new RuntimeException(
									"Unsupported object type in insertRow: "
											+ o.getClass());
						}
						i++;
					}
					prep.addBatch();
					j++;
					if (j % 1000 == 0) {
						prep.executeBatch();
						j = 0;
						prep.clearBatch();
					}
				}
				prep.executeBatch();

				prep.close();
			}

			// delete batch
			batch.clear();
			conn.commit();
		} catch (SQLException e) {
			if (e.getMessage() != null
					&& e.getMessage().endsWith("are not unique")) {
				logger.error(
						"The Primary Key is not unique! Please change the \"experiment\" name or the \"run\" identifiere",
						e);
			}
			throw new RuntimeException("SQLException", e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		flush();
		close();
		super.finalize();
	}

}
