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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetPostProcessing;


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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Coor2RootPostProcessor implements SkyNetPostProcessing {

	// private static Logger log = SimLogger
	// .getLogger(Coor2RootPostProcessor.class);

	private static int metricIndex = 75;

	private static int nodeIndex = 1;

	private static float errorThresholdMetric = 0.2f;

	private static float errorThresholdNode = 0.25f;

	private GnuPlotPlotter plotter;

	private File writingDataPath;

	private int maxFileNodeCount;

	private int lineIterNodeCount;

	private boolean evaluateNodeCount;

	private double thresholdNodeCount;

	private int maxFileMetric;

	private int lineIterMetric;

	private boolean evaluateMetric;

	private double thresholdMetric;

	private static String READING_DATA_PATH = Constants.TMP_DIR
			+ File.separator + "coordinatorRootData";

	private static String FILE_PREFIX = "Level";

	private static String FILE_POSTFIX = ".dat";

	private static final String COOR_2_ROOT_DAT_FILE = "CompleteCoor2Root.dat";

	public Coor2RootPostProcessor(String dataPath) {
		writingDataPath = new File(dataPath);
		plotter = new GnuPlotPlotter(writingDataPath.getParent()
				+ File.separator + SkyNetConstants.GNU_SCRIPTS_PATH);
		maxFileNodeCount = 0;
		lineIterNodeCount = 0;
		evaluateNodeCount = true;
		thresholdNodeCount = 0;
		maxFileMetric = 0;
		lineIterMetric = 0;
		evaluateMetric = true;
		thresholdMetric = 0;
	}

	@Override
	public void extractDataOfFiles() {
		File dir = new File(READING_DATA_PATH);
		if (dir.exists() && dir.isDirectory()) {
			List<String> list = Arrays.asList(dir.list());
			int correction = 0;
			if (list.contains(COOR_2_ROOT_DAT_FILE)) {
				correction = 1;
			}
			BufferedReader reader = null;
			String line = null;
			boolean writeHeader = true;
			File file = null;
			try {
				PrintWriter writer = new PrintWriter(
						new FileWriter(writingDataPath + File.separator
								+ COOR_2_ROOT_DAT_FILE));
				int fileIndex = 1;
				for (int i = 0; i < list.size() - correction; i++) {
					file = new File(READING_DATA_PATH + File.separator
							+ FILE_PREFIX + fileIndex + FILE_POSTFIX);
					while (!file.exists()) {
						fileIndex++;
						file = new File(READING_DATA_PATH + File.separator
								+ FILE_PREFIX + fileIndex + FILE_POSTFIX);
					}
					reader = new BufferedReader(new FileReader(file));
					writeHeader = true;
					while ((line = reader.readLine()) != null) {
						if (i == 0) {
							if (evaluateMetric) {
								evaluateLineForMetric(line, metricIndex);
							}
							if (evaluateNodeCount) {
								evaluateLineForNodeCount(line, nodeIndex);
							}
							writer.println(line);
						} else {
							if (!line.startsWith("#")) {
								if (writeHeader) {
									writeHeader = false;
									writer.println();
									writer.println();
									writer.println("#Data for level "
											+ fileIndex);
								}
								if (evaluateMetric) {
									evaluateLineForMetric(line, metricIndex);
								}
								if (evaluateNodeCount) {
									evaluateLineForNodeCount(line, nodeIndex);
								}
								writer.println(line);
							}
						}
					}
					// check the accumulated values of the file
					if ((thresholdNodeCount / lineIterNodeCount) < errorThresholdNode) {
						maxFileNodeCount++;
					} else {
						evaluateNodeCount = false;
					}
					thresholdNodeCount = 0;
					lineIterNodeCount = 0;

					if ((thresholdMetric / lineIterMetric) < errorThresholdMetric) {
						maxFileMetric++;
					} else {
						evaluateMetric = false;
					}
					thresholdMetric = 0;
					lineIterMetric = 0;
					fileIndex++;
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			// log.error("Cannot postprocess the"
			// + " Coordinator2Root-Comparison,"
			// + " as there exists no data");
		}
	}

	private void evaluateLineForNodeCount(String line, int index) {
		if (!line.startsWith("#")) {
			String[] elem = line.split("\t");
			lineIterNodeCount++;
			thresholdNodeCount += Math.abs(Double.parseDouble(elem[index + 5]));
		}
	}

	private void evaluateLineForMetric(String line, int index) {
		if (!line.startsWith("#")) {
			String[] elem = line.split("\t");
			lineIterMetric++;
			thresholdMetric += Math.abs(Double.parseDouble(elem[index + 3]));
		}
	}

	@Override
	public void processData() {
		// until now, not needed

	}

	@Override
	public void writeDataFile() {
		// nodeAmount
		plotter.openScriptFile("nodeAmount.plt");
		plotter.writePlot();
		plotter.writeLine("../data/CompleteCoor2Root.dat", true, "0", "1",
				(nodeIndex + 2) + "", 2, "Amount of nodes, root");
		if (maxFileNodeCount > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileNodeCount; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", (nodeIndex + 1) + "", 1, "Amount of nodes, level "
							+ (i + 1));
			if (i < (maxFileNodeCount - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();

		// interpolated nodeAmount
		plotter.openScriptFile("interpolatedAmountOfNodes.plt");
		plotter.writePlot();
		plotter.writeLine("../data/CompleteCoor2Root.dat", true, "0", "1",
				(nodeIndex + 2) + "", 2, "Amount of nodes, root");
		if (maxFileNodeCount > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileNodeCount; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", (nodeIndex + 4) + "", 1,
					"Interp. amount of nodes, level " + (i + 1));
			if (i < (maxFileNodeCount - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();

		// total error of node-interpolation
		plotter.openScriptFile("totalErrNodeInterpolation.plt");
		plotter.writePlot();
		String[] color = { "00", "00", "00" };
		plotter
				.writeLine("[x=0:] x-x", false, null, null, null, 2, color,
						null);
		if (maxFileNodeCount > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileNodeCount; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", (nodeIndex + 5) + "", 1,
					"Tot err of Node-Interp, level " + (i + 1));
			if (i < (maxFileNodeCount - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();

		// relative error of node-interpolation
		plotter.openScriptFile("relErrNodeInterpolation.plt");
		plotter.writePlot();
		plotter
				.writeLine("[x=0:] x-x", false, null, null, null, 2, color,
						null);
		if (maxFileNodeCount > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileNodeCount; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", "($" + (nodeIndex + 6) + "*100)", 1,
					"Rel err of Node-Interp, level " + (i + 1));
			if (i < (maxFileNodeCount - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();

		// avgValueMetric
		plotter.openScriptFile("avgValueMetric.plt");
		plotter.writePlot();
		plotter.writeLine("../data/CompleteCoor2Root.dat", true, "0", "1",
				(metricIndex + 2) + "", 2, "Avg value of a metric, root");
		if (maxFileMetric > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileMetric; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", (metricIndex + 1) + "", 1,
					"Avg value of a metric, level " + (i + 1));
			if (i < (maxFileMetric - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();

		// total error of metric
		plotter.openScriptFile("totalErrAvgMetric.plt");
		plotter.writePlot();
		plotter
				.writeLine("[x=0:] x-x", false, null, null, null, 2, color,
						null);
		if (maxFileMetric > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileMetric; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", (metricIndex + 3) + "", 1,
					"Tot err of avg Metric, level " + (i + 1));
			if (i < (maxFileMetric - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();

		// relative error of metric
		plotter.openScriptFile("relErrAvgMetric.plt");
		plotter.writePlot();
		plotter
				.writeLine("[x=0:] x-x", false, null, null, null, 2, color,
						null);
		if (maxFileMetric > 0) {
			plotter.rollOverLine();
		}
		for (int i = 0; i < maxFileMetric; i++) {
			plotter.writeLine("../data/CompleteCoor2Root.dat", true, "" + i,
					"1", "($" + (metricIndex + 4) + "*100)", 1,
					"Rel err of avg Metric, level " + (i + 1));
			if (i < (maxFileMetric - 1)) {
				plotter.rollOverLine();
			}
		}
		plotter.closeScriptFile();
	}

}
