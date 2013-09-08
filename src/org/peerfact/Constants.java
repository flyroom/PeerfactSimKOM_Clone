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

package org.peerfact;

import java.io.File;

/**
 * This interface contains global constants that required for simulations.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface Constants {

	/**
	 * Path for the tmp-directory, which shall be used to temporarily store
	 * data, that is required during or after a simulation (e.g. for online or
	 * post-processing)
	 */
	public static final String TMP_DIR = "outputs/tmp";

	/**
	 * Path for the output-directory, which shall be used for storing the
	 * results of a simulation, its post-processing or in general for data that
	 * must available for a longer period of time.
	 */
	public static final String OUTPUTS_DIR = "outputs";

	/**
	 * Path for the logging-directory, which contains the different
	 * configurations for log4j and is used to store the output of log4j
	 */
	public static final String LOGGING_DIR = "outputs/logging";

	/**
	 * Path for the gui configuration, which contains the current configuration
	 * (positions, last seeds, last files)
	 */
	public static final String GUI_CFG_DIR = "outputs/guiCfg";

	/**
	 * Path for icons used in the gui.
	 */
	public static final String ICONS_DIR = "icons";

	/**
	 * Path for the directory which contains the scripts for generating graphics
	 * with GnuPlot
	 */
	public static final String GNUPLOT_SCRIPTS = "gnuplot";

	/**
	 * Defines the command to run gnuplot out of the simulator if it is
	 * configured on the Path variable of the operating system.
	 */
	public static final String GNUPLOT_EXECUTABLE = "gnuplot";

	// Variables for the automatic integration of GnuPlot within the simulator

	/**
	 * For completeness only, this variable defines the path to the binary for
	 * gnuplot. On Linux systems gnuplot can be started directly from the
	 * command line without the provisioning of a path for the binary.
	 */
	public static final String GNU_BIN_DIRECTORY_LINUX = File.listRoots()[0]
			.toString();

	/**
	 * Defines the command to run gnuplot out of the simulator on a Linux
	 * machine.
	 */
	public static final String GNU_EXECUTABLE_LINUX = "gnuplot";

	/**
	 * Defines the path where the binaries for gnuplot can be found on Windows
	 * machines. Please adapt the path to the current location of your gnuplot
	 * binaries.
	 */
	public static final String GNU_BIN_DIRECTORY_WINDOWS = "C:"
			+ File.separator
			+ "gnuplot" + File.separator + "bin";

	/**
	 * Defines the command to run gnuplot out of the simulator on a Windows
	 * machine.
	 */
	public static final String GNU_EXECUTABLE_WINDOWS = "wgnuplot-pipes.exe";

	/**
	 * Contains the size of <code>byte</code> (in bytes)
	 */
	public static final long BYTE_SIZE = Byte.SIZE / Byte.SIZE;

	/**
	 * Contains the size of <code>boolean</code> (in bytes)
	 */
	public static final long BOOLEAN_SIZE = Byte.SIZE / Byte.SIZE;

	/**
	 * Contains the size of <code>char</code> (in bytes)
	 */
	public static final long CHAR_SIZE = Character.SIZE / BYTE_SIZE;

	/**
	 * Contains the size of <code>short</code> (in bytes)
	 */
	public static final long SHORT_SIZE = Short.SIZE / Byte.SIZE;

	/**
	 * Contains the size of <code>int</code> (in bytes)
	 */
	public static final long INT_SIZE = Integer.SIZE / Byte.SIZE;

	/**
	 * Contains the size of <code>float</code> (in bytes)
	 */
	public static final long FLOAT_SIZE = Float.SIZE / Byte.SIZE;

	/**
	 * Contains the size of <code>double</code> (in bytes)
	 */
	public static final long DOUBLE_SIZE = Double.SIZE / Byte.SIZE;

	/**
	 * Contains the size of <code>long</code> (in bytes)
	 */
	public static final long LONG_SIZE = Long.SIZE / Byte.SIZE;

	/**
	 * String for end of line.
	 */
	public static final String LINE_END = "\n";

	/**
	 * String for separator.
	 */
	public static final String SEPARATOR = "\t";

	/**
	 * String to mark comment line.
	 */
	public static final String COMMENT_LINE = "#";

}
