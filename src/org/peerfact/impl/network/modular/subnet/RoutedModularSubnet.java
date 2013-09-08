/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.network.modular.subnet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.network.modular.AbstractModularSubnet;
import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.ModularNetMessage;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.device.HostDevice;
import org.peerfact.impl.network.modular.subnet.routed.Channel;
import org.peerfact.impl.network.modular.subnet.routed.RoutingPath;
import org.peerfact.impl.network.modular.subnet.topology.NetworkTopology;


/**
 * This is a routed subnet. It supports heterogenity of devices and a structured
 * routing of messages in the subnet. Therefore you have to specify a network
 * topology, please have a look at the subpackage <code>routed</code>! The
 * network is maintained as a graph, where each node represents a Host or
 * Intermediate System.
 * 
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/13/2011
 */
public class RoutedModularSubnet extends AbstractModularSubnet implements
		RoutedSubnet {

	/**
	 * The graph representing the network
	 */
	private NetworkTopology topology;

	/**
	 * A Mapping of NetIDs to HostDevices for easy lookup
	 */
	private Map<NetID, HostDevice> hostDevices = new LinkedHashMap<NetID, HostDevice>();

	public RoutedModularSubnet() {
		// nothing to do here
	}

	@Override
	public void setTopology(NetworkTopology topology) {
		this.topology = topology;
	}

	@Override
	public NetworkTopology getTopology() {
		return topology;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeComplexType("Topology", topology);
	}

	@Override
	protected void netLayerWentOnline(NetLayer net) {
		ModularNetLayer mNet = (ModularNetLayer) net;
		hostDevices.put(net.getNetID(), mNet.getDevice());
		topology.addDevice(mNet.getDevice());
	}

	@Override
	protected void netLayerWentOffline(NetLayer net) {
		ModularNetLayer mNet = (ModularNetLayer) net;
		hostDevices.remove(net.getNetID());
		topology.removeDevice(mNet.getDevice());
	}

	/**
	 * Returns the HostDevice-Object for a given NetID
	 * 
	 * @param netId
	 * @return
	 */
	public HostDevice getDevice(NetID netId) {
		return hostDevices.get(netId);
	}

	// /**
	// * Basic caching for the last calculated RoutingPath, as several
	// Strategies
	// * might use it
	// */
	// private List<RoutingPath> lastPaths = new Vector<RoutingPath>();

	/**
	 * Calculate a path between sender and receiver. This may be overwritten by
	 * extending subnets to implement better caching, but in many applications
	 * it might be more convenient to let the topology decide how to cache calls
	 * to getPath().
	 * 
	 * @param nlSender
	 * @param nlReceiver
	 * @return a RoutingPath containing Hop-Devices and Connections
	 */
	protected RoutingPath calculatePath(ModularNetLayer nlSender,
			ModularNetLayer nlReceiver) {
		return topology.getPath(nlSender.getDevice(), nlReceiver.getDevice());
	}

	@Override
	protected boolean isConnectionPossible(ModularNetLayer nlSender,
			ModularNetLayer nlReceiver) {
		// A connection is only possible if a path exists
		RoutingPath path = calculatePath(nlSender, nlReceiver);
		return path != null;
	}

	/*
	 * 
	 * Methods for Modular-Net-Layer-Strategies
	 */
	@Override
	public long getMessagePropagationDelay(NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db) {
		/*
		 * Delay depends on routing path, so here we need to calculate a path!
		 * Path is not null in this context, as the subnet stops delivery in
		 * such a case
		 */
		RoutingPath path = calculatePath(nlSender, nlReceiver);
		List<Channel> channels = path.getChannels();
		long delay = 0;
		for (Channel channel : channels) {
			delay += channel.getPropagationDelay();
		}
		return delay;
	}

	@Override
	public long getJitter(long cleanMsgPropagationDelay, NetMessage msg,
			ModularNetLayer nlSender, ModularNetLayer nlReceiver,
			NetMeasurementDB db) {
		/*
		 * TODO bjoernr: Jitter depends on routing path!
		 */
		return 0;
	}

	@Override
	public boolean shallDrop(ModularNetMessage msg, ModularNetLayer nlSender,
			ModularNetLayer nlReceiver, NetMeasurementDB db) {
		/*
		 * TODO bjoernr: Drop depends on routing path (path exists?), find
		 * meaningful implementation
		 */
		return false;
	}

}
