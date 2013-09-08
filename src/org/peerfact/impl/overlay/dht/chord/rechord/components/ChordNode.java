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

package org.peerfact.impl.overlay.dht.chord.rechord.components;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.util.ChordContactGreaterThanComparator;
import org.peerfact.impl.overlay.dht.chord.rechord.callbacks.JoinOperation;
import org.peerfact.impl.overlay.dht.chord.rechord.messages.CreateLinkMessage;
import org.peerfact.impl.overlay.dht.chord.rechord.operations.ChordOperationListener;
import org.peerfact.impl.overlay.dht.chord.rechord.operations.PingOperation;
import org.peerfact.impl.overlay.dht.chord.rechord.operations.SendStabilisationMessagesPeriodicalyOperation;
import org.peerfact.impl.overlay.dht.chord.rechord.operations.StabilisationOperation;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * This class represents a Peer/Host in Chord Overlay and the main
 * functionality.
 * 
 * @author Minh Hoang Nguyen
 * 
 */
public class ChordNode extends AbstractChordNode {

	private static Logger log = SimLogger.getLogger(ChordNode.class);

	private final ChordMessageHandler messageHandler;

	/**
	 * handle operation time out, deliver operation results
	 */
	private ChordOperationListener operationListener;

	/**
	 * virtual Nodes -- ordered in ascending order of id.
	 */
	private LinkedList<ChordRoutingTable> virtualNodes = null;

	/**
	 * exponent of smallest virtualNode number of virtual nodes =
	 * ChordID.KEY_BIT_LENGTH - m.
	 */
	private int m = ChordID.KEY_BIT_LENGTH;

	public int createLinkOperationCounter = 0;

	LinkedList<CreateLinkMessage> createLinkMessageQueue = new LinkedList<CreateLinkMessage>();

	public boolean inRechordStableState = false;

	/**
	 * @param transLayer
	 * @param port
	 * @param bootstrap
	 */
	public ChordNode(TransLayer transLayer, short port,
			ChordBootstrapManager bootstrap) {

		super(transLayer, port, bootstrap);
		operationListener = new ChordOperationListener(this);

		// create message handler
		messageHandler = new ChordMessageHandler(this);
		// add message handler to the listeners list
		this.getTransLayer().addTransMsgListener(this.messageHandler,
				this.getPort());

		this.virtualNodes = new LinkedList<ChordRoutingTable>();
		this.routingTable = new ChordRoutingTable(this, this.getOverlayID(), 0);

		// // begin with the stabilisation algorithm.
		new StabilisationOperation(this).scheduleWithDelay((long) (Math
				.random() * ChordConfiguration.UPDATE_VIRTUAL_NODES_INTERVAL));

		// begin pinging neighbors.
		new PingOperation(this)
				.scheduleWithDelay((long) (Simulator.getRandom().nextDouble() * ChordConfiguration.PING_INTERVAL));

		// begin send messages
		new SendStabilisationMessagesPeriodicalyOperation(this)
				.scheduleImmediately();
	}

	public void createLinkOperation(AbstractChordNode component,
			AbstractChordContact sender,
			AbstractChordContact receiver,
			Set<AbstractChordContact> targetOfNewLink,
			String type) {

		createLinkOperationCounter++;

		if (sender.getTransInfo().getNetId() == receiver.getTransInfo()
				.getNetId()) {
			localCreateLinkOperation(receiver, targetOfNewLink, "unmarked");
			return;
		}

		CreateLinkMessage msg = new CreateLinkMessage(sender, receiver);
		msg.setTargetOfNewLink(targetOfNewLink);
		msg.setTypeOfEdge(type);

		createLinkMessageQueue.add(msg);

	}

	public void sendCreateLinkMessages(int times) {
		int size = createLinkMessageQueue.size();
		int num = size / times;
		if (num < 5) {
			num = 5;
		}
		if (num > size) {
			num = size;
		}

		for (int i = 0; i < num; i++) {
			CreateLinkMessage msg = createLinkMessageQueue.getFirst();
			createLinkMessageQueue.removeFirst();

			this.getTransLayer().send(msg,
					msg.getReceiverContact().getTransInfo(),
					this.getTransInfo().getPort(),
					ChordConfiguration.TRANSPORT_PROTOCOL);
		}
	}

	public void createLinkOperation(AbstractChordNode component,
			AbstractChordContact sender,
			AbstractChordContact receiver,
			AbstractChordContact targetOfNewLink, String type) {

		createLinkOperationCounter++;

		if (sender.getTransInfo().getNetId() == receiver.getTransInfo()
				.getNetId()) {
			LinkedHashSet<AbstractChordContact> s = new LinkedHashSet<AbstractChordContact>();
			s.add(targetOfNewLink);
			localCreateLinkOperation(receiver, s, "unmarked");
			return;
		}

		CreateLinkMessage msg = new CreateLinkMessage(sender, receiver);
		LinkedHashSet<AbstractChordContact> t = new LinkedHashSet<AbstractChordContact>();
		t.add(targetOfNewLink);
		msg.setTargetOfNewLink(t);
		msg.setTypeOfEdge(type);

		this.getTransLayer().send(msg, receiver.getTransInfo(),
				sender.getTransInfo().getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL);

	}

	/**
	 * update m
	 */
	public int updateM() {

		// get the closest neighbor known:
		Collection<AbstractChordContact> neighborhood = ((ChordRoutingTable) this.routingTable)
				.getRealNodeNeighbors();

		if (neighborhood.size() == 0) {
			return this.m;
		}

		ChordID closest = null;
		Iterator<AbstractChordContact> nIt = neighborhood.iterator();
		while (nIt.hasNext()) {
			OverlayContact<ChordID> nei = nIt.next();

			if (nei == null
					|| nei.getOverlayID().compareTo(this.getOverlayID()) == 0) {
				continue;
			}

			if (closest == null) {
				closest = nei.getOverlayID();
			}

			if (closest.getDistanceMod(this.getOverlayID()).compareTo(
					nei.getOverlayID().getDistanceMod(this.getOverlayID())) < 0) {
				if (this.getOverlayID().compareTo(nei.getOverlayID()) != 0) {
					closest = nei.getOverlayID();
				}
			}
		}

		if (closest == null) {
			return this.m;
		}

		// now we have the closest id - lets check how many virtual nodes we
		// need.
		BigInteger diff = this.getOverlayID().getDistanceMod(closest);

		int logarithm = (int) Math.floor(log(diff, 2.0));

		this.m = logarithm - 1;

		return this.m;
	}

