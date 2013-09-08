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

package org.peerfact.impl.service.aggregation.skyeye;

import java.math.BigDecimal;

import org.peerfact.api.overlay.OverlayID;

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
 * This class defines the type of the ID for SkyNet. Since the IDs of SkyNet
 * range between 0 and 1 and since we must map large overlay-IDs into that
 * interval, the IDs of SkyNet are represented by the class
 * <code>BigDecimal</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetID implements OverlayID<BigDecimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2858441757649061064L;

	private BigDecimal skyNetID;

	public SkyNetID(String id) {
		skyNetID = new BigDecimal(id);
	}

	public SkyNetID(BigDecimal id) {
		skyNetID = id;
	}

	/**
	 * This method returns the SkyNet-ID of node as <code>BigDecimal</code>.
	 * 
	 * @return the SkyNet-ID as <code>BigDecimal</code>.
	 */
	public BigDecimal getID() {
		return skyNetID;
	}

	/**
	 * This method returns the SkyNet-ID of node as <code>String</code>.
	 * 
	 * @return the SkyNet-ID as <code>String</code>.
	 */
	public String getPlainSkyNetID() {
		return skyNetID.toPlainString();
	}

	@Override
	public byte[] getBytes() {
		// not needed
		return null;
	}

	/**
	 * This method returns the SkyNet-ID of node as a simple <code>Object</code>
	 * .
	 * 
	 * @return the SkyNet-ID as <code>Object</code>.
	 */
	@Override
	public BigDecimal getUniqueValue() {
		return skyNetID;
	}

	@Override
	public int compareTo(OverlayID<BigDecimal> arg0) {
		if (arg0 == null) {
			return 1;
		} else {
			BigDecimal foreignID = ((SkyNetID) arg0).getID();
			return (this.skyNetID.compareTo(foreignID));
		}
	}

	/**
	 * This method tests, if this SkyNet-ID ranges between the two provided
	 * bounds.
	 * 
	 * @param a
	 *            the first bound
	 * @param b
	 *            the second bound
	 * @return <code>true</code>, if the value of this ID is between the
	 *         provided bounds, <code>false</code> otherwise.
	 */
	public boolean between(SkyNetID a, SkyNetID b) {
		if (a.compareTo(b) < 0) {
			if (this.compareTo(a) > 0 && this.compareTo(b) < 0) {
				return true;
			}
		} else {
			if (this.compareTo(a) > 0 || this.compareTo(b) < 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}
}
