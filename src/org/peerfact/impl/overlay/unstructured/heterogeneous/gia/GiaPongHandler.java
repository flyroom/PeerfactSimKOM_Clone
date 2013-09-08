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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import org.peerfact.impl.overlay.unstructured.heterogeneous.common.PongCachePongHandler;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages.GiaPongMessage;

/**
 * Works like the Pong Cache Pong Handler, but additionally emits and collects
 * meta information needed by the GIA overlay with the pong messages: the actual
 * degree of the node and the current token allocation rate.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaPongHandler extends
		PongCachePongHandler<GiaOverlayContact, GiaPongMessage> {

	private GiaNode node;

	private GiaConnectionManager mgr;

	public GiaPongHandler(GiaNode node, GiaConnectionManager mgr) {
		super(node, mgr);
		this.node = node;
		this.mgr = mgr;
	}

	@Override
	public GiaPongMessage generatePongMessage(
			GiaOverlayContact requestingContact, GiaOverlayContact thisContact) {
		return new GiaPongMessage(thisContact, getLocalPongCache(),
				node.getDegree(),
				node.getTokenAllocationRateFor(requestingContact));
	}

	@Override
	public void receivedPong(GiaPongMessage reply) {
		GiaConnectionMetadata meta = mgr.getMetadata(reply.getSender());
		if (meta != null) {
			meta.setLastDegreeObserved(reply.getActualDegree()); // Refreshes
																	// the last
																	// degree
																	// observed
			meta.getQueryTokenBucket().setAllocationRate(reply.getTokenRate()); // Sets
																				// the
																				// assigned
																				// token
																				// rate.
		} else {
			System.out
					.println(node.getOwnContact()
							+ ": Can not update degree and TAR from pong. No metadata found for node"
							+ reply.getSender());
		}
		updatePongCache(reply.getPongCache(), reply.getSender());
	}

}
