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

package org.peerfact.impl.service.aggregation.skyeye;

import java.io.Serializable;

import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;


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
 * This class defines the representation of a SkyNet-node, which is called
 * Support Peer and receives the <i>attribute-updates</i> of a SkyNet-node. The
 * <code>SupportPeerInfo</code>-object is provided by the parent of the sending
 * node. Beside the predefined methods of <code>AbstractAliasInfo</code>, this
 * class defines further variables, including their accessing methods, to handle
 * the required information, which are needed for sending
 * <i>attribute-updates</i> to the Support Peer. The node, which receives this
 * <code>SupportPeerInfo</code>-object, is denoted as Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public class SupportPeerInfo extends AbstractAliasInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6537098871926663852L;

	private int tThreshold;

	public SupportPeerInfo(SkyNetNodeInfo nodeInfo, int numberOfUpdates,
			long timestampOfUpdate, int tThreshold) {
		this.nodeInfo = nodeInfo;
		this.numberOfUpdates = numberOfUpdates;
		this.timestampOfUpdate = timestampOfUpdate;
		this.tThreshold = tThreshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.skynet.AliasInfo#setTimestampOfUpdate(long)
	 */
	@Override
	public void setTimestampOfUpdate(long timestampOfUpdate) {
		this.timestampOfUpdate = timestampOfUpdate;
	}

	public static long getSize() {
		return SkyNetConstants.SKY_NET_NODE_INFO_SIZE + 2
				* Constants.INT_SIZE + Constants.LONG_SIZE
				+ Constants.BOOLEAN_SIZE;
	}

	/**
	 * If an <i>attribute-update</i> is sent to the Support Peer, the variable
	 * <i>numberOfUpdates</i>, which contained the complete amount of updates
	 * for the Support Peer, is decremented.
	 */
	public void decrementNumberOfUpdates() {
		numberOfUpdates = numberOfUpdates - 1;
	}

	/**
	 * This method returns the amount of <code>AttributeEntry</code>s, which the
	 * Coordinator can send to the Support Peer at most.
	 * 
	 * @return the maximum amount of <code>AttributeEntry</code>s.
	 */
	public int getTThreshold() {
		return tThreshold;
	}

	/**
	 * This method sets the maximum amount of <code>AttributeEntry</code>s,
	 * which the Coordinator can send to the Support Peer at most.
	 * 
	 * @param threshold
	 *            contains the new maximum amount of <code>AttributeEntry</code>
	 *            s.
	 */
	public void setTThreshold(int threshold) {
		tThreshold = threshold;
	}

}
