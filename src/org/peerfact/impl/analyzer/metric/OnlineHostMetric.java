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

package org.peerfact.impl.analyzer.metric;

import java.util.Collections;
import java.util.List;

import org.peerfact.impl.util.oracle.GlobalOracle;


/**
 * A metric which provides the current online hosts.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 09/07/2012
 * @param <Peer>
 *            the peer
 */
public class OnlineHostMetric<Peer> extends SumMetric<Peer, Integer> {

	public OnlineHostMetric() {
		super("Online Hosts", "number");
	}

	@Override
	public List<String> getTimeMeasurementValues(long currentTime) {
		return Collections.singletonList(Integer.valueOf(
				GlobalOracle.getOnlineHostsNumber())
				.toString());
	}

	@Override
	public void resetTimeMeasurement() {
		// nothing to do
	}

	@Override
	public List<String> getPeerMeasurementValues(Object peer) {
		return Collections.singletonList("1");
	}

	@Override
	public void resetPeerMeasurement() {
		// nothing to do
	}

}
