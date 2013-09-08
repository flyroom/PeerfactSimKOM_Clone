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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaOverlayContact;

/**
 * Gia query message. Very similar to Gnutella-like query messages, but designed
 * for biased random walks.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaQueryMessage extends GiaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1608033240119192075L;

	/**
	 * Needed for the flow control of Gia.
	 * 
	 * wantedResponses = 2 initiator = 8 lastHop = 8 Query itself = 16
	 * 
	 */
	// public static final int ESTIMATED_SIZE = 34;
	/**
	 * For testing of the flow control
	 */
	public static final int ESTIMATED_SIZE = 100;

	/**
	 * Returns the last contact that sent this query.
	 * 
	 * @return
	 */
	public GiaOverlayContact getLastHop() {
		return lastHop;
	}

	/**
	 * Sets the last contact handling this query to the specified value.
	 * 
	 * @param lastHop
	 */
	public void setLastHop(GiaOverlayContact lastHop) {
		this.lastHop = lastHop;
	}

	/**
	 * Returns the number of responses still needed to make this query.
	 * 
	 * @return
	 */
	public int getWantedResponses() {
		return wantedResponses;
	}

	/**
	 * Returns the initiator of this query message
	 * 
	 * @return
	 */
	public GiaOverlayContact getInitiator() {
		return initiator;
	}

	/**
	 * Returns the query made via this query message.
	 * 
	 * @return
	 */
	public Query getQuery() {
		return q;
	}

	/**
	 * Decreases the number of responses still needed by dec.
	 * 
	 * @param dec
	 */
	public void decreaseWantedResponsesBy(int dec) {
		wantedResponses -= dec;
	}

	/**
	 * Increases the hop counter by one.
	 */
	public void increaseHopCounter() {
		hops++;
	}

	/**
	 * Returns the hop counter
	 * 
	 * @return
	 */
	public int getHopCounter() {
		return hops;
	}

	private int wantedResponses;

	private GiaOverlayContact initiator;

	private GiaOverlayContact lastHop;

	private Query q;

	private int hops;

	public GiaQueryMessage(GiaOverlayContact initiator,
			GiaOverlayContact lastHop, Query q, int wantedResponses) {
		this.initiator = initiator;
		this.lastHop = lastHop;
		this.q = q;
		this.wantedResponses = wantedResponses;
		hops = 0;
	}

	/**
	 * Returns the unique ID of this query.
	 * 
	 * @return
	 */
	public int getQueryUID() {
		return q.getQueryUID();
	}

	@Override
	public String toString() {
		return "QUERY_MSG: (uid=" + q.getQueryUID() + ", init=" + initiator
				+ ", lstHp=" + lastHop + ", q=" + q + ", hops=" + hops + ")";
	}

	@Override
	public long getGnutellaPayloadSize() {
		// return initiator.getSize() + lastHop.getSize() + q.getSize() + 2;
		return 100;
	}

}
