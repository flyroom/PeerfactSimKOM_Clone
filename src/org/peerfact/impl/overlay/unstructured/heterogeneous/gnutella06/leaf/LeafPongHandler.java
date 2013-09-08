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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.leaf;

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.IPongHandler;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.PongCache;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.IGnutella06Config;


/**
 * The pong handler of a leaf just replies with an empty pong without a pong
 * cache or any other node discovery information. It receives pong caches from
 * the ultrapeer to receive fresh nodes to connect to in case of a failure.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LeafPongHandler
		implements
		IPongHandler<Gnutella06OverlayContact, GnutellaPong<Gnutella06OverlayContact>> {

	private ConnectionManager<?, Gnutella06OverlayContact, ?, GnutellaPong<Gnutella06OverlayContact>> mgr;

	private Gnutella06OverlayContact ownContact;

	private IGnutella06Config config;

	private boolean considerOnlyLastEntry;

	/**
	 * Creates a new pong handler for the given leaf.
	 * 
	 * @param node
	 */
	public LeafPongHandler(Leaf node) {
		ownContact = node.getOwnContact();
		config = node.getConfig();
		considerOnlyLastEntry = config.getConsiderOnlyLastEntry();
	}

	@Override
	public void receivedPong(GnutellaPong<Gnutella06OverlayContact> reply) {
		update(reply.getPongCache(), reply.getSender());
	}

	/**
	 * Updates live node information from the received pong cache and tells it
	 * to the connection manager.
	 * 
	 * @param remotePongCache
	 * @param sender
	 */
	private void update(PongCache<Gnutella06OverlayContact> remotePongCache,
			Gnutella06OverlayContact sender) {
		Set<Gnutella06OverlayContact> seenContacts = new LinkedHashSet<Gnutella06OverlayContact>();
		seenContacts.add(ownContact);
		for (int i = 0; i < remotePongCache.getSize(); i++) {
			Gnutella06OverlayContact contact = remotePongCache.getEntry(i);
			if (contact != null && !seenContacts.contains(contact)) {
				if (!considerOnlyLastEntry) {
					newHostSeen(contact);
				}
				seenContacts.add(contact);
			}
		}

		Gnutella06OverlayContact lastEntry = remotePongCache
				.getEntry(remotePongCache.getSize() - 1);
		if (lastEntry != null) {
			newHostSeen(lastEntry);
		}
	}

	private void newHostSeen(Gnutella06OverlayContact c) {
		mgr.seenContact(c);
	}

	public void setConnectionManager(
			ConnectionManager<?, Gnutella06OverlayContact, ?, GnutellaPong<Gnutella06OverlayContact>> mgr) {
		this.mgr = mgr;
	}

	@Override
	public GnutellaPong<Gnutella06OverlayContact> generatePongMessage(
			Gnutella06OverlayContact requestingContact,
			Gnutella06OverlayContact thisContact) {
		return new GnutellaPong<Gnutella06OverlayContact>(ownContact, null); // Leaves
																				// have
																				// no
																				// pong
		// cache
	}

}
