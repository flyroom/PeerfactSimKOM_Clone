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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages;

import java.util.List;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;


/**
 * This message is sent as a reply on a connection attempt in order to notify it
 * about the acceptance of denial.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class GnutellaConnectReply<TContact extends GnutellaLikeOverlayContact>
		extends AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6632749373094813849L;

	/**
	 * Returns a list of contacts the receiver can try to connect to if it
	 * wants.
	 * 
	 * @return
	 */
	public List<TContact> getTryUltrapeers() {
		return tryUltrapeers;
	}

	/**
	 * Returns whether the connection was accepted or not.
	 * 
	 * @return
	 */
	public boolean isConnectionAccepted() {
		return connectionAccepted;
	}

	private List<TContact> tryUltrapeers;

	boolean connectionAccepted;

	/**
	 * Creates a new reply message
	 * 
	 * @param tryUltrapeers
	 *            : a list of contacts the receiver can try to connect to if it
	 *            wants
	 * @param connectionAccepted
	 *            : if the connection is accepted.
	 */
	public GnutellaConnectReply(List<TContact> tryUltrapeers,
			boolean connectionAccepted) {
		this.tryUltrapeers = tryUltrapeers;
		this.connectionAccepted = connectionAccepted;
	}

	@Override
	public long getGnutellaPayloadSize() {

		return tryUltrapeers.size() * GnutellaLikeOverlayContact.getSize();
	}

	@Override
	public String toString() {
		return "CONN_REPLY: accepted=" + connectionAccepted + " tryUPs="
				+ tryUltrapeers + " seq=" + this.getSeqNumber();
	}

}
