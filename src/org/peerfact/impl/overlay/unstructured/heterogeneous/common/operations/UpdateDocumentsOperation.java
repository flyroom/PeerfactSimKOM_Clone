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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.AbstractGnutellaLikeNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaAck;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaResources;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.SeqMessage;

/**
 * This operation transmits a node's resources to other nodes, e.g. if the
 * Gnutella overlay supports replication like in Gia or GNutella06v2. This
 * operation is acknowledged.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class UpdateDocumentsOperation<TContact extends GnutellaLikeOverlayContact>
		extends
		ReqRespOperation<TContact, AbstractGnutellaLikeNode<TContact, ?>, Object> {

	private ConnectionManager<?, TContact, ?, ?> mgr;

	public UpdateDocumentsOperation(
			AbstractGnutellaLikeNode<TContact, ?> component,
			ConnectionManager<?, TContact, ?, ?> mgr, TContact to,
			OperationCallback<Object> callback) {
		super(component, to, callback);
		this.mgr = mgr;
	}

	@Override
	public void execute() {
		if (!getComponent().getResources().isEmpty()) {
			super.execute();
		}
		else {
			operationFinished(true);
			// if no resources are shared, no replication information needs to
			// be
			// transmitted.
		}
	}

	@Override
	protected AbstractGnutellaMessage createReqMessage() {
		return new GnutellaResources<TContact>(getComponent().getOwnContact(),
				getComponent().getResources());
	}

	@Override
	protected long getTimeout() {
		return this.getComponent().getConfig().getResponseTimeout();
	}

	@Override
	protected boolean gotResponse(SeqMessage response) {
		if (response instanceof GnutellaAck) {
			operationFinished(true);
			return true;
		}
		return false;
	}

	@Override
	protected void timeoutOccured() {

		if (!mgr.foundDeadContact(this.getTo())) {
			this.scheduleImmediately(); // Retry
		}
		operationFinished(false);
	}

	@Override
	public Object getResult() {
		return null;
	}

}
