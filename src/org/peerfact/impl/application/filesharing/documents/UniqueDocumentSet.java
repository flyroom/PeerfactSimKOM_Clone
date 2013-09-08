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

package org.peerfact.impl.application.filesharing.documents;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.simengine.Simulator;


/**
 * Documents of this set are only allowed to be published by one host in the
 * entire overlay network. Furthermore, they are looked up equally-distributed.
 * 
 * Thought for modeling a scenario where every peer publishes e.g. its contact
 * data, like it can be used by a VoIP or Instant Messaging service.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UniqueDocumentSet extends AbstractDocumentSet {

	RandomGenerator rand = Simulator.getRandom();

	private int startRank;

	private int nextDoc;

	@Override
	public int getIdForLookup() {
		return startRank + rand.nextInt(size);
	}

	/**
	 * Returns a key that can be used for publishing.
	 */
	@Override
	public FileSharingDocument getDocumentForPublish() {
		if (nextDoc >= startRank + size) {
			throw new ConfigurationException(
					name
							+
							": Tried to publish more unique documents than "
							+ "are defined. Increase the document size of this set or decrease publishes.");
		}
		int id = nextDoc;
		nextDoc++;
		createDocument(id);
		return documents.get(id);
	}

	@Override
	public void setBeginRank(int rank) {
		this.startRank = rank;
		this.nextDoc = rank;
	}

	@Override
	public boolean containsResourcesOf(Iterable<Integer> set) {
		for (Integer key : set) {
			if (key >= startRank && key < startRank + size) {
				return true;
			}
		}
		return false;
	}

}
