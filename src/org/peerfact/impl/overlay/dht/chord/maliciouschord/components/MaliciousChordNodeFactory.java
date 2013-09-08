/*
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

package org.peerfact.impl.overlay.dht.chord.maliciouschord.components;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNodeFactory;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.maliciouschord.operations.EMaliciousLookupOperations;
import org.peerfact.impl.simengine.Simulator;

/**
 * 
 * This class is used by Simulator to create ChordNode instance
 * 
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 * 
 */
public class MaliciousChordNodeFactory extends AbstractChordNodeFactory {

	/**
	 * Defines the quote of malicious nodes.
	 */
	private double ratioOfMaliciousNodes;

	/**
	 * Defines which malicious lookup operation should be used by all malicious
	 * nodes.
	 */
	private EMaliciousLookupOperations typeOfMaliciousLookupOperations;

	/**
	 * How long should the malicious nodes behave normally?
	 */
	private int leadTimeForMaliciousNodes;

	@Override
	public Component createComponent(Host host) {

		// Randomly create malicious nodes
		ChordNode node;
		if (Simulator.getRandom().nextDouble() < ratioOfMaliciousNodes) {
			// Create malicious node
			node = new MaliciousChordNode(host.getTransLayer(), port,
					bootstrap, typeOfMaliciousLookupOperations,
					leadTimeForMaliciousNodes);
			// allMaliciousNodes.add(node);
			// allNodes.add(node);

		} else {
			// Create normal node
			node = new ChordNode(host.getTransLayer(), port, bootstrap);
			// allNodes.add(node);

		}

		node.setPeerStatus(PeerStatus.TO_JOIN);
		return node;

	}

	/**
	 * @param quoteOfMaliciousNodes
	 *            the quoteOfMaliciousNodes to set
	 */
	public void setRatioOfMaliciousNodes(double quoteOfMaliciousNodes) {
		this.ratioOfMaliciousNodes = quoteOfMaliciousNodes;
	}

	/**
	 * @return the quoteOfMaliciousNodes
	 */
	public double getRatioOfMaliciousNodes() {
		return ratioOfMaliciousNodes;
	}

	// /**
	// * @param typeOfMaliciousLookupOperations the
	// typeOfMaliciousLookupOperations to set
	// */
	// public void setTypeOfMaliciousLookupOperations(
	// EMaliciousLookupOperations typeOfMaliciousLookupOperations) {
	// this.typeOfMaliciousLookupOperations = typeOfMaliciousLookupOperations;
	// }
	/**
	 * @param typeOfMaliciousLookupOperations
	 *            the typeOfMaliciousLookupOperations to set as string
	 */
	public void setTypeOfMaliciousLookupOperations(
			String typeOfMaliciousLookupOperations) {
		this.typeOfMaliciousLookupOperations = EMaliciousLookupOperations
				.valueOf(typeOfMaliciousLookupOperations);
	}

	/**
	 * @return the typeOfMaliciousLookupOperations
	 */
	public EMaliciousLookupOperations getTypeOfMaliciousLookupOperations() {
		return typeOfMaliciousLookupOperations;
	}

	/**
	 * @param leadTimeForMaliciousNodes
	 *            the leadTimeForMaliciousNodes to set
	 */
	public void setLeadTimeForMaliciousNodes(int leadTimeForMaliciousNodes) {
		this.leadTimeForMaliciousNodes = leadTimeForMaliciousNodes;
	}

}
