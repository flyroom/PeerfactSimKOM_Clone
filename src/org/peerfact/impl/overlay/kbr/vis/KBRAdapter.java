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

package org.peerfact.impl.overlay.kbr.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.util.MultiKeyMap;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupReplyMsg;


/**
 * This Adapter may be used to visualize any overlay that complies to the
 * <code>KBR</code> interface.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KBRAdapter extends OverlayAdapter {

	/**
	 * The existing edges for neighborhood relations
	 */
	private final MultiKeyMap<NetID, EdgeHandle> neighborEdges = new MultiKeyMap<NetID, EdgeHandle>();

	public KBRAdapter() {
		addOverlayImpl(KBRNode.class);
		addOverlayImpl(KBRForwardMsg.class);
		addOverlayNodeMetric(KBRIDM.class);
	}

	@Override
	public Object getBootstrapManagerFor(OverlayNode<?, ?> nd) {
		return null;
	}

	@Override
	public String getOverlayName() {
		return "KBR enabled DHT";
	}

	@Override
	public void handleLeavingHost(Host host) {
		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {
		KBRNode<?, ?, ?> kbrNode = (KBRNode<?, ?, ?>) overlayNode.getHost()
				.getOverlay(KBRNode.class);

		attributes.put("OverlayID", kbrNode.getLocalOverlayContact()
				.getOverlayID().toString());
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		/*
		 * Refresh the edges to direct neighbors of this node
		 */
		KBRNode<?, OverlayContact<?>, ?> kbrNode = (KBRNode<?, OverlayContact<?>, ?>) overlayNode
				.getHost().getOverlay(
						KBRNode.class);
		refreshDirectNeighbors(kbrNode);
	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		/*
		 * Refresh the edges to direct neighbors of this node
		 */
		KBRNode<?, OverlayContact<?>, ?> kbrNode = (KBRNode<?, OverlayContact<?>, ?>) host
				.getOverlay(KBRNode.class);
		refreshDirectNeighbors(kbrNode);
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {
		/*
		 * Draw some colored edges when KBR specific messages are sent
		 */
		if (omsg instanceof KBRForwardMsg) {
			Message appMsg = omsg.getPayload();
			if (appMsg instanceof KBRLookupMsg) {
				KBRLookupMsg<?, ?> lookupMsg = (KBRLookupMsg<?, ?>) appMsg;
				this.flashEdge(lookupMsg.getSenderContact().getTransInfo()
						.getNetId(), to.getNetLayer().getNetID(), Color.red,
						"KBR-Lookup", appMsg.getClass());
			} else if (appMsg instanceof KBRLookupReplyMsg) {
				KBRLookupReplyMsg<?, ?> replyMsg = (KBRLookupReplyMsg<?, ?>) appMsg;
				this.flashEdge(replyMsg.getSenderContact().getTransInfo()
						.getNetId(), to.getNetLayer().getNetID(), Color.green,
						"KBR-Lookup-Reply", appMsg.getClass());
			}
		}

	}

	/**
	 * Refresh the edges drawn from this node to its neighbors
	 * 
	 * @param node
	 *            the node to refresh
	 */
	private void refreshDirectNeighbors(KBRNode<?, OverlayContact<?>, ?> node) {

		// Get all current neighbors of the node
		List<OverlayContact<?>> newNeighbors = node
				.neighborSet(Integer.MAX_VALUE);

		NetID from = node.getLocalOverlayContact().getTransInfo().getNetId();
		Set<NetID> neighborsToUse = new LinkedHashSet<NetID>();

		/*
		 * Add new edges
		 */
		for (OverlayContact<?> olContact : newNeighbors) {
			NetID to = olContact.getTransInfo().getNetId();

			if (!neighborEdges.contains(from, to)) {
				// This is a new edge
				EdgeHandle newEdge = this.addEdge(from, to, Color.GRAY,
						"Neighbors");
				if (newEdge != null) {
					// The edge can be drawn
					neighborEdges.put(from, to, newEdge);
					neighborsToUse.add(to);
				}
			} else {
				// This is a already existing edge
				neighborsToUse.add(to);
			}
		}

		/*
		 * Remove edges that are not used anymore
		 */

		// Get edges to remove
		Set<EdgeHandle> toRemove = neighborEdges.removeComplementarySet(from,
				neighborsToUse);

		// Remove edges
		for (EdgeHandle edgeHandle : toRemove) {
			edgeHandle.remove();
		}

	}

}
