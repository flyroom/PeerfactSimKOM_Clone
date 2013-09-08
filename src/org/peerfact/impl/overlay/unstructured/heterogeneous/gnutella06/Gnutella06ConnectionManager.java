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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.AbstractGnutellaLikeNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IManageableConnection;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Gnutella06ConnectionManager<ConnectionMetadata>
		extends
		ConnectionManager<ConnectionMetadata, Gnutella06OverlayContact, IGnutella06Config, GnutellaPong<Gnutella06OverlayContact>> {

	private int foreignAttemptKickProb;

	public Gnutella06ConnectionManager(
			AbstractGnutellaLikeNode<Gnutella06OverlayContact, IGnutella06Config> owner,
			IGnutella06Config config, int size, int foreignAttemptKickProb) {
		super(owner, config, size);
		this.foreignAttemptKickProb = foreignAttemptKickProb;
	}

	/**
	 * Randomly kicks a peer with a given probability. forContact can be sent
	 * along with the disconnection attempt to suggest the kicked peer to
	 * connect to forContact, too. Returns false if no peer was kicked. Only
	 * kicks peers if the config permits it.
	 * 
	 * @param probability
	 * @param forContact
	 * @return
	 */
	protected boolean randomlyKickPeer(int probability,
			Gnutella06OverlayContact forContact) {
		if (!getConfig().randomlyKickPeer()) {
			return false;
		}

		int rand = Simulator.getRandom().nextInt(100);
		if (rand >= probability) {
			return false;
		}

		Gnutella06OverlayContact c = getRandomContact();
		if (c == null) {
			return false;
		}

		closeConnection(c, forContact);
		return true;

	}

	/**
	 * Tells the connection manager that a peer has been discovered.
	 * 
	 * @param c
	 */
	@Override
	public void seenContact(Gnutella06OverlayContact c) {
		if (!contacts.containsKey(c) && !c.equals(owner.getOwnContact())
				&& (contacts.size() < size)) {
			new Connection(c, false);
		}
	}

	/**
	 * Tells the connection manager that another peer tries to connect. Returns
	 * true if the connection manager accepts the connection.
	 * 
	 * @param contact
	 * @return
	 */
	public boolean foreignConnectionAttempt(Gnutella06OverlayContact contact) {

		// Metadata not used in this default type.

		if (contacts.size() >= size) {
			randomlyKickPeer(this.foreignAttemptKickProb, contact);
		}

		if (contacts.size() < size && !contacts.containsKey(contact)
				&& !contact.equals(owner.getOwnContact())) {
			IManageableConnection<Gnutella06OverlayContact, ConnectionMetadata> c = new Connection(
					contact, true);
			c.connectionSucceeded();// The connection succeeds immediately if
									// triggered by a foreign host.
			return true;
		}
		if (contacts.containsKey(contact))
		{
			return true; // Already connected, so let it pass.
		}
		return false;
	}
}
