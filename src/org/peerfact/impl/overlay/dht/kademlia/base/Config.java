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

package org.peerfact.impl.overlay.dht.kademlia.base;

import org.peerfact.impl.overlay.dht.kademlia.base.components.ComponentsConfig;

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
 * Combines several Config interfaces into one and permits to retrieve
 * configuration constants. (That is, all values returned by methods from this
 * interface are constant.)
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface Config extends ComponentsConfig {
	/**
	 * @return the number of KademliaOverlayContacts that will be given to the
	 *         Nodes to initially fill their routing tables.
	 *         <p>
	 *         These contacts may be offline, hence this number need not be too
	 *         low. In contrast, it is assumed that the total number of peers
	 *         (in subclasses: number of peers per cluster) is much higher than
	 *         the number of initial routing table contacts. However, that
	 *         assumption is made only for efficiency considerations. The only
	 *         necessary condition is that the number of peers (per cluster) may
	 *         not be smaller than the number of initial routing table contacts.
	 */
	public int getNumberOfInitialRoutingTableContacts();

	/**
	 * @return the path to the file that contains the cluster mappings. Has to
	 *         be relative to the programme directory (or absolute).
	 */
	public String getClusterMappingFilePath();
}
