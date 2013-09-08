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

import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaOverlayContact;

/**
 * Third handshake message that carries the decision of the requester whether it
 * accepts the requested connection itself, now it has enough information from
 * the requestee, given by GiaHandshake2. Additionally sends the initial token
 * allocation rate, if the connection was successful.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaHandshake3 extends GiaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8326923782072183514L;

	private long initialTAR;

	public GiaHandshake3(GiaOverlayContact x, boolean accepted, long initialTAR) {
		this.x = x;
		this.accepted = accepted;
		this.initialTAR = initialTAR;
	}

	GiaOverlayContact x;

	boolean accepted;

	public boolean isAccepted() {
		return accepted;
	}

	public long getInitialTokenAllocationRate() {
		return initialTAR;
	}

	public GiaOverlayContact getX() {
		return x;
	}

	@Override
	public long getGnutellaPayloadSize() {
		return 3;
	}

	@Override
	public String toString() {
		return "GiaHandshake3(x=" + x + ", accepted=" + true + ")";
	}

}
