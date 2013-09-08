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

package org.peerfact.impl.network.bandwidthdetermination;

import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.BandwidthDetermination;
import org.peerfact.impl.simengine.Simulator;

/**
 * This class implements a randomized determination of bandwidth, that follows
 * no certain distribution, but is used to omit a static assignment of
 * bandwidth-capacities.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RandomizedBandwidthDetermination implements
		BandwidthDetermination<Integer> {

	private double maxDownBandwidth = 10000d;

	private double minDownBandwidth = 5000d;

	private double upstreamBWQuota = 0.1;

	@Override
	public Bandwidth getBandwidthByObject(Integer object) {
		// not needed
		return null;
	}

	@Override
	public Bandwidth getRandomBandwidth() {
		double downBandwidth = minDownBandwidth
				+ Simulator.getRandom().nextDouble()
				* (maxDownBandwidth - minDownBandwidth);
		double upBandwidth = downBandwidth * upstreamBWQuota;
		return new Bandwidth(Math.floor(downBandwidth), Math.floor(upBandwidth));
	}

	public void setMaxDownBandwidth(double maxDownBandwidth) {
		this.maxDownBandwidth = maxDownBandwidth;
	}

	public void setMinDownBandwidth(double minDownBandwidth) {
		this.minDownBandwidth = minDownBandwidth;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("maxDownBandwidth", maxDownBandwidth);
		bw.writeSimpleType("minDownBandwidth", minDownBandwidth);
	}

}
