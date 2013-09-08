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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing;

import java.io.Serializable;

import org.peerfact.api.overlay.OverlayKey;

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
public class FilesharingKey implements OverlayKey<Integer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147201521263760217L;

	private Integer rank;

	public FilesharingKey(Integer rank) {
		this.rank = rank;
	}

	public Integer getRank() {
		return rank;
	}

	@Override
	public byte[] getBytes() {
		// not implemented
		return null;
	}

	@Override
	public Integer getUniqueValue() {
		return rank;
	}

	@Override
	public int compareTo(OverlayKey<Integer> arg0) {
		if (arg0 instanceof FilesharingKey) {
			return ((FilesharingKey) arg0).getRank() - rank;
		}
		return 0;
	}

	public boolean equals(OverlayKey<Integer> key) {
		if (key instanceof FilesharingKey) {
			FilesharingKey filesharingKey = (FilesharingKey) key;
			if (filesharingKey.getRank().equals(rank)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return rank.toString();
	}

	@Override
	public long getTransmissionSize() {
		return 4;
	}

}
