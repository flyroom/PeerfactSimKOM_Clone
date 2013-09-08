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

package org.peerfact.impl.service.aggregation.skyeye.attributes;

import java.math.BigDecimal;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SubCoordinatorInfo;
import org.peerfact.impl.service.aggregation.skyeye.AbstractAliasInfo;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


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
 * This class defines the representation of a SkyNet-node, which sends its
 * <i>attribute-updates</i> to the SkyNet-node, which creates an
 * <code>AttributeSubCoordinatorInfo</code>-object out of the sending node.
 * Beside the predefined methods of <code>AbstractAliasInfo</code>, it defines
 * further variables, including their accessing methods, to handle the required
 * information for <i>attribute-updates</i>. Regarding in the SkyNet-tree, the
 * sending node, to whom this representation belongs, plays the role of a
 * Sub-Coordinator, while the receiving node, which creates this representation,
 * is a Coordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 14.11.2008
 * 
 */
public class AttributeSubCoordinatorInfo extends AbstractAliasInfo implements
		SubCoordinatorInfo {

	private static Logger log = SimLogger
			.getLogger(AttributeSubCoordinatorInfo.class);

	private long updatePeriode;

	private long updateThreshold;

	private int requestedEntries;

	private boolean isSupportPeer;

	private TreeMap<BigDecimal, AttributeEntry> data;

	public AttributeSubCoordinatorInfo(SkyNetNodeInfo nodeInfo,
			int numberOfUpdates, long timestampOfUpdate, long updateThreshold,
			int requestedEntries, TreeMap<BigDecimal, AttributeEntry> data,
			boolean isSupportPeer) {
		this.nodeInfo = nodeInfo;
		this.numberOfUpdates = numberOfUpdates;
		this.requestedEntries = requestedEntries;
		this.updateThreshold = updateThreshold;
		if (this.timestampOfUpdate == -1) {
			updatePeriode = updateThreshold;
		}
		this.timestampOfUpdate = timestampOfUpdate;
		this.data = data;
		this.isSupportPeer = isSupportPeer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.skynet.AliasInfo#setTimestampOfUpdate(long)
	 */
	@Override
	public void setTimestampOfUpdate(long timestampOfUpdate) {
		if (this.timestampOfUpdate < timestampOfUpdate) {
			if (timestampOfUpdate - this.timestampOfUpdate >= updateThreshold) {
				updatePeriode = timestampOfUpdate - this.timestampOfUpdate;
				log.debug("Got new UpdatePeriode "
						+ Simulator.getFormattedTime(updatePeriode));
			}
		} else {
			log.error("Received Message is to old");
		}
		this.timestampOfUpdate = timestampOfUpdate;
	}

	/**
	 * This method returns the period of time, between the actual and the last
	 * <i>attribute-updates</i>, which a Sub-Coordinator sent.
	 * 
	 * @return the period of time
	 */
	public long getUpdatePeriode() {
		return updatePeriode;
	}

	/**
	 * This method returns the data, which a Sub-Coordinator sent. The data
	 * consists of the <code>AttributeEntry</code> of the sending node, as well
	 * as of the <code>AttributeEntry</code>s, which it received from its
	 * children.
	 * 
	 * @return a sorted map, which contains data
	 */
	public TreeMap<BigDecimal, AttributeEntry> getData() {
		return data;
	}

	/**
	 * If the Sub-Coordinator sends further messages with data, the Coordinator,
	 * which manages this object, does not create a new
	 * <code>AttributeSubCoordinatorInfo</code>-object, but exchanges the old
	 * with the new data.
	 * 
	 * @param data
	 *            contains the <code>AttributeEntry</code>s of the
	 *            Sub-Coordinator.
	 */
	public void setData(TreeMap<BigDecimal, AttributeEntry> data) {
		this.data = data;
	}

	/**
	 * This method returns the threshold, which a new measured value may never
	 * under-run for <code>updatePeriode</code>.
	 * 
	 * @return the threshold for <code>updatePeriode</code>
	 */
	public long getUpdateThreshold() {
		return updateThreshold;
	}

	/**
	 * This method substitutes the old with the new threshold, which narrows the
	 * <code>updatePeriode</code>.
	 * 
	 * @param updateThreshold
	 *            contains the period of time for the threshold.
	 */
	public void setUpdateThreshold(long updateThreshold) {
		this.updateThreshold = updateThreshold;
	}

	/**
	 * This method returns the amount of <code>AttributeEntry</code>s, which the
	 * Sub-Coordinator would like to send.
	 * 
	 * @return the amount of advertised <code>AttributeEntry</code>s
	 */
	public int getRequestedEntries() {
		return requestedEntries;
	}

	/**
	 * This method substitutes the old with the new amount of entries, which a
	 * Sub-Coordinator would like to send.
	 * 
	 * @param requestedEntries
	 *            the new amount of advertised <code>AttributeEntry</code>s
	 */
	public void setRequestedEntries(int requestedEntries) {
		this.requestedEntries = requestedEntries;
	}

	@Override
	public String toString() {
		return "[" + nodeInfo.toString() + "; no. of Updates = "
				+ numberOfUpdates + "; timestamp of last update = "
				+ timestampOfUpdate + "; update periode = "
				+ Simulator.getFormattedTime(updatePeriode) + "]";
	}

	/**
	 * This method returns, if the sending node acts as Sub-Coordinator or
	 * Support Peer.
	 * 
	 * @return <code>true</code> if the sending node acts as Support Peer,
	 *         <code>false</code> otherwise
	 */
	public boolean isSupportPeer() {
		return isSupportPeer;
	}

	/**
	 * This method sets the current role of the sending node.
	 * 
	 * @param isSupportPeer
	 *            is <code>true</code>, if the sending node acts as Support
	 *            Peer, <code>false</code> otherwise
	 */
	public void setSupportPeer(boolean isSupportPeer) {
		this.isSupportPeer = isSupportPeer;
	}
}
