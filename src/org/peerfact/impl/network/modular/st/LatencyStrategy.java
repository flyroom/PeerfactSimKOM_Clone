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
 * This strategy defines the message propagation delay of a given network
 * message. This is the total time the network message is delayed minus the
 * jitter, minus the time the message stays in sender and receiver queues
 * defined by the traffic control strategy (if the traffic control mechanism
 * delays the messages at all).
 * 
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface LatencyStrategy extends ModNetLayerStrategy {

	/**
	 * <p>
	 * Returns the message propagation delay in simulation time units. This is
	 * the total time the network message is delayed minus the jitter, minus the
	 * time the message stays in sender and receiver queues defined by the
	 * traffic control strategy (if the traffic control mechanism delays the
	 * messages at all).
	 * </p>
	 * <p>
	 * Note that the message to be delayed may be split into multiple IP
	 * fragments, please implement an appropriate propagation delay.
	 * </p>
	 * 
	 * @param msg
	 *            , the message to be delayed
	 * @param nlSender
	 *            , the sender's network layer
	 * @param nlReceiver
	 *            , the receiver's network layer
	 * @param db
	 *            , the measurement database
	 * 
	 * @return the message propagation delay in simulation time units.
	 */
	public long getMessagePropagationDelay(NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db);

}
