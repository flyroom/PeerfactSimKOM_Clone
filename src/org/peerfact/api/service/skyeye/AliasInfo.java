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
 * This interface defines the information of a SkyNet-node within a certain
 * context, which another node stores. The storing node uses this representation
 * to change and alter the state of the represented node. The interface defines
 * the common methods, which are needed to get and set the state of that node.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public interface AliasInfo {

	/**
	 * This method returns the ID of the node, to which this information
	 * belongs.
	 * 
	 * @return the <code>SkyNetNodeInfo</code>-object of the node, to which this
	 *         information belongs.
	 */
	public SkyNetNodeInfo getNodeInfo();

	/**
	 * This method returns the number of updates, which the represented node
	 * gets from the node.
	 * 
	 * @return number of updates
	 */
	public int getNumberOfUpdates();

	/**
	 * This method defines the new amount of updates, which a node, to whom this
	 * representation belongs, still gets from the node, which actually manages
	 * the representation.
	 * 
	 * @param no
	 *            defines the new amount of updates
	 */
	public void setNumberOfUpdates(int no);

	/**
	 * This method returns the last point in time, when information was
	 * exchanged between the represented and the storing node.
	 * 
	 * @return the point in time of
	 */
	public long getTimestampOfUpdate();

	/**
	 * Stores the actual point in time, when the exchange of information between
	 * the represented and the storing node just finished.
	 * 
	 * @param timestampOfUpdate
	 *            contains the time of the last exchange
	 */
	public void setTimestampOfUpdate(long timestampOfUpdate);
}
