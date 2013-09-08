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

/**
 * Part of a network, either host or routing device which is included in the
 * network graph
 * 
 * This may act as a wrapper for any device information the host may specify, if
 * there is some kind of "HostProperty" addressing physical properties important
 * for routing.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04/15/2011
 */
public abstract class Device {

	/**
	 * Get this Devices' current Position. Used to calculate distances etc in a
	 * routed scenario
	 * 
	 * @return
	 */
	public abstract NetPosition getNetPosition();

	/**
	 * Returns, whether this device can connect to the given device (allows for
	 * implementation of different protocols etc). This decision could also be
	 * based on the devices distances.
	 * 
	 * @param device
	 * @return
	 */
	// public abstract boolean isAbleToConnect(Device device);

}
