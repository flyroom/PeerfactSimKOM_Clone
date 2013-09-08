/*
 * Copyright (c) UPB - University of Paderborn
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

package org.peerfact.impl.overlay.dht.chord.base.operations;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.simengine.Simulator;

/**
 * This Operation is called periodically to write the available bandwidth to
 * file.
 * 
 * @author Philip Wette <info@peerfact.org>
 * @version 1.0, 06/21/2011
 */
public class MonitorHostPropertiesOperation extends
		AbstractChordOperation<Boolean> {

	AbstractChordNode owner;

	// int numRetrys = 0; - Commented out because it was never used

	public MonitorHostPropertiesOperation(AbstractChordNode component) {
		super(component);
		this.owner = component;

	}

	@Override
	protected void execute() {

		// reschedule
		MonitorHostPropertiesOperation monOp = new MonitorHostPropertiesOperation(
				super.getComponent());
		monOp.scheduleWithDelay(Simulator.SECOND_UNIT * 10);

		operationFinished(true);
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

}
