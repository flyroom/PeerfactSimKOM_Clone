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

package org.peerfact.impl.overlay.unstructured.zeroaccess.components;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ZeroAccessCrawlOverlayNodeFactory implements ComponentFactory {
	private static Logger log = SimLogger
			.getLogger(ZeroAccessCrawlOverlayNodeFactory.class);

	private final static short port = 123;

	private static long id = 0;

	private String poison;

	private long downBandwidth;

	public void setDownBandwidth(long downBandwidth) {
		this.downBandwidth = downBandwidth;
	}

	public void setUpBandwidth(long upBandwidth) {
		this.upBandwidth = upBandwidth;
	}

	private long upBandwidth;

	public String getPoison() {
		return poison;
	}

	public void setPoison(String poison) {
		this.poison = poison;
	}

	@Override
	public Component createComponent(Host host) {
		return new ZeroAccessCrawlOverlayNode(host.getNetLayer(),
				host.getTransLayer(),
				newZeroAccessOverlayID(), port, this.downBandwidth,
				this.upBandwidth, this.poison);
	}

	public static ZeroAccessOverlayID newZeroAccessOverlayID() {
		ZeroAccessCrawlOverlayNodeFactory.id += 1;
		log.debug(new ZeroAccessOverlayID(BigInteger
				.valueOf(ZeroAccessCrawlOverlayNodeFactory.id)));
		return new ZeroAccessOverlayID(
				BigInteger.valueOf(ZeroAccessCrawlOverlayNodeFactory.id));
	}
}
