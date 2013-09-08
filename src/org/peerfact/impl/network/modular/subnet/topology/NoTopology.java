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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.network.modular.subnet.routed.Channel;
import org.peerfact.impl.network.modular.subnet.routed.RoutingPath;
import org.peerfact.impl.network.modular.subnet.routed.WiredChannel;


/**
 * If you want to use some features of a RoutedSubnet (for example heterogenity
 * of hosts) but you do not need some kind of Topology or Routing, you should
 * use this class as your topology, as it will speed up simulation.
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/16/2011
 */
public class NoTopology extends NetworkTopology {

	private long propagationDelay = 0;

	private Set<Device> allDevices;

	public NoTopology() {
		allDevices = new LinkedHashSet<Device>();
	}

	@Override
	public void addDevice(Device d) {
		allDevices.add(d);
	}

	@Override
	public void removeDevice(Device d) {
		allDevices.remove(d);
	}

	@Override
	public void updateDevice(Device d) {
		// nothing to do here
	}

	@Override
	public Set<Device> getAllDevices() {
		return Collections.unmodifiableSet(allDevices);
	}

	@Override
	public Set<Channel> getChannelsForDevice(Device d) {
		return null;
	}

	@Override
	public RoutingPath getPath(Device from, Device to) {
		List<Device> devices = new Vector<Device>();
		devices.add(from);
		devices.add(to);
		List<Channel> channels = new Vector<Channel>();
		channels.add(new WiredChannel(propagationDelay));
		return new RoutingPath(devices, channels);
	}

	/**
	 * Set a propagation-Delay for the specified graph
	 * 
	 * @param delay
	 */
	public void setPropagationDelay(long delay) {
		this.propagationDelay = delay;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeTime("propagationDelay", propagationDelay);
	}

}
