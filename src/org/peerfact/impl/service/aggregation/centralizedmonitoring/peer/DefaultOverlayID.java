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

package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

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
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class DefaultOverlayID implements OverlayID<Integer> {
	protected Integer myId;

	public byte[] myBytes;

	public DefaultOverlayID(Integer id) {
		myId = id;
		myBytes = myId.toString().getBytes();
	}

	public DefaultOverlayID(String id) {
		myId = Integer.parseInt(id);
		myBytes = id.getBytes();
	}

	@Override
	public Integer getUniqueValue() {
		return myId;
	}

	@Override
	public int hashCode() {
		return myId.hashCode();
	}

	@Override
	public String toString() {
		return myId.toString();
	}

	@Override
	public byte[] getBytes() {
		return myBytes;
	}

	@Override
	public int compareTo(OverlayID<Integer> o) {
		DefaultOverlayID gId = (DefaultOverlayID) o;
		if (gId.myId.intValue() == myId.intValue()) {
			return 0;
		} else if ((myId.intValue() - gId.myId.intValue()) > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}
}
