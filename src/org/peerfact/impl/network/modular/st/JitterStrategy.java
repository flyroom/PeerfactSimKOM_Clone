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

package org.peerfact.impl.network.modular.st;

import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;

/**
 * <p>
 * Determines the jitter that shall be used for a given network message.
 * </p>
 * <p>
 * Jitter is the variation of the message propagation delay in a network. In the
 * Modular Net Layer, the total time the message is delayed is defined as the
 * message propagation delay (defined by the LatencyStrategy) plus the jitter
 * (defined by this class), plus the time the message stays in sender and
 * receiver queues defined by the traffic control strategy (if the traffic
 * control mechanism delays the messages at all).
 * </p>
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface JitterStrategy extends ModNetLayerStrategy {

	/**
	 * Returns the jitter that shall be used for the given network message.
	 * 
	 * @param cleanMsgPropagationDelay
	 *            : the message propagation delay that was previously calculated
	 *            by the LatencyStrategy
	 * @param msg
	 *            : the network message that shall be jittered
	 * @param nlSender
	 *            : the network layer of the sender
	 * @param nlReceiver
	 *            : the network layer of the receiver
	 * @param db
	 *            : the network measurement database (may be null if none set)
	 * @return the jitter in simulation time units
	 */
	public long getJitter(long cleanMsgPropagationDelay, NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db);

}
