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

import org.peerfact.impl.simengine.Simulator;

/**
 * Basic wireless channel
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/26/2011
 */
public class WirelessChannel extends Channel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8391542458915737158L;

	@Override
	public long getPropagationDelay() {
		/*
		 * Delay: 20ms + 0.1ms for one point distance. This is just for testing
		 * purposes, you might want to replace it with a better formula!
		 */
		return ((long) (getLength() * 0.1)) * Simulator.MILLISECOND_UNIT + 20
				* Simulator.MILLISECOND_UNIT;
	}

	@Override
	public double calculateWeight() {
		/*
		 * Weight depends on distance of the hosts. This basic implementation
		 * just uses the propagation-delay
		 */
		return getPropagationDelay();
	}

}
