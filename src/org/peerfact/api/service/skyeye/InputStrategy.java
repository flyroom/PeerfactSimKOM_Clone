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

import org.peerfact.api.common.Message;

/**
 * This interface defines the functionality of a SkyNet-node for receiving
 * updates, regardless if they are updates of attributes or metrics.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public interface InputStrategy {

	/**
	 * This method adds a new Sub-Coordinator to the set of existing
	 * Sub-Coordinators. Since the new Sub-Coordinator
	 * <code>subCoordinator</code> is already known, this method just exchanges
	 * the stored values with the new values of <code>subCoordinator</code>.
	 * <code>subCoordinator</code> contains all required information of the
	 * SkyNet-node, which sent an update.
	 * 
	 * @param subCoordinator
	 *            contains the required information of the sending SkyNet-node
	 */
	public void addSubCoordinator(SubCoordinatorInfo subCoordinator);

	/**
	 * This method is called by the <code>SkyNetMessageHandler</code> to forward
	 * the message to this class, where further processing can be done.
	 * 
	 * @param msg
	 *            contains the message, which was received by
	 *            <code>SkyNetMessageHandler</code>
	 * @param timestamp
	 *            contains the point in time of receipt of the <code>msg</code>
	 */
	public void processUpdateMessage(Message msg, long timestamp);

	/**
	 * This method is used to collect and gather the required data from the own
	 * host. The bundled data is prepared and stored for fast access.
	 */
	public void writeOwnDataInStorage();

}
