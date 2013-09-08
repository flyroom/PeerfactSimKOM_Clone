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

package org.peerfact.impl.network.modular.st.jitter;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.SummaryRelation;
import org.peerfact.impl.network.modular.st.JitterStrategy;
import org.peerfact.impl.simengine.Simulator;


/**
 * Applies a jitter with parameters derived from the PingER project. Requires
 * the network measurement database containing PingER measurements.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PingErJitter implements JitterStrategy {

	RandomGenerator rand = Simulator.getRandom();

	@Override
	public long getJitter(long cleanMsgPropagationDelay, NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db) {

		if (db == null) {
			throw new IllegalArgumentException(
					"The PingEr jitter strategy can not access any network "
							+
							"measurement database. You may not have loaded it in the config file.");
		}

		SummaryRelation rel = db.getMostAccurateSummaryRelation(
				nlSender.getDBHostMeta(), nlReceiver.getDBHostMeta());

		return Math.round(rel.tGetJitterLnDist().inverseF(rand.nextDouble())
				* 0.5 * Simulator.MILLISECOND_UNIT);
		// divided by 2 because we have the RTT jitter, but want the delay
		// jitter

	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
