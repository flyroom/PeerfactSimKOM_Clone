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

import java.util.ArrayList;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSClientNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayID;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayKey;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.LookupRequestMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.messages.LookupResultMsg;
import org.peerfact.impl.simengine.Simulator;


public class LookupOperation extends
		AbstractOperation<CSClientNode, DHTObject> implements
		TransMessageCallback {

	private LookupRequestMsg queryMsg;

	public CSClientNode node;

	private LookupResultMsg replyMsg;

	private ArrayList<OverlayContact<?>> responsible = new ArrayList<OverlayContact<?>>();

	public LookupOperation(CSClientNode node,
			CSOverlayID serverID,
			CSOverlayKey docKey, OperationCallback<DHTObject> callback) {
		super(node, callback);
		this.node = node;
		this.queryMsg = new LookupRequestMsg(node.getClientID(), serverID,
				docKey);
	}

	@Override
	public void execute() {
		TransInfo serverInfo = node.getServerAddress();
		this.node.getTransLayer().sendAndWait(this.queryMsg, serverInfo,
				node.getPort(), TransProtocol.UDP, this,
				2 * Simulator.SECOND_UNIT);
	}

	@Override
	protected void operationTimeoutOccured() {
		log.warn("Message Timeout");
		operationFinished(false);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		log.warn("Message Timeout");
		// Inform Monitor
		Simulator.getMonitor().dhtLookupFailed(node.getLocalOverlayContact(),
				queryMsg.getKey());
		operationFinished(false);
	}

	@Override
	public DHTObject getResult() {
		return this.replyMsg.getDHTObject();
	}

	@Override
	public void receive(Message msg, TransInfo senderAddr, int commId) {
		if (msg instanceof LookupResultMsg) {
			this.replyMsg = (LookupResultMsg) msg;
			log.debug("received reply " + msg);
			assert this.replyMsg.getKey() == this.queryMsg.getKey();
			// Inform Monitor
			responsible.add(0, node.getLocalOverlayContact());
			Simulator.getMonitor().dhtLookupFinished(
					node.getLocalOverlayContact(), this.queryMsg.getKey(),
					responsible, 1);
			operationFinished(true);
		} else {
			log.warn("Received unknown msg type: " + msg);
		}

	}

	public CSOverlayKey getKey() {
		return this.queryMsg.getKey();
	}

}
