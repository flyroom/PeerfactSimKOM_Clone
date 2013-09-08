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

package org.peerfact.impl.network;

import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.BandwidthDetermination;
import org.peerfact.api.network.NetID;
import org.peerfact.impl.util.BackToXMLWritable;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public abstract class AbstractNetLayerFactory implements ComponentFactory,
		BackToXMLWritable {

	/**
	 * In bytes per second. This variable is used to initialize
	 * <code>downBandwidth</code> with a default value if the config-file does
	 * not specify a separate value.
	 */
	protected final static double DEFAULT_DOWN_BANDWIDTH = 5000l;

	/**
	 * In bytes per second. This variable is used to initialize
	 * <code>upBandwidth</code> with a default value if the config-file does not
	 * specify a separate value.
	 */
	protected final static double DEFAULT_UP_BANDWIDTH = 5000l;

	/**
	 * In bytes per second. <code>downBandwidth</code> is initialized with a
	 * value, that can be provided by the config-file, otherwise it gets the
	 * value stored in <code>DEFAULT_DOWN_BANDWIDTH</code>.
	 */
	protected double downBandwidth;

	/**
	 * In bytes per second. <code>upBandwidth</code> is initialized with a
	 * value, that can be provided by the config-file, otherwise it gets the
	 * value stored in <code>DEFAULT_UP_BANDWIDTH</code>.
	 */
	protected double upBandwidth;

	protected BandwidthDetermination<?> bandwidthDetermination;

	public AbstractNetLayerFactory() {
		this.downBandwidth = DEFAULT_DOWN_BANDWIDTH;
		this.upBandwidth = DEFAULT_UP_BANDWIDTH;
		this.bandwidthDetermination = null;
	}

	protected Bandwidth getBandwidth(NetID netID) {
		if (bandwidthDetermination == null) {
			return new Bandwidth(this.downBandwidth, this.upBandwidth);
		}
		return bandwidthDetermination.getRandomBandwidth();
	}

	public void setBandwidthDetermination(
			BandwidthDetermination<?> bandwidthDetermination) {
		this.bandwidthDetermination = bandwidthDetermination;
	}

	/**
	 * Sets the downstream bandwidth in bytes/sec
	 * 
	 * @param downBandwidth
	 */
	public void setDownBandwidth(double downBandwidth) {
		this.downBandwidth = downBandwidth;
	}

	/**
	 * Sets the upstream bandwidth in bytes/sec
	 * 
	 * @param upBandwidth
	 */
	public void setUpBandwidth(double upBandwidth) {
		this.upBandwidth = upBandwidth;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		bw.writeSimpleType("downBandwidth", downBandwidth);
		bw.writeSimpleType("upBandwidth", upBandwidth);
		bw.writeComplexType("BandwidthDetermination", bandwidthDetermination);
	}

}
