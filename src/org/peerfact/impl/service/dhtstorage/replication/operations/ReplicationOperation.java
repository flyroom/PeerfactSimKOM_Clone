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

package org.peerfact.impl.service.dhtstorage.replication.operations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTConfig;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTObject;
import org.peerfact.impl.service.dhtstorage.replication.ReplicationDHTService;
import org.peerfact.impl.service.dhtstorage.replication.messages.DeleteReplicationMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.KeepReplicationMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.ReplicationDHTMessage;
import org.peerfact.impl.service.dhtstorage.replication.messages.StoreReplicationMessage;


/**
 * Manages all issues of a replication. For a given ReplicationDHTObject
 * <code>obj</code> and a List of contacts <code>nodes</code> this Operation
 * will send three different Messages:
 * <ul>
 * <li>a KeepReplicationMessage to all contacts that already stored the object
 * to signal them that they should continue to do so.</li>
 * <li>a DeleteReplicationMessage to all contacts that previously stored the
 * Object and are not in the list of replicatorNodes.</li>
 * </ul>
 * 
 * For an initial distribution of a new Object the objects getReplications()
 * should return null.
 * 
 * If an object has changed this will ensure a proper update of the entry on all
 * affected nodes, this operation is also suitable for an update()-Operation as
 * long as the KEY does not change (it really should not do that)
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationOperation<K extends DHTKey<?>> extends
		ReplicationDHTAbstractOperation<K> {

	private ReplicationDHTObject<K> object;

	private List<TransInfo> replicatorNodes;

	/**
	 * Nodes that are contacted during this operation
	 */
	private List<TransInfo> recipients = new Vector<TransInfo>();

	private int actRecipient = 0;

	/**
	 * Messages that are distributed by this operation
	 */
	private LinkedHashMap<TransInfo, ReplicationDHTMessage> messages = new LinkedHashMap<TransInfo, ReplicationDHTMessage>();

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
	public ReplicationOperation(ReplicationDHTService<K> component,
			OperationCallback<Object> callback, ReplicationDHTConfig config,
			ReplicationDHTObject<K> objectToReplicate,
			List<TransInfo> replicatorNodes) {
		super(component, callback, config);
		this.object = objectToReplicate;
		this.replicatorNodes = replicatorNodes;

	}

	@Override
	protected void execute() {
		List<TransInfo> objectReplicators = object.getReplications();
		if (objectReplicators == null) {
			// publish to all new Nodes
			for (TransInfo actContact : replicatorNodes) {
				messages.put(
						actContact,
						new StoreReplicationMessage(object.getKey(), object
								.getValue(), replicatorNodes));
				recipients = replicatorNodes;
			}
		} else {
			for (TransInfo actContact : objectReplicators) {
				if (replicatorNodes == null) {
					// Object should be deleted on all replicators
					messages.put(actContact, new DeleteReplicationMessage<K>(
							object.getKey()));
				} else {
					if (replicatorNodes.contains(actContact)) {
						// Node already stores the object, keep!
						messages.put(actContact, new KeepReplicationMessage(
								object.getKey(), replicatorNodes));
					} else {
						// Node stored the object, is obsolete - delete!
						messages.put(actContact,
								new DeleteReplicationMessage<K>(
										object.getKey()));
					}
				}
				recipients.add(actContact);
			}
			if (replicatorNodes != null) {
				// Store on new Nodes
				for (TransInfo actContact : replicatorNodes) {
					if (!recipients.contains(actContact)) {
						messages.put(actContact, new StoreReplicationMessage(
								object.getKey(), object.getValue(),
								replicatorNodes));
						recipients.add(actContact);
					}
				}
			}
		}
		sendNextMessage();
	}

	protected void sendNextMessage() {
		if (actRecipient >= recipients.size()) {
			operationFinished(true);
			return;
		}
		sendMessage(messages.get(recipients.get(actRecipient)),
				recipients.get(actRecipient), getConfig()
						.getNumberOfReplicationTries());
	}

	@Override
	protected void sendMessageSucceeded() {
		actRecipient++;
		sendNextMessage();
	}

	@Override
	protected void sendMessageFailed() {
		getComponent().contactDidNotRespond(recipients.get(actRecipient));
		actRecipient++;
		sendNextMessage();
	}

	/**
	 * This operation has no result
	 */
	@Override
	public Object getResult() {
		return null;
	}

}
