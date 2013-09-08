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

package org.peerfact.impl.overlay;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.OverlayRoutingTable;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This is the class all concrete overlay node classes should inherit from.
 * 
 * @param <T>
 *            the concrete used implementation of the <code>OverlayID</code>
 * @author <info@peerfact.org>
 * @version 05/06/2011
 */
public abstract class AbstractOverlayNode<T extends OverlayID<?>, S extends OverlayContact<T>>
		implements OverlayNode<T, S>, ConnectivityListener {

	/**
	 * The possible overlay states
	 */
	public enum PeerStatus {
		/**
		 * The peer is not connected to the overlay
		 */
		ABSENT,
		/**
		 * The peer is connected to the overlay
		 */
		PRESENT,
		/**
		 * The peer is about to join the overlay
		 */
		TO_JOIN
	}

	final static Logger log = SimLogger.getLogger(AbstractOverlayNode.class);

	private PeerStatus peerStatus;

	private T overlayPeerId;

	protected OverlayRoutingTable<T, ? extends OverlayContact<T>> routingTable;

	private final short port;

	private Host host;

	protected AbstractOverlayNode(T peerId, short port) {
		this.overlayPeerId = peerId;
		this.peerStatus = PeerStatus.ABSENT;
		this.port = port;
	}

	/**
	 * Get information about the current status of the overlay of the peer.
	 * 
	 * Note: This does not give any information about the connectivity status of
	 * the network layer.
	 * 
	 * @return the current overlay status of the peer.
	 */
	public PeerStatus getPeerStatus() {
		return peerStatus;
	}

	/**
	 * Set a new overlay status.
	 * 
	 * Hint: This should be done for example when a peer starts joining,
	 * finishes joining or disconnects.
	 * 
	 * @param peerStatus
	 *            the new overlay status of the peer
	 */
	public void setPeerStatus(PeerStatus peerStatus) {
		this.peerStatus = peerStatus;
	}

	public OverlayRoutingTable<T, ? extends OverlayContact<T>> getRoutingTable() {
		if (this.routingTable == null) {
			throw new IllegalStateException("No RoutingTable defined!");
		}
		return this.routingTable;
	}

	@Override
	public T getOverlayID() {
		return overlayPeerId;
	}

	@Override
	public String toString() {
		return "{" + "overlayPeerId=" + overlayPeerId + ", peerStatus="
				+ peerStatus + '}';
	}

	@Override
	public short getPort() {
		return this.port;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
		this.host.getProperties().addConnectivityListener(this);
	}

	public void setOverlayID(T id) {
		overlayPeerId = id;
	}

	@Override
	public Host getHost() {
		return this.host;
	}

	public abstract TransLayer getTransLayer();

	@Override
	public boolean isPresent() {
		return getPeerStatus() == PeerStatus.PRESENT
				&& this.host.getNetLayer().isOnline();
	}

}
