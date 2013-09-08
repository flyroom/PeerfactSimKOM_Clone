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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.Analyzer;
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
public class StringBuilderFileAnalyzer implements Analyzer {

	protected final static Logger log = SimLogger
			.getLogger(StringBuilderFileAnalyzer.class);

	private final int initialBufferCapacity;

	private Writer out;

	private StringBuilder sb;

	private boolean started = false;

	/**
	 * Constructs a new FileAnalyzer with the given initial output buffer
	 * capacity. The output file path has to be set before the measurement is
	 * started.
	 * 
	 * @see #setOutputFile(String)
	 * @param bufferCapacity
	 *            the initial capacity of the output buffer (in "number of
	 *            chars").
	 */
	public StringBuilderFileAnalyzer(final int bufferCapacity) {
		initialBufferCapacity = bufferCapacity;
	}

	/**
	 * Sets the path of the output file. (If the file exists, it will be
	 * overwritten. If not, it will be created. If the path does not exist, it
	 * is attempted to create it as well.)
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
	public final void setOutputFile(final String path) throws IOException {
		final File fPath, parent;

		if (started) {
			return;
		}

		fPath = new File(path);
		parent = fPath.getParentFile();
		// create directories if necessary
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		out = new FileWriter(fPath);
		log.debug("Analyzer '" + this.getClass() + "' set output file to '"
				+ path + "'");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start() {
		if (!started) {
			if (out == null) {
				try {
					setOutputFile(Simulator.getOuputDir().getAbsolutePath()
							+ File.separator
							+ "kademlia2-StringBuilderFile.dat");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			started = true;
			sb = new StringBuilder(initialBufferCapacity);
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
		try {
			sb.trimToSize();
			out.write(sb.toString());
			out.close();
		} catch (NullPointerException ex) {
			log.error("The output file path has not been set - not writing "
					+ "any output.", ex);
		} catch (IOException e) {
			log.error("An error occured while writing the file output "
					+ "of this Analyzer.", e);
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
	protected final void appendToFile(final String str) {
		try {
			sb.append(str);
		} catch (NullPointerException ex) {
			// ignore
		}
	}

	/**
	 * Appends the given character to the file output.
	 * 
	 * @param c
	 *            the char that is to be appended.
	 */
	protected final void appendToFile(final char c) {
		try {
			sb.append(c);
		} catch (NullPointerException ex) {
			// ignore
		}
	}

	/**
	 * Appends the given long to the file output.
	 * 
	 * @param l
	 *            the Long that is to be appended.
	 */
	protected final void appendToFile(final long l) {
		try {
			sb.append(l);
		} catch (NullPointerException ex) {
			// ignore
		}
	}

	/**
	 * Appends the given double to the file output.
	 * 
	 * @param d
	 *            the double that is to be appended.
	 */
	protected final void appendToFile(final double d) {
		try {
			sb.append(d);
		} catch (NullPointerException ex) {
			// ignore
		}
	}

	/**
	 * Appends the given boolean to the file output.
	 * 
	 * @param b
	 *            the boolean that is to be appended.
	 */
	protected final void appendToFile(final boolean b) {
		try {
			sb.append(b);
		} catch (NullPointerException ex) {
			// ignore
		}
	}

	/**
	 * Switches the file output to a new line.
	 */
	protected final void appendNewLine() {
		try {
			sb.append('\n');
		} catch (NullPointerException ex) {
			// ignore
		}
	}

	/**
	 * Appends a column separator to the output file.
	 */
	protected final void appendSeparator() {
		try {
			sb.append(' ');
		} catch (NullPointerException ex) {
			// ignore
		}
	}

}
