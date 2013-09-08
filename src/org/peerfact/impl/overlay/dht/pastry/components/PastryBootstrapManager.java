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

package org.peerfact.impl.overlay.dht.pastry.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.pastry.proximity.ProximityMetricProvider;


/**
 * This class defines the logic for all bootstrapping needed by pastry.
 * 
 * @author Julius Ruckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryBootstrapManager implements BootstrapManager<PastryNode> {

	private static PastryBootstrapManager singeltonInstance;

	private final LinkedList<PastryContact> bootstrapNodes = new LinkedList<PastryContact>();

	private final LinkedList<TransInfo> bootstrapNodesTransInfos = new LinkedList<TransInfo>();

	private final ProximityMetricProvider proxProvider = PastryConstants.PROXIMITY_PROVIDER;

	public static PastryBootstrapManager getInstance() {
		if (singeltonInstance == null) {
			singeltonInstance = new PastryBootstrapManager();
		}
		return singeltonInstance;
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		return bootstrapNodesTransInfos;
	}

	/**
	 * @param c
	 * @return the peer that is closest to a given contact according to the used
	 *         proximity metric
	 */
	public PastryContact getNearbyContact(PastryContact c) {
		int nearestDistance = Integer.MAX_VALUE;
		PastryContact nearestContact = null;

		for (PastryContact n : bootstrapNodes) {
			int d = proxProvider.calculateDistance(c, n);

			if (d < nearestDistance) {
				nearestDistance = d;
				nearestContact = n;
			}
		}
		return nearestContact;
	}

	public List<PastryContact> getBoostrapNodesSortedByProximityDistance(
			final PastryContact c) {
		List<PastryContact> nodes = new LinkedList<PastryContact>(
				bootstrapNodes);

		Comparator<PastryContact> comp = new Comparator<PastryContact>() {

			@Override
			public int compare(PastryContact o1, PastryContact o2) {
				Integer d1 = proxProvider.calculateDistance(c, o1);
				Integer d2 = proxProvider.calculateDistance(c, o1);

				return d1.compareTo(d2);
			}
		};

		Collections.sort(nodes, comp);
		return nodes;
	}

	@Override
	public void registerNode(PastryNode node) {
		bootstrapNodes.add(node.getOverlayContact());
		bootstrapNodesTransInfos.add(node.getOverlayContact().getTransInfo());
	}

	@Override
	public void unregisterNode(PastryNode node) {
		bootstrapNodes.remove(node.getOverlayContact());
		bootstrapNodesTransInfos
				.remove(node.getOverlayContact().getTransInfo());
	}

	/**
	 * @return tells whether there is at least one available peer for
	 *         bootstrapping registered
	 */
	public boolean anyNodeAvailable() {
		return !bootstrapNodes.isEmpty();
	}

}
