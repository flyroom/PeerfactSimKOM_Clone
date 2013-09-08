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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06;

import org.peerfact.api.common.Host;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.AbstractGnutellaLikeNode;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.GnutellaBootstrap;

/**
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractGnutella06Node extends
		AbstractGnutellaLikeNode<Gnutella06OverlayContact, IGnutella06Config> {

	/**
	 * @param host
	 * @param id
	 * @param config
	 * @param bootstrap
	 * @param port
	 */
	public AbstractGnutella06Node(Host host, GnutellaOverlayID id,
			IGnutella06Config config,
			GnutellaBootstrap<Gnutella06OverlayContact> bootstrap, short port) {
		super(host, id, config, bootstrap, port);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the own contact information of this node.
	 */
	@Override
	public Gnutella06OverlayContact getOwnContact() {
		return new Gnutella06OverlayContact(id, host.getTransLayer()
				.getLocalTransInfo(port), isUltrapeer());
	}

	/**
	 * Returns whether this node is an ultrapeer or not.
	 * 
	 * @return
	 */
	protected abstract boolean isUltrapeer();

	// /** Method is never used.
	// * For e.g. debugging purposes. In a list of nodes, these ones are sorted
	// by their id,
	// * but all ultrapeers are greater than the leaves.
	// */
	// public int compareTo(AbstractGnutella06Node node) {
	//
	// if (node.isUltrapeer() && !this.isUltrapeer())
	// return 1;
	// if (!node.isUltrapeer() && this.isUltrapeer())
	// return -1;
	// return this.getOverlayID().compareTo(node.getOverlayID());
	// }

	@Override
	public boolean canBeUsedForBootstrapping() {
		return this.isUltrapeer();
	}

}
