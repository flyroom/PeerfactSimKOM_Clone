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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import org.peerfact.Constants;
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
 * This class implements the interface {@link SkyNetPostProcessing} and is
 * responsible for processing the data, which was monitored and collected during
 * a simulation in terms of the distribution of the SkyNet-nodes within the
 * SkyNet-tree. The captured data is situated in the statusData-directory and
 * will be utilized within this class to generate a dat-file, which is used for
 * the visualization within gnuPlot.<br>
 * <br>
 * <code>NumberLevelMatrix.dat</code> outlines the organization of the
 * SkyNet-tree during a simulation. It displays the number of nodes, support
 * peers, sub-coordinators, free capacities and leafs at each level of the tree
 * over the complete period of a simulation.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class StatusPostProcessor implements SkyNetPostProcessing {

	// private static Logger log =
	// SimLogger.getLogger(StatusPostProcessor.class);

	private static final String ID_LEVEL_MATRIX_DAT_FILE = "NumberLevelMatrix.dat";

	private static final String TREE_DEPTH_DAT_FILE = "TreeDepth.dat";

	private final String writingDataPath;

	private static String READING_DATA_PATH = Constants.TMP_DIR
			+ File.separator + "statusData";

	private String[] files;

	private PrintWriter qWriter;

	private PrintWriter treeDepthWriter;

	private final TreeMap<Integer, TreeMap<Integer, Double[]>> numberLevelMatrix;

	private int yMax;

	public StatusPostProcessor(String dataPath) {
		writingDataPath = dataPath;
		numberLevelMatrix = new TreeMap<Integer, TreeMap<Integer, Double[]>>();
		yMax = 0;
	}

	@Override
	public void extractDataOfFiles() {
		getListOfTempFiles(READING_DATA_PATH);
		if (files != null) {
			File file;
			int temp = 0;
			for (int i = 0; i < files.length; i++) {
				file = new File(READING_DATA_PATH + File.separator + files[i]);
				// log.debug("  Reading " + (i + 1) + ". status-file: "
				// + file.getName());
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(file));
					TreeMap<Integer, TreeMap<Integer, Double[]>> tempMatrix = (TreeMap<Integer, TreeMap<Integer, Double[]>>) ois
							.readObject();
					temp = ois.readInt();
					yMax = Math.max(yMax, temp);
					ois.close();
					numberLevelMatrix.putAll(tempMatrix);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void processData() {
		// not needed
	}

	@Override
	public void writeDataFile() {
		try {
			qWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(writingDataPath + File.separator
							+ ID_LEVEL_MATRIX_DAT_FILE))), true);

			treeDepthWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(writingDataPath + File.separator
							+ TREE_DEPTH_DAT_FILE))), true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		qWriter.println("# Number of periode");
		qWriter.println("# Level in the tree");
		qWriter.println("# Number of peers");
		qWriter.println("# Number of support peers");
		qWriter.println("# Number of sub-coordinators one level deeper");
		qWriter.println("# Number of leafs");
		qWriter
				.println("# Number of free capacities (BF - number of sub-coordinators)");
		qWriter.println("# Sum of attributes at level");
		qWriter.println("# Sum of attribute rank at level");

		qWriter.println();

		Iterator<Integer> xIter = numberLevelMatrix.keySet().iterator();
		TreeMap<Integer, Double[]> secondDim = null;
		Iterator<Integer> yIter = null;
		Integer x = null;
		Integer y = null;
		int xOffset = 1;
		int yOffset = 0;
		int xMax = numberLevelMatrix.lastKey().intValue() + 1;
		Double[] value = { 0d, 0d, 0d, 0d, 0d, 0d, 0d };
		Double[] maxValues = { 0d, 0d, 0d, 0d, 0d, 0d, 0d };
		Double[] minValues = { Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE, Double.MAX_VALUE };
		while (xIter.hasNext()) {
			x = xIter.next();

			if (xOffset < x.intValue()) {
				for (int i = xOffset; i < x.intValue(); i++) {
					for (int j = 0; j < yMax + 1; j++) {
						qWriter.println(i + "\t" + j + "\t0\t0\t0\t0\t0\t0\t0");
					}
					qWriter.println();
				}
			}
			xOffset = x.intValue() + 1;

			secondDim = numberLevelMatrix.get(x);
			yIter = secondDim.keySet().iterator();
			while (yIter.hasNext()) {
				y = yIter.next();
				if (y > -1) {
					if (yOffset < y.intValue()) {
						for (int i = yOffset; i < y.intValue(); i++) {
							qWriter.println(x.intValue() + "\t" + i
									+ "\t0\t0\t0\t0\t0\t0\t0");
						}
					}
					yOffset = y.intValue() + 1;
					value = secondDim.get(y);
					maxValues[0] = Math.max(maxValues[0], value[0]);
					maxValues[1] = Math.max(maxValues[1], value[1]);
					maxValues[2] = Math.max(maxValues[2], value[2]);
					maxValues[3] = Math.max(maxValues[3], value[3]);
					maxValues[4] = Math.max(maxValues[4], value[4]);

					minValues[0] = Math.min(minValues[0], value[0]);
					minValues[1] = Math.min(minValues[1], value[1]);
					minValues[2] = Math.min(minValues[2], value[2]);
					minValues[3] = Math.min(minValues[3], value[3]);
					minValues[4] = Math.min(minValues[4], value[4]);
					qWriter.println(x + "\t" + y + "\t" + value[0] + "\t"
							+ value[1] + "\t" + value[2] + "\t" + value[3]
							+ "\t" + value[4] + "\t" + value[5] + "\t"
							+ value[6]);
				}
			}
			if (yOffset < xMax) {
				for (int i = yOffset; i < yMax + 1; i++) {
					qWriter
							.println(x.intValue() + "\t" + i
									+ "\t0\t0\t0\t0\t0");
				}
			}
			yOffset = 0;
			qWriter.println();
		}
		qWriter.println("# MinValues = " + minValues[0] + " " + minValues[1]
				+ " " + minValues[2] + " " + minValues[3] + " " + minValues[4]);
		qWriter.println("# MaxValues = " + maxValues[0] + " " + maxValues[1]
				+ " " + maxValues[2] + " " + maxValues[3] + " " + maxValues[4]);

		qWriter.flush();
		qWriter.close();

		/*
		 * Computations for tree depth
		 */

		treeDepthWriter.println("# Number of periode");
		treeDepthWriter.println("# Number of peers in tree");
		treeDepthWriter.println("# Min level in tree");
		treeDepthWriter.println("# Max level in tree");
		treeDepthWriter.println("# Max level in tree (95-percentile of peers)");
		treeDepthWriter.println("# Avg level in tree");
		treeDepthWriter.println("# St.Dev. level in tree");
		treeDepthWriter.println("# St.Dev. under Avg level in tree");
		treeDepthWriter.println("# St.Dev. over Avg level in tree");
		treeDepthWriter
				.println("# Absolute deviation of tree depth to min tree depth");
		treeDepthWriter
				.println("# Absolute deviation of tree depth to min tree depth with 95-percentile");
		treeDepthWriter
				.println("# Relative deviation of tree depth to min tree depth");
		treeDepthWriter
				.println("# Relative deviation of tree depth to min tree depth with 95-percentile");

		for (Integer period : numberLevelMatrix.keySet()) {
			TreeMap<Integer, Double[]> levelMap = numberLevelMatrix.get(period);

			int minLevel = Integer.MAX_VALUE;
			int maxLevel = 0;
			int totalNumberOfPeersInTree = 0;

			int summedLevels = 0;

			for (Integer level : levelMap.keySet()) {
				Double[] levelMetrics = levelMap.get(level);
				double numOfPeersInLevel = levelMetrics[0];

				totalNumberOfPeersInTree += numOfPeersInLevel;

				if (level < minLevel && level > -1 && numOfPeersInLevel > 0) {
					minLevel = level;
				}

				if (level > maxLevel && numOfPeersInLevel > 0) {
					maxLevel = level;
				}

				summedLevels += numOfPeersInLevel * level;
			}

			double avgLevel = 0;
			if (totalNumberOfPeersInTree > 0) {
				avgLevel = (double) summedLevels / totalNumberOfPeersInTree;
			}

			/*
			 * Compute standard deviations
			 */
			double summedSqrDiff = 0;
			double summedSqrDiffUnderAvg = 0;
			double summedSqrDiffOverAvg = 0;
			int numUnderAvg = 0;
			int numOverAvg = 0;

			for (Integer level : levelMap.keySet()) {
				Double[] levelMetrics = levelMap.get(level);
				double numOfPeersOnLevel = levelMetrics[0];

				if (level == -1) {
					continue;
				}

				summedSqrDiff += numOfPeersOnLevel
						* Math.pow((level - avgLevel), 2);

				if (level > avgLevel) {
					summedSqrDiffUnderAvg += numOfPeersOnLevel
							* Math.pow((level - avgLevel), 2);
					numOverAvg += numOfPeersOnLevel;
				}

				if (level < avgLevel) {
					summedSqrDiffOverAvg += numOfPeersOnLevel
							* Math.pow((level - avgLevel), 2);
					numUnderAvg += numOfPeersOnLevel;
				}
			}

			double stdDev = 0;
			double stdDevUnder = 0;
			double stdDevOver = 0;
			if (totalNumberOfPeersInTree > 0) {
				stdDev = Math.sqrt(summedSqrDiff / totalNumberOfPeersInTree);

				if (numUnderAvg > 0) {
					stdDevUnder = Math
							.sqrt(summedSqrDiffUnderAvg / numUnderAvg);
				}

				if (numOverAvg > 0) {
					stdDevOver = Math.sqrt(summedSqrDiffOverAvg / numOverAvg);
				}
			}

			/*
			 * Compute tree depth for 95-percentile of peers
			 */
			int countedPeers = 0;
			int percentileLevel = 0;

			for (Integer level : levelMap.keySet()) {
				Double[] levelMetrics = levelMap.get(level);

				countedPeers += levelMetrics[0];

				if ((double) countedPeers / totalNumberOfPeersInTree >= 0.95) {
					percentileLevel = level;
					break;
				}
			}

			double log2N = Math.log(totalNumberOfPeersInTree) / Math.log(2);

			double absDevOfTreeDepthToMinTreeDepth = maxLevel - log2N;

			double absDevOfTreeDepthToMinTreeDepthWithPercentile = percentileLevel
					- log2N;

			double relDevOfTreeDepthToMinTreeDepth = (maxLevel - log2N) / log2N;

			double relDevOfTreeDepthToMinTreeDepthWithPercentile = (percentileLevel - log2N)
					/ log2N;

			treeDepthWriter.println(period + "\t" + totalNumberOfPeersInTree
					+ "\t" + minLevel + "\t" + maxLevel + "\t"
					+ percentileLevel + "\t" + avgLevel + "\t" + stdDev + "\t"
					+ stdDevUnder + "\t" + stdDevOver + "\t"
					+ absDevOfTreeDepthToMinTreeDepth + "\t"
					+ absDevOfTreeDepthToMinTreeDepthWithPercentile + "\t"
					+ relDevOfTreeDepthToMinTreeDepth + "\t"
					+ relDevOfTreeDepthToMinTreeDepthWithPercentile);
		}

		treeDepthWriter.flush();
		treeDepthWriter.close();

	}

	private void getListOfTempFiles(String dir) {
		File fileDir = new File(dir);
		if (checkForDirectory(fileDir)) {
			files = fileDir.list();
			Arrays.sort(files);
		}
	}

	private static boolean checkForDirectory(File name) {
		if (name.exists() && name.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}
}
