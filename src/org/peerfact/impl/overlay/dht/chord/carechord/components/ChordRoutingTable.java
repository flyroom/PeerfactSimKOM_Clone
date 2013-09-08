/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.carechord.components;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.util.MathHelper;


/**
 * 
 * This class provide the basic functionalities of Chord routing table.
 * RoutingTable provide the functionality to lookup and forward message more
 * efficiently. In Chord Overlay, a node respond for interval from next direct
 * predecessor Id (exclusive) to its id (inclusive)
 * 
 * @author Markus Benter (original author)
 * @author Thim Strothmann (Adaptions)
 * 
 */
public class ChordRoutingTable extends AbstractChordRoutingTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1949042590646964348L;

	private boolean isActive = true;

	/**
	 * number of finger table entry
	 */
	protected final int bitLength = ChordID.KEY_BIT_LENGTH;

	/**
	 * real neighbor with next lower id
	 */
	protected AbstractChordContact leftRealNeighbor = null;

	/**
	 * real neighbor with next higher id
	 */
	protected AbstractChordContact rightRealNeighbor = null;

	/**
	 * Lists used for storing different re-Chord edge types.
	 */
	protected LinkedList<AbstractChordContact> unmarkedEdgeList = null;

	protected LinkedList<AbstractChordContact> connectionEdgeList = null;

	protected LinkedList<AbstractChordContact> ringEdgeList = null;

	/**
	 * during stabilisation there are entries in the routing table that have to
	 * be inactive for some time.
	 */
	protected LinkedList<AbstractChordContact> unmarkedEdgeListToAddNextRound = null;

	protected LinkedList<AbstractChordContact> ringEdgeListToAddNextRound = null;

	protected LinkedList<AbstractChordContact> connectionEdgeListToAddNextRound = null;

	/**
	 * additional successor of successor ... edges
	 */
	private AbstractChordContact[] sucSuccessorList;

	/**
	 * master node
	 */

	protected transient ChordNode masterNode;

	/**
	 * id of the owning node (not equal to masterNode.id iff this is a virtual
	 * node.
	 */
	protected ChordID id;

	/**
	 * virtualnode number - owner.id + 2^powerof2 == id.
	 */
	protected int powerof2;

	protected boolean isReal;

	private int nextContactToPing = 0;

	public AbstractChordContact getNextContactToPing() {
		Collection<AbstractChordContact> nei = this.getNeighbors();
		if (nei.size() > 0) {
			Object[] arr = nei.toArray();
			if (nei.size() > 1) {
				nextContactToPing = (nextContactToPing + 1) % (arr.length - 1);
			} else {
				nextContactToPing = 0;
			}
			AbstractChordContact c = (AbstractChordContact) arr[nextContactToPing];
			return c;
		} else {
			return null;
		}
	}

	public ChordID getChordId() {
		return id;
	}

	public AbstractChordContact getChordContactOfThisRT() {
		return new ChordContact(id, masterNode.getTransInfo(),
				id.compareTo(masterNode.getOverlayID()) == 0);
	}

	public ChordRoutingTable(ChordNode masterNode, ChordID id, int powerof2) {
		this.masterNode = masterNode;
		unmarkedEdgeList = new LinkedList<AbstractChordContact>();
		connectionEdgeList = new LinkedList<AbstractChordContact>();
		ringEdgeList = new LinkedList<AbstractChordContact>();
		unmarkedEdgeListToAddNextRound = new LinkedList<AbstractChordContact>();
		ringEdgeListToAddNextRound = new LinkedList<AbstractChordContact>();
		connectionEdgeListToAddNextRound = new LinkedList<AbstractChordContact>();
		sucSuccessorList = new AbstractChordContact[CaReChordConfiguration.SUCCESSORS - 1];
		this.id = id;

		this.powerof2 = powerof2;

		isReal = this.id == masterNode.getOverlayID();

	}

	public AbstractChordContact[] getUnmarkedEdgesAsArray() {
		return unmarkedEdgeList.toArray(new AbstractChordContact[0]);
	}

	public AbstractChordContact[] getRingEdgesAsArray() {
		return ringEdgeList.toArray(new AbstractChordContact[0]);
	}

	public AbstractChordContact[] getringEdgeListToAddNextRoundAsArray() {
		return ringEdgeListToAddNextRound.toArray(new AbstractChordContact[0]);
	}

	public void addUnmarkedContact(AbstractChordContact cc) {
		if (cc != null) {
			if (!unmarkedEdgeList.contains(cc)) {
				unmarkedEdgeList.add(cc);
			}
		}
	}

	/**
	 * 
	 * @param key
	 *            id of data
	 * @return is this node responsible for this data-item?
	 */
	@Override
	public boolean responsibleFor(ChordID key) {
		AbstractChordContact predecessor = this.getPredecessor();

		if (predecessor != null) {
			if ((key.between(predecessor.getOverlayID(),
					masterNode.getOverlayID()))
					|| (key.equals(masterNode.getOverlayID()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param index
	 * @return start address of i-th finger point
	 */
	@Override
	public ChordID getPointAddress(int index) {
		BigInteger point = MathHelper.getFingerStartValue(masterNode
				.getOverlayID().getValue(), index);
		return new ChordID(point);
	}

	/**
	 * refresh finger table when receive leaving event of node
	 * 
	 * @param offlineNode
	 */
	@Override
	public void receiveOfflineEvent(AbstractChordContact offlineNode) {
		return;
	}

	@Override
	public ChordNode getMasterNode() {
		return masterNode;
	}

	@Override
	protected void sendAndWait(Message msg, AbstractChordContact receiver) {
		if (!isActive) {
			return;
		}

		MessageTimer messageTimer = new MessageTimer(masterNode, msg, receiver);
		masterNode.getTransLayer().sendAndWait(msg, receiver.getTransInfo(),
				masterNode.getPort(), ChordConfiguration.TRANSPORT_PROTOCOL,
				messageTimer, ChordConfiguration.MESSAGE_TIMEOUT);
	}

	@Override
	public void setInactive() {
		isActive = false;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public Collection<AbstractChordContact> getNeighbors() {
		while (unmarkedEdgeList.contains(null)) {
			unmarkedEdgeList.remove(null);
		}

		List<AbstractChordContact> contacts = new ArrayList<AbstractChordContact>(
				unmarkedEdgeList.size());
		for (AbstractChordContact cc : unmarkedEdgeList) {
			if (!contacts.contains(cc)) {
				contacts.add(cc);
			}
		}

		// add all unmarked edges of all virtual nodes, too
		Iterator<ChordRoutingTable> vIt = masterNode.getVirtualNodes()
				.iterator();
		while (vIt.hasNext()) {
			ChordRoutingTable rt = vIt.next();

			// add all unmarked of rt's unmarkedList:
			Iterator<AbstractChordContact> uIt = rt.unmarkedEdgeList.iterator();
			while (uIt.hasNext()) {
				AbstractChordContact c = uIt.next();
				if (c != null && !contacts.contains(c)) {
					contacts.add(c);
				}
			}

		}

		// add vNodes, too:
		for (ChordRoutingTable v : masterNode.getVirtualNodes()) {
			if (!contacts.contains(v.getChordContactOfThisRT())) {
				contacts.add(v.getChordContactOfThisRT());
			}
		}

		return contacts;
	}

	public Collection<AbstractChordContact> getRealNodeNeighbors() {
		List<AbstractChordContact> contacts = new ArrayList<AbstractChordContact>(
				0);

		for (AbstractChordContact c : this.getNeighbors()) {
			if (((ChordContact) c).isRealNode() && !contacts.contains(c)) {
				contacts.add(c);
			}
		}

		// for(ChordContact c: unmarkedEdgeList) {
		// if(c == null) continue;
		// if(c.isRealNode() && !contacts.contains(c))
		// contacts.add(c);
		// }
		//
		// //add all unmarked edges of all virtual nodes, too
		// for(ChordRoutingTable rt : masterNode.getVirtualNodes()) {
		//
		// //add all unmarked of rt's unmarkedList:
		// for(ChordContact c : rt.unmarkedEdgeList) {
		// if(c == null) continue;
		// if(!contacts.contains(c) && c.isRealNode()) {
		// contacts.add(c);
		// }
		// }
		//
		// }
		return contacts;
	}

	@Override
	public AbstractChordContact getSuccessor() {
		AbstractChordContact successor = this.rightRealNeighbor;

		if (successor == null
				|| successor.getOverlayID().compareTo(this.getChordId()) < 0) {
			// means this node thinks it is the real node with the smallest id
			// in the network:

			// if we have no right neighbor this might mean we are the node with
			// the highest ID - thus
			// oder right real neighbor is the real node with the smallest id
			// known to us:
			for (AbstractChordContact cc : this.getRealNodeNeighbors()) {
				if (successor == null
						|| successor.getOverlayID()
								.compareTo(cc.getOverlayID()) > 0) {
					successor = cc;
				}
			}
		}

		return successor;
	}

	@Override
	public AbstractChordContact getPredecessor() {
		AbstractChordContact predecessor = this.leftRealNeighbor;

		if (predecessor == null
				|| predecessor.getOverlayID().compareTo(this.getChordId()) > 0) {
			for (AbstractChordContact cc : this.getRealNodeNeighbors()) {
				if (predecessor == null
						|| predecessor.getOverlayID().compareTo(
								cc.getOverlayID()) < 0) {
					predecessor = cc;
				}
			}
		}

		return predecessor;
	}

	/**
	 * Returns the closest finger preceding id n.closest_preceding_finger(id)
	 * for i= m-1 downto 0 if(finger[i].node in (n,id) return finger[i].node
	 * return n
	 * 
	 * @param identifier
	 * @return the point in finger table, which is closest by the input id
	 * 
	 */
	@Override
	public AbstractChordContact getClosestPrecedingFinger(ChordID identifier) {

		// fingers are stored in the virtualNodes of the masterNode - thus we
		// search them.
		Iterator<ChordRoutingTable> vIt = masterNode.getVirtualNodes()
				.iterator();

		while (vIt.hasNext()) {
			ChordRoutingTable rt = vIt.next();

			AbstractChordContact succ = rt.getSuccessor();

			if (succ != null
					&& succ.getOverlayID().between(masterNode.getOverlayID(),
							identifier)) {
				return succ;
			}
		}
		return masterNode.getLocalOverlayContact();
	}

	/**
	 * get possible nexthops on a route to id
	 * 
	 * @param identifier
	 *            NodeID
	 * @param max
	 *            number of Contacts
	 * @return List of possible next hops to id
	 */
	@Override
	public List<AbstractChordContact> getClosestPrecedingFingers(
			ChordID identifier,
			int max) {
		return getClosestPrecedingFingers(identifier, max, false);
	}

	/**
	 * get possible nexthops on a route to id
	 * 
	 * @param identifier
	 *            NodeID
	 * @param max
	 *            number of Contacts
	 * @param includeSucSuccessors
	 *            add the k successors for each preceding finger as well
	 * @return List of possible next hops to id
	 */
	public List<AbstractChordContact> getClosestPrecedingFingers(
			ChordID identifier,
			int max, boolean includeSucSuccessors) {

		List<AbstractChordContact> retList = new LinkedList<AbstractChordContact>();
		// fingers are stored in the virtualNodes of the masterNode - thus we
		// search them.
		Iterator<ChordRoutingTable> vIt = masterNode.getVirtualNodes()
				.iterator();

		while (vIt.hasNext()) {
			ChordRoutingTable rt = vIt.next();

			if (rt.rightRealNeighbor != null
					&& rt.rightRealNeighbor.getOverlayID().between(
							masterNode.getOverlayID(), identifier)) {
				if (!retList.contains(rt.rightRealNeighbor)) {
					retList.add(rt.rightRealNeighbor);
				}

				if (includeSucSuccessors) {
					retList.addAll(rt.getSucSuccessors());
				}
			}
			if (retList.size() >= max) {
				break;
			}
		}

		if (retList.isEmpty()) {
			retList.add(masterNode.getLocalOverlayContact());
		}

		return retList;
	}

	/**
	 * get the finger-pointer no. index
	 * 
	 * @param index
	 * @return finger[index]
	 */
	@Override
	public AbstractChordContact getFingerEntry(int index) {

		AbstractChordContact ret = null;

		Iterator<ChordRoutingTable> vIt = masterNode.getVirtualNodes()
				.iterator();
		while (vIt.hasNext()) {
			ChordRoutingTable rt = vIt.next();
			if (rt.id.getDistance(masterNode.getOverlayID()).equals(
					BigInteger.valueOf(2 ^ index))) {
				// found the corresponding routingtable.
				ret = rt.getSuccessor();
			}
		}

		return ret;
	}

	/**
	 * 
	 * @return the only successor
	 */
	@Override
	public List<AbstractChordContact> getSuccessors() {
		List<AbstractChordContact> retList = new LinkedList<AbstractChordContact>();

		retList.add(rightRealNeighbor);

		return retList;
	}

	/**
	 * 
	 * @return the only predecessor
	 */
	@Override
	public List<AbstractChordContact> getPredecessors() {
		List<AbstractChordContact> retList = new LinkedList<AbstractChordContact>();

		retList.add(leftRealNeighbor);

		return retList;
	}

	@Override
	public AbstractChordContact[] copyFingerTable() {

		if (masterNode.getVirtualNodes() == null) {
			return new AbstractChordContact[0];
		}

		AbstractChordContact[] list = new AbstractChordContact[masterNode
				.getVirtualNodes()
				.size()];

		int i = 0;

		Iterator<ChordRoutingTable> vIt = masterNode.getVirtualNodes()
				.iterator();

		while (vIt.hasNext()) {
			ChordRoutingTable rt = vIt.next();
			list[i] = rt.getSuccessor();
			i++;
		}

		return list;
	}

	public void removeContactFromUnmarked(OverlayID<ChordID> oid) {
		AbstractChordContact toremove = null;

		for (AbstractChordContact cc : unmarkedEdgeList) {
			if (cc.getOverlayID().equals(oid)) {
				toremove = cc;
			}
		}
		unmarkedEdgeList.remove(toremove);

	}

	public void removeFromRouting(ChordID identifier) {
		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();

		// clean connection edges
		for (AbstractChordContact c : this.connectionEdgeList) {
			if (c != null && c.getOverlayID().equals(identifier)) {
				toDelete.add(c);
			}
		}
		for (AbstractChordContact c : toDelete) {
			this.connectionEdgeList.remove(c);
		}
		toDelete.clear();

		// clean ringedge list
		for (AbstractChordContact c : this.ringEdgeList) {
			if (c != null && c.getOverlayID().equals(identifier)) {
				toDelete.add(c);
			}
		}
		for (AbstractChordContact c : toDelete) {
			this.ringEdgeList.remove(c);
		}
		toDelete.clear();

		// clean unmarked edges
		for (AbstractChordContact c : this.unmarkedEdgeList) {
			if (c != null && c.getOverlayID().equals(identifier)) {
				toDelete.add(c);
			}
		}
		for (AbstractChordContact c : toDelete) {
			this.unmarkedEdgeList.remove(c);
		}
		toDelete.clear();

		if (this.leftRealNeighbor != null
				&& this.leftRealNeighbor.getOverlayID().equals(identifier)) {
			this.leftRealNeighbor = null;
		}

		if (this.rightRealNeighbor != null
				&& this.rightRealNeighbor.getOverlayID().equals(identifier)) {
			replaceRightRealNeighbor();
		}
	}

	/**
	 * replace the right real neighbor with the successor of the successor (the
	 * quick-fix operation)
	 */
	private void replaceRightRealNeighbor() {
		AbstractChordContact sucSuccessor = sucSuccessorList[0];

		for (int i = 0; i < sucSuccessorList.length - 1; i++) {
			sucSuccessorList[i] = sucSuccessorList[i + 1];
		}

		unmarkedEdgeList.add(sucSuccessor);
		rightRealNeighbor = sucSuccessor;
	}

	@Override
	public List<AbstractChordContact> getAllDistantPredecessor() {
		List<AbstractChordContact> retList = new LinkedList<AbstractChordContact>();

		retList.add(leftRealNeighbor);

		return retList;
	}

	@Override
	public List<AbstractChordContact> getAllDistantSuccessor() {
		List<AbstractChordContact> retList = new LinkedList<AbstractChordContact>();

		retList.add(leftRealNeighbor);

		return retList;
	}

	@Override
	public AbstractChordContact getDistantPredecessor(int index) {
		// FIXME IMPLEMENT
		return null;
	}

	@Override
	public AbstractChordContact getDistantSuccessor(int index) {
		// FIXME IMPLEMENT
		return null;
	}

	public void updateSucSuccessor(int i, AbstractChordContact successor) {
		sucSuccessorList[i - 2] = successor;
	}

	public AbstractChordContact getSucSuccessor(int i) {
		if (i == 1) {
			return getSuccessor();
		} else {
			return sucSuccessorList[i - 2];
		}
	}

	public LinkedList<AbstractChordContact> getSucSuccessors() {

		LinkedList<AbstractChordContact> sucSucs = new LinkedList<AbstractChordContact>();

		for (int i = 0; i < sucSuccessorList.length; i++) {
			if (sucSuccessorList[i] != null) {
				sucSucs.add(sucSuccessorList[i]);
			}
		}

		return sucSucs;
	}

	@Override
	public void addContact(AbstractChordContact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeContact(ChordID oid) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractChordContact getContact(ChordID oid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearContacts() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AbstractChordContact> allContacts() {
		// TODO Auto-generated method stub
		return null;
	}
}
