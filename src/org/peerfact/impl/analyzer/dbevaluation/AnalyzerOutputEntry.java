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
 */
package org.peerfact.impl.analyzer.dbevaluation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This class is a container for the output entry. The output entry is a
 * container for one metric. It has the following columns:<br>
 * <ul>
 * <li>hostID - An unique identification for one host. If it is a general
 * metric, then use the {@link AnalyzerOutputEntry#GENERAL_METRIC_ID} value.</li>
 * <li>time - simulation time for the measure of the metric</li>
 * <li>metric - An unique identifier for the metric</li>
 * <li>value - The value to this metric</li>
 * </ul>
 * 
 * 
 * @author Christoph Muenker
 * @version 1.0, 03/13/2011
 */
public class AnalyzerOutputEntry {

	/**
	 * HostID for the general metrics (not for one host)
	 */
	public final static long GENERAL_METRIC_ID = -1;

	/**
	 * The column name for host id
	 */
	private final static String HOST_ID_COLUMN_NAME = "hostID";

	/**
	 * The column name for simulation time
	 */
	private final static String TIME_COLUMN_NAME = "time";

	/**
	 * The column name for metric description
	 */
	private final static String METRIC_COLUMN_NAME = "metric";

	/**
	 * The column name for value of the metric
	 */
	private final static String VALUE_COLUMN_NAME = "value";

	/**
	 * Data structure, which store the value for a column.
	 */
	private Map<String, Object> keyValue;

	/**
	 * This is the constructor for rows, which are measure general metrics. It
	 * stores the given parameter in this container, with a predefined
	 * columnName. Additionally it sets the hostID as
	 * {@link AnalyzerOutputEntry#GENERAL_METRIC_ID}.
	 * 
	 * @param experimentName
	 *            The experiment name of the experiment.
	 * @param time
	 *            The simulation time.
	 * @param metric
	 *            The metric description for the metric, which should be stored.
	 * @param value
	 *            The value to the metric.
	 */
	public AnalyzerOutputEntry(long time, String metric, Object value) {
		this.keyValue = new LinkedHashMap<String, Object>();
		keyValue.put(TIME_COLUMN_NAME, time);
		keyValue.put(HOST_ID_COLUMN_NAME, GENERAL_METRIC_ID);
		keyValue.put(METRIC_COLUMN_NAME, metric);
		keyValue.put(VALUE_COLUMN_NAME, value);
	}

	/**
	 * This is the constructor for rows, which are measure a metric for an host.
	 * It stores the given parameter in this container, with a predefined
	 * columnName.
	 * 
	 * 
	 * @param experimentName
	 *            The experiment name of the experiment.
	 * @param time
	 *            The simulation time.
	 * @param metric
	 *            The metric description for the metric, which should be stored.
	 * @param value
	 *            The value to the metric.
	 */
	public AnalyzerOutputEntry(long hostID, long time, String metric,
			Object value) {
		this.keyValue = new LinkedHashMap<String, Object>();
		keyValue.put(TIME_COLUMN_NAME, time);
		keyValue.put(HOST_ID_COLUMN_NAME, hostID);
		keyValue.put(METRIC_COLUMN_NAME, metric);
		keyValue.put(VALUE_COLUMN_NAME, value);
	}

	/**
	 * Gets a map with columName and the associated value for the column back.
	 * The map describes an entry for a row.
	 * 
	 * @return A map with columnName as key and the associated value as value.
	 */
	public Map<String, Object> getEntry() {
		return keyValue;
	}
}
