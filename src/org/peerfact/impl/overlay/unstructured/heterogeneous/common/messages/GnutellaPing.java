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
 * The ping message is periodically sent to neighbors in order to check their
 * liveness and to request liveness information
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaPing<TContact extends GnutellaLikeOverlayContact> extends
AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7728884677687925658L;

	/**
	 * Returns the sender of this ping message
	 * 
	 * @return
	 */
	public TContact getSender() {
		return sender;
	}

	private TContact sender;

	/**
	 * Creates a new ping message with the given sender of it.
	 * 
	 * @param sender
	 */
	public GnutellaPing(TContact sender) {
		this.sender = sender;
	}

	@Override
	public long getGnutellaPayloadSize() {
		return 0;
	}

	@Override
	public String toString() {
		return "PING: sender=" + sender;
	}

}
