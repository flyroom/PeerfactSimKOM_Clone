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

import java.util.ArrayList;
import java.util.List;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.LiveMonitoring;
import org.peerfact.impl.util.LiveMonitoring.ProgressValue;


/**
 * Bootstrapping component for Gnutella-like overlays. Peers that successfully
 * connected to the overlay leave their contact information here. If a peer
 * wants to connect to the overlay at startup, it gets a random contact from
 * this pool for its bootstrap.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TContact>
 * @version 05/06/2011
 */
public class GnutellaBootstrap<TContact extends GnutellaLikeOverlayContact> {

	int bootstraps = 0;

	/**
	 * Ultrapeers that are connected to the overlay
	 */
	private List<TContact> nodes;

	protected GnutellaBootstrap() {
		nodes = new ArrayList<TContact>();
		LiveMonitoring.addProgressValue(new TotalBootstraps());
	}

	/**
	 * Returns a node that can be used for bootstrapping.
	 * 
	 * @return
	 */
	public TContact getANodeToConnectTo() {
		if (nodes.size() == 0) {
			return null;
		}
		bootstraps++;
		return nodes.get(Simulator.getRandom().nextInt(nodes.size()));
	}

	/**
	 * Adds a node that can be used for bootstrapping
	 * 
	 * @param contact
	 */
	public void addNode(TContact contact) {
		nodes.add(contact);
	}

	/**
	 * Removes a node that can be used for bootstrapping
	 * 
	 * @param contact
	 */
	public void removeNode(TContact contact) {
		nodes.remove(contact);
	}

	class TotalBootstraps implements ProgressValue {

		@Override
		public String getName() {
			return "Gnutella Total Bootstraps";
		}

		@Override
		public String getValue() {
			return String.valueOf(bootstraps);
		}

	}

}