	static double log(BigInteger a, double base) {
		int b = a.bitLength() - 1;
		double c = 0;
		double d = 1;
		for (int i = b; i >= 0; --i) {
			if (a.testBit(i)) {
				c += d;
			}
			d *= 0.5;
		}
		return (Math.log(c) + Math.log(2) * b) / Math.log(base);
	}

	/**
	 * creates / deletes virtual nodes.
	 * 
	 * @return virtual nodes that have been deleted.
	 */
	public LinkedList<ChordRoutingTable> createVirtualNodes() {
		LinkedList<ChordRoutingTable> toDelete = null;
		// check if we have to create / delete virtual Nodes.
		int oldM = this.m;
		updateM();

		int noOfOldVNodes = virtualNodes.size();
		int noOfNewVNodes = ChordID.KEY_BIT_LENGTH - this.m;

		if (noOfOldVNodes > noOfNewVNodes) {
			// this means that a real node has been settled between this real
			// node and its first virtual node.
			// thus all nodes with id smaller than this.id + 2^m have to be
			// deleted!
			// but not to delete any information transfer all knowldge to
			// virtual node this.id + 2^m.

			// get virtual Node m:
			ChordRoutingTable nodeM = null;
			Iterator<ChordRoutingTable> vIt = this.virtualNodes.iterator();
			while (vIt.hasNext()) {
				ChordRoutingTable rt = vIt.next();
				if (rt.powerof2 == m) {
					nodeM = rt;
					break;
				}
			}

			if (nodeM == null) {
				nodeM = (ChordRoutingTable) this.routingTable;
			}

			toDelete = new LinkedList<ChordRoutingTable>();
			// delete all nodes where powerof2 < m.
			vIt = this.virtualNodes.iterator();
			while (vIt.hasNext()) {
				ChordRoutingTable rt = vIt.next();
				if (rt.powerof2 < m) {
					// copy over all edges of rt.
					// in m this edges are unmarked then.
					for (AbstractChordContact c : rt.unmarkedEdgeList) {
						if (c == null) {
							continue;
						}
						if (!nodeM.unmarkedEdgeList.contains(c)) {
							nodeM.unmarkedEdgeList.add(c);
						}
					}

					for (AbstractChordContact c : rt.ringEdgeList) {
						if (c == null) {
							continue;
						}
						if (!nodeM.ringEdgeList.contains(c)) {
							nodeM.ringEdgeList.add(c);
						}
					}
					for (AbstractChordContact c : rt.connectionEdgeList) {
						if (c == null) {
							continue;
						}
						if (!nodeM.connectionEdgeList.contains(c)) {
							nodeM.connectionEdgeList.add(c);
						}
					}

					toDelete.add(rt);
				}
			}

			// delete nodes.
			vIt = toDelete.iterator();
			while (vIt.hasNext()) {
				this.virtualNodes.remove(vIt.next());
			}

		}

		if (noOfOldVNodes < noOfNewVNodes) { // means oldM > this.m
			// we have to create new virtual Nodes!

			for (int i = oldM; i > this.m; i--) {
				if (i != 0 && i != ChordID.KEY_BIT_LENGTH) {
					// bid = this.id + 2^i
					BigInteger bid = this.getOverlayID().getCorrespondingKey()
							.getUniqueValue();
					BigInteger exp = new BigInteger("2").pow(i);
					bid = bid.add(exp);

					BigInteger max_Key_Value = new BigInteger("2")
							.pow(ChordID.KEY_BIT_LENGTH);

					if (bid.compareTo(max_Key_Value) >= 0) {
						bid = bid.subtract(max_Key_Value);
					}

					ChordID newId = new ChordID(bid);
					this.virtualNodes
							.add(new ChordRoutingTable(this, newId, i));
				}
			}
		}
		return toDelete;
	}

	/**
	 * method used to reflect operation 2. of rechord: remove overlapping
	 * neighborhoods of virtual nodes belonging to the same real node
	 */
	public void removeOverlappingNeighborhoods() {
		removeOverlappingNeighborhood((ChordRoutingTable) this.routingTable);
		for (ChordRoutingTable v : this.virtualNodes) {
			removeOverlappingNeighborhood(v);
		}
	}

	private void removeOverlappingNeighborhood(ChordRoutingTable v) {

		AbstractChordContact largestKnownRealNode = getLargestKnownRealNode();
		AbstractChordContact smallestKnownRealNode = getSmallestKnownRealNode();

		AbstractChordContact successor = v.getSuccessor();
		AbstractChordContact predecessor = v.getPredecessor();

		// check masternode AND virtualNodes:
		LinkedList<ChordRoutingTable> toCheck = new LinkedList<ChordRoutingTable>();
		toCheck.addAll(v.getMasterNode().virtualNodes);
		toCheck.add(v.getMasterNode().getChordRoutingTable());

		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();
		LinkedList<Tuple<ChordRoutingTable, AbstractChordContact>> toAdd = new LinkedList<Tuple<ChordRoutingTable, AbstractChordContact>>();
		for (AbstractChordContact n : v.unmarkedEdgeList) {
			if (n == null || toDelete.contains(n)) {
				continue;
			}

			// check if there is another node between v and n:
			for (ChordRoutingTable u : toCheck) {
				if (u == null) {
					continue;
				}

				boolean delete = false;

				// v < n && u \in (v,n)
				if (v.getChordId().compareTo(n.getOverlayID()) < 0
						&& u.getChordId().between(v.getChordId(),
								n.getOverlayID())) {
					delete = true;
				}
				// v > n && u \in (n,v)
				if (v.getChordId().compareTo(n.getOverlayID()) > 0
						&& u.getChordId().between(n.getOverlayID(),
								v.getChordId())) {
					delete = true;
				}

				if (delete) {

					// u is between v and n!
					// so we have to remove n from v and add n to u.
					// log.debug(u.getChordId() + " is between " +
					// v.getChordId() + " and " + n.getOverlayID());
					if ((successor == null || successor.getOverlayID()
							.compareTo(v.getChordId()) < 0)
							&& n.getOverlayID().equals(
									smallestKnownRealNode.getOverlayID())) {
						// do not delete n - its our successor!
					} else {
						if ((predecessor == null || predecessor.getOverlayID()
								.compareTo(v.getChordId()) > 0)
								&& n.getOverlayID().equals(
										largestKnownRealNode.getOverlayID())) {
							// do not delete n - its our predecessor!
							// log.debug(v.id + " will not delete " + n
							// + " because its the predecessor!");
						} else {
							if (!toDelete.contains(n)) {
								toDelete.add(n);
							}
						}
					}

					toAdd.add(new Tuple<ChordRoutingTable, AbstractChordContact>(
							u, n));
				}

			}
		}

		// commit deletes:
		for (AbstractChordContact cc : toDelete) {
			v.unmarkedEdgeList.remove(cc);
			// log.debug(v.getChordId() + " removes " +
			// cc.getOverlayID() + "real: " + cc.isRealNode() +
			// "due to overlapping");
		}

		// commit adds:
		for (Tuple<ChordRoutingTable, AbstractChordContact> t : toAdd) {
			ChordRoutingTable rt = t.getA();
			AbstractChordContact cc = t.getB();

			if (!rt.unmarkedEdgeList.contains(cc)) {
				rt.unmarkedEdgeList.add(cc);
			}
		}
	}

