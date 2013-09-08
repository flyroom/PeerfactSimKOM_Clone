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

import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.AbstractGnutellaLikeNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IGnutellaConfig;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IManageableConnection;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaConnect;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaConnectReply;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.SeqMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * Tries to build up a connection. Sends a connect message and waits for a
 * reply. This operation should only be triggered by the connection manager.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ConnectOperation<TContact extends GnutellaLikeOverlayContact, TConfig extends IGnutellaConfig>
		extends
		ReqRespOperation<TContact, AbstractGnutellaLikeNode<TContact, TConfig>, Object> {

	private TContact to;

	IManageableConnection<TContact, ?> conn;

	private ConnectionManager<?, TContact, TConfig, ?> mgr;

	/**
	 * Creates a new connect operation
	 * 
	 * @param component
	 *            : the main component of the node that starts the connection
	 *            attempt.
	 * @param to
	 *            : the overlay contact to which the connection shall be
	 *            established.
	 * @param mgr
	 *            : the connection manager of the initiating node.
	 * @param conn
	 *            : the control interface of the connection that is maintained
	 *            by this operation
	 * @param callback
	 */
	public ConnectOperation(
			AbstractGnutellaLikeNode<TContact, TConfig> component,
			TContact to, ConnectionManager<?, TContact, TConfig, ?> mgr,
			IManageableConnection<TContact, ?> conn,
			OperationCallback<Object> callback) {
		super(component, to, callback);
		this.to = to;
		this.conn = conn;
		this.mgr = mgr;
	}

	@Override
	protected void execute() {
		super.execute();
		Simulator.getMonitor().unstructuredConnectionStarted(
				getComponent().getOwnContact(), to, this.getSequenceNumber());
	}

	@Override
	protected AbstractGnutellaMessage createReqMessage() {
		return new GnutellaConnect<TContact>(getComponent().getOwnContact(),
				getComponent()
						.hasLowConnectivity());
	}

	@Override
	public TContact getTo() {
		return to;
	}

	@Override
	public Object getResult() {
		// No result for this operation
		return null;
	}

	void finished(boolean succeeded) {
		this.operationFinished(succeeded);
	}

	protected void addReceivedUltrapeers(List<TContact> contacts) {
		if (mgr != null
				&& mgr.getNumberOfContacts() < getComponent().getConfig()
						.getTryPeersAddLimit()) {
			mgr.seenContacts(contacts);
		}
	}

	@Override
	protected long getTimeout() {
		return getComponent().getConfig().getConnectTimeout();
	}

	@Override
	protected void timeoutOccured() {
		conn.connectionTimeouted();
		Simulator.getMonitor().unstructuredConnectionTimeout(
				getComponent().getOwnContact(), getTo(),
				this.getSequenceNumber());
		finished(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean gotResponse(SeqMessage response) {

		if (response instanceof GnutellaConnectReply) {

			GnutellaConnectReply<TContact> reply = (GnutellaConnectReply<TContact>) response;

			boolean succeeded = false;

			if (reply.isConnectionAccepted()) {
				conn.connectionSucceeded();
				Simulator.getMonitor().unstructuredConnectionSucceeded(
						getComponent().getOwnContact(), getTo(),
						this.getSequenceNumber());
				succeeded = true;
			} else {
				conn.connectionFailed();
				Simulator.getMonitor().unstructuredConnectionDenied(
						getComponent().getOwnContact(), getTo(),
						this.getSequenceNumber());
			}
			addReceivedUltrapeers(reply.getTryUltrapeers());

			finished(succeeded);
			return true;
		}
		return false;
	}

}
