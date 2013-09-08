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

package org.peerfact.impl.analyzer.csvevaluation.derived;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.peerfact.impl.analyzer.csvevaluation.derived.lib.ConfidenceIntervals;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.IScale;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.IXY;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.IYValueSet;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.LogarithmicScale;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.Parser;


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
public class Scalability extends ConfidenceIntervalsPrinter {

	static final String[] overlayDirs = new String[] { "fs_gnutella",
			"fs_kademlia", "fs_gia" };// ", fs_chord"};

	static final String inputFileName = "/defaults_time";

	static final double LOG_SCALE = Math.pow(10d, 0.5d); // 10⁵

	// 0Time (ms) 1Hosts 2Messages/sec 3StaleContacts(%) 4StaleMsgs(%)
	// 5avgUpstreamBW 6peakUpstreamBW 7avgDownstreamBW 8peakDownstreamBW
	// 9AvgDnBWCons(%)
	// 10AvgUpBWCons(%) 11PeakDnBWCons(%) 12PeakUpBWCons(%) 13avgStaleBW
	// 14peakStaleBW
	// 15AvgQueryTime 16QuerySuccess(%) 17AvgNHops

	public static void main(String[] args) {

		for (String dirName : overlayDirs) {
			String dir = "outputs/" + dirName;

			Scalability.calculateBWWithHosts(dir);
			Scalability.calculatePeakBWWithHosts(dir);

			Scalability.calculatePeakDownBWConsWithHosts(dir);
			Scalability.calculatePeakUpBWConsWithHosts(dir);
			Scalability.calculateAvgDownBWConsWithHosts(dir);
			Scalability.calculateAvgUpBWConsWithHosts(dir);

			Scalability.calculateMessagesWithHosts(dir);
			Scalability.calculateQRTWithHosts(dir);
			Scalability.calculateQSuccessWithHosts(dir);
			Scalability.calculateStaleMsgsWithHosts(dir);
			Scalability.calculateStaleContactsWithHosts(dir);

			Scalability.calculateAvgNHopsWithHosts(dir);

			// ---

			Scalability.calculateQSuccessWithDocType(dir);
			Scalability.calculateAvgNHopsWithDocType(dir);
		}

	}

	public static void calculateMessagesWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_msgsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 2, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateQRTWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_qrtWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 15, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateBWWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_bwWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 7, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculatePeakBWWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_peakDownBWWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 8, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculatePeakDownBWConsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_peakDownBWConsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 11, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateAvgDownBWConsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_avgDownBWConsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {

			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 9, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculatePeakUpBWConsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_peakUpBWConsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 12, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateAvgUpBWConsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_avgUpBWConsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 10, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateQSuccessWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_qSuccessWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {

			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 16, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateAvgNHopsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_avgNHopsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 17, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateStaleMsgsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_staleMsgsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 4, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateStaleContactsWithHosts(String dir) {

		File inputFile = new File(dir + inputFileName);
		File outputFile = new File(dir + "/scal_staleContactsWithHosts");
		IScale scale = new LogarithmicScale(LOG_SCALE);
		try {
			ConfidenceIntervalsPrinter.printToFile(inputFile, outputFile,
					1, 3, scale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void calculateQSuccessWithDocType(String dir) {
		calculateDocTypeScalabilityGeneral(dir, 16, "/scal_qSuccessWithDocType");
	}

	public static void calculateAvgNHopsWithDocType(String dir) {
		calculateDocTypeScalabilityGeneral(dir, 17, "/scal_avgNHopsWithDocType");
	}

	public static void calculateDocTypeScalabilityGeneral(String dir,
			int yValue,
			String outputFile) {

		double rangeTop = 11000d;
		double rangeBottom = 4000d;

		/*
		 * if (dir.endsWith("fs_gia")) { //Workaround, Gia does not allow
		 * simulation with 10000 peers at the moment. rangeTop = 3500d;
		 * rangeBottom = 1100d; }
		 */

		String[] fileTypes = new String[] { "_300d", "", "_voip" };
		String[] niceNames = new String[] { "300docs", "20000docs", "Unique" };

		IYValueSet actualSet = new ConfidenceIntervals(0.95d, 2);

		try {

			BufferedWriter w = new BufferedWriter(new FileWriter(dir
					+ outputFile));
			w.write("#DocClass	ID	" + actualSet.printCaptionForFile()
					+ "num_values\n");

			for (int i = 0; i < fileTypes.length; i++) {

				File inputFile = new File(dir + "/defaults_time" + fileTypes[i]);
				if (inputFile.exists()) {

					Parser parser = new Parser(inputFile, 1, yValue);
					actualSet = new ConfidenceIntervals(0.95d, 2);
					IXY xy;
					while ((xy = parser.nextXY()) != null) {
						if (xy.getX() < rangeTop && xy.getX() > rangeBottom) {
							actualSet.addValue(xy.getY());
						}
					}

					w.write(niceNames[i] + "	" + i + "	"
							+ actualSet.printForFile() + "	"
							+ actualSet.getNumberOfValues() + "\n");
				}
			}

			w.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
