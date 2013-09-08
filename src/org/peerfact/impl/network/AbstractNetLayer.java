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

package org.peerfact.impl.network;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Monitor.Reason;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.network.NetMessageListener;
import org.peerfact.api.network.NetMsgEvent;
import org.peerfact.api.network.NetPosition;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * This abstract class provides a skeletal implementation of the
 * <code>NetLayer<code> interface to lighten the effort for implementing this interface.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public abstract class AbstractNetLayer implements NetLayer {

	protected static Logger log = SimLogger.getLogger(AbstractNetLayer.class);

	protected List<NetMessageListener> msgListeners;

	protected List<ConnectivityListener> connListeners;

	protected NetID myID;

	protected boolean online;

	protected boolean isolation;

	private NetPosition position;

	Bandwidth currentBandwidth;

	Bandwidth maxBandwidth;

	private Host host;

	protected AbstractSubnet<?> subnet;

	/**
	 * Abstract constructor called by a subclass of this instance
	 * 
	 * @param maxDownBandwidth
	 *            the maximum physical download bandwidth
	 * @param maxUpBandwidth
	 *            the maximum physical upload bandwidth
	 * @param position
	 *            the NetPosition of the network layer
	 * @param subnet
	 *            the Subnet of the network layer
	 */
	public AbstractNetLayer(Bandwidth maxBandwidth, NetPosition position,
			AbstractSubnet<?> subnet) {
		this.msgListeners = new LinkedList<NetMessageListener>();
		this.connListeners = new LinkedList<ConnectivityListener>();
		if (maxBandwidth.getDownBW() < maxBandwidth.getUpBW()) {
			log.warn("maxDownBandwidth < maxUpBandwidth on host with NetID "
					+ this.myID);
		}
		this.maxBandwidth = maxBandwidth;
		this.currentBandwidth = maxBandwidth.clone();
		this.position = position;
		this.subnet = subnet;
	}

	/**
	 * This message is called by the subnet to deliver a new NetMessage to a
	 * remote NetLayer. (@see org.peerfact.impl.network.AbstractSubnet). Calling
	 * this method informs further all registered NetMsgListeners about the
	 * receipt of this NetMessage using a appropriate NetMsgEvent.
	 * 
	 * @param message
	 *            The NetMessage that was received by the NetLayer.
	 */
	public void receive(NetMessage message) {
		NetLayer senderNetLayer = subnet.layers.get(message.getSender());

		// drop packages for offline nodes and if a node is isolated
		if (this.isOnline()
				&& (!this.isIsolated() && !senderNetLayer.isIsolated()
				|| this.getHost().getProperties().getGroupID()
						.equals(senderNetLayer.getHost().getProperties()
								.getGroupID()))) {

			log.info(Simulator.getSimulatedRealtime() + " Receiving " + message);

			Simulator.getMonitor().netMsgEvent(message, myID, Reason.RECEIVE);
			NetMsgEvent event = new NetMsgEvent(message, this);
			if (msgListeners == null || msgListeners.isEmpty()) {
				Simulator.getMonitor().netMsgEvent(message, myID, Reason.DROP);
				log.warn(this + "Cannot deliver message "
						+ message.getPayload() + " at netID=" + myID
						+ " as no message msgListeners registered");
			} else {
				for (NetMessageListener listener : msgListeners) {
					listener.messageArrived(event);
				}
			}
		} else {
			Simulator.getMonitor().netMsgEvent(message, myID, Reason.DROP);
		}
	}

	/**
	 * Return whether the required transport protocol is supported by the given
	 * NetLayer instance
	 * 
	 * @param protocol
	 *            the required transport protocol
	 * @return true if supported
	 */
	protected abstract boolean isSupported(TransProtocol protocol);

	/**
	 * As the bandwidth of a host might be shared between concurrently
	 * established connections, this method will be used by the subnet in order
	 * to adapt the current available bandwidth.
	 * 
	 * @param currentBandwidth
	 *            the new available bandwidth
	 */
	public void setCurrentBandwidth(Bandwidth currentBandwidth) {
		this.currentBandwidth = currentBandwidth;
	}

	@Override
	public void addNetMsgListener(NetMessageListener listener) {
		log.debug("Register msg listener " + listener);
		this.msgListeners.add(listener);
	}

	@Override
	public void removeNetMsgListener(NetMessageListener listener) {
		this.msgListeners.remove(listener);
	}

	@Override
	public NetID getNetID() {
		return this.myID;
	}

	@Override
	public void goOffline() {
		this.online = false;
		connectivityChanged(new ConnectivityEvent(this, this.online));
		Simulator.getMonitor().churnEvent(this.getHost(), Reason.OFFLINE);
		log.info(myID + " disconnected @ " + Simulator.getSimulatedRealtime());
	}

	@Override
	public void goOnline() {
		this.online = true;
		connectivityChanged(new ConnectivityEvent(this, this.online));
		Simulator.getMonitor().churnEvent(this.getHost(), Reason.ONLINE);
		log.info(myID + " connected @ " + Simulator.getSimulatedRealtime());
	}

	@Override
	public boolean isOffline() {
		return !online;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void startIsolation() {
		this.isolation = true;
		log.error(myID + " start isolation @ "
				+ Simulator.getSimulatedRealtime());
	}

	@Override
	public void stopIsolation() {
		this.isolation = false;
		log.error(myID + " stop isolation @ "
				+ Simulator.getSimulatedRealtime());
	}

	@Override
	public boolean isIsolated() {
		return this.isolation;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public Host getHost() {
		return this.host;
	}

	@Override
	public NetPosition getNetPosition() {
		return this.position;
	}

	@Override
	public Bandwidth getCurrentBandwidth() {
		return currentBandwidth;
	}

	@Override
	public Bandwidth getMaxBandwidth() {
		return maxBandwidth;
	}

	@Override
	public void addConnectivityListener(ConnectivityListener listener) {
		this.connListeners.add(listener);

	}

	@Override
	public void removeConnectivityListener(ConnectivityListener listener) {
		this.connListeners.remove(listener);
	}

	void connectivityChanged(ConnectivityEvent e) {
		for (ConnectivityListener l : connListeners) {
			l.connectivityChanged(e);
		}
	}

}
