/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.centralizedstorage.operations;

import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSClientNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSContact;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayID;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayKey;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.StoreRequestMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.StoreResultMsg;
import org.peerfact.impl.simengine.Simulator;


public class StoreOperation extends
		AbstractOperation<CSClientNode, Set<CSContact>> {

	private StoreRequestMsg announce;

	private CSClientNode node;

	private StoreResultMsg reply;

	private DHTObject storedValue;

	private CSOverlayKey id;

	public StoreOperation(CSClientNode node, CSOverlayID serverID,
			CSOverlayKey key, DHTObject value,
			OperationCallback<Set<CSContact>> callback) {
		super(node, callback);
		this.node = node;
		id = key;
		storedValue = value;
		this.announce = new StoreRequestMsg(node.getClientID(), serverID, key,
				value, node.getServerAddress());
	}

	@Override
	public void execute() {
		TransInfo serverInfo = node.getServerAddress();
		if (serverInfo == null) {
			log.warn("The server address cannot be resolved");
			// Notify Analyzer
			Simulator.getMonitor().dhtStoreFailed(
					this.getComponent().getLocalOverlayContact(), id,
					storedValue);
			operationFinished(false);
		} else {
			this.node.getTransLayer().sendAndWait(this.announce, serverInfo,
					node.getPort(), TransProtocol.UDP,
					new TransMessageCallback() {
						@Override
						public void messageTimeoutOccured(int commId) {
							// messageTimeoutOccured(commId);
						}

						@Override
						public void receive(Message msg, TransInfo senderInfo,
								int commId) {
							receiveMsg(msg, senderInfo, commId);
						}
					}, 2 * Simulator.SECOND_UNIT);
		}
	}

	@Override
	protected void operationTimeoutOccured() {
		// Notify Analyzer
		Simulator.getMonitor().dhtStoreFailed(
				this.getComponent().getLocalOverlayContact(), id, storedValue);
		operationFinished(false);
	}

	public void messageTimeoutOccuredNow(int commId) {
		// Notify Analyzer
		Simulator.getMonitor().dhtStoreFailed(
				this.getComponent().getLocalOverlayContact(), id, storedValue);
		operationFinished(false);
	}

	@Override
	public Set<CSContact> getResult() {
		return reply.getDescription();
	}

	public void receiveMsg(Message msg, TransInfo senderAddr, int commId) {
		if (msg instanceof StoreResultMsg) {
			this.reply = (StoreResultMsg) msg;
			log.debug("received reply " + msg);
			// Notify Analyzers - As a client we do not care where the file is
			// stored.
			Simulator.getMonitor().dhtStoreFinished(
					this.getComponent().getLocalOverlayContact(), id,
					storedValue,
					null);
			operationFinished(true);
		} else {
			log.warn("Received unknown msg type: " + msg);
		}
	}

}
