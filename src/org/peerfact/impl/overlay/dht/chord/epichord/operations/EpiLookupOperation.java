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

package org.peerfact.impl.overlay.dht.chord.epichord.operations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.OperationTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.epichord.components.EpiChordConfiguration;


/**
 * This operation tries to find the responsible node for a specified key
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class EpiLookupOperation
		extends
		org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation {

	private Set<AbstractChordContact> contacts;

	public EpiLookupOperation(AbstractChordNode component, ChordID target,
			OperationCallback<List<AbstractChordContact>> callback) {

		super(component, target, callback);
		contacts = new LinkedHashSet<AbstractChordContact>();
	}

	public EpiLookupOperation(AbstractChordNode component, ChordID target,
			OperationCallback<List<AbstractChordContact>> callback, int lookupId) {

		super(component, target, callback, lookupId);
		contacts = new LinkedHashSet<AbstractChordContact>();
	}

	@Override
	protected void execute() {

		if (masterNode.isPresent()) {
			// scheduleOperationTimeout(ChordConfiguration.LOOKUP_TIMEOUT);

			// register in LookupStore
			log.debug("start lookup id = " + lookupId + " redo = "
					+ redoCounter);

			// Start Operation Timer
			OperationTimer operationTime = new OperationTimer(this);
			operationTime.schedule(ChordConfiguration.OPERATION_TIMEOUT);

			AbstractChordRoutingTable routingTable = masterNode
					.getChordRoutingTable();

			if (routingTable.responsibleFor(target)) {
				// itself is responsible for the key

				deliverResult(masterNode.getLocalOverlayContact(), target,
						getLookupId(), 0);

			} else {

				// get best p contacts from cache
				AbstractChordContact[] nextContacts = ((ChordRoutingTable) routingTable)
						.getChordCache()
						.lookup(target, EpiChordConfiguration.P);

				for (AbstractChordContact nextHop : nextContacts) {

					if (addContact(nextHop)) {
						// send parallel to p best contacts which have not yet
						// been used for this lookup operation
						if (masterNode.getOverlayID().compareTo(
								nextHop.getOverlayID()) != 0) {

							LookupMessage forwardMsg = new LookupMessage(
									masterNode.getLocalOverlayContact(),
									nextHop, target,
									lookupId, 0);
							MessageTimer msgTimer = new MessageTimer(
									masterNode, forwardMsg,
									nextHop);
							masterNode.getTransLayer().sendAndWait(forwardMsg,
									nextHop.getTransInfo(),
									masterNode.getPort(),
									ChordConfiguration.TRANSPORT_PROTOCOL,
									msgTimer,
									ChordConfiguration.MESSAGE_TIMEOUT);
						}
					}
				}
			}
		} else {

			masterNode.removeLookupOperation(getLookupId());
			operationFinished(false);
		}
	}

	/**
	 * Add used contact for a lookup operation. This contact should not be used
	 * later for the same operation.
	 * 
	 * @param contact
	 *            the contact to use
	 * @return true if it was not yet used
	 */
	public boolean addContact(AbstractChordContact contact) {
		if (contacts.contains(contact)) {
			return false;
		} else {
			contacts.add(contact);
			return true;
		}
	}

}
