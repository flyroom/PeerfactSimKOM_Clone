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
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.PongCache;

/**
 * The pong message is replied to every connected peer that requests it via a
 * ping message. It tells the requesting peer that it is alive and transmits
 * information for node discovery, like a pong cache.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaPong<TContact extends GnutellaLikeOverlayContact> extends
AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -903972431325873476L;

	/**
	 * Returns the sender of this pong message.
	 * 
	 * @return
	 */
	public TContact getSender() {
		return sender;
	}

	private TContact sender;

	private PongCache<TContact> pongCache;

	/**
	 * Creates a new pong message, given the sender of it and the pong cache
	 * that shall be transmitted.
	 * 
	 * @param sender
	 * @param pongCache
	 */
	public GnutellaPong(TContact sender, PongCache<TContact> pongCache) {
		this.sender = sender;
		this.pongCache = pongCache;
	}

	@Override
	public long getGnutellaPayloadSize() {
		if (pongCache == null) {
			return 0;
		}
		return pongCache.getSize();
	}

	/**
	 * Returns the pong cache transmitted with this message.
	 * 
	 * @return
	 */
	public PongCache<TContact> getPongCache() {
		return pongCache;
	}

	@Override
	public String toString() {
		return "PONG: sender=" + sender + ", pongCache=" + pongCache;
	}

}
