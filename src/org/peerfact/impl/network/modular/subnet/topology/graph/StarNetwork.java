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
package org.peerfact.impl.network.modular.subnet.topology.graph;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.network.modular.st.positioning.PlanePositioning;
import org.peerfact.impl.network.modular.subnet.routed.Channel;
import org.peerfact.impl.network.modular.subnet.routed.RoutingPath;
import org.peerfact.impl.network.modular.subnet.routed.WiredChannel;
import org.peerfact.impl.network.modular.subnet.topology.GraphBasedTopology;
import org.peerfact.impl.simengine.Simulator;


/**
 * Mainly for testing and performance evaluation, representation of a network
 * with only one router. Every host is connected directly to that router.
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/15/2011
 */
public class StarNetwork extends GraphBasedTopology {

	private Device CENTER_ROUTER = new CenterRouter();

	/**
	 * in this scenario each Edge has weight = 1
	 */
	private double edgeWeight = 1;

	public StarNetwork() {
		super();
		getGraph().addVertex(CENTER_ROUTER);
	}

	@Override
	public void addDevice(Device d) {
		getGraph().addVertex(d);
		Channel edge = new WiredChannel(10 * Simulator.MILLISECOND_UNIT);
		getGraph().addEdge(d, CENTER_ROUTER, edge);
		getGraph().setEdgeWeight(edge, edgeWeight);
	}

	@Override
	public void removeDevice(Device d) {
		getGraph().removeVertex(d);
	}

	@Override
	public RoutingPath getPath(Device from, Device to) {
		List<Channel> path = DijkstraShortestPath.findPathBetween(
				getGraph(), from, to);
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

	static class CenterRouter extends Device {
		// Dummy for central routing instance

		@Override
		public NetPosition getNetPosition() {
			return new PlanePositioning.PlanePosition(0, 0);
		}
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// no configurable parameters
	}
}
