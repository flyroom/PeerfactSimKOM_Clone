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

package org.peerfact.impl.service.dhtstorage.past;

import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;

/**
 * Contact Information of a Node that stores a DHTObject.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PASTContact implements Cloneable {

	/**
	 * this Contacts TransInfo
	 */
	private TransInfo transInfo;

	/**
	 * time this Contact had its most recent interaction with this service
	 */
	private long lastAction;

	private long ttl;

	private boolean isOffline = false;

	/**
	 * Create a new Contact
	 * 
	 * @param transInfo
	 * @param ttl
	 *            Time to Live for this contact
	 */
	public PASTContact(TransInfo transInfo, long ttl) {
		this.transInfo = transInfo;
		this.ttl = ttl;
		updateLastAction();
	}

	/**
	 * Set lastAction of this contact to "now"
	 */
	public void updateLastAction() {
		lastAction = Simulator.getCurrentTime();
		isOffline = false;
	}

	/**
	 * Mark this contact as offline
	 * 
	 * @param offline
	 */
	public void markAsOffline() {
		isOffline = true;
	}

	/**
	 * is this contact offline?
	 * 
	 * @return
	 */
	public boolean isOffline() {
		return isOffline || Simulator.getCurrentTime() > ttl + lastAction;
	}

	/**
	 * time this Contact had its last interaction with our service
	 * 
	 * @return
	 */
	public long getLastAction() {
		return lastAction;
	}

	/**
	 * the contacts TransInfo
	 * 
	 * @return
	 */
	public TransInfo getTransInfo() {
		return transInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isOffline ? 1231 : 1237);
		result = prime * result + (int) (lastAction ^ (lastAction >>> 32));
		result = prime * result
				+ ((transInfo == null) ? 0 : transInfo.hashCode());
		result = prime * result + (int) (ttl ^ (ttl >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PASTContact)) {
			return false;
		}
		PASTContact other = (PASTContact) obj;
		if (isOffline != other.isOffline) {
			return false;
		}
		if (lastAction != other.lastAction) {
			return false;
		}
		if (transInfo == null) {
			if (other.transInfo != null) {
				return false;
			}
		} else if (!transInfo.equals(other.transInfo)) {
			return false;
		}
		if (ttl != other.ttl) {
			return false;
		}
		return true;
	}

	/**
	 * Clone a Contact, so that timestamps are deleted. The cloned instance and
	 * the original instance will return equals() == true
	 */
	@Override
	public PASTContact clone() {
		return new PASTContact(getTransInfo(), ttl);
	}

}
