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

import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.network.modular.subnet.routed.Channel;
import org.peerfact.impl.network.modular.subnet.routed.RoutingPath;
import org.peerfact.impl.util.BackToXMLWritable;


/**
 * Representation of a network topology. Implementations might include graph
 * based approaches or some mobile AdHoc-Implementation.
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/14/2011
 */
public abstract class NetworkTopology implements BackToXMLWritable {

	/**
	 * Create a new Topology
	 */
	public NetworkTopology() {
		// nothing to do here
	}

	/**
	 * Adds a Device to the network
	 * 
	 * @param d
	 *            Depending on the device class the network may provide
	 *            different strategies for adding (ie to differ between hosts
	 *            and routers)
	 */
	public abstract void addDevice(Device d);

	/**
	 * Removes a Device from the network
	 * 
	 * @param d
	 */
	public abstract void removeDevice(Device d);

	/**
	 * If properties of a device change, it might be needed to change the
	 * network-Topology as well. Therefore the subnet is supposed to call this
	 * method whenever a device changes properties, for example its physical
	 * position. The NetworkTopology decides, if and how it wants to use this
	 * information.
	 * 
	 * @param d
	 */
	public abstract void updateDevice(Device d);

	/**
	 * Get a routing path
	 * 
	 * @param from
	 * @param to
	 * @return null, if no path can be found
	 */
	public abstract RoutingPath getPath(Device from, Device to);

	/**
	 * get all Devices in a Topology, used for drawing
	 * 
	 * @return
	 */
	public abstract Set<Device> getAllDevices();

	public abstract Set<Channel> getChannelsForDevice(Device d);

}
