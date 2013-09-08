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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations;

import java.math.BigInteger;
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaConfiguration;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayNode;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing.FilesharingKey;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class PushOperation extends BaseOperation {

	private GnutellaOverlayNode node;

	private GnutellaOverlayID pushTarget = null;

	public PushOperation(GnutellaOverlayNode node, BigInteger descriptor,
			FilesharingKey key, OperationCallback<Object> callback) {
		super(node, GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL,
				GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL, 0, descriptor,
				callback);
		this.node = node;

	}

	public PushOperation(GnutellaOverlayNode node, int ttl, int hops,
			BigInteger descriptor,
			OverlayContact<GnutellaOverlayID> pushInitiator,
			GnutellaOverlayID pushTarget, FilesharingKey key,
			OperationCallback<Object> callback) {
		super(node, GnutellaConfiguration.GNUTELLA_QUERY_MAX_TTL, ttl, hops,
				descriptor, callback);
		this.node = node;

		this.pushTarget = pushTarget;

	}

	@Override
	protected void execute() {
		if (pushTarget == null) {
			List<OverlayContact<GnutellaOverlayID>> queryResults = node
					.getQueryResults(descriptor);
			if (queryResults.isEmpty()) {
				this.operationFinished(false);
				return;
			} else {
				this.pushTarget = queryResults.get(0).getOverlayID();
			}
		}
		// TODO Push Operation
		/*
		 * OverlayContact<GnutellaOverlayID> contactReceiver =
		 * ((GnutellaOverlayRoutingTable)
		 * getComponent().getRoutingTable()).outgoingPush(this.pushInitiator,
		 * this.descriptor); PushMessage message = new
		 * PushMessage((GnutellaOverlayID) this.getComponent().getOverlayID(),
		 * contactReceiver.getOverlayID(), this.ttl, this.hops, this.descriptor,
		 * this.pushInitiator, this.pushTarget, this.key);
		 * this.getComponent().getTransLayer().send(message,
		 * contactReceiver.getTransInfo(), this.getComponent().getPort(),
		 * TransProtocol.UDP); this.operationFinished(true);
		 */
	}

}
