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

package org.peerfact.impl.overlay.dht.kademlia.kandy.components;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HAbstractNodeFactory;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Factory for Kandy2Nodes.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KandyNodeFactory extends HAbstractNodeFactory {

	private final static Logger log = SimLogger
			.getLogger(KandyNodeFactory.class);

	public KandyNodeFactory() {
		log.info("Using Kandy2NodeFactory.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected KandyNode buildHierarchicalNode(
			final HKademliaOverlayID id, final short port,
			final TransLayer msgMgr) {
		final KademliaOverlayContact<HKademliaOverlayID> itsContact = new KademliaOverlayContact<HKademliaOverlayID>(
				id, msgMgr.getLocalTransInfo(port));
		final KandyNode newNode = new KandyNode(
				itsContact, msgMgr, config);
		return newNode;
	}

}
