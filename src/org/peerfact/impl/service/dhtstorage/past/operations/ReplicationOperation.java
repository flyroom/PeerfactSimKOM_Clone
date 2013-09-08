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

package org.peerfact.impl.service.dhtstorage.past.operations;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.service.dhtstorage.past.PASTConfig;
import org.peerfact.impl.service.dhtstorage.past.PASTObject;
import org.peerfact.impl.service.dhtstorage.past.PASTService;
import org.peerfact.impl.service.dhtstorage.past.messages.PASTMessage;
import org.peerfact.impl.service.dhtstorage.past.messages.StoreReplicationMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * Determines a node that should store a replica of a given object. The
 * Operation sends a StoreReplicationMessage to the determined node. If this
 * succeeds the node is included into the set of replica holders for this file.
 */
public class ReplicationOperation extends AbstractPASTOperation {

	private PASTObject object;

	/**
	 * Nodes that are contacted during this operation
	 */
	private TransInfo recipient;

	private Set<TransInfo> ignore;

	private BigInteger distance;

	/**
	 * Messages that are distributed by this operation
	 */
	private PASTMessage message;

	/**
	 * Create a new ReplicationOperation
	 * 
	 * @param component
	 * @param callback
	 * @param config
	 * @param objectToReplicate
	 *            The Object that is to be replicated
	 * @param replicatorNodes
	 *            Nodes this object is supposed to be replicated on after this
	 *            operation
	 */
	public ReplicationOperation(PASTService component,
			OperationCallback<Object> callback, PASTConfig config,
			PASTObject objectToReplicate, AbstractChordContact target,
			Set<TransInfo> ignore) {
		super(component, callback, config);
		this.object = objectToReplicate;
		this.ignore = ignore;
		if (target != null) {
			this.recipient = getComponent().convertTransInfo(
					target.getTransInfo());
			getComponent();
			distance = target.getOverlayID().getMinDistance(
					PASTService.getIDForKey(getKey()));
		}

	}

	@Override
	protected void execute() {

		// getComponent().getHost().getComponent(AbstractChordNode.class).nodeLookup((ChordKey)object.getKey(),
		// this, true);
		if (recipient == null) {
			Set<TransInfo> ignoreTargets = new LinkedHashSet<TransInfo>(
					object.getReplicationHolders());
			if (ignore != null) {
				ignoreTargets.addAll(ignore);
			}
			ignoreTargets.add(getComponent().getOwnTransInfo());
			getComponent();
			Map<TransInfo, BigInteger> targets = getComponent().getTargets(
					PASTService.getIDForKey(object.getKey()), ignoreTargets);
			if (targets.isEmpty()) {
				operationFinished(false);
			} else {
				recipient = getComponent().getMinimum(targets);
				distance = targets.get(recipient);
			}
		}
		if (recipient != null) {
			message = new StoreReplicationMessage(object.getKey(),
					object.getValue(), object.getReplications());
			long timeout = 1
					* Simulator.MINUTE_UNIT
					+ (int) (message.getSize() / getComponent().getHost()
							.getNetLayer().getCurrentBandwidth().getUpBW());
			sendMessage(message, recipient, getConfig()
					.getNumberOfReplicationTries(), timeout);
		} else {
			operationFinished(false);
		}
	}

	@Override
	protected void sendMessageSucceeded() {
		object.getReplications().put(recipient, distance);
		operationFinished(true);
	}

	@Override
	protected void sendMessageFailed() {
		operationFinished(false);
	}

	/**
	 * This operation has no result
	 */
	@Override
	public Object getResult() {
		return object;
	}

	public TransInfo getRecipient() {
		return recipient;
	}

	public DHTKey<?> getKey() {
		return object.getKey();
	}

}
