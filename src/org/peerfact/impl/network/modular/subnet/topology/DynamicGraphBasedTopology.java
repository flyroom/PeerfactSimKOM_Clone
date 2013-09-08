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

import java.util.LinkedHashSet;
import java.util.Set;

import org.jgrapht.Graphs;
import org.peerfact.impl.network.modular.device.Device;


/**
 * A dynamic Graph based topology may be used for example in mobile
 * Ad-Hoc-Networks. It consists of a set of devices with each device being
 * physically connected to a subset of other devices (for example based on
 * distance). As this kind of network frequently changes the graph needs to be
 * updated as well.
 * 
 * This class may define some commonly used utility methods for this task
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/19/2011
 */
public abstract class DynamicGraphBasedTopology extends GraphBasedTopology {

	/**
	 * In such a Topology updateDevice has to be implemented, as it should be
	 * called by the positioning upon movement of a device
	 */
	@Override
	public abstract void updateDevice(Device d);

	/**
	 * Returns all nodes within a given distance from center. This does not
	 * limit returned nodes to be HostNodes! The nodes are <b>not</b> sorted by
	 * distance
	 * 
	 * @return A Set of neighbors, unsorted
	 */
	protected Set<Device> getNeighborsBasedOnDistance(Device center,
			double distance) {
		Set<Device> devices = getGraph().vertexSet();
		Set<Device> neighbors = new LinkedHashSet<Device>();
		for (Device d : devices) {
			if (d.getNetPosition().getDistance(center.getNetPosition()) <= distance) {
				neighbors.add(d);
			}
		}

		neighbors.remove(center);
		return neighbors;
	}

	/**
	 * computes all Devices that are n-Hop-Neighbors of centerDevice in the
	 * current graph
	 * 
	 * @param updateDevice
	 * @param hops
	 *            minimum one, specifies how many hops away a node still should
	 *            be considered a neighbor
	 * @return
	 */
	protected Set<Device> getNeighbors(Device centerDevice, double hops) {
		if (hops <= 1) {
			return getDirectNeighborsOf(centerDevice);
		}
		Set<Device> toCompute = new LinkedHashSet<Device>();
		Set<Device> newNeighbors = new LinkedHashSet<Device>();
		Set<Device> allNeighbors = new LinkedHashSet<Device>();
		allNeighbors.add(centerDevice);
		toCompute.add(centerDevice);

		/*
		 * populate allNeighbors with a list of all possible neighbors via n
		 * hops
		 */
		for (int i = 0; i < hops; i++) {
			for (Device actDev : toCompute) {
				newNeighbors.addAll(getDirectNeighborsOf(actDev));
			}
			toCompute.clear();
			newNeighbors.removeAll(allNeighbors);
			toCompute.addAll(newNeighbors);
			allNeighbors.addAll(newNeighbors);
		}

		// finally: remove own device from list
		allNeighbors.remove(centerDevice);

		return allNeighbors;
	}

	/**
	 * Get Direct Neighbors of a Device based on the Graphs current Data
	 * 
	 * @param d
	 * @return
	 */
	private Set<Device> getDirectNeighborsOf(Device d) {
		Set<Device> neighbors = new LinkedHashSet<Device>();
		neighbors.addAll(Graphs.neighborListOf(getGraph(), d));
		return neighbors;
	}

}
