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

package org.peerfact.impl.util.guirunner.seed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.peerfact.Constants;
import org.peerfact.impl.util.guirunner.impl.ConfigFile;
import org.peerfact.impl.util.toolkits.HashToolkit;

/**
 * Class that provides a seed for the simulation, depending on user options.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SeedDetermination {

	long newSeed;

	long lastUsedSeed;

	boolean foundLastUsedSeed = false;

	private SeedDeterminationChoice choice = SeedDeterminationChoice.newSeed;

	List<SeedDeterminationListener> listeners = new ArrayList<SeedDeterminationListener>();

	Map<String, Long> seeds = new LinkedHashMap<String, Long>();

	private long customSeed;

	private ConfigFile file;

	static final String SEEDS_FILE = Constants.GUI_CFG_DIR + "/seeds.db";

	public static final int NULL_SEED = 286591039;

	public SeedDetermination() {
		readSeedsDB();
		newSeed = new Random().nextLong();
		customSeed = 0;
	}

	public void loadFile(ConfigFile f) {
		file = f;
		determineLastUsedSeed();
		fileChanged();
	}

	public ConfigFile getConfigFile() {
		return file;
	}

	private void readSeedsDB() {
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(SEEDS_FILE));
		} catch (FileNotFoundException e) {
			return;
		}
		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] elements = line.split(" ");
				if (elements.length >= 2) {
					seeds.put(elements[0], Long.parseLong(elements[1]));
				}
			}
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void determineLastUsedSeed() {
		String hash = getFileNameHash();
		if (seeds.containsKey(hash)) {
			lastUsedSeed = seeds.get(hash);
			foundLastUsedSeed = true;
			return;
		}
		lastUsedSeed = NULL_SEED;
		foundLastUsedSeed = false;
	}

	protected boolean foundLastUsedSeed() {
		return foundLastUsedSeed;
	}

	protected long getLastUsedSeed() {
		return lastUsedSeed;
	}

	protected long getNewSeed() {
		return newSeed;
	}

	protected void choose(SeedDeterminationChoice c) {
		this.choice = c;
	}

	protected String getFileNameHash() {
		return HashToolkit.getMD5Hash(file.getFile().getAbsolutePath());
	}

	public void saveSettings() {
		if (choice != SeedDeterminationChoice.fromLastRun) {

			seeds.put(getFileNameHash(), getChosenSeed());

			try {
				FileWriter fstream = new FileWriter(SEEDS_FILE);
				BufferedWriter out = new BufferedWriter(fstream);
				for (Entry<String, Long> seed : seeds.entrySet()) {
					out.write(seed.getKey() + " "
							+ String.valueOf(seed.getValue()) + "\r\n");
				}
				out.close();
			} catch (Exception e) {
				System.err.println("Could not write seed cache file. Error: "
						+ e.getMessage());
			}
		}
	}

	public void setCustomSeed(int customSeed) {
		this.customSeed = customSeed;
	}

	public int getConfigSeed() {
		String seed = file.getSeedInConfig();
		if (seed == null) {
			return NULL_SEED;
		}
		try {
			return Integer.parseInt(seed);
		} catch (NumberFormatException e) {
			return NULL_SEED;
		}
	}

	public long getChosenSeed() {
		if (choice == SeedDeterminationChoice.fromLastRun) {
			return lastUsedSeed;
		}
		if (choice == SeedDeterminationChoice.newSeed) {
			return newSeed;
		}
		if (choice == SeedDeterminationChoice.fromConfig) {
			return getConfigSeed();
		} else {
			return customSeed;
		}
	}

	protected enum SeedDeterminationChoice {
		fromLastRun,
		newSeed,
		fromConfig,
		customSeed;
	}

	public Long getCustomSeed() {
		return customSeed;
	}

	public void addListener(SeedDeterminationListener l) {
		listeners.add(l);
	}

	void fileChanged() {
		for (SeedDeterminationListener l : listeners) {
			l.fileChanged();
		}
	}

	public interface SeedDeterminationListener {
		public void fileChanged();
	}
}
