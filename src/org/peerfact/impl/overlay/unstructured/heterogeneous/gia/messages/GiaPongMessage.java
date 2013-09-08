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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gia.messages;

import org.peerfact.impl.overlay.unstructured.heterogeneous.common.PongCache;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gia.GiaOverlayContact;

/**
 * Like the Gnutella pong message, but additionally carries the peer's actual
 * degree and the token rate the neighbor that pinged shall use for its query
 * token bucket.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaPongMessage extends GnutellaPong<GiaOverlayContact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3263669525285086848L;

	int actualDegree;

	long tokenRate;

	public int getActualDegree() {
		return actualDegree;
	}

	public long getTokenRate() {
		return tokenRate;
	}

	public GiaPongMessage(GiaOverlayContact sender,
			PongCache<GiaOverlayContact> pongCache, int actualNumNeighbors,
			long tokenRate) {
		super(sender, pongCache);
		this.actualDegree = actualNumNeighbors;
		this.tokenRate = tokenRate;

	}

	@Override
	public long getGnutellaPayloadSize() {
		return super.getGnutellaPayloadSize() + 3;
	}

}
