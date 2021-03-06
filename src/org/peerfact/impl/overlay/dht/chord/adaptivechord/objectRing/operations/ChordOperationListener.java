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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.operations;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.callbacks.UpdateDirectSuccessorOperation;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperationListener;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This class is used to start the events periodically
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordOperationListener extends AbstractChordOperationListener {

	private static Logger log = SimLogger
			.getLogger(ChordOperationListener.class);

	private ChordNode masterNode;

	private boolean isActive = true;

	public ChordOperationListener(ChordNode _masterNode) {
		this.masterNode = _masterNode;
	}

	@Override
	public void calledOperationFailed(Operation<AbstractChordContact> op) {
		if (masterNode.isPresent()) {
			log.info("calledOperationFailed");
			addNextOperation(op);
		}
	}

	@Override
	public void calledOperationSucceeded(Operation<AbstractChordContact> op) {
		if (masterNode.isPresent()) {
			log.debug("Operation Succeeded " + op);
			addNextOperation(op);
		}
	}

	@Override
	public void addNextOperation(Operation<AbstractChordContact> op) {

		if (!masterNode.isPresent() || !isActive) {
			return;
		}

		if (op instanceof UpdateDirectSuccessorOperation) {
			long exeTime = Simulator.getCurrentTime()
					- ((UpdateDirectSuccessorOperation) op).getBeginTime();
			exeTime = exeTime % masterNode.UPDATE_SUCCESSOR_INTERVAL;

			new UpdateDirectSuccessorOperation(masterNode, this)
					.scheduleWithDelay(masterNode.UPDATE_SUCCESSOR_INTERVAL
							- exeTime);

		} else if (op instanceof UpdateFingerPointOperation) {

			long exeTime = Simulator.getCurrentTime()
					- ((UpdateFingerPointOperation) op).getBeginTime();
			exeTime = exeTime % masterNode.UPDATE_SUCCESSOR_INTERVAL;

			new UpdateFingerPointOperation(masterNode, this)
					.scheduleWithDelay(masterNode.UPDATE_FINGERTABLE_INTERVAL
							- exeTime);
		}

	}

	@Override
	public void setInactive() {
		this.isActive = false;
	}
}
