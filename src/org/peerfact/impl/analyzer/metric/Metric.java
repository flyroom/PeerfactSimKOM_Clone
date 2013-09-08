/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.analyzer.metric;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * A general interface for metrics which collect data in dependency of the time
 * and the peer.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 07/20/2011
 * @param <Peer>
 *            the peer
 * @param <Value>
 *            the value type
 */
public interface Metric<Peer, Value> {

	/**
	 * Returns a constant name that identifies this metric.
	 * 
	 * @return the name of the metric
	 */
	String getName();

	/**
	 * Returns a constant name that represents the unit of this metric.
	 * 
	 * @return the unit of the metric
	 */
	String getUnit();

	/**
	 * Returns headlines for different output values of this metric.
	 * 
	 * @return the headlines of the metric
	 */
	List<String> getHeadlines();

	/**
	 * Generates different time plots for this metric and writes them into
	 * script.
	 * 
	 * @param script
	 *            the output writer
	 * @param analyzer
	 *            where the metric is used
	 * @param startIndex
	 *            the first column number of this metric
	 * @throws IOException
	 */
	void generateTimePlots(Writer script, String analyzer, int startIndex)
			throws IOException;

	/**
	 * Retrieves the current time measurement information.
	 * 
	 * @param currentTime
	 *            the current simulation time
	 * @return the current time measurement
	 */
	List<String> getTimeMeasurementValues(long currentTime);

	/**
	 * Resets the current time measurement information.
	 */
	void resetTimeMeasurement();

	/**
	 * Generates different peer plots for this metric and writes them into
	 * script.
	 * 
	 * @param script
	 *            the output writer
	 * @param analyzer
	 *            where the metric is used
	 * @param startIndex
	 *            the first column number of this metric
	 * @throws IOException
	 */
	void generatePeerPlots(Writer script, String analyzer, int startIndex)
			throws IOException;

	/**
	 * Retrieves the current peer measurement information.
	 * 
	 * @param peer
	 *            the peer
	 * @return the current peer measurement
	 */
	List<String> getPeerMeasurementValues(Object peer);

	/**
	 * Resets the current peer measurement information.
	 */
	void resetPeerMeasurement();
}
