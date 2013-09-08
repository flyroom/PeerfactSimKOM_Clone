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

package org.peerfact.impl.service.publishsubscribe.mercury;

import java.io.Serializable;
import java.util.Arrays;

import org.peerfact.api.common.Transmitable;
import org.peerfact.api.transport.TransInfo;

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
 * Information needed to contact another MercuryService, including attribute
 * range this node is responsible for. This Information is transmitted within a
 * MercuryMessage
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryContact implements Transmitable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3188279607847341937L;

	private String attributeName = null;

	private TransInfo transInfo = null;

	private Comparable<Object>[] range;

	public MercuryContact(String attributeName, TransInfo transInfo,
			Comparable<?>[] range) {
		this.attributeName = attributeName;
		this.transInfo = transInfo;
		this.range = (Comparable<Object>[]) range;
	}

	/**
	 * get the name of the Attribute this node is responsible for
	 * 
	 * @return
	 */
	public String getAttribute() {
		return attributeName;
	}

	/**
	 * get this nodes TransInfo
	 * 
	 * @return
	 */
	public TransInfo getTransInfo() {
		return transInfo;
	}

	/**
	 * Get min and max value of the attribute range this node is responsible for
	 * 
	 * @return
	 */
	public Comparable<Object>[] getRange() {
		return range;
	}

	/**
	 * update Range
	 * 
	 * @param range
	 */
	public void setRange(Comparable<Object> rangeMin,
			Comparable<Object> rangeMax) {
		if (rangeMin != null) {
			this.range[0] = rangeMin;
		}
		if (rangeMax != null) {
			this.range[1] = rangeMax;
		}
	}

	@Override
	public long getTransmissionSize() {
		return attributeName.getBytes().length + transInfo.getTransmissionSize();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + Arrays.hashCode(range);
		result = prime * result
				+ ((transInfo == null) ? 0 : transInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MercuryContact other = (MercuryContact) obj;
		if (attributeName == null) {
			if (other.attributeName != null) {
				return false;
			}
		} else if (!attributeName.equals(other.attributeName)) {
			return false;
		}
		if (!Arrays.equals(range, other.range)) {
			return false;
		}
		if (transInfo == null) {
			if (other.transInfo != null) {
				return false;
			}
		} else if (!transInfo.equals(other.transInfo)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MC: " + attributeName + " " + range[0] + "-" + range[1] + " IP"
				+ getTransInfo().getNetId().toString();
	}

}
