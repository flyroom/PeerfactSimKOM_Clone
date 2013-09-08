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

package org.peerfact.impl.overlay.dht.pastry.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.messages.PastryBaseMsg;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class PastryAdapter extends OverlayAdapter {

	protected Map<NetID, PastryNode> overlayImpls = new LinkedHashMap<NetID, PastryNode>();

	protected Map<NetID, Set<EdgeHandle>> neighbors = new LinkedHashMap<NetID, Set<EdgeHandle>>();

	protected Map<NetID, Set<EdgeHandle>> leafset = new LinkedHashMap<NetID, Set<EdgeHandle>>();

	protected Map<NetID, Set<EdgeHandle>> routingTable = new LinkedHashMap<NetID, Set<EdgeHandle>>();

	protected Map<PastryNode, Integer> numOfNeighbors = new LinkedHashMap<PastryNode, Integer>();

	protected Map<PastryNode, Integer> numOfLeafs = new LinkedHashMap<PastryNode, Integer>();

	protected Map<PastryNode, Integer> numOfRTNeighbors = new LinkedHashMap<PastryNode, Integer>();

	public PastryAdapter() {
		addOverlayImpl(PastryNode.class);

		addOverlayImpl(PastryBaseMsg.class);

		addOverlayNodeMetric(NeighborsOfPeer.class);
		addOverlayNodeMetric(LeafsOfPeer.class);
		addOverlayNodeMetric(RoutingTableNeighborsOfPeer.class);
		addOverlayNodeMetric(PastryIDM.class);
	}

	@Override
	public String getOverlayName() {
		return "Pastry";
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {
		if (!(overlayNode instanceof PastryNode)) {
			return;
		}
		PastryNode pNode = (PastryNode) overlayNode;

		/*
		 * Initialize all node attributes
		 */
		attributes.put("pastry_id", pNode.getOverlayID().toString());

		attributes.put("num_neighbors", pNode.getNumOfAllNeighbors());
		numOfNeighbors.put(pNode, pNode.getNumOfAllNeighbors());

		int numLeafs = pNode.getLeafSetNodes().size();
		attributes.put("num_leafs", numLeafs);
		numOfLeafs.put(pNode, numLeafs);

		int numRTNeighbors = pNode.getRoutingTableNodes().size() - 1;
		attributes.put("num_rt_neighbors", numRTNeighbors);
		numOfRTNeighbors.put(pNode, numRTNeighbors);

		overlayImpls.put(host.getNetLayer().getNetID(), pNode);
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		if (!(overlayNode instanceof PastryNode))
		{
			return;
			// PastryNode pNode = (PastryNode) overlayNode;
		}

	}

	@Override
	public void handleLeavingHost(Host host) {
		NetID nId = host.getNetLayer().getNetID();

		// Remove all edges from or to this node
		removeHostFromMap(nId, neighbors);
		removeHostFromMap(nId, leafset);
		removeHostFromMap(nId, routingTable);
	}

	private static void removeHostFromMap(NetID nId,
			Map<NetID, Set<EdgeHandle>> set) {
		set.remove(nId);
		for (Entry<NetID, Set<EdgeHandle>> enrty : set.entrySet()) {
			Set<EdgeHandle> nSet = enrty.getValue();
			Iterator<EdgeHandle> it = nSet.iterator();
			while (it.hasNext()) {
				EdgeHandle edge = it.next();
				if (edge.getTo().equals(nId)) {
					edge.remove();
					it.remove();
				}
			}
			// for (EdgeHandle edge : nSet)
			// if (edge.getTo().equals(nId)) {
			// edge.remove();
			// nSet.remove(edge);
			// }
		}
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {

		if (from != null) {
			updateNeighbors(from);
			updateNeighborCount(fromID);
			updateLeafCount(fromID);
		}
		if (to != null) {
			updateNeighbors(to);
			updateNeighborCount(toID);
			updateLeafCount(toID);
		}

	}

	@Override
	public Object getBootstrapManagerFor(OverlayNode<?, ?> nd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {

		if (host != null) {
			updateNeighbors(host);

			updateNeighborCount(host.getNetLayer().getNetID());
			updateLeafCount(host.getNetLayer().getNetID());
		}
	}

	@Override
	public SchematicPositioner getNewPositioner() {
		return new PastryRingPositioner();
	}

	// private void updateAllNeighborsOfAllHosts() {
	// List<Host> hosts = GlobalOracle.getHosts();
	//
	// for (Host host : hosts) {
	// updateNeighbors(host);
	// }
	// }

	private void updateNeighbors(Host h) {

		NetID nId = h.getNetLayer().getNetID();

		PastryNode pNode = overlayImpls.get(nId);
		if (pNode == null) {
			return;
		}

		/*
		 * Handle changes in the leaf set
		 */
		Set<EdgeHandle> nSet = leafset.get(nId);
		if (nSet == null) {
			nSet = new LinkedHashSet<EdgeHandle>();
			leafset.put(nId, nSet);
		}
		renewSet(h, nSet, pNode.getLeafSetNodes(), "Leaf Set Entry",
				Color.YELLOW);
		updateLeafCount(nId);

		/*
		 * Handle changes in the neighbor set
		 */
		nSet = neighbors.get(nId);
		if (nSet == null) {
			nSet = new LinkedHashSet<EdgeHandle>();
			neighbors.put(nId, nSet);
		}
		renewSet(h, nSet, pNode.getNeighborSetNodes(),
				"Neighborhood Set Entry", Color.GRAY);
		updateNeighborCount(nId);

		/*
		 * Handle changes in the routing table
		 */
		nSet = routingTable.get(nId);
		if (nSet == null) {
			nSet = new LinkedHashSet<EdgeHandle>();
			routingTable.put(nId, nSet);
		}
		renewSet(h, nSet, pNode.getRoutingTableNodes(), "Routing Table Entry",
				Color.ORANGE);
		updateRTNeighborsCount(nId);
	}

	public void renewSet(Host h, Set<EdgeHandle> knownNeighbors,
			Collection<PastryContact> currentNeighbors, String edgeName,
			Color edgeColor) {
		Set<NetID> oldEdges = new LinkedHashSet<NetID>();
		LinkedHashMap<NetID, EdgeHandle> oldHandles = new LinkedHashMap<NetID, EdgeHandle>();

		for (EdgeHandle e : knownNeighbors) {
			NetID to = e.getTo();
			oldEdges.add(to);
			oldHandles.put(to, e);
		}

		Set<NetID> newEdges = new LinkedHashSet<NetID>();
		for (PastryContact c : currentNeighbors) {
			newEdges.add(c.getTransInfo().getNetId());
		}

		Set<NetID> toAdd = new LinkedHashSet<NetID>(newEdges);
		toAdd.removeAll(oldEdges);

		Set<NetID> toRemove = new LinkedHashSet<NetID>(oldEdges);
		toRemove.removeAll(newEdges);

		for (NetID id : toAdd) {
			EdgeHandle handle = addEdge(h.getNetLayer().getNetID(), id,
					edgeColor, edgeName);
			knownNeighbors.add(handle);
		}

		for (NetID id : toRemove) {
			EdgeHandle handle = oldHandles.remove(id);
			handle.remove();
		}
	}

	private void updateNeighborCount(NetID nodeID) {
		PastryNode node = overlayImpls.get(nodeID);
		if (node != null) {
			int newCount = node.getNumOfAllNeighbors();
			int oldCount = numOfNeighbors.get(node);

			if (newCount != oldCount) {
				getTranslator().nodeAttributeChanged(nodeID, "num_neighbors",
						newCount);
				numOfNeighbors.put(node, newCount);
			}
		}
	}

	private void updateLeafCount(NetID nodeID) {
		PastryNode node = overlayImpls.get(nodeID);
		if (node != null) {
			int newCount = node.getLeafSetNodes().size();
			int oldCount = numOfLeafs.get(node);

			if (newCount != oldCount) {
				getTranslator().nodeAttributeChanged(nodeID, "num_leafs",
						newCount);
				numOfNeighbors.put(node, newCount);
			}
		}
	}

	private void updateRTNeighborsCount(NetID nodeID) {
		PastryNode node = overlayImpls.get(nodeID);
		if (node != null) {
			int newCount = node.getRoutingTableNodes().size() - 1;
			int oldCount = numOfRTNeighbors.get(node);

			if (newCount != oldCount) {
				getTranslator().nodeAttributeChanged(nodeID,
						"num_rt_neighbors", newCount);
				numOfRTNeighbors.put(node, newCount);
			}
		}
	}
}
