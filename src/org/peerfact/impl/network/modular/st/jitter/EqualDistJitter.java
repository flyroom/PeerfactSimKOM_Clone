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
import org.peerfact.impl.network.modular.st.JitterStrategy;
import org.peerfact.impl.simengine.Simulator;


/**
 * Applies a jitter that is equally distributed from 0 to max.
 * 
 * Parameters: max (long)
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class EqualDistJitter implements JitterStrategy {

	public long max = 5 * Simulator.MILLISECOND_UNIT;

	RandomGenerator rand = Simulator.getRandom();

	@Override
	public long getJitter(long cleanMsgPropagationDelay, NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db) {
		return Math.round(rand.nextDouble() * max);
	}

	/**
	 * Sets the maximum jitter in simulation time units.
	 * 
	 * @param maxJitter
	 */
	public void setMax(long maxJitter) {
		this.max = maxJitter;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeTime("max", max);
	}

}
