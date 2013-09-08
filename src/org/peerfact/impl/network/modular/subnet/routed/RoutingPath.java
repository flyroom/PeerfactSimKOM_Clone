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
package org.peerfact.impl.network.modular.subnet.routed;

import java.util.List;

import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.simengine.Simulator;


/**
 * Container for representing a routing path consisting of multiple hops via
 * different Channels and Devices
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/15/2011
 */
public class RoutingPath {

	private List<Device> devices = null;

	private List<Channel> channels = null;

	private long timeCreated = Simulator.getCurrentTime();

	public RoutingPath(List<Device> devices, List<Channel> channels) {
		this.devices = devices;
		this.channels = channels;
	}

	/**
	 * Get all Devices of a connection. First in List is the sender, last the
	 * receiver.
	 * 
	 * @return List of hops or null if there is no connection
	 */
	public List<Device> getDevices() {
		return devices;
	}

	/**
	 * Channels taken on this path
	 * 
	 * @return
	 */
	public List<Channel> getChannels() {
		return channels;
	}

	/**
	 * Timestamp this path was created at
	 * 
	 * @return
	 */
	public long getTimeCreated() {
		return timeCreated;
	}

	/**
	 * Number of hops. This is the number of connections taken from source to
	 * sink, so size(hops) - 1
	 * 
	 * @return
	 */
	public int getNumOfHops() {
		return channels.size();
	}

	public Device getSender() {
		return devices.get(0);
	}

	public Device getReceiver() {
		return devices.get(devices.size() - 1);
	}

	@Override
	public String toString() {
		return "RoutingPath from " + getSender().toString() + " to "
				+ getReceiver().toString() + ", has " + getNumOfHops()
				+ " Hops.";
	}

}
