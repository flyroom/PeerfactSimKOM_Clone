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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.AbstractFileAnalyzer;
import org.peerfact.impl.analyzer.AbstractFileMetricAnalyzer;
import org.peerfact.impl.util.stats.StatHelper;

/**
 * A metric which provides all relevant statistic data for the values (count,
 * sum, minimum, maximum, average, standard deviation, median, 90% Quantil, 95%
 * Quantil).
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 09/07/2012
 * @param <Peer>
 *            the peer
 * @param <Num>
 *            the value type
 */
public class StatisticMetric<Peer, Num extends Number & Comparable<Num>>
		implements Metric<Peer, Number> {

	private String name;

	private String unit;

	private Map<Peer, List<Num>> peerValues = new LinkedHashMap<Peer, List<Num>>();

	private List<Num> values = new ArrayList<Num>();

	private StatHelper<Num> statHelper = new StatHelper<Num>();

	public StatisticMetric(String name, String unit) {
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
		List<String> headlines = new ArrayList<String>();
		headlines.add(getName() + " Count");
		headlines.add(getName() + " Sum");
		headlines.add(getName() + " Minimum");
		headlines.add(getName() + " Maximum");
		headlines.add(getName() + " Average");
		headlines.add(getName() + " Standard Deviation");
		headlines.add(getName() + " Minus Standard Deviation");
		headlines.add(getName() + " Plus Standard Deviation");
		headlines.add(getName() + " Median");
		headlines.add(getName() + " 5% Quantil");
		headlines.add(getName() + " 95% Quantil");
		return headlines;
	}

	@Override
	public void generateTimePlots(Writer script, String analyzer, int startIndex)
			throws IOException {
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " Plot " + getName() + " - Average "
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write("set term png giant size 800,600 font 'Helvetica,20'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Average"
				+ AbstractFileMetricAnalyzer.FILE_PNG_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("set key top left" + Constants.LINE_END);
		script.write("set xlabel 'Time [minutes]'"
				+ Constants.LINE_END);
		script.write("set ylabel '" + getName() + " [" + getUnit() + "]'"
				+ Constants.LINE_END);
		script.write("plot '" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:($" + (startIndex + 4) + "+$" + (startIndex + 7)
				+ ")" + " title 'Plus Standard Deviation of " + getName()
				+ "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 4) + " title 'Average of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:($" + (startIndex + 4) + "-$" + (startIndex + 6)
				+ ")" + " title 'Minus Standard Deviation of " + getName()
				+ "' with lines axis x1y1 smooth unique"
				+ Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write("set terminal pdf monochrome font 'Helvetica,10'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Average"
				+ AbstractFileMetricAnalyzer.FILE_PDF_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("replot" + Constants.LINE_END);

		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);

		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " Plot " + getName() + " - Median "
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write("set term png giant size 800,600 font 'Helvetica,20'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Median"
				+ AbstractFileMetricAnalyzer.FILE_PNG_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("set key top left" + Constants.LINE_END);
		script.write("set xlabel 'Time [minutes]'"
				+ Constants.LINE_END);
		script.write("set ylabel '" + getName() + " [" + getUnit() + "]'"
				+ Constants.LINE_END);
		script.write("plot '" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 3) + " title 'Maximum of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 10) + " title '95%-Quantil of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 8) + " title 'Median of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 9) + " title '5%-Quantil of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 2) + " title 'Minimum of "
				+ getName() + "' with lines axis x1y1 smooth unique"
				+ Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write("set terminal pdf monochrome font 'Helvetica,10'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Median"
				+ AbstractFileMetricAnalyzer.FILE_PDF_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("replot" + Constants.LINE_END);
	}

	/**
	 * Generate a result list with all needed statistic values.
	 * 
	 * @param statValues
	 *            a list of values
	 * @return a list of statistic values
	 */
	private List<String> generateStatistics(List<Num> statValues) {
		List<String> result = new ArrayList<String>();
		if (statValues != null && statValues.size() > 0) {
			Integer count = StatHelper.count(statValues);
			if (count != null) {
				result.add(count.toString());
			} else {
				result.add("0");
			}
			Double sum = statHelper.sum(statValues);
			if (sum != null) {
				result.add(sum.toString());
			} else {
				result.add("0");
			}
			Num min = statHelper.min(statValues);
			if (min != null) {
				result.add(min.toString());
			} else {
				result.add("0");
			}
			Num max = statHelper.max(statValues);
			if (max != null) {
				result.add(max.toString());
			} else {
				result.add("0");
			}
			Double[] avgStdDev = statHelper
					.computeAverageAndStandardDeviation(statValues);
			if (avgStdDev[0] != null) {
				result.add(avgStdDev[0].toString());
			} else {
				result.add("0");
			}
			if (avgStdDev[1] != null) {
				result.add(avgStdDev[1].toString());
			} else {
				result.add("0");
			}
			if (avgStdDev[2] != null) {
				result.add(avgStdDev[2].toString());
			} else {
				result.add("0");
			}
			if (avgStdDev[3] != null) {
				result.add(avgStdDev[3].toString());
			} else {
				result.add("0");
			}
			Num median = statHelper.median(statValues);
			if (median != null) {
				result.add(median.toString());
			} else {
				result.add("0");
			}
			Num quantile005 = statHelper.quantile(statValues, 0.05);
			if (quantile005 != null) {
				result.add(quantile005.toString());

			} else {
				result.add("0");
			}
			Num quantile095 = statHelper.quantile(statValues, 0.95);
			if (quantile095 != null) {
				result.add(quantile095.toString());
			} else {
				result.add("0");
			}
		} else {
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
			result.add("0");
		}
		return result;
	}

	@Override
	public List<String> getTimeMeasurementValues(long currentTime) {
		return generateStatistics(values);
	}

	@Override
	public void resetTimeMeasurement() {
		values.clear();
	}

	@Override
	public void generatePeerPlots(Writer script, String analyzer, int startIndex)
			throws IOException {
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " Plot " + getName() + " - Average "
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write("set term png giant size 800,600 font 'Helvetica,20'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Average"
				+ AbstractFileMetricAnalyzer.FILE_PNG_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("set key top right" + Constants.LINE_END);
		script.write("set xlabel 'Peer'" + Constants.LINE_END);
		script.write("set ylabel '" + getName() + " [" + getUnit() + "]'"
				+ Constants.LINE_END);
		script.write("plot '" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:($" + (startIndex + 4) + "+$" + (startIndex + 7)
				+ ")" + " title 'Plus Standard Deviation of " + getName()
				+ "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 4) + " title 'Average of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:($" + (startIndex + 4) + "-$" + (startIndex + 6)
				+ ")" + " title 'Minus Standard Deviation of " + getName()
				+ "' with lines axis x1y1 smooth unique"
				+ Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write("set terminal pdf monochrome font 'Helvetica,10'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Average"
				+ AbstractFileMetricAnalyzer.FILE_PDF_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("replot" + Constants.LINE_END);

		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);

		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " Plot " + getName() + " - Median "
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write("set term png giant size 800,600 font 'Helvetica,20'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Median"
				+ AbstractFileMetricAnalyzer.FILE_PNG_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("set key top left" + Constants.LINE_END);
		script.write("set xlabel 'Peer'" + Constants.LINE_END);
		script.write("set ylabel '" + getName() + " [" + getUnit() + "]'"
				+ Constants.LINE_END);
		script.write("plot '" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 3) + " title 'Maximum of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 10) + " title '95%-Quantil of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 8) + " title 'Median of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 9) + " title '5%-Quantil of "
				+ getName() + "' with lines axis x1y1 smooth unique" + ", ");
		script.write("'" + analyzer + AbstractFileAnalyzer.FILE_EXTENSION
				+ "' using 1:" + (startIndex + 2) + " title 'Minimum of "
				+ getName() + "' with lines axis x1y1 smooth unique"
				+ Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write("set terminal pdf monochrome font 'Helvetica,10'"
				+ Constants.LINE_END);
		script.write("set output '" + analyzer + "-"
				+ getName().replaceAll(" ", "_") + "-Median"
				+ AbstractFileMetricAnalyzer.FILE_PDF_EXTENSION + "'"
				+ Constants.LINE_END);
		script.write("replot" + Constants.LINE_END);
	}

	@Override
	public List<String> getPeerMeasurementValues(Object peer) {
		return generateStatistics(peerValues.get(peer));
	}

	@Override
	public void resetPeerMeasurement() {
		peerValues.clear();
	}

	public void addValue(Peer peer, Num value) {
		if (!peerValues.containsKey(peer)) {
			peerValues.put(peer, new ArrayList<Num>());
		}
		peerValues.get(peer).add(value);
		values.add(value);
	}

}
