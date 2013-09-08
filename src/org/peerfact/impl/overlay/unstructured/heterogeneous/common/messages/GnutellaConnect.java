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

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;

/**
 * This message is sent to a node to initiate the connection to it.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaConnect<TContact extends GnutellaLikeOverlayContact>
extends AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9080684205828802632L;

	private TContact senderInfo;

	private boolean hasLowConnectivity;

	/**
	 * Creates a new connection.
	 * 
	 * @param senderInfo
	 *            : information about the sender.
	 * @param hasLowConnectivity
	 *            : a flag the indicates if the sender is currently in a low
	 *            connectivity state according to the configuration.
	 */
	public GnutellaConnect(TContact senderInfo, boolean hasLowConnectivity) {
		super();
		this.senderInfo = senderInfo;
		this.hasLowConnectivity = hasLowConnectivity;
	}

	/**
	 * Returns information about the sender.
	 * 
	 * @return
	 */
	public TContact getSenderInfo() {
		return senderInfo;
	}

	/**
	 * Returns whether the sender is in a low connectivity state.
	 * 
	 * @return
	 */
	public boolean senderHasLowConnectivity() {
		return hasLowConnectivity;
	}

	@Override
	public long getGnutellaPayloadSize() {
		return 1;
	}

	@Override
	public String toString() {
		return "CONNECT: sender=" + senderInfo + " seq=" + this.getSeqNumber();
	}

}
