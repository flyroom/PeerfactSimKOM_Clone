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

import org.peerfact.impl.service.aggregation.skyeye.attributes.SPAttributeInputStrategy;
import org.peerfact.impl.service.aggregation.skyeye.attributes.SPAttributeUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.queries.SPQueryHandler;

/**
 * This interface defines the methods for a SkyNet-node, which plays its role as
 * Support Peer. Since we divided the complete SkyNet-node into two parts, this
 * part describes the functionality, which only a Support Peer can employ. In
 * detail, the listed methods appoint the components of a SkyNet-node, which the
 * Support Peer may access.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SupportPeer extends SkyNetLayer {

	/**
	 * This method allows the calling Support Peer to access the functionality
	 * of the <code>SPAttributeInputStrategy</code>
	 * 
	 * @return the reference of the <code>SPAttributeInputStrategy</code>-object
	 */
	public SPAttributeInputStrategy getSPAttributeInputStrategy();

	/**
	 * This method allows the calling Support Peer to access the functionality
	 * of the <code>SPAttributeUpdateStrategy</code>
	 * 
	 * @return the reference of the <code>SPAttributeUpdateStrategy</code>
	 *         -object
	 */
	public SPAttributeUpdateStrategy getSPAttributeUpdateStrategy();

	/**
	 * This method is used to activate the Support Peer at a SkyNet-node and to
	 * set the flag of an active Support Peer. By executing the appropriate
	 * getter- method, the whole SkyNet-node can check the status of its Support
	 * Peer (activated or idle).
	 * 
	 * @param flag
	 *            sets the status for the part as Support Peer.
	 *            <code>true</code> activates the Support Peer, while
	 *            <code>false</code> shuts the part down.
	 */
	public void setSupportPeer(boolean flag);

	/**
	 * This method allows the calling Support Peer to access the functionality
	 * of the <code>SPQueryHandler</code>
	 * 
	 * @return the reference of the <code>SPQueryHandler</code>-object
	 */
	public SPQueryHandler getSPQueryHandler();

}
