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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.AbstractFileAnalyzer;
import org.peerfact.impl.analyzer.AbstractFileMetricAnalyzer;

/**
 * A metric which provides the sum for all values.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 09/07/2012
 * @param <Peer>
 *            the peer
 * @param <Num>
 *            the value type
 */
public class SumMetric<Peer, Num extends Number> implements Metric<Peer, Num> {

	private String name;

	private String unit;

	protected Map<Peer, Double> peerValues = new LinkedHashMap<Peer, Double>();

	protected double currentSum = 0;

	public SumMetric(String name, String unit) {
		this.name = name;
		this.unit = unit;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUnit() {
		return unit;
	}

	@Override
	public List<String> getHeadlines() {
		return Collections.singletonList(getName());
	}

	@Override
	public void generateTimePlots(Writer script, String analyzer, int startIndex)
			throws IOException {
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " Plot " + getName() + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write("set term png giant size 800,600 font 'Helvetica,20'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_")
				+ AbstractFileMetricAnalyzer.FILE_PNG_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("set key top left" + Constants.LINE_END);
		script.write("set xlabel 'Time [minutes]'"
				+ Constants.LINE_END);
		script.write("set ylabel '" + getName() + " [" + getUnit() + "]'"
				+ Constants.LINE_END);
		script.write("plot '" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + startIndex + " title 'Sum of " + getName()
				+ "' with lines axis x1y1 smooth unique"
				+ Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write("set terminal pdf monochrome font 'Helvetica,10'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_")
				+ AbstractFileMetricAnalyzer.FILE_PDF_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("replot" + Constants.LINE_END);
	}

	@Override
	public List<String> getTimeMeasurementValues(long currentTime) {
		return Collections.singletonList(new Double(currentSum).toString());
	}

	@Override
	public void resetTimeMeasurement() {
		currentSum = 0;
	}

	@Override
	public void generatePeerPlots(Writer script, String analyzer, int startIndex)
			throws IOException {
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " Plot " + getName() + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write("set term png giant size 800,600 font 'Helvetica,20'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_")
				+ AbstractFileMetricAnalyzer.FILE_PNG_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("set key top right" + Constants.LINE_END);
		script.write("set xlabel 'Peer'" + Constants.LINE_END);
		script.write("set ylabel '" + getName() + " [" + getUnit() + "]'"
				+ Constants.LINE_END);
		script.write("plot '" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + startIndex + " title 'Sum of " + getName()
				+ "' with lines axis x1y1 smooth unique"
				+ Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write("set terminal pdf monochrome font 'Helvetica,10'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_")
				+ AbstractFileMetricAnalyzer.FILE_PDF_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("replot" + Constants.LINE_END);
	}

	@Override
	public List<String> getPeerMeasurementValues(Object peer) {
		if (peerValues.containsKey(peer)) {
			return Collections.singletonList(peerValues.get(peer).toString());
		} else {
			return Collections.singletonList("0");
		}
	}

	@Override
	public void resetPeerMeasurement() {
		peerValues.clear();
	}

	public void addValue(Peer peer, Num value) {
		if (!peerValues.containsKey(peer)) {
			peerValues.put(peer, value.doubleValue());
		} else {
			peerValues.put(peer, peerValues.get(peer) + value.doubleValue());
		}
		currentSum += value.doubleValue();
	}

}
