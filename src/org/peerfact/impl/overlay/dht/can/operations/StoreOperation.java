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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.can.components.CanConfig;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayKey;
import org.peerfact.impl.overlay.dht.can.messages.StoreMsg;
import org.peerfact.impl.simengine.Simulator;

/**
 * This operation tries to store a hash value in the CAN. It sends a store
 * message with its hash value and its contact to the area, which is responsible
 * for the hash value. The operation is saved in the CanNode and if the
 * storeReplyMsg arrives it is removed. The hash values are refreshed every
 * CanConfig.waitTimeToRefreshHash.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class StoreOperation extends
		AbstractOperation<CanNode, Set<CanOverlayContact>>
		implements OperationCallback<Set<CanOverlayContact>> {

	private CanNode master;

	private CanOverlayKey id;

	private DHTObject object;

	private StoreMsg storeMsg;

	private CanOverlayContact owner;

	private boolean succ;

	/**
	 * stores a hash value
	 * 
	 * @param component
	 *            CanNode which wants to save
	 * @param key
	 *            the hash value
	 * @param callback
	 */
	public StoreOperation(CanNode component, CanOverlayKey key,
			DHTObject object,
			OperationCallback<Set<CanOverlayContact>> callback) {
		super(component, callback);
		master = getComponent();
		this.id = key;
		this.object = object;
		succ = false;
	}

	@Override
	protected void execute() {
		if (!succ) {
			if (id.includedInArea(master.getLocalOverlayContact().getArea())) {
				found(master.getLocalOverlayContact());
				calledOperationSucceeded(this);
			} else {

				storeMsg = new StoreMsg(
						master.getLocalOverlayContact().getOverlayID(), master
								.routingNext(id).getOverlayID(), master
								.getLocalOverlayContact().clone(), id, object,
						this.getOperationID());
				master.getTransLayer().send(storeMsg,
						master.routingNext(id).getTransInfo(),
						master.getPort(), TransProtocol.UDP);
				master.registerLookupStore(getOperationID(), this);
				this.scheduleWithDelay(CanConfig.waitTimeToStore);
			}
		} else {
			calledOperationSucceeded(this);
		}
	}

	@Override
	public Set<CanOverlayContact> getResult() {
		if (!isError() && owner != null) {
			Set<CanOverlayContact> receivers = new LinkedHashSet<CanOverlayContact>();
			receivers.add(owner);
			return receivers;
		}
		return null;
	}

	/**
	 * Is used when the storeReplyMsg arrives
	 * 
	 * @param foundOwner
	 *            the owner of the store object
	 */
	public void found(CanOverlayContact foundOwner) {
		this.owner = foundOwner;
		master.addStoredHashs(id, owner);
		master.lookupStoreFinished(getOperationID());
		succ = true;
	}

	@Override
	public void calledOperationFailed(Operation<Set<CanOverlayContact>> op) {
		log.warn(Simulator.getSimulatedRealtime() + " Couldn't store data!!"
				+ " process ID " + this.getOperationID());
		// Notify analyzer
		Simulator.getMonitor().dhtStoreFailed(
				this.getComponent().getLocalOverlayContact(), id, null);
		this.operationFinished(false);
		this.scheduleWithDelay(CanConfig.waitTimeToRefreshHash);
	}

	@Override
	public void calledOperationSucceeded(Operation<Set<CanOverlayContact>> op) {
		log.debug("Store succeded");
		// Notify analyzer
		List<OverlayContact<?>> responsible = new ArrayList<OverlayContact<?>>(
				op.getResult());
		Simulator.getMonitor().dhtStoreFinished(
				this.getComponent().getLocalOverlayContact(), id, object,
				responsible);
		this.scheduleWithDelay(CanConfig.waitTimeToRefreshHash);
		operationFinished(true);
	}
}
