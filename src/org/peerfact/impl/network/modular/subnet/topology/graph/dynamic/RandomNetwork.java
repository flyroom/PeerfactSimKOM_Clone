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
package org.peerfact.impl.network.modular.subnet.topology.graph.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.network.modular.subnet.routed.Channel;
import org.peerfact.impl.network.modular.subnet.routed.RoutingPath;
import org.peerfact.impl.network.modular.subnet.routed.WiredChannel;
import org.peerfact.impl.network.modular.subnet.routed.WirelessChannel;
import org.peerfact.impl.network.modular.subnet.topology.DynamicGraphBasedTopology;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.movement.MovementSupported;


/**
 * This network is randomly created (it reflects some kind of adHoc snapshot for
 * testing purposes, ie routing algorithm)
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/19/2011
 */
public class RandomNetwork extends DynamicGraphBasedTopology {

	public List<Device> fixedDevices = new Vector<Device>();

	@Override
	public void writeBackToXML(BackWriter bw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDevice(Device d) {
		Set<Device> neighbors = getNeighborsBasedOnDistance(d, 100);
		Set<Device> oldNeighbors = getNeighbors(d, 1);
		int numNewConnections = 0;
		for (Device neighbor : neighbors) {
			oldNeighbors.remove(neighbor);
			Channel c = new WirelessChannel();
			// add only modifies graph, if edge did not exist
			boolean added = getGraph().addEdge(d, neighbor, c);
			if (added) {
				numNewConnections++;
			}
		}

		// remove old edges
		for (Device neighbor : oldNeighbors) {
			getGraph().removeEdge(d, neighbor);
		}

		Set<Channel> edges = getGraph().edgesOf(d);
		int updatedConnections = 0;
		for (Channel actChannel : edges) {
			getGraph().setEdgeWeight(actChannel, actChannel.calculateWeight());
			updatedConnections++;
		}

	}

	@Override
	public void addDevice(Device d) {
		/*
		 * create a random structure
		 */
		Device picked = null;
		if (fixedDevices.size() > 1) {
			picked = fixedDevices.get(Simulator.getRandom().nextInt(
					fixedDevices.size()));
		}
		getGraph().addVertex(d);
		if (!(d instanceof MovementSupported)) {
			fixedDevices.add(d);
			if (picked != null) {
				Channel edge = new WiredChannel(10 * Simulator.MILLISECOND_UNIT);
				getGraph().addEdge(d, picked, edge);
				getGraph().setEdgeWeight(edge, 1);
			}
		} else {
			updateDevice(d);
		}
	}

	@Override
	public void removeDevice(Device d) {
		// not implemented in this simple type of Network
	}

	@Override
	public RoutingPath getPath(Device from, Device to) {
		List<Channel> path = DijkstraShortestPath.findPathBetween(getGraph(),
				from, to);
		if (path == null) {
			return null;
		}

		List<Device> nodes = new ArrayList<Device>();
		Device v = from;
		nodes.add(from);
		for (Channel e : path) {
			v = Graphs.getOppositeVertex(getGraph(), e, v);
			nodes.add(v);
		}
		return new RoutingPath(nodes, path);
	}

}
