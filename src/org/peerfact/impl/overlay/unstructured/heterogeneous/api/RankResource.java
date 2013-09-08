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

package org.peerfact.impl.overlay.unstructured.heterogeneous.api;

import org.peerfact.Constants;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTValue;

/**
 * Resource that is just being identified with a unique rank. The resource is
 * equal to another one if the ranks are equal.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RankResource implements IResource {

	private int rank;

	private DHTKey<Integer> key;

	/**
	 * Cre
	 * 
	 * @param rank
	 */
	public RankResource(int rank) {
		this.rank = rank;
		this.key = new GnutellaOverlayKey<Integer>(rank);
	}

	/**
	 * Returns the integer rank of this resource.
	 * 
	 * @return
	 */
	public int getRank() {
		return rank;
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof RankResource)) {
			return false;
		}

		return ((RankResource) o).rank == this.rank;
	}

	@Override
	public int hashCode() {
		return rank;
	}

	@Override
	public String toString() {
		return String.valueOf(rank);
	}

	@Override
	public int getSize() {
		return (int) (key.getTransmissionSize() + Constants.INT_SIZE);
		// Long
		// identifier
		// and reference
		// to an IP
		// address.
	}

	@Override
	public DHTKey<Integer> getKey() {
		return key;
	}

	@Override
	public DHTValue getValue() {
		return this;
	}

	@Override
	public long getTransmissionSize() {
		return getSize();
	}

}
