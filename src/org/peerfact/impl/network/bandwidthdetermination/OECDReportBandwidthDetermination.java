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

package org.peerfact.impl.network.bandwidthdetermination;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.BandwidthDetermination;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * The implemented bandwidth distribution is taken from the latest OECD
 * broadband report (For further information, please see Oecd broadband portal,
 * http://www.oecd.org/sti/ict/broadband). The provided values for upload- and
 * download-bandwidth are denoted in bytes per second.
 * 
 * @author Leo Nobach (additional changes: Dominik Stingl)
 *         <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class OECDReportBandwidthDetermination implements
		BandwidthDetermination<Integer> {

	private static Logger log = SimLogger
			.getLogger(OECDReportBandwidthDetermination.class);

	RandomGenerator rand = Simulator.getRandom();

	int distributionMax = 0;

	List<Integer> distribution = new ArrayList<Integer>();

	List<Bandwidth> bandwidths = new ArrayList<Bandwidth>();

	public OECDReportBandwidthDetermination() {
		log.debug("Init RandomBandwidthDetermination");
		addNewBandwidth(7000, 7000, 600); // Modem 15%
		addNewBandwidth(8000, 8000, 600); // ISDN
		addNewBandwidth(87355, 1202843, 1601); // DSL 41%
		addNewBandwidth(89000, 236111, 47); // Wireless 1%
		addNewBandwidth(158001, 1856966, 758); // Cable 19%
		addNewBandwidth(4280818, 8165793, 258); // FTTx 6%
		// 3864
	}

	/**
	 * Adds a new type of a network interface to the collection of different
	 * network interfaces. The specification for a new type consists of the
	 * upload- and download-bandwidth as well as of the relative usage-amount of
	 * the network interface
	 * 
	 * @param upBW
	 *            describes the maximum upload-bandwidth in byte per second
	 * @param downBW
	 *            describes the maximum download-bandwidth in byte per second
	 * @param part
	 *            describes the proportion of network interfaces of the given
	 *            type
	 */
	private void addNewBandwidth(double upBW, double downBW, int part) {
		distributionMax += part;
		distribution.add(distributionMax);
		bandwidths.add(new Bandwidth(downBW, upBW));
	}

	@Override
	public Bandwidth getRandomBandwidth() {
		int random = rand.nextInt(distributionMax);

		for (int i = 0; i < bandwidths.size(); i++) {
			if (distribution.get(i) > random) {
				log.trace("Getting " + bandwidths.get(i).toString());
				return bandwidths.get(i);
			}
		}
		log
				.trace("Getting "
						+ bandwidths.get(bandwidths.size() - 1).toString());
		return bandwidths.get(bandwidths.size() - 1);

	}

	@Override
	public Bandwidth getBandwidthByObject(Integer object) {
		// nothing to do
		return null;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
