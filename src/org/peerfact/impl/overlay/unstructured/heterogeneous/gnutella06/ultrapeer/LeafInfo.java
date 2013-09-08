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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.ultrapeer;

import java.util.Collections;
import java.util.Set;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IResource;


/**
 * Information that an ultrapeer carries about a leaf connected to it
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LeafInfo {

	/**
	 * Returns the resources shared at the leaf the ultrapeer knows.
	 * 
	 * @return
	 */
	public Set<IResource> getLeafResources() {
		return leafResources;
	}

	/**
	 * Sets the resources shared at the leaf the ultrapeers knows to the
	 * specified set.
	 * 
	 * @param leafResources
	 */
	public void setLeafResources(Set<IResource> leafResources) {
		this.leafResources = leafResources;
	}

	Set<IResource> leafResources = Collections.emptySet();

	@Override
	public String toString() {
		return leafResources.toString();
	}

}
