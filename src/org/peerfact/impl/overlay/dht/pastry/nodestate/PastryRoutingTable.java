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

package org.peerfact.impl.overlay.dht.pastry.nodestate;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.overlay.OverlayRoutingTable;
import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.operations.RequestRouteSetOperation;
import org.peerfact.impl.simengine.Simulator;


/**
 * This class represents the routing table of node in the pastry overlay.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PastryRoutingTable implements
		OverlayRoutingTable<PastryID, PastryContact>, Iterable<PastryContact> {

	/**
	 * The routing table array
	 */
	private RouteSet[][] routingTable;

	/**
	 * There are 2^b entries in each row (including an entry for the owning node
	 * for simplicity).
	 */
	private static final int NUM_OF_COLUMNS = 1 << PastryConstants.ID_BASE_BIT_LENGTH;

	/**
	 * There are ID_BIT_LENGTH/b rows.
	 */
	private static final int NUM_OF_ROWS = PastryConstants.ID_BIT_LENGTH
			/ PastryConstants.ID_BASE_BIT_LENGTH;

	/**
	 * The ID of the node owning this routing table.
	 */
	private PastryID nodeId;

	/**
	 * Node owning this table
	 */
	private PastryNode node;

	/**
	 * Tells when the routing table was changed the last time.
	 */
	private long lastChanged;

	public PastryRoutingTable(PastryNode node) {
		this.nodeId = node.getOverlayID();
		this.node = node;
		reset();
	}

	public void reset() {
		routingTable = new RouteSet[NUM_OF_ROWS][NUM_OF_COLUMNS];

		// Insert the owners Entries
		for (int i = 0; i < NUM_OF_ROWS; i++) {
			int digit = nodeId.getDigit(i);
			routingTable[i][digit] = RouteSet.OWNER_SET;
		}

		lastChanged = Simulator.getCurrentTime();
	}

	/**
	 * Insert a contact to the set
	 * 
	 * @param c
	 */
	public void insertContact(PastryContact c) {
		if (c.getOverlayID().equals(nodeId)) {
			return;
		}

		RouteSet setToInsertIn = makeBestEntry(c.getOverlayID());
		if (!setToInsertIn.contains(c)) {
			setToInsertIn.insertEntry(c);
			cleanSet(setToInsertIn);
			if (setToInsertIn.contains(c)) {
				lastChanged = Simulator.getCurrentTime();
			}
		}

	}

	/**
	 * Makes sure that the given set's size does not exceed the defined maximum
	 * size.
	 * 
	 * @param s
	 *            the set to check
	 */
	private void cleanSet(RouteSet s) {

		while (s.getSize() > PastryConstants.MAX_REDUNDANT_ROUTING_ENTRIES) {
			/*
			 * FIXME: Here we have to decide what to do. In my opinion we should
			 * keep the entries with the best proximity metric value compared to
			 * the owning peers.
			 */

			// For now: Remove any of the contacts
			PastryContact contactToRemove = s.iterator().next();
			s.removeEntry(contactToRemove);

			lastChanged = Simulator.getCurrentTime();
		}

	}

	public void insertAll(Collection<PastryContact> contacts) {

		for (PastryContact c : contacts) {
			insertContact(c);
		}

		// if (contacts.size() > 0)
		// lastChanged = Simulator.getCurrentTime();
		// FIXED: this should not be updated if the set does not change. Taken
		// care of in insertContact()
	}

	public boolean remove(PastryContact c) {
		RouteSet targetSet = getBestRouteSet(c.getOverlayID());
		if (targetSet == null) {
			return true;
		}
		boolean removed = targetSet.removeEntry(c);
		if (removed) {
			lastChanged = Simulator.getCurrentTime();
		}
		if (targetSet.getSize() == 0) {
			/*
			 * Set will be empty after removal of this node. We try to find a
			 * new Set of contacts by querying other nodes. Make sure this
			 * operation will only be started once for each dead node.
			 * 
			 * TODO maybe define a certain threshold != 0 with respect to
			 * Config.MAX_REDUNDANT_ROUTING_ENTRIES
			 */

			if (!targetSet.isUpdating()) {
				RequestRouteSetOperation op = new RequestRouteSetOperation(
						node, targetSet, c.getOverlayID());
				targetSet.startUpdateOperation(op);
				op.scheduleImmediately();
			}

		}
		return removed;
	}

	// public void removeAndSubstitute(PastryContact c) {
	// RouteSet targetSet = getBestRouteSet(c.getOverlayID());
	// boolean removed = targetSet.removeEntry(c);
	// if (removed) {
	// lastChanged = Simulator.getCurrentTime();
	//
	// if (targetSet.getSize() == 0) {
	// PastryContact nodeToAskForSubstitution = getNumericallyClosest(c
	// .getOverlayID());
	// // FIXME: Go on here. Request an substitution for the
	// // entry at
	// // the closest node we have.
	// }
	// }
	// }

	/**
	 * Gets the RouteSet that match at least one more digit of the ID than the
	 * local ID.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return the RouteSet, or null if there is none
	 */
	private RouteSet getBestRouteSet(PastryID id) {
		int diffDigit = nodeId.indexOfMSDD(id); // FIXED: should be nodeID

		// if there is no difference, return null
		if (diffDigit < 0) {
			return null;
		}

		int digit = id.getDigit(diffDigit);

		return routingTable[diffDigit][digit];
	}

	private RouteSet[] getBestRouteSetRow(PastryID id) {
		int diffDigit = nodeId.indexOfMSDD(id);

		// if there is no difference, return null
		if (diffDigit < 0) {
			return null;
		}

		// Return a new instance of the RouteSet
		return routingTable[diffDigit];
	}

	public PastryContact getContactWithLongerCommonPrefix(PastryID id) {
		return getContactWithLongerCommonPrefix(id,
				new LinkedList<PastryContact>());
	}

	public PastryContact getContactWithLongerCommonPrefix(PastryID id,
			List<PastryContact> exclude) {
		/*
		 * Check the best RouteSet according to ID prefix length
		 */
		RouteSet bestSet = getBestRouteSet(id);

		return StateHelpers.getClosestContact(id, exclude, bestSet);
	}

	public PastryContact getNumericallyClosest(PastryID id) {
		return getNumericallyClosest(id, new LinkedList<PastryContact>());
	}

	public PastryContact getNumericallyClosest(PastryID id,
			List<PastryContact> exclude) {
		/*
		 * Check the complete row for a numerically closer contact
		 */

		PastryContact minContact = null;
		BigInteger minDistance = null;

		RouteSet[] bestSetRow = getBestRouteSetRow(id);

		for (RouteSet rS : bestSetRow) {
			if (rS == null || rS.getSize() == 0) {
				continue;
			}

			PastryContact localMinContact = StateHelpers.getClosestContact(id,
					exclude, rS);

			if (minContact == null) {
				minContact = localMinContact;
				minDistance = id.getMinAbsDistance(minContact.getOverlayID());
			} else {
				BigInteger d = id.getMinAbsDistance(minContact.getOverlayID());

				if (d.compareTo(minDistance) < 0) {
					minDistance = d;
					minContact = localMinContact;
				}
			}
		}

		/*
		 * Check the other rows for a numerically closer contact
		 */
		if (minContact == null) {
			// TODO: Check for the closest contact in other rows
		}

		return minContact;
	}

	/**
	 * Like getBestEntry, but creates an entry if none currently exists.
	 * 
	 * @param id
	 *            the ID
	 * 
	 * @return the RouteSet, a new, empty RouteSet if there was none
	 */
	private RouteSet makeBestEntry(PastryID id) {
		int diffDigit = nodeId.indexOfMSDD(id);

		// If there is no difference, return null
		if (diffDigit < 0) {
			return null;
		}

		int digit = id.getDigit(diffDigit);

		// Insert new RouteSet if there is none for the indices
		if (routingTable[diffDigit][digit] == null) {
			routingTable[diffDigit][digit] = new RouteSet();
		}

		// Return a new instance of the RouteSet
		return routingTable[diffDigit][digit];
	}

	/**
	 * Counts the number of unique entries in the routing table
	 * 
	 * @return the number of unique entries
	 */
	public int getNumOfUniqueEntries() {
		/*
		 * Note: This method relies on a correct implementation of the methods
		 * equals and hashCode for PastryContacts.
		 * 
		 * --> Both were implemented in a meaningful way.
		 */

		LinkedHashSet<PastryContact> set = new LinkedHashSet<PastryContact>();

		for (int row = 0; row < NUM_OF_ROWS; row++) {
			for (int col = 0; col < NUM_OF_COLUMNS; col++) {
				RouteSet rs = routingTable[row][col];
				if (rs != null && rs != RouteSet.OWNER_SET) {
					for (PastryContact contact : rs) {
						set.add(contact);
					}
				}
			}
		}
		return set.size();
	}

	/**
	 * @return Tells when the routing table was changed the last time.
	 */
	public long getLastChanged() {
		return lastChanged;
	}

	/**
	 * Get the Set stored at given row and col in the routing Table.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public RouteSet getEntrySet(int row, int col) {
		if (row < NUM_OF_ROWS && col < NUM_OF_COLUMNS) {
			return routingTable[row][col];
		} else {
			return null;
		}
	}

	public RouteSet[] getRow(int row) {
		if (row < NUM_OF_ROWS) {
			return routingTable[row];
		} else {
			return null;
		}
	}

	@Override
	public Iterator<PastryContact> iterator() {
		LinkedHashSet<PastryContact> result = new LinkedHashSet<PastryContact>();
		for (RouteSet[] rs1 : routingTable) {
			if (rs1 != null) {
				for (RouteSet rs2 : rs1) {
					if (rs2 != null) {
						for (PastryContact c : rs2) {
							if (c != null) {
								result.add(c);
							}
						}
					}
				}
			}
		}
		return result.iterator();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Routing-Table for node " + nodeId.toString());
		for (int row = 0; row < NUM_OF_ROWS; row++) {
			buf.append("\n");
			for (int col = 0; col < NUM_OF_COLUMNS; col++) {
				RouteSet rs = routingTable[row][col];
				if (rs != null && rs != RouteSet.OWNER_SET) {
					buf.append(rs.toString());
				} else {
					buf.append("[####]");
				}
			}
		}
		return buf.toString();
	}

	@Override
	public void addContact(PastryContact contact) {
		throw new IllegalStateException("not implemented");
	}

	@Override
	public void removeContact(PastryID oid) {
		throw new IllegalStateException("not implemented");
	}

	@Override
	public PastryContact getContact(PastryID oid) {
		throw new IllegalStateException("not implemented");
	}

	@Override
	public void clearContacts() {
		throw new IllegalStateException("not implemented");
	}

	@Override
	public List<PastryContact> allContacts() {
		throw new IllegalStateException("not implemented");
	}

}
