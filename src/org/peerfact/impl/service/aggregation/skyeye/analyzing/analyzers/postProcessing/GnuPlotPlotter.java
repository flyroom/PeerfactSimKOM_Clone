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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
public class GnuPlotPlotter {

	// private Logger log = SimLogger.getLogger(GnuPlotPlotter.class);

	private PrintWriter pw;

	private File writingDir;

	private boolean writingEnabled;

	public GnuPlotPlotter(String writingDir) {
		this.writingDir = new File(writingDir);
		writingEnabled = false;
		pw = null;
	}

	public GnuPlotPlotter(File writingDir) {
		this.writingDir = writingDir;
		writingEnabled = false;
	}

	public void openScriptFile(String name) {
		if (pw == null) {
			try {
				pw = new PrintWriter(new FileWriter(new File(writingDir, name)));
				writingEnabled = true;
			} catch (IOException e) {
				// log.error("Could not create file " + name + " in directory "
				// + writingDir.getPath() + ". Reason = " + e.toString());
			}
		} else {
			// log.error("This instance is currently used for another file");
		}
	}

	public void closeScriptFile() {
		if (writingEnabled) {
			pw.close();
			pw = null;
		}
	}

	public void writePlot() {
		if (writingEnabled) {
			pw.write("plot ");
		}
	}

	public void rollOverLine() {
		if (writingEnabled) {
			pw.println(",\\");
		}
	}

	public void newLine() {
		if (writingEnabled) {
			pw.println();
		}
	}

	public void writeLine(String inputFile, boolean isFileInput, String index,
			String usingStart, String usingEnd, int lw, String title) {
		if (writingEnabled) {
			if (isFileInput) {
				pw.print("\"" + inputFile + "\" ");
				pw.print("index " + index + ":" + index + " ");
				pw.print("using " + usingStart + ":" + usingEnd + " ");
			} else {
				pw.print(inputFile + " ");
			}

			pw.print("lw " + lw + " ");
			if (title == null) {
				pw.print("notitle");
			} else {
				pw.print("ti\"" + title + "\"");
			}
		}
	}

	public void writeLine(String inputFile, boolean isFileInput, String index,
			String usingStart, String usingEnd, int lw, String[] color,
			String title) {
		if (writingEnabled) {
			if (isFileInput) {
				pw.print("\"" + inputFile + "\" ");
				pw.print("index " + index + ":" + index + " ");
				pw.print("using " + usingStart + ":" + usingEnd + " ");
			} else {
				pw.print(inputFile + " ");
			}
			pw.print("lw " + lw + " ");
			pw.print("lt rgb \"#" + color[0] + color[1] + color[2] + "\" ");
			if (title == null) {
				pw.print("notitle");
			} else {
				pw.print("ti\"" + title + "\"");
			}
		}
	}
}
