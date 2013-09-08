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

package org.peerfact.impl.network.simple;

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetLatencyModel;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.AbstractNetLayerFactory;
import org.peerfact.impl.simengine.Simulator;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SimpleNetFactory extends AbstractNetLayerFactory {

	private SimpleSubnet subnet;

	private int idCounter = 0;

	private Set<Integer> usedIds = new LinkedHashSet<Integer>();

	public SimpleNetFactory() {
		subnet = new SimpleSubnet();
	}

	private static NetPosition newPosition() {
		return new SimpleEuclidianPoint(Simulator.getRandom().nextDouble()
				* SimpleSubnet.SUBNET_HEIGHT, Simulator.getRandom()
				.nextDouble()
				* SimpleSubnet.SUBNET_WIDTH);
	}

	public void setLatencyModel(NetLatencyModel model) {
		subnet.setLatencyModel(model);
	}

	@Override
	public SimpleNetLayer createComponent(Host host) {
		// parameter of getBandwidth is not needed here
		Bandwidth bw = getBandwidth(null);
		return new SimpleNetLayer(subnet, this.createNewID(), newPosition(), bw);
	}

	public SimpleNetID createNewID() {
		while (usedIds.contains(idCounter)) {
			idCounter++;
		}
		SimpleNetID nextId = new SimpleNetID(idCounter);
		usedIds.add(idCounter++);
		return nextId;
	}

	public SimpleNetID parseID(String s) {
		int id = Integer.parseInt(s);
		SimpleNetID nextId = new SimpleNetID(id);
		usedIds.add(id);
		return nextId;
	}

	@Override
	public String toString() {
		return "Simple Network Factory";
	}
}