	/**
	 * method for the 4. point of record.
	 */
	public void linearization() {
		linLeft();
		linRight();
		mirroring();

	}

	public AbstractChordContact getLargestKnownRealNode() {
		for (ChordRoutingTable v : this.getVirtualNodes()) {
			AbstractChordContact succ, pred = null;
			succ = v.getSuccessor();
			pred = v.getPredecessor();

			if (succ != null && !v.unmarkedEdgeList.contains(succ)) {
				v.unmarkedEdgeList.add(succ);
			}
			if (pred != null && !v.unmarkedEdgeList.contains(pred)) {
				v.unmarkedEdgeList.add(pred);
			}
		}
		ChordRoutingTable rt = this.getChordRoutingTable();
		AbstractChordContact succ, pred = null;
		succ = rt.getSuccessor();
		pred = rt.getPredecessor();
		if (succ != null && !rt.unmarkedEdgeList.contains(succ)) {
			rt.unmarkedEdgeList.add(succ);
		}
		if (pred != null && !rt.unmarkedEdgeList.contains(pred)) {
			rt.unmarkedEdgeList.add(pred);
		}

		AbstractChordContact largestKnownRealNode = null;
		for (AbstractChordContact cc : this.getChordRoutingTable()
				.getRealNodeNeighbors()) {
			if (largestKnownRealNode == null
					|| largestKnownRealNode.getOverlayID().compareTo(
							cc.getOverlayID()) < 0) {
				largestKnownRealNode = cc;
			}
		}
		return largestKnownRealNode;
	}

	public AbstractChordContact getSmallestKnownRealNode() {
		for (ChordRoutingTable v : this.getVirtualNodes()) {
			AbstractChordContact succ, pred = null;
			succ = v.getSuccessor();
			pred = v.getPredecessor();

			if (succ != null && !v.unmarkedEdgeList.contains(succ)) {
				v.unmarkedEdgeList.add(succ);
			}
			if (pred != null && !v.unmarkedEdgeList.contains(pred)) {
				v.unmarkedEdgeList.add(pred);
			}
		}
		ChordRoutingTable rt = this.getChordRoutingTable();
		AbstractChordContact succ, pred = null;
		succ = rt.getSuccessor();
		pred = rt.getPredecessor();
		if (succ != null && !rt.unmarkedEdgeList.contains(succ)) {
			rt.unmarkedEdgeList.add(succ);
		}
		if (pred != null && !rt.unmarkedEdgeList.contains(pred)) {
			rt.unmarkedEdgeList.add(pred);
		}

		AbstractChordContact smallestKnownRealNode = null;
		for (AbstractChordContact cc : this.getChordRoutingTable()
				.getRealNodeNeighbors()) {
			if (smallestKnownRealNode == null
					|| smallestKnownRealNode.getOverlayID().compareTo(
							cc.getOverlayID()) > 0) {
				smallestKnownRealNode = cc;
			}
		}
		return smallestKnownRealNode;
	}

	public void linLeft() {
		// for all peers w smaller than v:
		// find max u: u<w.
		// add edge between u and w
		// delete edge (v,u)

		AbstractChordContact smallestKnownRealNode = getSmallestKnownRealNode();

		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();

		AbstractChordContact successor = getChordRoutingTable().getSuccessor();

		List<OverlayContact<ChordID>> neighbors = new LinkedList<OverlayContact<ChordID>>(
				this
						.getChordRoutingTable().getNeighbors());
		Collections.sort(neighbors, new ChordContactGreaterThanComparator());
		Collections.reverse(neighbors);

		ChordContact old = null;
		for (OverlayContact<ChordID> oc : neighbors) {
			ChordContact w = (ChordContact) oc;

			if (w == null || toDelete.contains(w)
					|| w.getOverlayID().compareTo(getOverlayID()) >= 0)
			{
				continue;
				// w < old < v
			}

			// make link from old to w!
			if (old != null) {
				createLinkOperation(this, getLocalOverlayContact(), w, old,
						"unmarked");
				// new CreateLinkOperation(this, getLocalOverlayContact(), old,
				// w,
				// "unmarked").scheduleImmediately();
				toDelete.add(old);
			}
			old = w;
		}

		// commit deletes.
		for (AbstractChordContact cc : toDelete) {
			if (cc == null) {
				continue;
			}
			if ((successor == null || successor.getOverlayID().compareTo(
					getOverlayID()) < 0)
					&& cc == smallestKnownRealNode) {
				// log.debug(this + ": not deleting " + cc +
				// " because its the smallest node i know.");
				getChordRoutingTable().rightRealNeighbor = cc;
				continue;
			}
			while (getChordRoutingTable().unmarkedEdgeList.contains(cc)) {
				getChordRoutingTable().unmarkedEdgeList.remove(cc);
			}

		}
		toDelete.clear();

		// every virtual node must only know its next smallest and next largest
		// neighbor.
		for (ChordRoutingTable v : virtualNodes) {
			AbstractChordContact nextSmallest = null;
			AbstractChordContact nextSmallestReal = null;
			AbstractChordContact nextLargest = null;
			AbstractChordContact largestKnownNode = getLargestKnownRealNode();

			AbstractChordContact vsuccessor = v.getSuccessor();

			for (AbstractChordContact cc : v.unmarkedEdgeList) {
				if (cc.getOverlayID().compareTo(v.getChordId()) > 0) {
					if (nextLargest == null
							|| cc.getOverlayID().compareTo(
									nextLargest.getOverlayID()) < 0) {
						nextLargest = cc;
					}
				}
				if (cc.getOverlayID().compareTo(v.getChordId()) < 0) {
					if (nextSmallest == null
							|| cc.getOverlayID().compareTo(
									nextSmallest.getOverlayID()) > 0) {
						nextSmallest = cc;
						if (((ChordContact) cc).isRealNode()) {
							nextSmallestReal = cc;
						}
					}
				}
			}
			for (AbstractChordContact cc : v.unmarkedEdgeList) {
				if (cc != nextSmallest && cc != nextLargest) {
					toDelete.add(cc);
				}
			}

			for (AbstractChordContact cc : toDelete) {
				if (cc == null) {
					continue;
				}
				if ((vsuccessor == null || vsuccessor.getOverlayID().compareTo(
						v.getChordId()) < 0)
						&& cc == smallestKnownRealNode) {
					// log.debug(v.getChordContactOfThisRT() +
					// ": not deleting " + cc +
					// " because its the smallest node i know.");
					v.rightRealNeighbor = smallestKnownRealNode;
					continue;
				}

				if (!(nextSmallestReal == null && cc.getOverlayID().compareTo(
						largestKnownNode.getOverlayID()) == 0)) {
					if (!(nextSmallest == null && cc.getOverlayID().compareTo(
							largestKnownNode.getOverlayID()) == 0)) {
						while (v.unmarkedEdgeList.contains(cc)) {
							v.unmarkedEdgeList.remove(cc);

							// String nl = "none"; if(nextLargest != null) nl =
							// nextLargest.getOverlayID().toString();
							// String ns = "none"; if(nextSmallest != null) ns =
							// nextSmallest.getOverlayID().toString();
							// log.debug(v.getChordId() +
							// ": linLeft: removing from unmarked list: " + cc +
							// " nl: " + nl + " ns: " + ns);
						}
					}
				}

			}
			toDelete.clear();
		}

	}

