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

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.Config;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

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
 * Superclass for Analyzers that need to write output into a file. The output is
 * entirely buffered and written when this Analyzer stops.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class FileAnalyzer implements Analyzer {

	protected final static Logger log = SimLogger.getLogger(FileAnalyzer.class);

	/**
	 * The capacity of the underlying BufferedWriter.
	 */
	private static final int BUFFER_CAPACITY = 100000;

	private final int clusterIDLength;

	private PrintWriter out;

	private boolean started = false;

	private String fileDescr = "unnamed";

	private String fileSuffix = ".dat";

	public FileAnalyzer() {
		final Config conf = KademliaSetup.getConfig();
		clusterIDLength = conf.getHierarchyDepth()
				* conf.getHierarchyTreeOrder();
	}

	/**
	 * Sets a description of the measurement, for instance data lookup analysis,
	 * to be used in the filename.
	 * 
	 * @param descr
	 *            a filename safe measurement file description.
	 */
	public final void setOutputFileDescr(final String descr) {
		if (!started) {
			fileDescr = descr;
		}
	}

	/**
	 * Sets the suffix of the output file (including the file extension). The
	 * file name will be constructed by appending the prefix, some configuration
	 * and version information, and the suffix. (If that file already exists, it
	 * will be overwritten. If not, it will be created. If the path does not
	 * exist, it is attempted to create it as well. A new file is created only
	 * if none has been opened by this Analyzer before. If one file is still
	 * open from an earlier Analyzer run, it will be used.)
	 * <p>
	 * The output file is constructed with this prefix only if
	 * {@link #setOutputFile(String)} has never been called before (setting the
	 * complete output file path disables prefix/suffix settings).
	 * <p>
	 * The contents of this output file is aimed at supporting in-depth analysis
	 * of the measurement results. It is not the same as the contents written
	 * onto the Writer passed to {@link #stop(Writer)} as that contents is
	 * intended to be displayed after the simulation run.
	 * <p>
	 * Calls to this method are ignored if this Analyzer has already been
	 * started.
	 * 
	 * @see #start()
	 * 
	 * @param path
	 *            the path to the output file, either absolute or relative to
	 *            the programme directory.
	 * @throws IOException
	 *             if the output file could not be opened/written etc.
	 */
	public final void setOutputFileSuffix(final String suffixAndExtension) {
		if (!started) {
			fileSuffix = suffixAndExtension;
		}
	}

	/**
	 * Sets the path of the output file (including the path to its directory).
	 * (If that file already exists, it will be overwritten. If not, it will be
	 * created. If the path does not exist, it is attempted to create it as
	 * well.)
	 * <p>
	 * Calling this method enforces the given filename. If this method is never
	 * called, the file name is constructed according to its prefix and suffix
	 * (either the default values or other values, if these have been set).
	 * <p>
	 * The contents of this output file is aimed at supporting in-depth analysis
	 * of the measurement results. It is not the same as the contents written
	 * onto the Writer passed to {@link #stop(Writer)} as that contents is
	 * intended to be displayed after the simulation run.
	 * <p>
	 * Calls to this method are ignored if this Analyzer has already been
	 * started.
	 * 
	 * @see #start()
	 * 
	 * @param path
	 *            the path to the output file, either absolute or relative to
	 *            the programme directory.
	 * @throws IOException
	 *             if the output file could not be opened/written etc.
	 */
	public void setOutputFile(final String path) {
		final File fPath, parent;
		final FileWriter fw;
		final BufferedWriter bw;

		if (started) {
			return;
		}

		try {
			fPath = new File(path);
			parent = fPath.getParentFile();
			// create directories if necessary
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			fw = new FileWriter(fPath);
			bw = new BufferedWriter(fw, BUFFER_CAPACITY);
			out = new PrintWriter(bw);
			log.debug("Analyzer '" + this.getClass() + "' set output file to '"
					+ path + "'");
		} catch (IOException e) {
			log.error("Could not open file '" + path + "'.", e);
		}
	}

	private String getFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(Simulator.getOuputDir().getAbsolutePath());
		sb.append(File.separator);
		sb.append(fileDescr);
		sb.append(fileSuffix);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start() {
		if (!started) {
			if (out == null) {
				setOutputFile(getFileName());
			}
			started = true;
			log.debug("Analyzer '" + this.getClass() + "' started.");
			started();
		}
	}

	/**
	 * The measurement has been started.
	 */
	protected void started() {
		// do some custom initialisation - empty by default.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop(final Writer output) {
		if (!started) {
			return;
		}
		started = false;
		try {
			stopped(output);
		} catch (IOException e) {
			log.error("An error occured while writing the display output "
					+ "of this Analyzer.", e);
		}
		if (out != null) {
			out.close();
			if (out.checkError()) {
				log.error("An error occured while writing the file output "
						+ "of this Analyzer.");
			}
		} else {
			log.error("The output file path has not been set - not writing "
					+ "any output.");
		}
	}

	/**
	 * The measurement has been stopped - write display output.
	 * 
	 * @param output
	 *            a Writer onto which output can be written (will be displayed).
	 * @throws IOException
	 *             if an error occurred while writing the output.
	 */
	protected void stopped(final Writer output) throws IOException {
		// do some custom cleanup - empty by default.
	}

	/**
	 * @return whether the measurement has started.
	 */
	protected final boolean isStarted() {
		return started;
	}

	/**
	 * Appends the given string to the file output.
	 * 
	 * @param str
	 *            the String that is to be appended.
	 */
	public final void appendToFile(final String str) {
		if (out != null) {
			out.print(str);
		}
	}

	/**
	 * Appends the given character to the file output.
	 * 
	 * @param c
	 *            the char that is to be appended.
	 */
	public final void appendToFile(final char c) {
		if (out != null) {
			out.print(c);
		}
	}

	/**
	 * Appends the given long to the file output.
	 * 
	 * @param l
	 *            the Long that is to be appended.
	 */
	public final void appendToFile(final long l) {
		if (out != null) {
			out.print(l);
		}
	}

	/**
	 * Appends the given double to the file output.
	 * 
	 * @param d
	 *            the double that is to be appended.
	 */
	public final void appendToFile(final double d) {
		if (out != null) {
			out.print(d);
		}
	}

	/**
	 * Appends the given boolean to the file output.
	 * 
	 * @param b
	 *            the boolean that is to be appended.
	 */
	public final void appendToFile(final boolean b) {
		if (out != null) {
			out.print(b);
		}
	}

	/**
	 * Switches the file output to a new line.
	 */
	public final void appendNewLine() {
		if (out != null) {
			out.print('\n');
		}
	}

	/**
	 * Appends a column separator to the output file.
	 */
	protected final void appendSeparator() {
		if (out != null) {
			out.print(' ');
		}
	}

	/**
	 * Appends the given BigInteger in its binary representation and fills it
	 * with leading zeros.
	 * 
	 * @param cluster
	 *            a BigInteger to be interpreted as cluster ID.
	 */
	protected final void appendClusterID(final BigInteger cluster) {
		final String cStr = cluster.toString(2);
		final int additionalZeros = clusterIDLength - cStr.length();
		for (int i = 1; i <= additionalZeros; i++) {
			appendToFile('0');
		}
		appendToFile(cStr);
	}

}
