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

package org.peerfact.impl.network.modular.device;

import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.util.BackToXMLWritable;

/**
 * Representation of a host in the network-graph. All Device-Classes that are
 * used for Hosts in our Network must extend this class! This base class does
 * not support movement nor positioning.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04/15/2011
 */
public class HostDevice extends Device implements BackToXMLWritable, Cloneable {

	/**
	 * A Host is defined by its NetLayer
	 */
	private ModularNetLayer net;

	public HostDevice() {
		// blank constructor for usage with config.xml
	}

	/**
	 * Copyconstructor
	 * 
	 * @param device
	 */
	protected HostDevice(HostDevice device) {
		// do NOT copy the net-Layer!
		// nothing to do here
	}

	@Override
	public NetPosition getNetPosition() {
		return net.getNetPosition();
	}

	/**
	 * Set this Hosts NetLayer
	 * 
	 * @param net
	 */
	public void setNet(ModularNetLayer net) {
		this.net = net;
	}

	/**
	 * Get this Hosts NetLayer
	 * 
	 * @return
	 */
	public ModularNetLayer getNetLayer() {
		return net;
	}

	@Override
	public HostDevice clone() {
		try {
			super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HostDevice(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HostDevice) {
			HostDevice h = (HostDevice) obj;
			return h.getNetLayer().equals(getNetLayer());
		}
		return false;
	}

	/**
	 * This is very important for graph-backed networks, as it ensures each Net
	 * is only present once in the graph!
	 */
	@Override
	public int hashCode() {
		if (net != null) {
			return net.hashCode();
		} else {
			return super.hashCode();
		}
	}

	@Override
	public String toString() {
		return "HostDevice: " + net.toString();
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// nothing to write back
	}

}
