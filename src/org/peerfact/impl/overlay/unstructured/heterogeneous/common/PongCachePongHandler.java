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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common;

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;


/**
 * Manages incoming pong messages and creates outgoing ones. Keeps the pong
 * cache up-to-date and tells the connection manager about freshly discovered
 * nodes.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class PongCachePongHandler<TContact extends GnutellaLikeOverlayContact, TPongMsg extends GnutellaPong<TContact>>
		implements IPongHandler<TContact, TPongMsg> {

	private ConnectionManager<?, TContact, ?, TPongMsg> mgr;

	private PongCache<TContact> localPongCache;

	private IGnutellaConfig config;

	private boolean considerOnlyLastEntry;

	/**
	 * Creates a new pong handler for the specified node.
	 * 
	 * @param node
	 */
	public PongCachePongHandler(AbstractGnutellaLikeNode<TContact, ?> node,
			ConnectionManager<?, TContact, ?, TPongMsg> mgr) {
		config = node.getConfig();
		considerOnlyLastEntry = config.getConsiderOnlyLastEntry();
		localPongCache = new PongCache<TContact>(config);
		localPongCache.setEntry(0, node.getOwnContact());
		this.mgr = mgr;
	}

	@Override
	public void receivedPong(TPongMsg reply) {
		updatePongCache(reply.getPongCache(), reply.getSender());
	}

	/**
	 * Updates the local pong cache, given a pong message
	 * 
	 * @param remotePongCache
	 * @param sender
	 */
	protected void updatePongCache(PongCache<TContact> remotePongCache,
			TContact sender) {

		int size = localPongCache.getSize();
		Set<GnutellaLikeOverlayContact> seenContacts = new LinkedHashSet<GnutellaLikeOverlayContact>();
		for (int i = 0; i < localPongCache.getSize(); i++) {
			seenContacts.add(localPongCache.getEntry(i));
		}
		for (int i = 1; i < size; i++) {
			TContact contact = remotePongCache.getEntry(i - 1);
			if (contact != null && !seenContacts.contains(contact)) {
				if (!considerOnlyLastEntry) {
					newHostSeen(contact);
				}
				localPongCache.setEntry(i, contact);
			}
		}

		TContact lastEntry = remotePongCache.getEntry(size - 1);
		if (lastEntry != null) {
			newHostSeen(lastEntry);
		}
	}

	/**
	 * Called when a new host has been discovered in a pong message
	 * 
	 * @param c
	 */
	private void newHostSeen(TContact c) {
		mgr.seenContact(c);
	}

	@Override
	public abstract TPongMsg generatePongMessage(
			TContact requestingContact,
			TContact thisContact);

	/**
	 * Returns the local pong cache of this node.
	 * 
	 * @return
	 */
	public PongCache<TContact> getLocalPongCache() {
		return localPongCache;
	}

	protected ConnectionManager<?, TContact, ?, ?> getConnectionMgrOfOwner() {
		return mgr;
	}

}
