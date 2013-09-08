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

package org.peerfact.impl.util.guirunner.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.peerfact.Constants;


/**
 * 
 * The manager for recently opened files.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class LastOpened {

	public static final String LAST_OPENED_CONF = Constants.GUI_CFG_DIR
			+ "/runnerLastOpened.files";

	public static final int LAST_OPENED_LENGTH = 5;

	List<ConfigFile> lastOpened = new ArrayList<ConfigFile>();

	public LastOpened() {
		this.loadFromFile();
	}

	/**
	 * Appends a file to the recently opened files.
	 * 
	 * @param f
	 */
	public void append(ConfigFile f) {

		removeOccurrences(f);

		lastOpened.add(0, f);

		correctSize();
	}

	/**
	 * Removes the occurrence from the last opened files.
	 * 
	 * Nötig, da equals und damit remove entsprechend bei Dateien nicht
	 * funktioniert!
	 * 
	 * @param f
	 */
	private void removeOccurrences(ConfigFile f) {
		for (ConfigFile f2 : lastOpened) {
			if (f2.getFile().getAbsolutePath()
					.equals(f.getFile().getAbsolutePath())) {
				lastOpened.remove(f2);
				break;
			}
		}
	}

	/**
	 * Returns all last opened files that are saved.
	 * 
	 * @return
	 */
	public List<ConfigFile> getLastOpened() {
		return lastOpened;
	}

	/**
	 * Cuts the size to the maximum entries allowed.
	 */
	private void correctSize() {
		if (lastOpened.size() > LAST_OPENED_LENGTH) {
			lastOpened = lastOpened.subList(0, LAST_OPENED_LENGTH - 1);
		}
	}

	/**
	 * Loads the entries from the configuration file.
	 */
	public void loadFromFile() {

		lastOpened = new ArrayList<ConfigFile>();

		BufferedReader cfgFileStream;
		try {
			cfgFileStream = new BufferedReader(new FileReader(LAST_OPENED_CONF));

			String line;
			while ((line = cfgFileStream.readLine()) != null) {

				File openedFile = new File(line);
				if (openedFile.exists()) {
					lastOpened.add(new ConfigFile(openedFile));
				} else {
					System.err
							.println("File \""
									+ line
									+ "\" does not exist, will be removed from 'previously opened files'.");
				}
			}

		} catch (FileNotFoundException e) {
			// einfach leere Liste übergeben.
		} catch (IOException e) {
			System.err
					.println("Error during parsing the previously opened files: ");
			e.printStackTrace();
		}

		correctSize();
	}

	/**
	 * Saves the entries to the configuration file
	 */
	public void saveToFile() {
		try {

			BufferedWriter w = new BufferedWriter(new FileWriter(
					LAST_OPENED_CONF));

			for (ConfigFile f : lastOpened) {
				w.write(f.getFile().getAbsolutePath() + "\r\n");
			}

			w.close();

		} catch (IOException e) {
			System.err
					.println("Error during writing the previously opened files: ");
			e.printStackTrace();
		}
	}

}