	public void linRight() {
		// for all peers w greater than v:
		// find min u: u>w.
		// add edge between u and w
		// delete edge (v,u)
		AbstractChordContact largestKnownRealNode = getLargestKnownRealNode();

		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();

		AbstractChordContact predecessor = getChordRoutingTable()
				.getPredecessor();

		List<OverlayContact<ChordID>> neighbors = new LinkedList<OverlayContact<ChordID>>(
				this
						.getChordRoutingTable().getNeighbors());
		Collections.sort(neighbors, new ChordContactGreaterThanComparator());

		ChordContact old = null;
		for (OverlayContact<ChordID> oc : neighbors) {
			ChordContact w = (ChordContact) oc;

			if (w == null || toDelete.contains(w)
					|| w.getOverlayID().compareTo(getOverlayID()) <= 0)
			{
				continue;
				// w > old > v
			}

			// make link from old to w!
			if (old != null) {
				createLinkOperation(this, getLocalOverlayContact(), w, old,
						"unmarked");
				// new CreateLinkOperation(this, getLocalOverlayContact(), old,
				// w,
				// "unmarked").scheduleImmediately();
				toDelete.add(old);
			}
			old = w;
		}

		// commit deletes.
		for (AbstractChordContact cc : toDelete) {
			if (cc == null) {
				continue;
			}
			if ((predecessor == null || predecessor.getOverlayID().compareTo(
					getOverlayID()) > 0)
					&& cc == largestKnownRealNode) {
				// log.debug(this + ": not deleting " + cc +
				// " because its the largest node i know.");
				getChordRoutingTable().leftRealNeighbor = cc;
				continue;
			}
			while (getChordRoutingTable().unmarkedEdgeList.contains(cc)) {
				getChordRoutingTable().unmarkedEdgeList.remove(cc);
			}

		}
		toDelete.clear();

		// every virtual node must only know its next smallest and next largest
		// neighbor.
		for (ChordRoutingTable v : virtualNodes) {
			AbstractChordContact nextSmallest = null;
			AbstractChordContact nextLargest = null;
			AbstractChordContact nextLargestReal = null;
			AbstractChordContact smalestKnownNode = getSmallestKnownRealNode();

			AbstractChordContact vpredecessor = v.getPredecessor();

			for (AbstractChordContact cc : v.unmarkedEdgeList) {
				if (cc.getOverlayID().compareTo(v.getChordId()) > 0) {
					if (nextLargest == null
							|| cc.getOverlayID().compareTo(
									nextLargest.getOverlayID()) < 0) {
						nextLargest = cc;
						if (((ChordContact) cc).isRealNode()) {
							nextLargestReal = cc;
						}
					}
				}
				if (cc.getOverlayID().compareTo(v.getChordId()) < 0) {
					if (nextSmallest == null
							|| cc.getOverlayID().compareTo(
									nextSmallest.getOverlayID()) > 0) {
						nextSmallest = cc;
					}
				}
			}
			for (AbstractChordContact cc : v.unmarkedEdgeList) {
				if (cc != nextSmallest && cc != nextLargest) {
					toDelete.add(cc);
				}
			}

			for (AbstractChordContact cc : toDelete) {
				if (cc == null) {
					continue;
				}
				if ((vpredecessor == null || vpredecessor.getOverlayID()
						.compareTo(v.getChordId()) > 0)
						&& cc == largestKnownRealNode) {
					v.leftRealNeighbor = largestKnownRealNode;
					// log.debug(v.getChordContactOfThisRT() +
					// ": not deleting " + cc +
					// " because its the largest node i know.");
					continue;
				}

				if (!(nextLargest == null && cc.getOverlayID().compareTo(
						smalestKnownNode.getOverlayID()) == 0)) {
					if (!(nextLargestReal == null && cc.getOverlayID()
							.compareTo(smalestKnownNode.getOverlayID()) == 0)) {
						while (v.unmarkedEdgeList.contains(cc)) {
							v.unmarkedEdgeList.remove(cc);

							// String nl = "none"; if(nextLargest != null) nl =
							// nextLargestReal.getOverlayID().toString();
							// log.debug(v.getChordId() +
							// ": linRight: removing from unmarked list: " + cc
							// + " nl: " + nl);
						}
					}
				}

			}
			toDelete.clear();
		}

	}

	public void mirroring() {
		ChordRoutingTable t = this.getChordRoutingTable();
		for (AbstractChordContact c : t.unmarkedEdgeList) {
			if (c == null) {
				continue;
			}
			createLinkOperation(this, getLocalOverlayContact(), c,
					t.getChordContactOfThisRT(), "unmarked");
		}
		AbstractChordContact succ, pred = null;
		succ = t.getSuccessor();
		pred = t.getPredecessor();
		if (!t.unmarkedEdgeList.contains(succ) && succ != null) {
			t.unmarkedEdgeList.add(succ);
		}
		if (!t.unmarkedEdgeList.contains(pred) && pred != null) {
			t.unmarkedEdgeList.add(pred);
		}

		for (ChordRoutingTable v : this.virtualNodes) {
			for (AbstractChordContact c : v.unmarkedEdgeList) {
				if (c == null) {
					continue;
				}
				createLinkOperation(this, v.getChordContactOfThisRT(), c,
						v.getChordContactOfThisRT(), "unmarked");
			}
			succ = null;
			pred = null;
			succ = v.getSuccessor();
			pred = v.getPredecessor();
			if (!v.unmarkedEdgeList.contains(succ) && succ != null) {
				v.unmarkedEdgeList.add(succ);
			}
			if (!v.unmarkedEdgeList.contains(pred) && pred != null) {
				v.unmarkedEdgeList.add(pred);
			}
		}
		{
			return;
		}

		// make sure that allpeers in our routingtables have the corresponding
		// nodes in their routingtable aswell.

	}

