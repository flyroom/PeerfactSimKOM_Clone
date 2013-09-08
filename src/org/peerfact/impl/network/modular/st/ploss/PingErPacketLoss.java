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

package org.peerfact.impl.network.modular.st.ploss;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.ModularNetMessage;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.SummaryRelation;
import org.peerfact.impl.network.modular.st.PLossStrategy;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Applies a packet loss as measured by the PingEr project
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PingErPacketLoss implements PLossStrategy {

	RandomGenerator rand = Simulator.getRandom();

	static Logger log = SimLogger.getLogger(PingErPacketLoss.class);

	@Override
	public boolean shallDrop(ModularNetMessage msg, ModularNetLayer nlSender,
			ModularNetLayer nlReceiver, NetMeasurementDB db) {

		if (db == null) {
			throw new IllegalArgumentException(
					"The PingER packet loss strategy can not access any network "
							+ "measurement database. You may not have loaded it in the config file.");
		}

		SummaryRelation sumRel = db.getMostAccurateSummaryRelation(
				nlSender.getDBHostMeta(), nlReceiver.getDBHostMeta());

		if (sumRel == null) {
			throw new AssertionError("No summary relation could be found for "
					+ nlSender + " - " + nlReceiver);
		}

		// If the message consists of multiple fragments, the loss probability
		// will be the probability that all fragments have arrived, and
		// every fragment itself has the probability of "sumRel.getpLoss()" to
		// be dropped
		double prob = 1d - Math.pow(1d - sumRel.getpLoss() * 0.01,
				msg.getNoOfFragments());
		log.debug("Dropping with probability " + prob + ", fragments "
				+ msg.getNoOfFragments());
		return rand.nextDouble() < prob;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
