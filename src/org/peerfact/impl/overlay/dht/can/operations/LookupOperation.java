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

package org.peerfact.impl.overlay.dht.can.operations;

import java.util.ArrayList;
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.components.CanConfig;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayKey;
import org.peerfact.impl.overlay.dht.can.messages.LookupMsg;
import org.peerfact.impl.overlay.dht.can.messages.LookupReplyMsg;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * This operation starts a lookup for a certain hash value. The operation target
 * is saved and if the lookup reply arrives the operation is removed.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class LookupOperation extends
		AbstractOperation<CanNode, List<CanOverlayContact>> {

	private CanNode master;

	private CanOverlayKey target;

	private LookupMsg lookupMsg;

	private boolean succ;

	private int allreadyTried;

	private int lookupHopCount;

	private List<OverlayContact<?>> responsible = new ArrayList<OverlayContact<?>>();

	/**
	 * starts a lookup. Sends a hash and gets a CanOverlayContact
	 * 
	 * @param component
	 *            node which needs a contact to a hash value
	 * @param target
	 *            Hash value
	 * @param callback
	 */
	public LookupOperation(CanNode component, CanOverlayKey id,
			OperationCallback<List<CanOverlayContact>> callback) {
		super(component, callback);
		this.target = id;
		this.master = getComponent();
		succ = false;
		allreadyTried = 0;
	}

	@Override
	public void execute() {
		if (master.getPeerStatus() == PeerStatus.PRESENT) {
			if (succ == false && allreadyTried < CanConfig.numberLookups) {
				if (target.includedInArea(master.getLocalOverlayContact()
						.getArea())) {
					if (master.getStoredHashToID(target) != null) {
						// Notify analyzer
						responsible.add(0, this.master
								.getLocalOverlayContact());
						Simulator.getMonitor()
								.dhtLookupFinished(
										this.master.getLocalOverlayContact(),
										target,
										responsible,
										lookupHopCount);
						master.lookupStoreFinished(getOperationID());
						operationFinished(true);
					} else {
						// Inform analyzer
						Simulator.getMonitor().dhtLookupFailed(
								this.master.getLocalOverlayContact(), target);
						master.lookupStoreFinished(getOperationID());
						operationFinished(false);
					}
				} else {
					lookupMsg = new LookupMsg(master.getLocalOverlayContact()
							.getOverlayID(), master.routingNext(target)
							.getOverlayID(), master.getLocalOverlayContact()
							.clone(), target, this.getOperationID());

					master.getTransLayer().send(lookupMsg,
							master.routingNext(target).getTransInfo(),
							master.getPort(), TransProtocol.UDP);
					master.registerLookupStore(getOperationID(), this);
					// Schedule a timeout to redo the lookup after some time
					scheduleOperationTimeout(CanConfig.waitTimeToStore);

				}
				allreadyTried++;
			}
		}
	}

	/**
	 * Is used to tell the operation that a hash value was found.
	 * 
	 * @param reply
	 *            reply message with the CanOveralyContact to the hash value;
	 */
	public void found(LookupReplyMsg reply) {
		if (master.getPeerStatus() == PeerStatus.PRESENT) {
			if (reply.getResult() != null) {
				succ = true;
			}
			this.lookupHopCount = reply.getHopCount();

			// Notify analyzer.
			responsible.add(0, reply.getResult());
			Simulator.getMonitor()
					.dhtLookupFinished(this.master.getLocalOverlayContact(),
							target,
							responsible,
							lookupHopCount);
			master.lookupStoreFinished(getOperationID());
			operationFinished(true);
		}
	}

	@Override
	public void operationTimeoutOccured() {
		if (!succ && allreadyTried < CanConfig.numberLookups) {
			execute();
		} else {
			master.lookupStoreFinished(getOperationID());
			// Inform analyzer
			Simulator.getMonitor().dhtLookupFailed(
					this.master.getLocalOverlayContact(), target);
			operationFinished(false);
		}
	}

	@Override
	public List<CanOverlayContact> getResult() {
		List<CanOverlayContact> respNodes = new ArrayList<CanOverlayContact>();
		for (OverlayContact<?> contact : responsible) {
			respNodes.add((CanOverlayContact) contact);
		}
		return respNodes;
	}

	public int getLookupHopCount() {
		return lookupHopCount;
	}

	public CanOverlayKey getTarget() {
		return target;
	}

}
