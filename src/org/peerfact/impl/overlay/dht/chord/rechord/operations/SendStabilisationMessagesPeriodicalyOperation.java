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

package org.peerfact.impl.overlay.dht.chord.rechord.operations;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.rechord.components.ChordNode;
import org.peerfact.impl.simengine.Simulator;

public class SendStabilisationMessagesPeriodicalyOperation extends
		AbstractChordOperation<AbstractChordNode> {

	private ChordNode masterNode;

	public SendStabilisationMessagesPeriodicalyOperation(
			AbstractChordNode component) {
		super(component);
		masterNode = (ChordNode) component;
	}

	@Override
	protected void execute() {
		if (getComponent().isPresent()) {
			int times = (int) (ChordConfiguration.UPDATE_VIRTUAL_NODES_INTERVAL / Simulator.SECOND_UNIT);
			masterNode.sendCreateLinkMessages(times);
		}
		// make sure this operation is scheduled again.
		new SendStabilisationMessagesPeriodicalyOperation(masterNode)
				.scheduleWithDelay(Simulator.SECOND_UNIT);
		operationFinished(true);
	}

	@Override
	public AbstractChordNode getResult() {
		return null;
	}

}
