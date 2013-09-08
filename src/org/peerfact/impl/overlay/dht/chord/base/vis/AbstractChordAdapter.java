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

package org.peerfact.impl.overlay.dht.chord.base.vis;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public abstract class AbstractChordAdapter extends OverlayAdapter {

	protected Map<NetID, AbstractChordNode> overlayImpls = new LinkedHashMap<NetID, AbstractChordNode>();

	protected Map<Host, EdgeHandle> predecessors = new LinkedHashMap<Host, EdgeHandle>();

	protected Map<NetID, Set<NetID>> fingertables = new LinkedHashMap<NetID, Set<NetID>>();

	protected Map<AbstractChordNode, Integer> docCount = new LinkedHashMap<AbstractChordNode, Integer>();

	@Override
	public Object getBootstrapManagerFor(OverlayNode<?, ?> nd) {
		if (nd instanceof AbstractChordNode) {
			return ((AbstractChordNode) nd).getBootstrapManager();
		} else {
			return null;
		}
	}

	@Override
	public String getOverlayName() {
		return "Chord";
	}

	@Override
	public abstract void handleLeavingHost(Host host);

	@Override
	public abstract void handleNewHost(Map<String, Serializable> attributes,
			Host host,
			OverlayNode<?, ?> overlayNode);

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		// currently not needed

	}

	@Override
	public abstract void handleOperation(Host host, Operation<?> op,
			boolean finished);

	@Override
	public abstract void handleOverlayMsg(Message omsg, Host from,
			NetID fromID,
			Host to, NetID toID);

	protected void updateDocCount(NetID nodeID) {
		AbstractChordNode node = overlayImpls.get(nodeID);
		if (node != null) {
			int newDocCount = node.getDHT().getNumberOfDHTEntries();
			int oldDocCount = docCount.get(node);

			if (newDocCount != oldDocCount) {
				getTranslator().nodeAttributeChanged(nodeID, "doc_count",
						newDocCount);
				docCount.put(node, newDocCount);
				log.debug("[ChordAdapter - 563] - Node " + nodeID.toString()
						+ " : Number of documents changed from [" + oldDocCount
						+ "] to [" + newDocCount + "]");
			}
		}
	}

	@SuppressWarnings("static-method")
	protected NetID netID(Host host) {
		return host.getNetLayer().getNetID();
	}

	@SuppressWarnings("static-method")
	protected NetID getOverlayPredecessor(Host fromNode) {
		AbstractChordRoutingTable crt = ((AbstractChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getChordRoutingTable();
		if (crt == null) {
			return null;
		}
		AbstractChordContact pre = crt.getPredecessor();
		if (pre != null) {
			return pre.getTransInfo().getNetId();
		} else {
			return null;
		}
	}

	protected static NetID getOverlaySuccessor(Host fromNode) {
		AbstractChordRoutingTable crt = ((AbstractChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getChordRoutingTable();
		if (crt == null) {
			return null;
		}
		AbstractChordContact pre = crt.getSuccessor();
		if (pre != null) {
			return pre.getTransInfo().getNetId();
		} else {
			return null;
		}
	}

	protected abstract void refreshFingertable(Host fromNode);

	@Override
	public SchematicPositioner getNewPositioner() {
		return new ChordRingPositioner();
	}

}
