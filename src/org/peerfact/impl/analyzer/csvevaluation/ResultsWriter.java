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

package org.peerfact.impl.analyzer.csvevaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;

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
public class ResultsWriter {

	static final Logger logger = Logger.getLogger(ResultsWriter.class);

	static final String separator = "\t";

	public enum FileAction {
		increment, overwrite, append
	}

	private String fileName;

	BufferedWriter out;

	FileAction fileAction;

	public ResultsWriter(String fileName, FileAction fileAction) {
		this.fileAction = fileAction;
		File file = determineFileName(fileName);
		prepareDirectories(file);
		this.fileName = file.getAbsolutePath();
		logger.debug("Writing to: " + this.fileName);
		prepare();
	}

	public static void prepareDirectories(File fileContained) {
		if (!fileContained.getParentFile().exists()) {
			fileContained.getParentFile().mkdirs();
		}
	}

	private File determineFileName(String filename) {
		File f = new File(Simulator.getOuputDir().getAbsolutePath()
				+ File.separator + filename);
		logger.debug("Trying file: " + f.getAbsolutePath() + " Exists: "
				+ f.exists());
		if (!f.exists() || fileAction != FileAction.increment) {
			return f;
		}
		for (int i = 0;; i++) {
			f = new File(Simulator.getOuputDir().getAbsolutePath()
					+ File.separator + filename + "_" + i);
			logger.debug("Trying file: " + f.getAbsolutePath() + " Exists: "
					+ f.exists());
			if (!f.exists()) {
				return f;
			}
		}

	}

	private void prepare() {
		try {
			FileWriter fstream = new FileWriter(fileName,
					fileAction == FileAction.append);
			out = new BufferedWriter(fstream);
			out.write("# ResultsWriter v0.1\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeHeader(List<String> fieldNames) {
		try {
			out.write("# ");
			for (String field : fieldNames) {
				out.write(field + separator);
			}
			out.write("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeDataSet(List<String> measurements) {
		try {
			for (String field : measurements) {
				out.write(field + separator);
			}
			out.write("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void finish() {
		try {
			out.write("# End of measurement.\n");
			out.close();
			logger.debug("Finished writing of: " + this.fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
