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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.callbacks.CheckPredecessorOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.callbacks.CheckSuccessorOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.callbacks.UpdateDirectSuccessorOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations.UpdateFingerPointOperation;
import org.peerfact.impl.overlay.dht.chord.base.callbacks.MessageTimer;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyOfflineMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyPredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifySuccessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.util.MathHelper;
import org.peerfact.impl.overlay.dht.chord.base.util.RoutingTableContructor;
import org.peerfact.impl.simengine.Simulator;


/**
 * This class provide the basic functionalities of Chord routing table.
 * RoutingTable provide the functionality to lookup and forward message more
 * efficiently. In Chord Overlay, a node respond for interval from next direct
 * predecessor Id (exclusive) to its id (inclusive)
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordRoutingTable extends AbstractChordRoutingTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 557468840031118746L;

	private boolean isActive = true;

	/**
	 * number of finger table entry
	 */
	protected final int bitLength = ChordID.KEY_BIT_LENGTH;

	// from index 1 to bitLength
	protected AbstractChordContact[] finger;

	/**
	 * list of direct predecessors
	 */
	protected LinkedList<AbstractChordContact> predecessorList = null;

	/**
	 * list of direct successors
	 */
	protected LinkedList<AbstractChordContact> successorList = null;

	protected transient ChordNode masterNode;

	/**
	 * index of finger table, which will be updated in next phase
	 */
	private int nextUpdateFingerPoint = 1;

	/**
	 * number of stored successor and predecessor
	 */
	private int num_Neighbours = ChordConfiguration.STORED_NEIGHBOURS_LOADNET;

	/**
	 * successor and predecessor, which will be updated in next Stabilize phase
	 */
	private AbstractChordContact nextUpdatePredecessor,
			nextUpdateSuccessor;

	public ChordRoutingTable(ChordNode masterNode,
			AbstractChordContact predecessor,
			AbstractChordContact successor,
			Set<AbstractChordContact> succFingerTable) {
		this.masterNode = masterNode;
		successorList = new LinkedList<AbstractChordContact>();
		predecessorList = new LinkedList<AbstractChordContact>();
		successorList.add(successor);
		predecessorList.add(predecessor);

		Set<AbstractChordContact> nodeList;
		if (succFingerTable == null) {
			nodeList = new LinkedHashSet<AbstractChordContact>();
		} else {
			nodeList = new LinkedHashSet<AbstractChordContact>(succFingerTable);
		}
		nodeList.add(masterNode.getLocalOverlayContact());
		nodeList.add(successor);
		nodeList.add(predecessor);
		finger = RoutingTableContructor.getFingerTable(
				masterNode.getLocalOverlayContact(), nodeList);

		// periodically check successor and finger table
		// update successor and predecessor immediately!!!
		new UpdateDirectSuccessorOperation(masterNode,
				masterNode.getOperationListener()).scheduleImmediately();
		new UpdateFingerPointOperation(masterNode,
				masterNode.getOperationListener()).scheduleImmediately();
		new CheckSuccessorOperation(masterNode).scheduleImmediately();
		new CheckPredecessorOperation(masterNode).scheduleImmediately();

		num_Neighbours = masterNode.STORED_NEIGHBOURS;
	}

	/**
	 * Returns the closest finger preceding id n.closest_preceding_finger(id)
	 * for i= m-1 downto 0 if(finger[i].node in (n,id) return finger[i].node
	 * return n
	 * 
	 * @param id
	 * @return the point in finger table, which is closest by the input id
	 * 
	 */
	@Override
	public AbstractChordContact getClosestPrecedingFinger(ChordID id) {

		for (int index = bitLength - 1; index >= 0; index--) {
			if (finger[index].getOverlayID().between(masterNode.getOverlayID(),
					id)) {
				return finger[index];
			}
		}
		return masterNode.getLocalOverlayContact();
	}

	/**
	 * Returns the num closest preceding fingers
	 * 
	 * @param id
	 * @param num
	 * @return
	 */
	@Override
	public List<AbstractChordContact> getClosestPrecedingFingers(ChordID id,
			int num) {

		List<AbstractChordContact> closestPrecedingFingers = new LinkedList<AbstractChordContact>();

		for (int index = bitLength - 1; index >= 0; index--) {
			if (finger[index].getOverlayID().between(masterNode.getOverlayID(),
					id)) {
				closestPrecedingFingers.add(finger[index]);
				if (closestPrecedingFingers.size() >= num) {
					break;
				}
			}
		}
		if (closestPrecedingFingers.isEmpty()) {
			closestPrecedingFingers.add(masterNode.getLocalOverlayContact());
		}
		return closestPrecedingFingers;
	}

	/**
	 * notify of changing next direct predecessor
	 * 
	 * @param newPredecessor
	 */
	public void updatePredecessor(AbstractChordContact newPredecessor) {
		log.debug("node id = " + masterNode.getOverlayID()
				+ " old predecessor = " + getPredecessor()
				+ " new predecessor = " + newPredecessor);
		if (newPredecessor.equals(masterNode.getLocalOverlayContact())) {
			return;
		}
		// this case occurs only if masterNode is the first Node in ring
		if (getPredecessor().equals(masterNode.getLocalOverlayContact())) {
			setPredecessor(newPredecessor);

		} else if (newPredecessor.between(getPredecessor(),
				masterNode.getLocalOverlayContact())) {
			setPredecessor(newPredecessor);
		}
	}

	/**
	 * notify of changing next direct successor
	 * 
	 * @param newSuccessor
	 */
	public void updateSuccessor(AbstractChordContact newSuccessor) {
		log.debug("node id = " + masterNode.getOverlayID()
				+ " old successor = " + getSuccessor() + " new successor = "
				+ newSuccessor);
		if (newSuccessor.equals(masterNode.getLocalOverlayContact())) {
			return;
		}
		// this case occurs only if masterNode is the first Node in ring
		if (getSuccessor().equals(masterNode.getLocalOverlayContact())) {
			setSuccessor(newSuccessor);

		} else if (newSuccessor.between(masterNode.getLocalOverlayContact(),
				getSuccessor())) {
			setSuccessor(newSuccessor);
		}

	}

	/**
	 * This method is called to deliver current updated predecessor of next
	 * direct successor
	 * 
	 * @param successor
	 * @param predOfSucc
	 *            : null if successor was off-line
	 */
	public void updatePredecessorOfSuccessor(AbstractChordContact successor,
			AbstractChordContact predOfSucc) {

		log.debug("Node " + masterNode + " Predecessor Of Successor "
				+ predOfSucc);
		if (!successor.equals(getSuccessor())) {
			log.info("check pred of out-of-date succ node = " + masterNode);
			return;
		}
		if (predOfSucc != null) {
			// the node is predecessor of it's successor => nothing to do
			if (predOfSucc.equals(masterNode.getLocalOverlayContact())) {
				return;
			}

			// successor consider that it is alone
			else if (predOfSucc.equals(getSuccessor())) {
				// notify successor that this node is its new predecessor
				NotifyPredecessorMsg msg = new NotifyPredecessorMsg(
						masterNode.getLocalOverlayContact(), getSuccessor(),
						masterNode.getLocalOverlayContact().clone());
				// masterNode.getTransLayer().send(msg,
				// getSuccessor().getTransInfo(), masterNode.getPort(),
				// ChordConfiguration.TRANSPORT_PROTOCOL);
				sendAndWait(msg, getSuccessor());
			}

			// predecessor of successor is really next neighbor in Chord ring
			else if (predOfSucc.between(masterNode.getLocalOverlayContact(),
					getSuccessor())) {
				updateSuccessor(predOfSucc);
			}

			// inform successor about myself
			else {
				// notify successor that this node is its new predecessor
				NotifyPredecessorMsg msg = new NotifyPredecessorMsg(
						masterNode.getLocalOverlayContact(), getSuccessor(),
						masterNode.getLocalOverlayContact().clone());
				// masterNode.getTransLayer().send(msg,
				// getSuccessor().getTransInfo(), masterNode.getPort(),
				// ChordConfiguration.TRANSPORT_PROTOCOL);
				sendAndWait(msg, getSuccessor());
			}
		} else {
			log.info("inform offline direct succ failed");
			AbstractChordContact oldSucc = getSuccessor();
			receiveOfflineEvent(oldSucc);
		}

	}

	/**
	 * replace finger point by new value
	 * 
	 * @param entryIndex
	 * @param newClosestSucc
	 */
	public void setFingerPoint(int entryIndex,
			AbstractChordContact newClosestSucc) {
		log.debug("update finger node = " + masterNode + " entryindex = "
				+ entryIndex + " point " + getPointAddress(entryIndex)
				+ finger[entryIndex] + " " + newClosestSucc);

		if (newClosestSucc == null) {
			log.info("update finger failed node = " + masterNode + " point "
					+ getPointAddress(entryIndex));
			return;
		}
		finger[entryIndex] = newClosestSucc;

		// check for next points
		while (newClosestSucc.getOverlayID().between(
				getPointAddress(nextUpdateFingerPoint),
				finger[nextUpdateFingerPoint].getOverlayID())
				|| newClosestSucc.equals(finger[nextUpdateFingerPoint])) {

			log.trace("next point has the same successor index = "
					+ nextUpdateFingerPoint + " point "
					+ getPointAddress(nextUpdateFingerPoint) + " old value "
					+ finger[nextUpdateFingerPoint] + " new value "
					+ newClosestSucc);
			finger[nextUpdateFingerPoint] = newClosestSucc;
			incNextUpdateFingerPoint();
			// prevent endless loop cause the node is alone
			if (nextUpdateFingerPoint == entryIndex) {
				log.info("updated whole finger table node = " + masterNode);
				break;
			}
		}
		log.trace("break check next point" + " index = "
				+ nextUpdateFingerPoint + " point "
				+ getPointAddress(nextUpdateFingerPoint)
				+ finger[nextUpdateFingerPoint] + " " + newClosestSucc);
	}

	@Override
	public boolean responsibleFor(ChordID key) {
		AbstractChordContact predecessor = getPredecessor();

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
	 * refresh finger table when receive joining event of new node
	 * 
	 * @param newNode
	 */
	public void refreshFingerTable(AbstractChordContact newNode) {
		log.debug("refreshFingerTable new node = " + newNode);
		// update finger table
		for (int index = 0; index < bitLength; index++) {

			if (newNode.getOverlayID().between(getPointAddress(index),
					finger[index].getOverlayID())) {
				log.trace("node " + masterNode + " replace index "
						+ getPointAddress(index) + " old succ "
						+ finger[index].getOverlayID() + " by "
						+ newNode.getOverlayID());
				finger[index] = newNode;
			} else {
				log.trace("node " + masterNode + " index "
						+ getPointAddress(index) + " value "
						+ finger[index].getOverlayID());
			}
		}
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

	@Override
	public AbstractChordContact[] copyFingerTable() {
		return finger.clone();
	}

	@Override
	public AbstractChordContact getFingerEntry(int index) {
		return finger[index];
	}

	@Override
	public ChordNode getMasterNode() {
		return masterNode;
	}

	protected void setPredecessor(AbstractChordContact newPredecessor) {
		log.debug("node " + masterNode + " old pred " + getPredecessor()
				+ " new pred " + newPredecessor);
		AbstractChordContact oldPredecessor = predecessorList.get(0);

		predecessorList.set(0, newPredecessor);

		// refreshFingerTable(newPredecessor); --> Not needed in this case

		if (oldPredecessor.compareTo((masterNode.getLocalOverlayContact())) != 0) {
			NotifySuccessorMsg msg = new NotifySuccessorMsg(
					masterNode.getLocalOverlayContact(), oldPredecessor,
					newPredecessor);
			sendAndWait(msg, oldPredecessor);
		} else {
			updatePredecessor(newPredecessor);
		}
	}

	protected void setSuccessor(AbstractChordContact newSuccessor) {
		log.debug("node " + masterNode + " old succ " + getSuccessor()
				+ " new succ " + newSuccessor);
		AbstractChordContact oldSuccessor = successorList.get(0);

		successorList.set(0, newSuccessor);

		refreshFingerTable(newSuccessor);

		if (oldSuccessor.compareTo((masterNode.getLocalOverlayContact())) != 0) {
			NotifyPredecessorMsg msg = new NotifyPredecessorMsg(
					masterNode.getLocalOverlayContact(), oldSuccessor,
					newSuccessor);
			sendAndWait(msg, oldSuccessor);
		} else {
			updateSuccessor(newSuccessor);
		}
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
	public AbstractChordContact getPredecessor() {
		return predecessorList.getFirst();
	}

	@Override
	public List<AbstractChordContact> getPredecessors() {
		return predecessorList;
	}

	@Override
	public AbstractChordContact getSuccessor() {
		return successorList.getFirst();
	}

	@Override
	public List<AbstractChordContact> getSuccessors() {
		return successorList;
	}

	public int getNextUpdateFingerPoint() {
		return nextUpdateFingerPoint;
	}

	public void incNextUpdateFingerPoint() {
		nextUpdateFingerPoint++;
		nextUpdateFingerPoint = nextUpdateFingerPoint % bitLength;
	}

	public boolean isNodeItsOwnSuccessor() {
		return getSuccessor().getOverlayID().compareTo(
				masterNode.getOverlayID()) == 0;
	}

	public boolean isNodeItsOwnSPredecessor() {
		return getPredecessor().getOverlayID().compareTo(
				masterNode.getOverlayID()) == 0;
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
		List<AbstractChordContact> contacts = new ArrayList<AbstractChordContact>(
				predecessorList.size() + successorList.size());
		contacts.addAll(successorList);
		contacts.addAll(predecessorList);
		for (int i = 0; i < finger.length; i++) {
			if (!contacts.contains(finger[i])) {
				contacts.add(finger[i]);
			}
		}
		return contacts;
	}

	// it should be protected
	@Override
	public void receiveOfflineEvent(AbstractChordContact offlineNode) {
		log.info("receive offline event node = " + masterNode
				+ " offline node = " + offlineNode + "at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		log.info("succ " + Arrays.toString(successorList.toArray()));
		log.info("pred " + Arrays.toString(predecessorList.toArray()));
		boolean isDirectSucc = getSuccessor().equals(offlineNode);
		boolean isDirectPred = getPredecessor().equals(offlineNode);

		// check successor and next update successor
		int index = successorList.indexOf(offlineNode);
		if (index >= 0) {
			if (offlineNode.equals(nextUpdateSuccessor)) {
				int nextIndex = (index + 1) % successorList.size();
				nextUpdateSuccessor = successorList.get(nextIndex);
			}
			successorList.remove(offlineNode);
		}

		// check predecessor and next update predecessor
		index = predecessorList.indexOf(offlineNode);
		if (index >= 0) {
			if (offlineNode.equals(nextUpdatePredecessor)) {
				int nextIndex = (index + 1) % predecessorList.size();
				nextUpdatePredecessor = predecessorList.get(nextIndex);
			}
			predecessorList.remove(offlineNode);
		}

		// check finger table
		updateFingerTable(offlineNode);

		// check is successorList and predecessorList empty?
		if (successorList.size() == 0) {
			successorList.add(finger[0]);
		}
		if (predecessorList.size() == 0) {
			predecessorList.add(finger[bitLength - 1]);
		}

		// notify next direct successor and predecessor
		// update next successor
		if (isDirectSucc) {
			notifyOfflineEvent(getSuccessor(), offlineNode);
		}
		// update next predecessor
		if (isDirectPred) {
			notifyOfflineEvent(getPredecessor(), offlineNode);
		}
	}

	private void updateFingerTable(AbstractChordContact offlineNode) {
		AbstractChordContact nextSucc = masterNode.getLocalOverlayContact();
		for (int index = bitLength - 1; index >= 0; index--) {
			if (offlineNode.equals(finger[index])) {
				finger[index] = nextSucc;
			} else {
				nextSucc = finger[index];
			}
		}
	}

	/**
	 * This method is called to deliver new successor of specified successor
	 * 
	 * @param successor
	 * @param succOfSuccessor
	 *            null if successor was off-line
	 */
	public void updateDistantSuccessor(AbstractChordContact successor,
			AbstractChordContact succOfSuccessor) {

		if (!isActive()) {
			return;
		}

		if (succOfSuccessor == null) {
			log.info("Inform offline Distant Successor null");
			// successor offline
			receiveOfflineEvent(successor);
		} else {
			if (!successorList.contains(successor)) {
				nextUpdateSuccessor = successorList.getFirst();
			} else {
				int index = successorList.indexOf(successor);

				// check next successors
				while ((index + 1 < successorList.size())
						&& successorList.get(index + 1).between(successor,
								succOfSuccessor)) {
					successorList.remove(index + 1);
				}
				if (index + 1 == successorList.size()
						|| !successorList.get(index + 1)
								.equals(succOfSuccessor)) {
					successorList.add(index + 1, succOfSuccessor);
				}

				// calculate next update
				if (index + 1 == num_Neighbours) {
					nextUpdateSuccessor = successorList.getFirst();
				} else {
					nextUpdateSuccessor = successorList.get(index + 1);
				}
			}

			if (successorList.size() > num_Neighbours) {
				successorList = new LinkedList<AbstractChordContact>(
						successorList
								.subList(0, num_Neighbours));
			}

		}

		new CheckSuccessorOperation(masterNode)
				.scheduleWithDelay(masterNode.UPDATE_NEIGHBOURS_INTERVAL);
	}

	/**
	 * This method is call to deliver new predecessor of a specified predecessor
	 * 
	 * @param predecessor
	 * @param predOfPredecessor
	 *            null if predecessor was off-line
	 */
	public void updateDistantPredecessor(AbstractChordContact predecessor,
			AbstractChordContact predOfPredecessor) {

		if (!isActive()) {
			return;
		}

		if (predOfPredecessor == null) {
			// predecessor offline
			log.info("Inform offline Distant Predecessor offline");
			receiveOfflineEvent(predecessor);
		} else {
			if (!predecessorList.contains(predecessor)) {
				nextUpdatePredecessor = predecessorList.getFirst();
			} else {
				int index = predecessorList.indexOf(predecessor);

				// check next successors
				while ((index + 1 < predecessorList.size())
						&& predecessorList.get(index + 1).between(
								predOfPredecessor, predecessor)) {
					predecessorList.remove(index + 1);
				}
				if (index + 1 == predecessorList.size()
						|| !predecessorList.get(index + 1).equals(
								predOfPredecessor)) {
					predecessorList.add(index + 1, predOfPredecessor);
				}

				// calculate next update
				if (index + 1 == num_Neighbours) {
					nextUpdatePredecessor = predecessorList.getFirst();
				} else {
					nextUpdatePredecessor = predecessorList.get(index + 1);
				}
			}

			if (predecessorList.size() > num_Neighbours) {
				predecessorList = new LinkedList<AbstractChordContact>(
						predecessorList
								.subList(0, num_Neighbours));
			}

		}

		new CheckPredecessorOperation(masterNode)
				.scheduleWithDelay(masterNode.UPDATE_NEIGHBOURS_INTERVAL);

	}

	/**
	 * notify neighbors to leaving event
	 * 
	 * @param notifier
	 * @param offliner
	 */
	protected void notifyOfflineEvent(AbstractChordContact notifier,
			AbstractChordContact offliner) {

		if (!isActive()) {
			return;
		}

		NotifyOfflineMsg notifyOfflineMsg = new NotifyOfflineMsg(
				masterNode.getLocalOverlayContact(), notifier, offliner);
		MessageTimer messageTimer = new MessageTimer(masterNode,
				notifyOfflineMsg, notifier);

		if (!masterNode.getHost().getNetLayer().isOffline()
				&& masterNode.getOverlayID().compareTo(notifier.getOverlayID()) != 0) {
			masterNode.getTransLayer().sendAndWait(notifyOfflineMsg,
					notifier.getTransInfo(), masterNode.getPort(),
					ChordConfiguration.TRANSPORT_PROTOCOL, messageTimer,
					ChordConfiguration.MESSAGE_TIMEOUT);
		}
	}

	// Getters and Setters

	@Override
	public AbstractChordContact getDistantPredecessor(int index) {
		if (index >= predecessorList.size()) {
			return null;
		}
		return predecessorList.get(index);
	}

	@Override
	public List<AbstractChordContact> getAllDistantPredecessor() {
		return Collections.unmodifiableList(predecessorList);
	}

	@Override
	public AbstractChordContact getDistantSuccessor(int index) {
		if (index >= successorList.size()) {
			return null;
		}
		return successorList.get(index);
	}

	@Override
	public List<AbstractChordContact> getAllDistantSuccessor() {
		return Collections.unmodifiableList(successorList);
	}

	public AbstractChordContact getNextUpdatePredecessor() {
		if (predecessorList.size() == 0) {
			log.error("all predcessors left node = " + masterNode);
		}
		if (!predecessorList.contains(nextUpdatePredecessor)) {
			nextUpdatePredecessor = predecessorList.get(0);
		}
		return nextUpdatePredecessor;
	}

	public AbstractChordContact getNextUpdateSuccessor() {
		if (predecessorList.size() == 0) {
			log.error("all successors left node = " + masterNode);
		}
		if (!successorList.contains(nextUpdateSuccessor)) {
			nextUpdateSuccessor = successorList.get(0);
		}
		return nextUpdateSuccessor;
	}

	@Override
	public void addContact(AbstractChordContact contact) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeContact(ChordID oid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractChordContact getContact(ChordID oid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearContacts() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AbstractChordContact> allContacts() {
		throw new UnsupportedOperationException();
	}

}
