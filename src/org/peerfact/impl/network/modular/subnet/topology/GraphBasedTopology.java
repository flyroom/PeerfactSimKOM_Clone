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
package org.peerfact.impl.network.modular.subnet.topology;

import java.util.Set;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.network.modular.subnet.routed.Channel;


/**
 * These are topologies based on the representation as a graph (uses
 * JGraphT-Library)
 * 
 * @author bjoernr
 * @version 1.0, mm/dd/2011
 */
public abstract class GraphBasedTopology extends NetworkTopology {

	private SimpleWeightedGraph<Device, Channel> graph;

	/**
	 * Create a new Graph
	 */
	public GraphBasedTopology() {
		graph = new SimpleWeightedGraph<Device, Channel>(Channel.class);
	}

	/**
	 * Only for usage in extending classes, this method returns the graph-Object
	 * 
	 * @return
	 */
	protected SimpleWeightedGraph<Device, Channel> getGraph() {
		return graph;
	}

	@Override
	public void updateDevice(Device d) {
		// nothing to do here
	}

	@Override
	public Set<Device> getAllDevices() {
		return getGraph().vertexSet();
	}

	@Override
	public Set<Channel> getChannelsForDevice(Device d) {
		return getGraph().edgesOf(d);
	}

}
