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

package org.peerfact.impl.overlay.dht.can.components;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * The Contact-card of every node. It includes the ID, TransInfo, isAllive and
 * CanArea. These contact cards are sent through the network.
 * 
 * @author Bjoern Dollk <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanOverlayContact implements OverlayContact<CanOverlayID>,
		Comparable<CanOverlayID>, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2568297838203457209L;

	private static Logger log = SimLogger.getLogger(CanNode.class);

	private CanOverlayID ID;

	private TransInfo transInfo;

	private boolean isAlive;

	private CanArea area;

	public CanOverlayContact(CanOverlayID ID, TransInfo transInfo) {
		this.ID = ID;
		this.transInfo = transInfo;
		this.area = new CanArea();
		isAlive = false;
		log.debug(Simulator.getSimulatedRealtime() + " New Contact: ID: "
				+ ID.toString());
	}

	public CanOverlayContact(CanOverlayID ID, TransInfo transInfo,
			CanArea area, boolean isAlive) {
		this.ID = ID;
		this.transInfo = transInfo;
		this.area = area;
		this.isAlive = isAlive;
	}

	@Override
	public CanOverlayID getOverlayID() {
		return this.ID;
	}

	@Override
	public TransInfo getTransInfo() {
		return this.transInfo;
	}

	public void setAlive(boolean alive) {
		this.isAlive = alive;
	}

	public boolean isAlive() {
		return this.isAlive;
	}

	@Override
	public String toString() {
		String ret = "[oid= x=" + ID.getValue() + " -> ip="
				+ transInfo.getNetId() + "]";
		return ret;
	}

	@Override
	public int compareTo(CanOverlayID o) {
		return this.ID.compareTo(o);
	}

	@Override
	public CanOverlayContact clone() {
		CanOverlayContact cloned = new CanOverlayContact(this.ID,
				this.transInfo, this.area, this.isAlive());
		cloned.setAlive(this.isAlive);
		return cloned;
	}

	public CanArea getArea() {
		return area;
	}

	public void setArea(CanArea area) {
		this.area = area;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + (isAlive ? 1231 : 1237);
		result = prime * result
				+ ((transInfo == null) ? 0 : transInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CanOverlayContact other = (CanOverlayContact) obj;
		if (ID == null) {
			if (other.ID != null) {
				return false;
			}
		} else if (!ID.equals(other.ID)) {
			return false;
		}
		if (area == null) {
			if (other.area != null) {
				return false;
			}
		} else if (!area.equals(other.area)) {
			return false;
		}
		if (isAlive != other.isAlive) {
			return false;
		}
		if (transInfo == null) {
			if (other.transInfo != null) {
				return false;
			}
		} else if (!transInfo.equals(other.transInfo)) {
			return false;
		}
		return true;
	}

	@Override
	public long getTransmissionSize() {
		return ID.getTransmissionSize() + transInfo.getTransmissionSize();
	}

}
