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

package org.peerfact.impl.overlay.dht.chord.rechord.operations;

import java.math.BigInteger;
import java.util.LinkedList;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.rechord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.rechord.components.ChordRoutingTable;


public class StabilisationOperation extends
		AbstractChordOperation<AbstractChordNode> {

	private ChordNode masterNode;

	private LinkedList<ChordRoutingTable> deletedVirtualNodes;

	public StabilisationOperation(AbstractChordNode component) {
		super(component);
		masterNode = (ChordNode) component;
	}

	@Override
	protected void execute() {

		// to check weather or not this node got new neighbors in this round we
		// calculate the sum of the
		// current neighbors ids - if they match the new neighbors sum the
		// neighbors did not change with
		// high probability
		BigInteger neighborSum = new BigInteger("0");

		if (masterNode.getChordRoutingTable() != null) {
			for (OverlayContact<ChordID> c : masterNode.getChordRoutingTable()
					.getNeighbors()) {
				neighborSum = neighborSum.add(c.getOverlayID()
						.getUniqueValue());
			}

			if (getComponent().isPresent()) {
				masterNode.createLinkOperationCounter = 0;

				// 0. commit all edges that have to be added
				masterNode.commitNewEdges();

				// 1. update virtual Nodes!
				deletedVirtualNodes = masterNode.createVirtualNodes();

				// 2. Overlapping Neighborhod.
				masterNode.removeOverlappingNeighborhoods();

				masterNode.createLinkOperationCounter = 0;
				// 3. find Closest Real Neighbors
				masterNode.findClosestRealNeighbors();
				// 4. linearization
				masterNode.linearization();

				// 5. create Ring edges
				masterNode.createAllRingEdges();
				masterNode.forwardAllRingEdges();

				// 6. connection Edges.
				masterNode.connectVirtualNodes();
				masterNode.forwardAllCEdges();

				// log.debug(masterNode + ": created " +
				// masterNode.createLinkOperationCounter + " Link operations.");
				masterNode.createLinkOperationCounter = 0;

			}

			for (OverlayContact<ChordID> c : masterNode.getChordRoutingTable()
					.getNeighbors()) {
				neighborSum = neighborSum.subtract(c
						.getOverlayID().getUniqueValue());
			}

			if (neighborSum.compareTo(new BigInteger("0")) == 0) {
				// nothing changed
				masterNode.inRechordStableState = true;
			} else {
				// got new neighbors!
				masterNode.inRechordStableState = false;
			}
		}

		// make sure this operation is scheduled again.
		new StabilisationOperation(masterNode)
				.scheduleWithDelay(ChordConfiguration.UPDATE_VIRTUAL_NODES_INTERVAL);
		operationFinished(true);
	}

	@Override
	public AbstractChordNode getResult() {
		return null;
	}

	public LinkedList<ChordRoutingTable> getDeletedVirtualNodes() {
		return deletedVirtualNodes;
	}

}
