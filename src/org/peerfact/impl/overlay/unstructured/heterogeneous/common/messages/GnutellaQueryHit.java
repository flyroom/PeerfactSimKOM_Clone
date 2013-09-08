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
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;


/**
 * Sent to the initiator of a query by a peer (ultrapeer in Gnutella06) that
 * shares a matching document, either by itself or through one of its leaves.
 * Encapsulates multiple query hits, one for every match in the local content,
 * including the replicated content (QRP, One-hop replication).
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaQueryHit<TContact extends GnutellaLikeOverlayContact>
		extends AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 786191060178833089L;

	private List<QueryHit<TContact>> hits;

	private int queryUID;

	/**
	 * Creates a new query hit message, given the query's identifier and the
	 * given list of query hits.
	 * 
	 * @param queryUID
	 * @param hits
	 */
	public GnutellaQueryHit(int queryUID, List<QueryHit<TContact>> hits) {
		this.hits = hits;
		this.queryUID = queryUID;
	}

	/**
	 * Returns the UID of the query that caused this query hit message.
	 * 
	 * @return
	 */
	public int getQueryUID() {
		return queryUID;
	}

	/**
	 * Returns the query hits that are encapsulated in this message.
	 * 
	 * @return
	 */
	public List<QueryHit<TContact>> getQueryHits() {
		return hits;
	}

	@Override
	public long getGnutellaPayloadSize() {
		return 4 + hits.size() * QueryHit.getSize();
	}

	@Override
	public String toString() {
		return "QUERY_HIT: queryUID=" + queryUID + ", hits=" + hits;
	}

}
