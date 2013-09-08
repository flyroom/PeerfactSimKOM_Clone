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

package org.peerfact.impl.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.metric.Metric;
import org.peerfact.impl.analyzer.metric.OnlineHostMetric;
import org.peerfact.impl.analyzer.metric.TimeMetric;
import org.peerfact.impl.simengine.Simulator;

/**
 * Abstract analyzer class as superclass for all metric related analyzers. It
 * handles the printing and reseting of the metrics and also supports node
 * related output next to time related ones.
 * 
 * The concrete analyzers have to implement the abstract methods
 * initializeMetrics() to define the metrics and use the addPeer() and
 * addMetric() method to give data.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 07/20/2011
 * @param <Peer>
 *            the class representing the single peers
 */
public abstract class AbstractFileMetricAnalyzer<Peer> extends
		AbstractFileAnalyzer {

	public static final String FILE_PEER_EXTENSION = "Peers";

	public static final String FILE_SORT_EXTENSION = "Sort";

	public static final String FILE_SCRIPT_EXTENSION = ".plt";

	public static final String FILE_PNG_EXTENSION = ".png";

	public static final String FILE_PDF_EXTENSION = ".pdf";

	private File scriptFile;

	private File scriptPeerFile;

	private File scriptPeerSortFile;

	private List<Peer> peers = new ArrayList<Peer>();

	private List<Metric<Peer, ?>> metrics = new ArrayList<Metric<Peer, ?>>();

	@Override
	protected void initialize() throws IOException {
		// initialize metrics
		addMetric(new TimeMetric<Peer>());
		addMetric(new OnlineHostMetric<Peer>());
		initializeMetrics();

		// write head line
		output.write(Constants.COMMENT_LINE);
		for (Metric<?, ?> metric : metrics) {
			for (String headline : metric.getHeadlines()) {
				output.write(headline + Constants.SEPARATOR);
			}
		}
		output.write(Constants.LINE_END);
		if (flushEveryLine) {
			output.flush();
		}

		// write gnuplot script
		scriptFile = new File(Simulator.getOuputDir(), outputFileName
				+ FILE_SCRIPT_EXTENSION);
		BufferedWriter script = new BufferedWriter(new FileWriter(scriptFile));

		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " Plot " + outputFileName
				+ " results"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " @author Matthias Feldotto <info@peerfact.org>"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " analyzer class:"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.SEPARATOR
				+ this.getClass().getCanonicalName() + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "" + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " data file (in a sub-folder of 'outputs/...'):"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.SEPARATOR
				+ outputFileName + FILE_EXTENSION
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "" + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);

		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " Available data fields"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		for (Metric<?, ?> metric : metrics) {
			for (String headline : metric.getHeadlines()) {
				script.write(Constants.COMMENT_LINE + headline
						+ Constants.LINE_END);
			}
		}
		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);

		int index = 1;
		for (Metric<?, ?> metric : metrics) {
			metric.generateTimePlots(script, outputFileName, index);
			script.write(Constants.LINE_END);
			script.write(Constants.LINE_END);
			index += metric.getHeadlines().size();
		}

		script.flush();
		script.close();
	}

	@Override
	protected void doEvaluation(long currentTime) throws IOException {
		// Output the generated line string
		for (Metric<Peer, ?> metric : metrics) {
			for (String value : metric.getTimeMeasurementValues(currentTime)) {
				output.write(value + Constants.SEPARATOR);
			}
			metric.resetTimeMeasurement();
		}
		output.write(Constants.LINE_END);
		if (flushEveryLine) {
			output.flush();
		}
	}

	@Override
	protected void doFinalEvaluation() throws IOException {
		File outputFile = new File(Simulator.getOuputDir(), outputFileName
				+ FILE_PEER_EXTENSION + FILE_EXTENSION);

		BufferedWriter peerOutput = new BufferedWriter(new FileWriter(
				outputFile));
		List<List<String>> values = new ArrayList<List<String>>();

		// Write head line
		peerOutput.write(Constants.COMMENT_LINE);
		peerOutput.write("Peer" + Constants.SEPARATOR);
		values.add(new ArrayList<String>());
		for (Metric<?, ?> metric : metrics) {
			if (!(metric instanceof TimeMetric)) {
				for (String headline : metric.getHeadlines()) {
					peerOutput.write(headline + Constants.SEPARATOR);
					values.add(new ArrayList<String>());
				}
			}
		}
		peerOutput.write(Constants.LINE_END);
		if (flushEveryLine) {
			peerOutput.flush();
		}

		// write gnuplot script
		scriptPeerFile = new File(Simulator.getOuputDir(), outputFileName
				+ FILE_PEER_EXTENSION + FILE_SCRIPT_EXTENSION);
		BufferedWriter script = new BufferedWriter(new FileWriter(
				scriptPeerFile));

		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " Plot " + outputFileName
				+ FILE_PEER_EXTENSION + " results"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " @author Matthias Feldotto <info@peerfact.org>"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " analyzer class:"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.SEPARATOR
				+ this.getClass().getCanonicalName() + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "" + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " data file (in a sub-folder of 'outputs/...'):"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.SEPARATOR
				+ outputFileName
				+ FILE_PEER_EXTENSION + FILE_EXTENSION
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "" + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);

		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " Available data fields"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "Peer" + Constants.LINE_END);
		for (Metric<?, ?> metric : metrics) {
			if (!(metric instanceof TimeMetric)) {
				for (String headline : metric.getHeadlines()) {
					script.write(Constants.COMMENT_LINE + headline
							+ Constants.LINE_END);
				}
			}
		}
		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);

		int index = 1;
		for (Metric<?, ?> metric : metrics) {
			metric.generatePeerPlots(script, outputFileName
					+ FILE_PEER_EXTENSION, index);
			script.write(Constants.LINE_END);
			script.write(Constants.LINE_END);
			index += metric.getHeadlines().size();
		}

		script.flush();
		script.close();

		// Output the generated line string
		int id = 1;
		index = 0;
		for (Object peer : peers) {
			peerOutput.write(id + Constants.SEPARATOR);
			values.get(index).add(new Integer(id).toString());
			index++;
			for (Metric<?, ?> metric : metrics) {
				if (!(metric instanceof TimeMetric)) {
					for (String value : metric.getPeerMeasurementValues(peer)) {
						peerOutput.write(value + Constants.SEPARATOR);
						values.get(index).add(value);
						index++;
					}
				}
			}
			peerOutput.write(Constants.LINE_END);
			if (flushEveryLine) {
				peerOutput.flush();
			}
			id++;
			index = 0;
		}

		peerOutput.flush();
		peerOutput.close();

		// sort
		boolean first = true;
		for (List<String> list : values) {
			if (!first) {
				// compare as doubles
				Collections.sort(list, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Double.compare(Double.parseDouble(o2),
								Double.parseDouble(o1));
					}
				});
			}
			first = false;
		}
		// Output the sorted values
		outputFile = new File(Simulator.getOuputDir(), outputFileName
				+ FILE_PEER_EXTENSION + FILE_SORT_EXTENSION + FILE_EXTENSION);

		BufferedWriter sortOutput = new BufferedWriter(new FileWriter(
				outputFile));

		// Write head line
		sortOutput.write(Constants.COMMENT_LINE);
		sortOutput.write("Peer" + Constants.SEPARATOR);
		for (Metric<?, ?> metric : metrics) {
			if (!(metric instanceof TimeMetric)) {
				for (String headline : metric.getHeadlines()) {
					sortOutput.write(headline + Constants.SEPARATOR);
				}
			}
		}
		sortOutput.write(Constants.LINE_END);
		if (flushEveryLine) {
			sortOutput.flush();
		}

		// write gnuplot script
		scriptPeerSortFile = new File(Simulator.getOuputDir(), outputFileName
				+ FILE_PEER_EXTENSION + FILE_SORT_EXTENSION
				+ FILE_SCRIPT_EXTENSION);
		script = new BufferedWriter(new FileWriter(
				scriptPeerSortFile));

		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " Plot " + outputFileName
				+ FILE_PEER_EXTENSION + FILE_SORT_EXTENSION + " results"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " @author Matthias Feldotto <info@peerfact.org>"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " analyzer class:"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.SEPARATOR
				+ this.getClass().getCanonicalName() + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "" + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ " data file (in a sub-folder of 'outputs/...'):"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + Constants.SEPARATOR
				+ outputFileName
				+ FILE_PEER_EXTENSION + FILE_SORT_EXTENSION + FILE_EXTENSION
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "" + Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);

		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + " Available data fields"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE
				+ "------------------------------------------------------------"
				+ Constants.LINE_END);
		script.write(Constants.COMMENT_LINE + "Peer" + Constants.LINE_END);
		for (Metric<?, ?> metric : metrics) {
			if (!(metric instanceof TimeMetric)) {
				for (String headline : metric.getHeadlines()) {
					script.write(Constants.COMMENT_LINE + headline
							+ Constants.LINE_END);
				}
			}
		}
		script.write(Constants.LINE_END);
		script.write(Constants.LINE_END);

		index = 1;
		for (Metric<?, ?> metric : metrics) {
			metric.generatePeerPlots(script, outputFileName
					+ FILE_PEER_EXTENSION + FILE_SORT_EXTENSION, index);
			script.write(Constants.LINE_END);
			script.write(Constants.LINE_END);
			index += metric.getHeadlines().size();
		}

		script.flush();
		script.close();

		// Output the generated line string
		for (int i = 0; i < values.get(0).size(); i++) {
			for (int j = 0; j < values.size(); j++) {
				sortOutput.write(values.get(j).get(i) + Constants.SEPARATOR);
			}
			sortOutput.write(Constants.LINE_END);
			if (flushEveryLine) {
				sortOutput.flush();
			}
		}

		sortOutput.flush();
		sortOutput.close();

		for (Metric<?, ?> metric : metrics) {
			metric.resetPeerMeasurement();
		}
		peers.clear();

		// run gnuplot
		try {
			ProcessBuilder process = new ProcessBuilder("gnuplot",
					scriptFile.getName());
			process.directory(scriptFile.getParentFile());
			process.start();

			process = new ProcessBuilder("gnuplot",
					scriptPeerFile.getName());
			process.directory(scriptPeerFile.getParentFile());
			process.start();

			process = new ProcessBuilder("gnuplot",
					scriptPeerSortFile.getName());
			process.directory(scriptPeerSortFile.getParentFile());
			process.start();
		} catch (IOException e) {
			log.warn("Gnuplot path variable in operating system not configured, no plots created");
		}
	}

	/**
	 * Adds a new peer to the list of all peers.
	 * 
	 * @param peer
	 *            the new peer
	 */
	protected void addPeer(Peer peer) {
		if (!peers.contains(peer)) {
			peers.add(peer);
		}
	}

	/**
	 * Adds a new metric to the list of all metrics.
	 * 
	 * @param metric
	 *            the new metric
	 */
	protected void addMetric(Metric<Peer, ?> metric) {
		metrics.add(metric);
	}

	/**
	 * Initializes all metrics at the beginning.
	 */
	protected abstract void initializeMetrics();

}