	/**
	 * method used to reflect operation 3. of rechord: get the closest known
	 * real neighbor.
	 */
	public void findClosestRealNeighbors() {

		findClosestRealNeighbor((ChordRoutingTable) this.routingTable);

		for (ChordRoutingTable v : this.virtualNodes) {
			findClosestRealNeighbor(v);
		}

		// inform other nodes!

		List<OverlayContact<ChordID>> realNodeNeighbors = new LinkedList<OverlayContact<ChordID>>(
				this.getChordRoutingTable().getRealNodeNeighbors());
		Collection<AbstractChordContact> neighbors = this
				.getChordRoutingTable()
				.getNeighbors();

		Collections.sort(realNodeNeighbors,
				new ChordContactGreaterThanComparator());
		Collections.reverse(realNodeNeighbors);

		OverlayContact<ChordID> smallestRealNode = null;
		OverlayContact<ChordID> largestRealNode = null;
		if (realNodeNeighbors.size() > 0) {
			smallestRealNode = realNodeNeighbors.get(0);
			largestRealNode = realNodeNeighbors
					.get(realNodeNeighbors.size() - 1);
		}

		for (OverlayContact<ChordID> n : neighbors) {
			OverlayContact<ChordID> prev = null, next = null;
			// find next greater and next smaller realNode:
			for (OverlayContact<ChordID> r : realNodeNeighbors) {
				if (r == null) {
					continue;
				}

				if (r.getOverlayID().compareTo(n.getOverlayID()) < 0) {
					prev = r;
				}

				if (r.getOverlayID().compareTo(n.getOverlayID()) > 0) {
					next = r;
					break;
				}

			}

			if (prev == null) {
				prev = largestRealNode;
			}
			if (next == null) {
				next = smallestRealNode;
			}

			// found prev and next for our friend n.
			LinkedHashSet<AbstractChordContact> s = new LinkedHashSet<AbstractChordContact>();
			if (prev != null) {
				s.add((ChordContact) prev);
			} else {
				s.add((ChordContact) realNodeNeighbors.get(realNodeNeighbors
						.size() - 1));
			}
			if (next != null) {
				s.add((ChordContact) next);
			}
			createLinkOperation(this, getLocalOverlayContact(),
					(AbstractChordContact) n,
					s, "unmarked");

		}

	}

	private static void findClosestRealNeighbor(ChordRoutingTable rt) {
		// get closest left neighbor:
		AbstractChordContact max = null;
		AbstractChordContact oldLeft = rt.leftRealNeighbor;

		for (AbstractChordContact cc : rt.getRealNodeNeighbors()) {

			if (cc == null) {
				continue;
			}

			if (cc.getOverlayID().compareTo(rt.id) < 0) {
				if (max == null) {
					max = cc;
				}
				if (max.getOverlayID().compareTo(cc.getOverlayID()) < 0) {
					max = cc;
				}
			}
		}
		if (max != oldLeft && max != null) {

			// we found a new closest left real neighbor!

			// add max to unmarked neighbors.
			if (!rt.unmarkedEdgeList.contains(max)) {
				rt.unmarkedEdgeList.add(max);
			}

			// //inform other nodes.
			// for(ChordContact y : rt.unmarkedEdgeList) {
			// if(y == null)
			// continue;
			//
			// if( y.getOverlayID().between(rt.id, max.getOverlayID()) ) {
			//
			// //inform y that it has to add max as a new unmarked neighbor in
			// the next round!
			// CreateLinkOperation op = new CreateLinkOperation(this,
			// rt.getChordContactOfThisRT(), y, max, "unmarked");
			// op.scheduleImmediately();
			//
			//
			// }
			// }

			rt.leftRealNeighbor = max;
		}

		// get closest right neighbor:
		AbstractChordContact min = null;
		AbstractChordContact oldRight = rt.rightRealNeighbor;

		for (AbstractChordContact cc : rt.getRealNodeNeighbors()) {
			if (cc == null) {
				continue;
			}

			if (cc.getOverlayID().compareTo(rt.id) > 0) {
				if (min == null) {
					min = cc;
				}
				if (min.getOverlayID().compareTo(cc.getOverlayID()) > 0) {
					min = cc;
				}
			}
		}
		if (min != oldRight && min != null) {

			// we found a new closest right real neighbor!

			// add max to unmarked neighbors.
			if (!rt.unmarkedEdgeList.contains(min)) {
				rt.unmarkedEdgeList.add(min);
			}

			// //inform other nodes.
			// for(ChordContact y : rt.unmarkedEdgeList) {
			// if(y == null) continue;
			// if( y.getOverlayID().between(rt.id, min.getOverlayID()) ) {
			//
			// //inform y that it has to add min as a new unmarked neighbor in
			// the next round!
			// CreateLinkOperation op = new CreateLinkOperation(this,
			// rt.getChordContactOfThisRT(), y, min, "unmarked");
			// op.scheduleImmediately();
			//
			// }
			// }

			rt.rightRealNeighbor = min;
		}

	}

	private static ChordContact getSmallestNeighbor(
			Collection<AbstractChordContact> neighbors) {
		ChordContact min = null;

		for (AbstractChordContact c : neighbors) {
			if (c == null) {
				continue;
			}
			ChordContact cc = (ChordContact) c;
			if (min == null
					|| cc.getOverlayID().compareTo(min.getOverlayID()) < 0) {
				min = cc;
			}
		}
		return min;
	}

	private static ChordContact getLargestNeighbor(
			Collection<AbstractChordContact> neighbors) {
		ChordContact max = null;

		for (AbstractChordContact c : neighbors) {
			if (c == null) {
				continue;
			}
			ChordContact cc = (ChordContact) c;
			if (max == null
					|| cc.getOverlayID().compareTo(max.getOverlayID()) > 0) {
				max = cc;
			}
		}
		return max;
	}

