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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.stats.distributions.Distribution;
import org.peerfact.impl.util.stats.distributions.ExponentialDistribution;
import org.peerfact.impl.util.stats.distributions.ZipfDistribution;


/**
 * Documents in this set have a zipfian-distributed probability to be published
 * and looked up. Furthermore, the popularity of the documents is reordered at a
 * specified time interval. At this interval, two documents are taken and their
 * popularity is swapped. An example for the declaration of this document set in
 * the XML config file:
 * 
 * <pre>
 * &lt;ResourceSet class="org.peerfact.impl.application.filesharing2.documents.ZipfDocumentSet" name="files1" size="$size" zipfExp="0.7" meanReorderIntvl="10m"/&gt;
 * </pre>
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ZipfDocumentSet extends AbstractDocumentSet {

	ZipfDistribution dist;

	private final static Logger log = SimLogger
			.getLogger(ZipfDocumentSet.class);

	Distribution reorderIntvlDist;

	double zipfExp = -1;

	List<Integer> keyPopularity = new LinkedList<Integer>();

	RandomGenerator rand = Simulator.getRandom();

	private long meanReorderIntvl = -1;

	private boolean useRanks = true;

	private boolean configured;

	private int startRank;

	/**
	 * Returns the distribution of reorder events over time that is used.
	 * 
	 * @return
	 */
	public Distribution getReorderDistribution() {
		return reorderIntvlDist;
	}

	/**
	 * Sets the zipf parameter 1/s that is used for the given popularity
	 * distribution.
	 * 
	 * @param zipfExp
	 */
	public void setZipfExp(double zipfExp) {
		this.zipfExp = zipfExp;
	}

	/**
	 * Sets the mean interval at which the document set's popularity is
	 * reordered.
	 * 
	 * @param meanReorderIntvl
	 */
	public void setMeanReorderIntvl(long meanReorderIntvl) {
		this.meanReorderIntvl = meanReorderIntvl;
	}

	protected void checkConfigure() {
		if (!configured) {
			configure();
			configured = true;
		}
	}

	protected void configure() {
		dist = new ZipfDistribution(size, zipfExp);
		reorderIntvlDist = new ExponentialDistribution(meanReorderIntvl);

		if (!useRanks) {
			log.info("Using random keys");
			for (int i = 0; i < size; i++) {
				keyPopularity.add(rand.nextInt());
			}
		} else {
			log.info("Using ranks");
			for (int i = 0; i < size; i++) {
				keyPopularity.add(i);
			}
		}

		new ReorderPopularityEvent(this)
				.scheduleWithDelay((long) getReorderDistribution()
						.returnValue());

	}

	@Override
	public int getIdForLookup() {

		int key;

		checkConfigure();

		int popIndex = (int) (dist.returnValue() * size);
		key = startRank + keyPopularity.get(popIndex);
		log.debug("Returning " + key + " from index " + popIndex
				+ " for lookup");

		return key;
	}

	@Override
	public FileSharingDocument getDocumentForPublish() {
		checkConfigure();
		int popIndex = (int) (dist.returnValue() * size);
		int id = startRank + keyPopularity.get(popIndex);
		log.debug("Returning " + id + " from index " + popIndex
				+ " for sharing");
		if (!documents.containsKey(id)) {
			createDocument(id);
		}
		return documents.get(id);
	}

	/**
	 * Does a popularity reorder on this document set.
	 */
	public void doReorder() {
		int oldPos = rand.nextInt(size);
		int newPos = rand.nextInt(size);
		log.debug("REORDER EVENT at " + Simulator.getCurrentTime()
				+ " Moving element " + oldPos + " to " + newPos);
		keyPopularity.add(newPos, keyPopularity.remove(oldPos));
		getIdForLookup(); // DEBUG
	}

	@Override
	public void setBeginRank(int rank) {
		this.startRank = rank;
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
