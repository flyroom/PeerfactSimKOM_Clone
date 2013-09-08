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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.messages;

import java.util.List;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;


/**
 * Sent to a leaf as a response to a query request the leaf has made. Carries
 * the results.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Gnutella06LeafQueryResponse extends AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3802401958585008563L;

	public List<QueryHit<GnutellaLikeOverlayContact>> queryHits;

	private int queryUID;

	/**
	 * Creates a response message with the list of query hits received and the
	 * UID of the query.
	 * 
	 * @param queryHits
	 * @param queryUID
	 */
	public Gnutella06LeafQueryResponse(
			List<QueryHit<GnutellaLikeOverlayContact>> queryHits, int queryUID) {
		this.queryHits = queryHits;
		this.queryUID = queryUID;
	}

	/**
	 * Returns a list of query hits the ultrapeer has received for the leaf's
	 * query.
	 * 
	 * @return
	 */
	public List<QueryHit<GnutellaLikeOverlayContact>> getQueryHits() {
		return queryHits;
	}

	/**
	 * Returns the UID of the query that led to the results.
	 * 
	 * @return
	 */
	public int getQueryUID() {
		return queryUID;
	}

	@Override
	public long getGnutellaPayloadSize() {

		return 4 + queryHits.size() * QueryHit.getSize();
	}

	@Override
	public String toString() {
		return "LEAF_QUERY_RESPONSE: hits=" + queryHits + ", uid=" + queryUID;
	}

}
