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

package org.peerfact.api.service.skyeye;

/**
 * This enumeration defines the types of events, which are used in SkyNet.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public enum SkyNetEventType {

	/**
	 * This event is responsible for triggering the periodical
	 * <i>metric-update</i>.
	 */
	METRICS_UPDATE,
	/**
	 * This event is responsible for triggering the periodical
	 * <i>attribute-update</i> at a Coordinator.
	 */
	ATTRIBUTE_UPDATE,
	/**
	 * This event is responsible for triggering the periodical
	 * <i>attribute-update</i> at a Support Peer, if the node must play this
	 * role. The periodical <i>attribute-update</i> of the Support Peer is
	 * executed in addition to the <i>attribute-update</i> of the Coordinator at
	 * the same node.
	 */
	SUPPORT_PEER_UPDATE,
	/**
	 * This event is responsible for triggering the periodical request, if a
	 * query can be originated.
	 */
	QUERY_REMAINDER,
	/**
	 * This event is responsible for triggering the initialization of a
	 * <code>ParentCoordinatorInformationOperation</code> by which, every
	 * Coordinator sends <code>ParentCoordinatorInformationMsg</code>s to its
	 * Sub-Coordinators.
	 */
	PARENT_COORDINATOR_INFORMATION_UPDATE;
}