	public void createAllRingEdges() {
		Collection<AbstractChordContact> neighbors = this
				.getChordRoutingTable()
				.getNeighbors();
		createAllRingEdges((ChordRoutingTable) this.routingTable, neighbors);
		for (ChordRoutingTable v : this.virtualNodes) {
			createAllRingEdges(v, neighbors);
		}
	}

	private void createAllRingEdges(ChordRoutingTable v,
			Collection<AbstractChordContact> neighbors) {
		createRingEdgeLeft(v, neighbors);
		createRingEdgeRight(v, neighbors);
	}

	private void createRingEdgeLeft(ChordRoutingTable v,
			Collection<AbstractChordContact> neighbors) {
		// check if there exists a node with a smaller id:
		boolean smallerNodeFound = false;
		for (AbstractChordContact c : v.unmarkedEdgeList) {
			if (c == null) {
				continue;
			}
			if (c.getOverlayID().compareTo(v.id) < 0) {
				smallerNodeFound = true;
				break;
			}
		}

		if (!smallerNodeFound) {
			// find maximum known node to the master-node and create ring.
			ChordContact max = getLargestNeighbor(neighbors);

			if (max == null) {
				return;
			}
			// say max to create ring-edge to me.
			createLinkOperation(this, v.getChordContactOfThisRT(), max,
					v.getChordContactOfThisRT(), "ring");

			// log.debug(v.getChordId() + "(to "+ max.getOverlayID()
			// +"): i have no left neighbor.");
		}

	}

	private void createRingEdgeRight(ChordRoutingTable v,
			Collection<AbstractChordContact> neighbors) {
		// check if there exists a node with a higher id:
		boolean biggerNodeFound = false;
		for (AbstractChordContact c : v.unmarkedEdgeList) {
			if (c == null) {
				continue;
			}
			if (c.getOverlayID().compareTo(v.id) > 0) {
				biggerNodeFound = true;
				break;
			}
		}

		if (!biggerNodeFound) {
			// find minimum known node to the master-node and create ring.
			ChordContact min = getSmallestNeighbor(neighbors);

			if (min == null) {
				return;
			}
			// say min to create ring-edge to me.
			createLinkOperation(this, v.getChordContactOfThisRT(), min,
					v.getChordContactOfThisRT(), "ring");

			// log.debug(v.getChordId() + "(to "+ min.getOverlayID()
			// +"): i have no right neighbor.");
		}
	}

	public void forwardAllRingEdges() {
		Collection<AbstractChordContact> neighbors = this
				.getChordRoutingTable()
				.getNeighbors();
		forwardAllRingEdges((ChordRoutingTable) this.routingTable, neighbors);
		for (ChordRoutingTable v : this.virtualNodes) {
			forwardAllRingEdges(v, neighbors);
		}
	}

	private void forwardAllRingEdges(ChordRoutingTable v,
			Collection<AbstractChordContact> neighbors) {
		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();

		v.getMasterNode();
		ChordContact smallestNeighbor = ChordNode.getSmallestNeighbor(
				neighbors);
		v.getMasterNode();
		ChordContact largestNeighbor = ChordNode.getLargestNeighbor(
				neighbors);

		for (AbstractChordContact w : v.ringEdgeList) {
			if (w == null || toDelete.contains(w)) {
				continue;
			}

			if (w.getOverlayID().compareTo(v.getChordId()) > 0) {
				// L part

				// get maximum element from N(v) \cup N_r(v):
				AbstractChordContact max = largestNeighbor;
				for (AbstractChordContact c : v.ringEdgeList) {
					if (c == null || toDelete.contains(c)) {
						continue;
					}
					if (max == null
							|| c.getOverlayID().compareTo(max.getOverlayID()) > 0) {
						max = c;
					}
				}
				// got max.

				if (max != null
						&& max.getOverlayID().compareTo(w.getOverlayID()) > 0) {
					// L2
					toDelete.add(w);
					createLinkOperation(this, v.getChordContactOfThisRT(), max,
							w, "unmarked");

					// log.debug(v.getChordId() +
					// ": creating unmarked edge from " + max.getOverlayID() +
					// " to " + w.getOverlayID());
				} else {
					// L1
					ChordContact min = smallestNeighbor;
					if (min.getOverlayID().compareTo(v.getChordId()) < 0) {
						toDelete.add(w);
						createLinkOperation(this, v.getChordContactOfThisRT(),
								min, w, "ring");

						// log.debug(v.getChordId() +
						// ": creating ring edge from " + min.getOverlayID() +
						// " to " + w.getOverlayID());
					}
				}
			} else {
				// R part

				// get minimum element from N(v) \cup N_r(v):
				AbstractChordContact min = smallestNeighbor;
				for (AbstractChordContact c : v.ringEdgeList) {
					if (c == null || toDelete.contains(c)) {
						continue;
					}
					if (min == null
							|| c.getOverlayID().compareTo(min.getOverlayID()) < 0) {
						min = c;
					}
				}
				// got min.

				if (min != null
						&& min.getOverlayID().compareTo(w.getOverlayID()) < 0) {
					// R2
					toDelete.add(w);
					createLinkOperation(this, v.getChordContactOfThisRT(), min,
							w, "unmarked");

					// log.debug(v.getChordId() +
					// ": creating unmarked edge from " + min.getOverlayID() +
					// " to " + w.getOverlayID());
				} else {
					// R1
					ChordContact max = largestNeighbor;

					if (v.getChordId().compareTo(max.getOverlayID()) < 0) {
						toDelete.add(w);
						createLinkOperation(this, v.getChordContactOfThisRT(),
								max, w, "ring");

						// log.debug(v.getChordId() +
						// ": creating ring edge from " + max.getOverlayID() +
						// " to " + w.getOverlayID());
					}
				}
			}

		}

		// commit deletes
		for (AbstractChordContact c : toDelete) {
			if (c == null) {
				continue;
			}

			v.ringEdgeList.remove(c);
		}

	}

	public void connectVirtualNodes() {
		Iterator<ChordRoutingTable> vIt = virtualNodes.descendingIterator();
		ChordRoutingTable v1 = null, v2 = null;
		while (vIt.hasNext()) {
			v1 = v2;
			v2 = vIt.next();

			if (v1 == null) {
				v1 = (ChordRoutingTable) this.routingTable;
			}

			v1.connectionEdgeList.add(v2.getChordContactOfThisRT());
		}
	}

	public void forwardAllCEdges() {
		forwardAllCEdges((ChordRoutingTable) this.routingTable);
		for (ChordRoutingTable v : this.virtualNodes) {
			forwardAllCEdges(v);
		}
	}

	private void forwardAllCEdges(ChordRoutingTable v) {
		forwardAllCEdges1(v);
		forwardAllCEdges2(v);
	}

