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

package org.peerfact.api.service.skyeye;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.service.aggregation.skyeye.components.MessageCounter;

/**
 * This interface defines the methods, which the complete SkyNet-node can
 * execute. Since a SkyNet-node performs the tasks of a Coordinator and maybe
 * the tasks of a Support Peer, we divided the structure of a node into two
 * parts, from which one defines the functionality for the Coordinator (see
 * {@link SkyNetNodeInterface}), while the other defines the functionality for
 * the Support Peer (see {@link SupportPeer}). As both parts need a base to be
 * built on, this interface, from which <code>SkyNetNodeInterface</code> and
 * <code>SupportPeer</code> extend, provides the requested foundation.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetLayer extends
OverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>>,
SimulationEventHandler {

	/**
	 * This method returns the <code>SkyNetNodeInfo</code>-object of the node,
	 * which contains its ID as well as its <code>TransInfo</code>.
	 * 
	 * @return the <code>SkyNetNodeInfo</code>-object of the node
	 */
	public SkyNetNodeInfo getSkyNetNodeInfo();

	/**
	 * This method sets the new <code>SkyNetNodeInfo</code>-object for the node.
	 * 
	 * @param info
	 *            contains the new object.
	 */
	public void setSkyNetNodeInfo(SkyNetNodeInfo info);

	/**
	 * This method checks if the SkyNet-node is actually utilized as Support
	 * Peer.
	 * 
	 * @return <code>true</code>, if the SkyNet-node actually serves as Support
	 *         Peer.
	 */
	public boolean isSupportPeer();

	/**
	 * This method returns the reference of the own Transport-Layer from the
	 * host.
	 * 
	 * @return a reference of the own Transport-layer.
	 */
	public TransLayer getTransLayer();

	/**
	 * This method returns a reference of the peer, which participates in the
	 * underlying overlay and represents the host in the overlay. The peer
	 * belongs to the Overlay-Layer of the host.
	 * 
	 * @return the reference of the peer from the Overlay-Layer.
	 */
	public OverlayNode<?, ?> getOverlayNode();

	/**
	 * This method returns a reference of <code>MessageCounter</code>.
	 * 
	 * @return the reference of <code>MessageCounter</code>.
	 */
	public MessageCounter getMessageCounter();
}
