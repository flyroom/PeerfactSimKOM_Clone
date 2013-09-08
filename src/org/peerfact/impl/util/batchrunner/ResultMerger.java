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

package org.peerfact.impl.util.batchrunner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * Merges the results of comparative simulations produced by the batch runner.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 02/03/2013
 */
public class ResultMerger {

	static Logger log = SimLogger.getLogger(ResultMerger.class);

	private File outputDir;

	/**
	 * Constructor for new merger.
	 * 
	 * @param outputDir
	 *            the root output directory for merging
	 */
	public ResultMerger(File outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * Merges the results of different simulations.
	 * 
	 * @param variablesLists
	 *            the list of variables of the different simulations
	 */
	public void mergeResults(List<Map<String, String>> variablesLists) {

		try {
			List<File> gnuplotSampleScripts = new ArrayList<File>();
			List<String[]> names = new ArrayList<String[]>();

			// Iterate over all simulations
			for (Map<String, String> variablesMap : variablesLists) {

				// save folder and title name
				String outputName = Simulator.generateOutputDirectoryName(
						"simulation", null, null, variablesMap, null, null);
				File output = new File(outputDir, outputName);

				String title = "";
				for (Entry<String, String> variable : variablesMap.entrySet()) {
					title += variable.getKey() + "=" + variable.getValue()
							+ ", ";
				}
				if (title.length() > 0) {
					title = title.substring(0, title.length() - 2);
					title += ": ";
				}
				names.add(new String[] { outputName, title });

				boolean first = false;
				List<File> gnuplotFiles = new ArrayList<File>();
				Map<String, String> headlines = new LinkedHashMap<String, String>();
				Map<String, List<List<String>>> values = new LinkedHashMap<String, List<List<String>>>();

				// Iterate over all seeds and collect values
				for (File simulationFolder : output.listFiles()) {
					if (simulationFolder.isDirectory()
							&& !simulationFolder.getName().endsWith("__ERROR")) {
						for (File file : simulationFolder.listFiles()) {
							if (file.getName().endsWith(".plt")) {
								// copy gnuplot files once
								if (!first) {
									File gnuplotFile = new File(output,
											file.getName());
									gnuplotFiles.add(gnuplotFile);
									copyFile(file, gnuplotFile);
								}
							} else if (file.getName().endsWith(".dat")) {

								// collect all values
								if (!values.containsKey(file.getName())) {
									values.put(file.getName(),
											new ArrayList<List<String>>());
								}
								BufferedReader reader = new BufferedReader(
										new FileReader(file));
								String line = reader.readLine();

								if (!first) {
									headlines.put(file.getName(), line);
								}
								while ((line = reader.readLine()) != null) {
									String[] cells = line.split("\t");
									values.get(file.getName())
											.add(Arrays.asList(cells));
								}
								reader.close();
							}
						}
						first = true;
					}
				}

				// sort values and write to new merged data files
				for (Entry<String, List<List<String>>> content : values
						.entrySet()) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File(output, content.getKey())));

					writer.write(headlines.get(content.getKey()));
					writer.write(Constants.LINE_END);

					if (content.getKey().endsWith("Sort.dat")) {
						List<List<String>> sortValues = new ArrayList<List<String>>();
						for (int i = 0; i < content.getValue().size(); i++) {
							for (int j = 0; j < content.getValue().get(0)
									.size(); j++) {
								if (sortValues.size() <= j) {
									sortValues.add(new ArrayList<String>());
								}
								sortValues.get(j).add(
										content.getValue().get(i).get(j));
							}
						}

						// sort
						boolean firstColumn = true;
						for (List<String> list : sortValues) {
							if (firstColumn) {
								// compare as doubles ascending
								Collections.sort(list,
										new Comparator<String>() {
											@Override
											public int compare(String o1,
													String o2) {
												return Double.compare(
														Double.parseDouble(o1),
														Double.parseDouble(o2));
											}
										});
								firstColumn = false;
							} else {
								// compare as doubles descending
								Collections.sort(list,
										new Comparator<String>() {
											@Override
											public int compare(String o1,
													String o2) {
												return Double.compare(
														Double.parseDouble(o2),
														Double.parseDouble(o1));
											}
										});
							}
						}

						if (sortValues.size() > 0) {
							for (int i = 0; i < sortValues.get(0).size(); i++) {
								for (int j = 0; j < sortValues.size(); j++) {
									writer.write(sortValues.get(j).get(i)
											+ Constants.SEPARATOR);
								}
								writer.write(Constants.LINE_END);
							}
						}
					} else if (content.getKey().endsWith(".dat")) {
						// compare as doubles
						Collections.sort(content.getValue(),
								new Comparator<List<String>>() {
									@Override
									public int compare(List<String> o1,
											List<String> o2) {
										return Double.compare(
												Double.parseDouble(o1.get(0)),
												Double.parseDouble(o2.get(0)));
									}
								});

						for (List<String> contentLine : values
								.get(content.getKey())) {
							for (String cell : contentLine) {
								writer.write(cell);
								writer.write(Constants.SEPARATOR);
							}
							writer.write(Constants.LINE_END);
						}
					}

					writer.flush();
					writer.close();
				}

				// run gnuplot for merged seeds
				runGnuplotScripts(gnuplotFiles);
				if (gnuplotSampleScripts.size() == 0) {
					gnuplotSampleScripts = gnuplotFiles;
				}

			}