	private void forwardAllCEdges1(ChordRoutingTable v) {
		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();
		for (AbstractChordContact u : v.connectionEdgeList) {
			if (u == null || toDelete.contains(u)) {
				continue;
			}

			// w = leftmost element in N_u(v) \cup S(v)
			AbstractChordContact w = null;
			for (AbstractChordContact c : v.unmarkedEdgeList) {
				if (c == null) {
					continue;
				}
				if (c.getOverlayID().compareTo(u.getOverlayID()) < 0) {
					if (w == null
							|| c.getOverlayID().compareTo(w.getOverlayID()) > 0) {
						w = c;
					}
				}
			}
			for (ChordRoutingTable c : this.virtualNodes) {
				if (c.getChordId().compareTo(u.getOverlayID()) < 0) {
					if (w == null
							|| c.getChordId().compareTo(w.getOverlayID()) > 0) {
						w = c.getChordContactOfThisRT();
					}
				}
			}
			if (w != null) {
				// w is leftmost element known.
				if (w.getOverlayID().compareTo(v.getChordId()) != 0) {
					// w != v
					createLinkOperation(this, v.getChordContactOfThisRT(), w,
							u, "connection");

					toDelete.add(u);
				}
			}

		}

		// commit deletes
		for (AbstractChordContact cc : toDelete) {
			if (cc != null) {
				v.connectionEdgeList.remove(cc);
			}
		}
	}

	private void forwardAllCEdges2(ChordRoutingTable v) {
		LinkedList<AbstractChordContact> toDelete = new LinkedList<AbstractChordContact>();
		for (AbstractChordContact u : v.connectionEdgeList) {
			if (u == null || toDelete.contains(u)) {
				continue;
			}

			// w = leftmost element in N_u(v) \cup S(v)
			AbstractChordContact w = null;
			for (AbstractChordContact c : v.unmarkedEdgeList) {
				if (c == null) {
					continue;
				}
				if (c.getOverlayID().compareTo(u.getOverlayID()) < 0) {
					if (w == null
							|| c.getOverlayID().compareTo(w.getOverlayID()) > 0) {
						w = c;
					}
				}
			}
			for (ChordRoutingTable c : this.virtualNodes) {
				if (c.getChordId().compareTo(u.getOverlayID()) < 0) {
					if (w == null
							|| c.getChordId().compareTo(w.getOverlayID()) > 0) {
						w = c.getChordContactOfThisRT();
					}
				}
			}
			if (w != null) {
				// w is leftmost element known.
				if (w.getOverlayID().compareTo(v.getChordId()) == 0) {
					// w == v
					createLinkOperation(this, v.getChordContactOfThisRT(), u,
							v.getChordContactOfThisRT(), "unmarked");

					toDelete.add(u);
				}
			}

		}

		// commit deletes
		for (AbstractChordContact cc : toDelete) {
			if (cc != null) {
				v.connectionEdgeList.remove(cc);
			}
		}
	}

	private JoinOperation joinOperation = null;

