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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.util.stats.distributions.ZipfDistribution;


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
public class FileFactory {

	private ZipfDistribution distribution;

	private static FileFactory singletonInstance;

	private Map<Integer, FilesharingDocument> files = new LinkedHashMap<Integer, FilesharingDocument>();

	public static FileFactory getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new FileFactory();
		}
		return singletonInstance;
	}

	public FileFactory() {
		distribution = new ZipfDistribution(10000, 1.0);
	}

	public FilesharingDocument getFile(Integer rank) {
		if (!files.containsKey(rank)) {
			files.put(rank, new FilesharingDocument(rank));
		}
		return files.get(rank);
	}

	public FilesharingDocument getFile() {
		Integer rank = distribution.returnRank();
		return getFile(rank);
	}

	public FilesharingKey getKey(Integer rank) {
		return this.getFile(rank).getKey();
	}

	public FilesharingKey getKey() {
		return this.getFile().getKey();
	}
}