			// extend gnuplot scripts for comparision and run gnuplot
			List<File> gnuplotFiles = extendsPlots(gnuplotSampleScripts, names);
			runGnuplotScripts(gnuplotFiles);

		} catch (IOException e) {
			log.error("Exception while merging results", e);
		}
	}

	/**
	 * Extends gnuplot scripts to compare multiple simulations
	 * 
	 * @param gnuplotScripts
	 *            the template scripts to use
	 * @param titles
	 *            a list of folder-title-pairs
	 * @return a list of modified gnuplot scripts
	 */
	private List<File> extendsPlots(List<File> gnuplotScripts,
			List<String[]> titles) {

		List<File> gnuplotFiles = new ArrayList<File>();

		try {
			// comparative gnuplot scripts
			for (File script : gnuplotScripts) {
				File file = new File(outputDir, script.getName());
				gnuplotFiles.add(file);
				BufferedReader reader = new BufferedReader(new FileReader(
						script));
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));

				String line;
				while ((line = reader.readLine()) != null) {

					if (line.startsWith("plot")) {

						// split old line
						String[] graphs = line.split(", ");
						if (graphs[0].startsWith("plot ")) {
							graphs[0] = graphs[0].substring(5);
						}

						// create new line
						line = "plot ";
						for (String graph : graphs) {
							for (String[] name : titles) {
								line += "'"
										+ name[0]
										+ "/"
										+ graph.substring(1,
												graph.indexOf("title '") + 7);
								line += name[1]
										+ graph.substring(
												graph.indexOf("title '") + 7,
												graph.length());
								line += ", ";
							}
						}
						line = line.substring(0, line.length() - 2);
					}

					writer.write(line);
					writer.write(Constants.LINE_END);
				}
				writer.flush();
				writer.close();
				reader.close();
			}
		} catch (IOException e) {
			log.error("Exception while extending plot scripts", e);
		}
		return gnuplotFiles;
	}

	/**
	 * Run gnuplot and create plots.
	 * 
	 * @param gnuplotFiles
	 *            the gnuplot scripts to use
	 */
	private static void runGnuplotScripts(List<File> gnuplotFiles) {
		try {
			for (File gnuplotFile : gnuplotFiles) {
				// create own process and run gnuplot with each script
				ProcessBuilder processBuilder = new ProcessBuilder("gnuplot",
						gnuplotFile.getName());
				processBuilder.directory(gnuplotFile.getParentFile());
				processBuilder.start();
			}
		} catch (IOException e) {
			log.warn(
					"Gnuplot path variable in operating system not configured, no plots created",
					e);
		}
	}

	/**
	 * Copy one file to another place.
	 * 
	 * @param source
	 *            the source file
	 * @param target
	 *            the target file
	 * @throws FileNotFoundException
	 *             if file not exists
	 * @throws IOException
	 *             if exception occurred while copying
	 */
	public static void copyFile(File source, File target)
			throws FileNotFoundException, IOException {

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				source));
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(target, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();
	}

	/**
	 * Zips a folder to a file.
	 * 
	 * @param inFolder
	 *            the folder to zip
	 * @param outFile
	 *            the zipped file
	 * @return the generated zipped file
	 * @throws IOException
	 *             if an exception occurred while zipping
	 */
	public static File zipFolder(File inFolder, File outFile)
			throws IOException {
		// compress output file stream
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(outFile)));

		byte[] data = new byte[1024];
		File[] files = inFolder.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(files[i]),
						1024);

				// write data header
				out.putNextEntry(new ZipEntry(files[i].getName()));

				int count;
				while ((count = in.read(data, 0, 1024)) != -1) {
					out.write(data, 0, count);
				}
				// close each entry
				out.closeEntry();
				in.close();
			}
		}
		out.flush();
		out.close();

		return new File(outFile + ".zip");
	}

}