	/**
	 * Join the overlay with a delay
	 * 
	 * @param callback
	 * @return the Id of the JoinOperation
	 */
	@Override
	public int joinWithDelay(OperationCallback<Object> callback, long delay) {
		setPeerStatus(PeerStatus.TO_JOIN);
		// Node intentionally joined --> Do rejoins after churn on-line events
		this.rejoinOnOnlineEvent = true;

		log.debug("Node initiated join " + this + " at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		joinOperation = new JoinOperation(this, callback);

		if (delay > 0) {
			joinOperation.scheduleWithDelay(delay);
		} else {
			joinOperation.scheduleImmediately();
		}

		return joinOperation.getOperationID();
	}

	public LinkedList<ChordRoutingTable> getVirtualNodes() {
		return virtualNodes;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {

		log.debug("Connectivity changed " + this + " to online="
				+ ce.isOnline());
		if (ce.isOnline()) {
			if (getPeerStatus().equals(PeerStatus.ABSENT)
					&& this.rejoinOnOnlineEvent) {

				log.debug(Simulator.getSimulatedRealtime() + " Peer "
						+ getHost().getNetLayer().getNetID()
						+ " received online event ");
				setPeerStatus(PeerStatus.TO_JOIN);
				join(Operations.EMPTY_CALLBACK);
			}
		} else if (ce.isOffline()) {
			if (getPeerStatus().equals(PeerStatus.PRESENT)
					|| getPeerStatus().equals(PeerStatus.TO_JOIN)) {

				log.debug(Simulator.getSimulatedRealtime() + " Peer "
						+ getHost().getNetLayer().getNetID()
						+ " is affected by churn ");

				setPeerStatus(PeerStatus.ABSENT);

				// Mark the old listener as inactive and create an new one
				operationListener.setInactive();
				operationListener = new ChordOperationListener(this);

				// Mark the old routing table as inactive
				if (routingTable != null) {
					((ChordRoutingTable) routingTable).setInactive();
				}
				routingTable = null;

				// Unregister node at bootstrap manager
				this.getBootstrapManager().unregisterNode(this);

				// delete the stored data items
				// TODO check this remove operation as it throws a
				// ConcurrentModificationException
				// Set<DHTEntry> entries = dht.getDHTEntries();
				// Iterator<DHTEntry> it = entries.iterator();
				// while (it.hasNext()) {
				// DHTEntry dhtEntry = (DHTEntry) it.next();
				// it.remove();
				// dht.removeDHTEntry(dhtEntry.getKey());
				// }

				// Reset the SkyNet node
				OverlayNode<?, ?> olNode = getHost().getOverlay(
						SkyNetNode.class);
				if (olNode != null && olNode instanceof SkyNetNode) {
					((SkyNetNode) olNode).resetSkyNetNode(Simulator
							.getCurrentTime());
				}
			}
		}
	}

	/**
	 * 
	 * This method is called when join operation is finished.
	 * 
	 * @param entryPoint
	 *            the first node the new node gets to know.
	 * 
	 */
	public void joinOperationFinished(AbstractChordContact entryPoint) {

		log.debug(Simulator.getSimulatedRealtime() + " Peer "
				+ getHost().getNetLayer().getNetID() + " joined ");

		// Start SkyNet after the joining was successful
		SkyNetNode node = ((SkyNetNode) getHost().getOverlay(
				AbstractSkyNetNode.class));
		if (node != null) {
			node.startSkyNetNode(Simulator.getCurrentTime());
		}
	}

	/**
	 * This method is called when leave operation is finished
	 */
	@Override
	public void leaveOperationFinished() {

		// Stop SkyNet
		SkyNetNode node = ((SkyNetNode) getHost().getOverlay(
				AbstractSkyNetNode.class));
		if (node != null) {
			node.resetSkyNetNode(Simulator.getCurrentTime());
		}

		log.info(" node leave " + this + " at Time[s] "
				+ Simulator.getCurrentTime() / Simulator.SECOND_UNIT);
		setPeerStatus(PeerStatus.ABSENT);

		// Mark the old listener as inactive and create an new one
		operationListener.setInactive();
		operationListener = new ChordOperationListener(this);

		// Mark the old routing table as inactive
		if (routingTable != null) {
			((ChordRoutingTable) routingTable).setInactive();
		}

	}

	// Getters and Setters

	@Override
	public ChordRoutingTable getChordRoutingTable() {

		if (routingTable == null) {
			routingTable = new ChordRoutingTable(this, getOverlayID(), 0);
		}

		return (ChordRoutingTable) routingTable;
	}

	@Override
	public JoinOperation getJoinOperation() {
		return joinOperation;
	}

	public ChordOperationListener getOperationListener() {
		return operationListener;
	}

	@Override
	public ChordMessageHandler getMessageHandler() {
		return messageHandler;
	}

	@Override
	public String toString() {
		return "Node " + getOverlayID() + " " + getPeerStatus();
	}

	@Override
	public boolean isOnline() {
		return getHost().getNetLayer().isOnline();
	}

	@Override
	public boolean absentCausedByChurn() {
		return (getPeerStatus() == PeerStatus.ABSENT) && rejoinOnOnlineEvent;
	}

	/*
	 * KBR methods
	 */

	@Override
	public ChordContact getLocalOverlayContact() {
		return new ChordContact(this.getOverlayID(), this.getTransInfo(), true);
	}

	@Override
	public ChordContact getOverlayContact(ChordID id, TransInfo transInfo) {
		if (!(id != null)) {
			return null;
		}

		return new ChordContact(id, transInfo,
				id == this.getOverlayID());
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getM() {
		return m;
	}

	/**
	 * create all edges that have to be active at the beginning of the new
	 * stabilisation round.
	 */
	public void commitNewEdges() {

		// unmarked
		for (AbstractChordContact cc : ((ChordRoutingTable) this.routingTable).unmarkedEdgeListToAddNextRound) {
			if (!((ChordRoutingTable) this.routingTable).unmarkedEdgeList
					.contains(cc)) {
				((ChordRoutingTable) this.routingTable).unmarkedEdgeList
						.add(cc);
				// log.debug(this + ": added node " + cc +
				// " to the unmarked list.");
			}
		}
		((ChordRoutingTable) this.routingTable).unmarkedEdgeListToAddNextRound
				.clear();

		for (ChordRoutingTable v : this.virtualNodes) {
			for (AbstractChordContact cc : v.unmarkedEdgeListToAddNextRound) {
				if (!v.unmarkedEdgeList.contains(cc)) {
					v.unmarkedEdgeList.add(cc);
				}
			}
			v.unmarkedEdgeListToAddNextRound.clear();
		}

		// ring
		for (AbstractChordContact cc : ((ChordRoutingTable) this.routingTable).ringEdgeListToAddNextRound) {
			if (!((ChordRoutingTable) this.routingTable).ringEdgeList
					.contains(cc)) {
				((ChordRoutingTable) this.routingTable).ringEdgeList.add(cc);
			}
		}
		((ChordRoutingTable) this.routingTable).ringEdgeListToAddNextRound
				.clear();

		for (ChordRoutingTable v : this.virtualNodes) {
			for (AbstractChordContact cc : v.ringEdgeListToAddNextRound) {
				if (!v.ringEdgeList.contains(cc)) {
					v.ringEdgeList.add(cc);
				}
			}
			v.ringEdgeListToAddNextRound.clear();
		}

		// connection
		for (AbstractChordContact cc : ((ChordRoutingTable) this.routingTable).connectionEdgeListToAddNextRound) {
			if (!((ChordRoutingTable) this.routingTable).connectionEdgeList
					.contains(cc)) {
				((ChordRoutingTable) this.routingTable).connectionEdgeList
						.add(cc);
			}
		}
		((ChordRoutingTable) this.routingTable).connectionEdgeListToAddNextRound
				.clear();

		for (ChordRoutingTable v : this.virtualNodes) {
			for (AbstractChordContact cc : v.connectionEdgeListToAddNextRound) {
				if (!v.connectionEdgeList.contains(cc)) {
					v.connectionEdgeList.add(cc);
				}
			}
			v.connectionEdgeListToAddNextRound.clear();
		}

		// remove nulls from lists -- if present

		if (this.getChordRoutingTable().unmarkedEdgeList.contains(null)) {
			this.getChordRoutingTable().unmarkedEdgeList.remove(null);
		}
		if (this.getChordRoutingTable().ringEdgeList.contains(null)) {
			this.getChordRoutingTable().ringEdgeList.remove(null);
		}
		if (this.getChordRoutingTable().connectionEdgeList.contains(null)) {
			this.getChordRoutingTable().connectionEdgeList.remove(null);
		}

		for (ChordRoutingTable v : this.virtualNodes) {
			if (v.unmarkedEdgeList.contains(null)) {
				v.unmarkedEdgeList.remove(null);
			}
			if (v.ringEdgeList.contains(null)) {
				v.ringEdgeList.remove(null);
			}
			if (v.connectionEdgeList.contains(null)) {
				v.connectionEdgeList.remove(null);
			}
		}

	}

	public void foundDeadHost(ChordID id) {
		((ChordRoutingTable) this.routingTable).removeFromRouting(id);
		for (ChordRoutingTable v : this.virtualNodes) {
			v.removeFromRouting(id);
		}
	}

	// local delivery of CreateLinkOperation Messages -- used for inter-node
	// communication of vhost to vhost.
	public void localCreateLinkOperation(AbstractChordContact receiver,
			Set<AbstractChordContact> targetOfNewLink, String typeOfCreatedEdge) {

		for (ChordRoutingTable v : getVirtualNodes()) {
			if (v.id.equals(receiver.getOverlayID())) {
				for (AbstractChordContact cc : targetOfNewLink) {
					if (typeOfCreatedEdge.equals("unmarked")) {
						v.unmarkedEdgeListToAddNextRound.add(cc);
					}
					if (typeOfCreatedEdge.equals("ring")) {
						v.ringEdgeListToAddNextRound.add(cc);
					}
					if (typeOfCreatedEdge.equals("connection")) {
						v.connectionEdgeListToAddNextRound.add(cc);
					}
				}
				break;
			}
		} // for

	}

} // class
