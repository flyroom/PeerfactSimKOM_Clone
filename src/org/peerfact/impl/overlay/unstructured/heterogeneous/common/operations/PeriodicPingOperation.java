/*

 * Copyright (c) 2011 University of Paderborn - UPB
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
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IManageableConnection;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IPongHandler;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.AbstractGnutellaMessage;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPing;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.SeqMessage;
import org.peerfact.impl.simengine.Simulator;

/**
 * This operation is periodically triggered for each connected peer. It sends a
 * ping to the connected peer and waits for a pong replied. This operation
 * should only be triggered by the connection manager.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PeriodicPingOperation<TContact extends GnutellaLikeOverlayContact, TPongType extends GnutellaPong<TContact>>
		extends
		ReqRespOperation<TContact, AbstractGnutellaLikeNode<TContact, ?>, Object> {

	private AbstractGnutellaLikeNode<TContact, ?> component;

	IManageableConnection<TContact, ?> conn;

	boolean stopped = false;

	IPongHandler<TContact, TPongType> pongHdlr;

	/**
	 * Creates a new ping operation
	 * 
	 * @param component
	 *            : the main component of the node that triggers this operation
	 * @param to
	 *            : the
	 * @param pongHdlr
	 * @param conn
	 * @param callback
	 */
	public PeriodicPingOperation(
			AbstractGnutellaLikeNode<TContact, ?> component,
			TContact to, IPongHandler<TContact,
			TPongType> pongHdlr,
			IManageableConnection<TContact, ?> conn,
			OperationCallback<Object> callback) {
		super(component, to, callback);
		this.conn = conn;
		this.pongHdlr = pongHdlr;
		this.component = component;
	}

	@Override
	protected void execute() {
		if (stopped) {
			return;
		}
		super.execute();
		this.scheduleWithDelay(component.getConfig().getPingInterval()); // Ping
																			// is
																			// rescheduled.
	}

	@Override
	protected AbstractGnutellaMessage createReqMessage() {
		return new GnutellaPing<TContact>(component.getOwnContact());
	}

	public void stop() {
		stopped = true;
		finished(true);
	}

	@Override
	public Object getResult() {
		// No result for this operation
		return null;
	}

	void finished(boolean succeeded) {
		this.operationFinished(succeeded);
	}

	@Override
	protected long getTimeout() {
		return component.getConfig().getPingTimeout();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean gotResponse(SeqMessage response) {
		if (stopped) {
			operationFinished(true);
			return true;
		}
		if (!(response instanceof GnutellaPong)) {
			return false;
		}
		TPongType reply = ((TPongType) response);

		conn.markAsAlive();
		if (pongHdlr != null) {
			pongHdlr.receivedPong(reply);
		}
		operationFinished(true);
		return true;
	}

	@Override
	protected void timeoutOccured() {
		if (stopped) {
			return;
		}
		conn.markAsDead();
		Simulator.getMonitor().unstructuredPingTimeouted(
				getComponent().getOwnContact(), getTo());
		operationFinished(false);
	}

}
