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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnutellaAccessOverlayContact implements
		OverlayContact<GnutellaOverlayID>, Comparable<GnutellaAccessOverlayContact> {

	private GnutellaOverlayID overlayID;

	private TransInfo transInfo;

	private long timeActivated;

	private long lastRefresh;

	// TODO clever ranking

	public GnutellaAccessOverlayContact(GnutellaOverlayID overlayID,
			TransInfo transInfo) {
		this.overlayID = overlayID;
		this.transInfo = transInfo;
	}

	public GnutellaAccessOverlayContact(OverlayContact<GnutellaOverlayID> contact) {
		this.overlayID = contact.getOverlayID();
		this.transInfo = contact.getTransInfo();
	}

	@Override
	public GnutellaOverlayID getOverlayID() {
		return this.overlayID;
	}

	@Override
	public TransInfo getTransInfo() {
		return this.transInfo;
	}

	@Override
	public int hashCode() {
		return this.overlayID.hashCode();
	}

	@Override
	public int compareTo(GnutellaAccessOverlayContact contact) {
		return this.getOverlayID().compareTo(contact.getOverlayID());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GnutellaAccessOverlayContact) {
			GnutellaAccessOverlayContact contact = (GnutellaAccessOverlayContact) o;
			return this.overlayID.equals(contact.getOverlayID());
		}
		return false;
	}

	@Override
	public String toString() {
		return this.overlayID.toString();
	}

	public void reset() {
		this.timeActivated = Simulator.getCurrentTime();
	}

	public double getRank() {
		return Double.MAX_VALUE
				/ (Simulator.getCurrentTime() - this.timeActivated);
	}

	public long getLastRefresh() {
		return lastRefresh;
	}

	public void refresh() {
		this.lastRefresh = Simulator.getCurrentTime();
	}

	@Override
	public long getTransmissionSize() {
		return overlayID.getTransmissionSize()
				+ transInfo.getTransmissionSize();
	}

}
