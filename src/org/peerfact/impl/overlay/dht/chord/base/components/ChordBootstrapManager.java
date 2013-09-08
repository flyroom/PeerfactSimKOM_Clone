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

package org.peerfact.impl.overlay.dht.chord.base.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;


/**
 * This class contains the list of all active nodes. When a node join the
 * network, it chooses one of active nodes to send a join-request to.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class ChordBootstrapManager implements
		BootstrapManager<AbstractChordNode> {

	// private static Logger log = SimLogger
	// .getLogger(ChordBootstrapManager.class);

	/**
	 * list of active node
	 */
	private final List<AbstractChordNode> availableNodes = new LinkedList<AbstractChordNode>();

	private static final List<ChordBootstrapManager> instances = new Vector<ChordBootstrapManager>();

	public ChordBootstrapManager() {
		instances.add(this);
	}

	public static ChordBootstrapManager getInstance(ChordID id) {
		for (ChordBootstrapManager cbm : instances) {
			for (AbstractChordNode node : cbm.getAvailableNodes()) {
				if (node.getOverlayID().equals(id)) {
					return cbm;
				}
			}
		}
		return null;
	}

	public static List<AbstractChordNode> getAllAvailableNodes() {
		List<AbstractChordNode> result = new Vector<AbstractChordNode>();
		for (ChordBootstrapManager cbm : instances) {
			result.addAll(cbm.getAvailableNodes());
		}
		return result;
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> list = new LinkedList<TransInfo>();
		for (AbstractChordNode cNode : availableNodes) {
			list.add(cNode.getLocalOverlayContact()
					.getTransInfo());
		}
		return list;
	}

	@Override
	public void registerNode(AbstractChordNode node) {
		if (node != null) {
			availableNodes.add(node);
		}
	}

	@Override
	public void unregisterNode(AbstractChordNode node) {

		if (node != null) {
			AbstractChordNode n = node;
			availableNodes.remove(n);
		}
	}

	public boolean isEmpty() {
		return getAvailableNodes().isEmpty();
	}

	public List<AbstractChordNode> getAvailableNodes() {
		return new ArrayList<AbstractChordNode>(availableNodes);
	}

	public int getNumOfAvailableNodes() {
		return getAvailableNodes().size();
	}

	public AbstractChordNode getOverlayNode(TransInfo transInfo) {
		for (AbstractChordNode node : availableNodes) {
			if (node.getTransInfo().equals(transInfo)) {
				return node;
			}
		}
		return null;
	}

	public AbstractChordContact getRandomAvailableNode() {
		int index = Simulator.getRandom().nextInt(availableNodes.size());
		return availableNodes.get(index).getLocalOverlayContact();
	}
}
