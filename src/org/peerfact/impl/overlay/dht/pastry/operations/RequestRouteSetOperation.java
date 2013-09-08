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

package org.peerfact.impl.overlay.dht.pastry.operations;

import java.util.LinkedHashSet;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.pastry.components.PastryConstants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.overlay.dht.pastry.components.TransmissionCallback.Failed;
import org.peerfact.impl.overlay.dht.pastry.components.TransmissionCallback.Succeeded;
import org.peerfact.impl.overlay.dht.pastry.messages.MsgTransInfo;
import org.peerfact.impl.overlay.dht.pastry.messages.RequestRouteSetMsg;
import org.peerfact.impl.overlay.dht.pastry.messages.StateUpdateMsg;
import org.peerfact.impl.overlay.dht.pastry.nodestate.PastryRoutingTable;
import org.peerfact.impl.overlay.dht.pastry.nodestate.RouteSet;


/**
 * This operation tries to find a substitute for an entry R_r/c in the Routing
 * Table, defined by its row r and column c. It therefore first queries nodes on
 * the same row (R_r/x, x != c) for their entry R*_r/c. If there is no valid
 * result, this operation queries all Nodes on the next row (R_r+1/x). If that
 * also fails this operation will restart itself later.
 * 
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 03/22/2011
 */
public class RequestRouteSetOperation extends
		AbstractPastryOperation<Object> implements Failed, Succeeded {

	private PastryRoutingTable table = getComponent()
			.getPastryRoutingTable();

	private int row;

	private int col;

	private PastryID deadID;

	private RouteSet affectedSet;

	private boolean deactivate = false;

	private int triesLeft;

	/**
	 * already polled contacts
	 */
	private LinkedHashSet<PastryContact> polledContacts = new LinkedHashSet<PastryContact>();

	/**
	 * contacts that were in the set before updating
	 */
	private LinkedHashSet<PastryContact> prevContacts = new LinkedHashSet<PastryContact>();

	private RequestRouteSetOperation(PastryNode node, RouteSet affectedSet,
			PastryID deadID, int triesLeft) {
		this(node, affectedSet, deadID);
		this.triesLeft = triesLeft;
	}

	public RequestRouteSetOperation(PastryNode node, RouteSet affectedSet,
			PastryID deadID) {
		super(node);
		this.deadID = deadID;
		this.affectedSet = affectedSet;
		this.triesLeft = PastryConstants.OP_MAX_RETRIES;

		for (PastryContact c : affectedSet) {
			prevContacts.add(c);
		}

		row = node.getOverlayID().indexOfMSDD(deadID);
		col = deadID.getDigit(row);

		if (row == 0) {
			// Contacts in the last row will be direct leafs of this node.
			// There is only one node possible per Entry, so there is no need to
			// execute this operation. If the node rejoins/joins, it will be
			// found by Leaf-Set
			deactivate = true;
		}
	}

	@Override
	public void transmissionSucceeded(Message msg, Message reply) {
		/*
		 * queried contact answered!
		 */
		if (reply instanceof StateUpdateMsg) {
			StateUpdateMsg uMsg = (StateUpdateMsg) reply;
			/*
			 * If set has changed, our query was successful.
			 */
			if (uMsg.getContacts().isEmpty()) {
				tryNextContact();
			} else {
				for (PastryContact c : uMsg.getContacts()) {
					if (!c.getOverlayID().equals(deadID)) {
						// do not add dead contact again!
						getComponent().addContact(c);
						/*
						 * System.err.println("Node [" +
						 * getComponent().getOverlayID().toString() +
						 * "] found a substitute for [" + deadID.toString() +
						 * "]: [" + c.getOverlayID().toString() + "]");
						 */
					}
				}
				if (isObsolete()) {
					operationFinished(true);
					return;
				}
				// Failure: no new Nodes were added
				tryNextContact();
			}
		}
	}

	@Override
	public void transmissionFailed(Message msg) {
		/*
		 * TransListener will correctly mark last checked contact as offline,
		 * resulting in new Operations to update Routing/Leaf/Neighbor-Sets.
		 */
		tryNextContact();
	}

	/**
	 * Main Method of this Operation, probes a contact for a RouteSet
	 */
	private void tryNextContact() {
		/*
		 * if there are no more contacts to poll, sleep and restart later!
		 */
		PastryContact nextContact = getNextContact();
		if (nextContact == null) {
			RequestRouteSetOperation op = new RequestRouteSetOperation(
					getComponent(), affectedSet, deadID, --triesLeft);
			affectedSet.startUpdateOperation(op);
			op.scheduleWithDelay(PastryConstants.OP_ROUTE_SET_RETRY_DELAY);
			operationFinished(true);
			return;
		} else {
			RequestRouteSetMsg msg = new RequestRouteSetMsg(getComponent()
					.getOverlayID(), nextContact.getOverlayID(), row, col);
			getComponent().getMsgHandler().sendMsg(
					new MsgTransInfo<PastryContact>(msg, nextContact), this);
		}
	}

	@Override
	protected void execute() {
		/*
		 * Operations in row 0 are cancelled in the constructor.
		 */
		if (deactivate) {
			operationFinished(true);
			return;
		}
		/*
		 * if this operation was scheduled in the past, make sure that the
		 * routing set has not been updated before executing
		 */
		if (isObsolete() || triesLeft == 0) {
			/*
			 * System.err.println("Node [" +
			 * getComponent().getOverlayID().toString() +
			 * "] gave up searching a substitute for [" + deadID.toString() +
			 * "]");
			 */
			operationFinished(true);
			return;
		} else {
			/*
			 * if (triesLeft == PastryConstants.OP_MAX_RETRIES) {
			 * System.err.println("Node [" +
			 * getComponent().getOverlayID().toString() +
			 * "] started searching a substitute for [" + deadID.toString() +
			 * "] in row " + row + " and col " + col + "");
			 * System.err.println(getComponent().getPastryRoutingTable()
			 * .toString()); } else { System.err.println("Node [" +
			 * getComponent().getOverlayID().toString() +
			 * "] continued searching a substitute for [" + deadID.toString() +
			 * "] in row " + row + " and col " + col + ""); }
			 */
			tryNextContact();
		}
	}

	/**
	 * set changed, which means there is at least one new contact - operation is
	 * obsolete
	 * 
	 * @return
	 */
	private boolean isObsolete() {
		for (PastryContact c : affectedSet) {
			if (!prevContacts.contains(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Contact to poll next
	 * 
	 * @return
	 */
	private PastryContact getNextContact() {
		/*
		 * search on own row
		 */
		RouteSet[] rowSets = table.getRow(row);
		for (int i = 0; i < rowSets.length; i++) {
			if (i == row || rowSets[i] == null) {
				continue;
			}
			for (PastryContact c : rowSets[i]) {
				if (!polledContacts.contains(c)) {
					polledContacts.add(c);
					return c;
				}
			}
		}
		/*
		 * search one row below
		 */
		if (row - 1 < 0) {
			return null;
		}
		RouteSet[] rowSetsSecond = table.getRow(row - 1);
		if (rowSetsSecond == null) {
			return null;
		}
		for (int i = 0; i < rowSetsSecond.length; i++) {
			if (rowSetsSecond[i] == null) {
				continue;
			}
			for (PastryContact c : rowSetsSecond[i]) {
				if (!polledContacts.contains(c)) {
					polledContacts.add(c);
					return c;
				}
			}
		}
		return null;
	}

	@Override
	public Object getResult() {
		// there is no result
		return null;
	}

}
