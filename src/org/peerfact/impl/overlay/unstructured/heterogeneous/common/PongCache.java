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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;


/**
 * A pong cache encapsulates connectivity information of contacts that are
 * different hops away. One contact is one hop away, the next contact is two
 * hops away and so on.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PongCache<TContact extends GnutellaLikeOverlayContact> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -620254459614931209L;

	List<TContact> contacts;

	/**
	 * Creates a new pong cache given the configuration of the node.
	 * 
	 * @param config
	 */
	public PongCache(IGnutellaConfig config) {
		contacts = new ArrayList<TContact>();
		for (int i = 0; i < config.getPongCacheSize(); i++) {
			contacts.add(null);
		}
	}

	/**
	 * Returns the size of this pong cache.
	 * 
	 * @return
	 */
	public int getSize() {
		return contacts.size();
	}

	/**
	 * Returns the size of this pong cache (transmission repressentation) in
	 * bytes.
	 * 
	 * @return
	 */
	public int getByteSize() {
		return contacts.size() * GnutellaLikeOverlayContact.getSize();
	}

	/**
	 * Returns the entry in the pong cache at the defined index. The closest
	 * contact in the pong cache has the index 0.
	 * 
	 * @param index
	 * @return
	 */
	public TContact getEntry(int index) {
		return contacts.get(index);
	}

	/**
	 * Sets an entry to c in the pong cache at the given position.
	 * 
	 * @param index
	 * @param c
	 */
	public void setEntry(int index, TContact c) {
		contacts.set(index, c);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("PongCache(");
		for (TContact c : contacts) {
			buf.append(c + ", ");
		}
		buf.append(")");
		return buf.toString();
	}

}
