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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

/**
 * Decides about accepting, rejecting and dropping contacts for connection
 * according to "Algorithm 1" in the Gia paper.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ConnectAcceptanceDecider {

	Decision decision;

	GiaOverlayContact contactToDrop;

	public ConnectAcceptanceDecider(GiaConnectionManager mgr,
			GiaOverlayContact requester, int requesterDegree, IGiaConfig config) {

		if (mgr.getDegree() < mgr.getMaxNbrs()) {
			// we have room
			decision = Decision.Accept;
			return;
		}
		// we need to drop a neighbor

		GiaOverlayContact highestDegreeNeighbor = null;
		int highestDegree = -1;

		for (GiaOverlayContact c : mgr.getConnectedContacts()) {
			if (c.getCapacity() <= requester.getCapacity()) {
				int degree = getDegreeOf(c, mgr);
				if (degree > highestDegree) {
					highestDegree = degree;
					highestDegreeNeighbor = c;
				}
			}
		}
		if (highestDegreeNeighbor == null) {
			decision = Decision.Reject;
			return;
		}
		if (requester.getCapacity() > getMaximumCapacityNeighbor(mgr)
				.getCapacity()
				||
				getDegreeOf(highestDegreeNeighbor, mgr) > requesterDegree
				+ config.getConnectDecisionHysteresis()) {
			decision = Decision.DropAndAccept;
			contactToDrop = highestDegreeNeighbor;
			return;
		}
		decision = Decision.Reject;
	}

	private static int getDegreeOf(GiaOverlayContact c, GiaConnectionManager mgr) {
		return mgr.getMetadata(c).getLastDegreeObserved();
	}

	public static GiaOverlayContact getMaximumCapacityNeighbor(
			GiaConnectionManager mgr) {
		int maxCap = -1;
		GiaOverlayContact result = null;
		for (GiaOverlayContact c : mgr.getConnectedContacts()) {
			int capacity = c.getCapacity();
			if (capacity > maxCap) {
				maxCap = capacity;
				result = c;
			}
		}
		return result;
	}

	public Decision getDecision() {
		return decision;
	}

	public GiaOverlayContact getContactToDrop() {
		return contactToDrop;
	}

	public enum Decision {
		Accept,
		Reject,
		DropAndAccept
	}

}
