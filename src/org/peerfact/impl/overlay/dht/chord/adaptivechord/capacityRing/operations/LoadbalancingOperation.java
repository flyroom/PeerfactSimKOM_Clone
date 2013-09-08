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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.operations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.components.AdaptiveChordNode;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.interfaces.AdaptiveRemoteControl;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.Tuple;


/**
 * This Operation is called periodically to check if my mirrors are still
 * online.
 * 
 * @author wette
 * @version 1.0, 06/21/2011
 */
public class LoadbalancingOperation extends
		AbstractOperation<AdaptiveChordNode, Boolean>
		implements OperationCallback<List<AbstractChordContact>> {

	AdaptiveChordNode owner;

	ChordKey documentToMirror;

	long delay = ChordConfiguration.LOADBALANCING_OPERATION_INTERVALL;

	boolean isRescheduled = false;

	public LoadbalancingOperation(AdaptiveChordNode component) {
		super(component);

		this.owner = component;

		this.documentToMirror = null;
	}

	private void reschedule() {
		if (!isRescheduled) {
			new LoadbalancingOperation(owner).scheduleWithDelay(delay);
			isRescheduled = true;
		}
	}

	@Override
	protected void execute() {

		BigInteger MAX_KEY_SIZE = new BigInteger(
				"1461501637330902918203684832716283019655932542975");

		// check overloaded
		ChordNode n = (ChordNode) owner.getDataNetNode();
		ChordNode loadNode = (ChordNode) owner.getLoadbalancingNetNode();

		n.updateLoads();

		boolean overLoaded = n.isOverloaded();

		if (!n.isPresent()) {
			operationFinished(true);
			// reschedule:
			reschedule();
			return;
		}

		if (!loadNode.isPresent()) {
			if (loadNode.getPeerStatus() == PeerStatus.ABSENT) {
				loadNode.join(null);
				// reschedule:
				reschedule();
				operationFinished(true);
				return;
			}
		}

		// check if the node is overloaded:
		if (overLoaded) {

			// first check if we mirror a document - if so - get rid of it!
			if (n.getNumMirroredObjects() > 0) {
				if (n.removeHighestLoadMirroredObject()) {
					System.out
							.println(n
									+ ": deleting a mirrored object because of overloaded.");
					// reschedule:
					reschedule();
					return;
				}
			}

			// check if we can get rid of the document with the highest load:
			double max = 0;
			ChordKey key = null;

			for (Tuple<ChordKey, Double> t : n.getOfferedObjects()) {
				if (n.getMirrorForObject(t.getA()) == null) {
					if (key == null || t.getB() > max) {
						if (n.getMirrorForObject(key) == null) {
							max = t.getB();
							key = t.getA();
						}
					}
				}
			}

			if (key != null) {
				// there is a key we can get rid of:
				// find a node that is capable of this additional load
				documentToMirror = key;

				// double left =
				// this.getComponent().getHost().getProperties().getMaxUploadBandwidth()
				// - n.getLoad();
				//
				// log.debug(n.getOverlayID() + ": document " + key +
				// "creates too much load: " + max );
				// log.debug(n.getOverlayID() + ": i have only " + left
				// + " left.");
				// log.debug(n.getOverlayID() + ": max up: " +
				// this.getComponent().getHost().getProperties().getMaxUploadBandwidth()
				// + " load: " + n.getLoad());
				//
				// do not look for to low values.
				if (max < ((ChordNode) this.owner.getDataNetNode()).minimalMirrorBandwidth) {
					max = ((ChordNode) this.owner.getDataNetNode()).minimalMirrorBandwidth;
				}

				log.debug(n.getOverlayID()
						+ ": looking for a mirror with" + max
						+ " free bandwidth");

				// create ChordID corresponding to max:
				BigInteger id = new BigInteger(MAX_KEY_SIZE.toString());

				double factor = max
						/ AdaptiveRemoteControl.MAXIMUM_UPLOAD_BANDWIDTH;

				BigDecimal bf = new BigDecimal(factor);

				bf = bf.multiply(new BigDecimal(id));
				id = bf.toBigInteger();

				// check that the id is not to low:
				if (id.compareTo(new BigInteger(
						ChordConfiguration.RANDOM_TIE_BREAKER_SIZE.toString())) < 0) {
					id = id.add(new BigInteger(
							ChordConfiguration.RANDOM_TIE_BREAKER_SIZE
									.toString()));
				}

				// add tiebreaker on id so that not only one mirror gets all the
				// load:
				id = id.add(new BigInteger(
						Long.valueOf(
								(long) (Simulator.getRandom().nextDouble() * ChordConfiguration.RANDOM_TIE_BREAKER_SIZE))
								.toString()));

				// get the new mirror for this object:
				loadNode.overlayNodeLookup(
						new ChordID(new BigInteger(id.toString())), this);
				return;
			}

		}

		// calculate current load and reposition in the Loadbalancing Network
		double load = n.getPerformanceIndex();
		if (load < 0) {
			load = 0;
		}

		// calculate the id in the load network:
		BigDecimal temp;

		temp = new BigDecimal(load);
		temp = temp.multiply(new BigDecimal(MAX_KEY_SIZE));
		BigInteger newid = temp.toBigInteger();

		if (overLoaded) {
			newid = new BigInteger("0");
		}

		if ((n.getNumMirroredObjects() >= ChordConfiguration.MAX_MIRROR_COUNT)) {
			newid = new BigInteger("0");
		}

		// check if the difference between our old id and our new id is
		// sufficient for a change.
		BigInteger bf = new BigInteger(newid.toString());
		BigInteger oldid = loadNode.getOverlayID().getUniqueValue();
		bf = bf.subtract(oldid);
		bf = bf.abs();

		boolean change = false;

		if (newid.compareTo(new BigInteger(
				ChordConfiguration.RANDOM_TIE_BREAKER_SIZE.toString())) < 0) { // wenn
			// neue
			// id
			// kleiner
			// als
			// 10
			// mio
			if (oldid.compareTo(new BigInteger(
					ChordConfiguration.RANDOM_TIE_BREAKER_SIZE.toString())) > 0) {
				change = true;
			}
			else {
				change = false; // nothing to change - is already 0.
			}
		} else {
			temp = new BigDecimal(bf.abs());
			temp = temp
					.divide(new BigDecimal(newid), 2, RoundingMode.HALF_DOWN);

			if (temp.compareTo(new BigDecimal(new Float(
					ChordConfiguration.LOADNET_ID_CHANGE_FACTOR).toString())) > 0) {
				change = true;
			}
		}

		// now temp is the "amount of change"
		if (change) {
			// it is sufficient to change.

			// re-join the network with a new identifier.
			loadNode.changeIdentTo(new ChordID(newid));
		}

		// reschedule:
		operationFinished(true);
		reschedule();
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

	@Override
	public void calledOperationFailed(Operation<List<AbstractChordContact>> op) {
		operationFinished(false);

		// reschedule:
		reschedule();

	}

	@Override
	public void calledOperationSucceeded(
			Operation<List<AbstractChordContact>> op) {
		if (op.getResult().size() > 0) {
			// found someone:
			AbstractChordContact mirror = op.getResult().get(0);
			ChordNode n = (ChordNode) this.owner.getDataNetNode();

			n.addRedirection(documentToMirror, mirror);
			operationFinished(true);
		} else {
			// no node found - we have to keep the value.
			ChordNode n = (ChordNode) this.owner.getDataNetNode();

			operationFinished(false);
		}

		// reschedule:
		reschedule();
	}

}
