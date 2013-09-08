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

package org.peerfact.impl.service.aggregation.skyeye.addressresolution;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayID;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetID;
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
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class NapsterAddressResolutionImpl extends
		AbstractAddressResolution<CIOverlayID, SkyNetID, Object> {

	private static Logger log = SimLogger
			.getLogger(NapsterAddressResolutionImpl.class);

	private static NapsterAddressResolutionImpl ari;

	private NapsterAddressResolutionImpl(int size) {
		byte[] bound = new byte[size + 1];
		bound[0] = 1;
		upperBound = new BigDecimal(new BigInteger(bound));
		log.debug("the upper bound of the addressSpace is "
				+ upperBound.toPlainString());
	}

	public static NapsterAddressResolutionImpl getInstance(int size) {
		if (ari == null) {
			ari = new NapsterAddressResolutionImpl(size);
		}
		return ari;
	}

	@Override
	public CIOverlayID getOverlayID(SkyNetID skyNetID) {
		BigDecimal dec = skyNetID.getID().multiply(upperBound);
		BigInteger chordID = null;
		try {
			chordID = dec.toBigIntegerExact();
		} catch (Exception e) {
			log.fatal("Unable to create exact integer out of "
					+ dec.toPlainString());
		}
		return new CIOverlayID(chordID.subtract(BigInteger.ONE));
	}

	@Override
	public SkyNetID getSkyNetID(CIOverlayID overlayID) {
		BigDecimal chordID = new BigDecimal(overlayID.getID())
				.add(BigDecimal.ONE);
		BigDecimal skyNetID = chordID.divide(upperBound);
		return new SkyNetID(skyNetID);
	}

	@Override
	public Object getOverlayKey(SkyNetID skyNetKey) {
		// not needed
		return null;
	}

}
