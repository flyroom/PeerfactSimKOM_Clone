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

package org.peerfact.impl.overlay.informationdissemination.cs;

import java.util.List;
import java.util.Vector;

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
 * The class stores the clientNodeInfo, which are send the server. This are all
 * nodes, which are in the area of interest of the local client node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 * 
 */
public class ClientStorage {

	/**
	 * A list of {@link ClientNodeInfo}.
	 */
	private List<ClientNodeInfo> storage;

	/**
	 * Create the client storage
	 */
	public ClientStorage() {
		this.storage = new Vector<ClientNodeInfo>();
	}

	/**
	 * Clear the storage and add the given List.
	 * 
	 * @param neighbors
	 *            The new neighbors, which are send the server.
	 */
	public void replaceNeighbors(List<ClientNodeInfo> neighbors) {
		storage.clear();
		this.storage.addAll(neighbors);
	}

	/**
	 * Returns the list of neighbors.
	 * 
	 * @return A copy of the storage.
	 */
	public List<ClientNodeInfo> getNeighbors() {
		return new Vector<ClientNodeInfo>(storage);
	}
}
