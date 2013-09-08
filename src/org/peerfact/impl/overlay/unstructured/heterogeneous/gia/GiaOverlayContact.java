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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import java.util.Comparator;

import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;


/**
 * Contact information of a Gia peer in the overlay network.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GiaOverlayContact extends GnutellaLikeOverlayContact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3735650837148011667L;

	private int capacity;

	/**
	 * @param id
	 * @param transInfo
	 * @param isUltrapeer
	 * @param capacity
	 */
	public GiaOverlayContact(GnutellaOverlayID id, TransInfo transInfo,
			int capacity) {
		super(id, transInfo);
		this.capacity = capacity;
	}

	/**
	 * Returns the Gia capacity assigned to this node
	 * 
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns a new comparator for comparing the node's capacities.
	 * 
	 * @return
	 */
	public static Comparator<GiaOverlayContact> getCapacityComparator() {
		return new Comparator<GiaOverlayContact>() {

			@Override
			public int compare(GiaOverlayContact o1, GiaOverlayContact o2) {
				return o1.getCapacity() - o2.getCapacity();

			}

		};
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

}
