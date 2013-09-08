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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.leaf;

import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.SeqMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations.ReqRespOperation;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.AbstractGnutella06Node;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.messages.Gnutella06LeafQueryRequest;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.messages.Gnutella06LeafQueryResponse;


/**
 * This operation is triggered only by leaves. It requests an ultrapeer to make
 * a query and waits for an incoming reply.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LeafQueryOperation
		extends
		ReqRespOperation<Gnutella06OverlayContact, AbstractGnutella06Node, List<QueryHit<GnutellaLikeOverlayContact>>> {

	AbstractGnutella06Node node;

	private List<QueryHit<GnutellaLikeOverlayContact>> result = null;

	private Query query;

	private int hitsWanted;

	/**
	 * Creates a new LeafQueryOperation
	 * 
	 * @param component
	 *            : the main component of the node that initiated the query
	 * @param query
	 *            : the query that has been made.
	 * @param to
	 *            : the receiver i.e. the ultrapeer that shall manage this query
	 * @param callback
	 */
	public LeafQueryOperation(
			AbstractGnutella06Node component,
			Query query,
			int hitsWanted,
			Gnutella06OverlayContact to,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		super(component, to, callback);
		this.node = component;
		this.query = query;
		this.hitsWanted = hitsWanted;
	}

	@Override
	protected AbstractGnutellaMessage createReqMessage() {
		return new Gnutella06LeafQueryRequest(query, hitsWanted, getComponent()
				.getOwnContact());
	}

	@Override
	protected long getTimeout() {
		return getComponent().getConfig().getLeafQueryDuration();
	}

	@Override
	protected boolean gotResponse(SeqMessage response) {
		if (!(response instanceof Gnutella06LeafQueryResponse)) {
			return false;
		}
		Gnutella06LeafQueryResponse queryResp = (Gnutella06LeafQueryResponse) response;

		if (queryResp.getQueryUID() == query.getQueryUID()) {
			this.result = queryResp.getQueryHits();
			boolean succeeded = QueryHit.getTotalHits(result) >= hitsWanted;
			operationFinished(succeeded);
			return succeeded;
		}
		return false;

	}

	@Override
	protected void timeoutOccured() {
		this.operationFinished(false);
	}

	@Override
	public List<QueryHit<GnutellaLikeOverlayContact>> getResult() {
		return result;
	}

}
