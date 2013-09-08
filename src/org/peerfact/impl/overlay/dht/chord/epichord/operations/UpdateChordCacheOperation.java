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

package org.peerfact.impl.overlay.dht.chord.epichord.operations;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.epichord.components.EpiChordConfiguration;

/**
 * This Operation is called periodically to hold the chord cache up to date.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 */
public class UpdateChordCacheOperation extends
		AbstractChordOperation<Boolean> {

	AbstractChordNode masterNode;

	public UpdateChordCacheOperation(AbstractChordNode component) {
		super(component);
		this.masterNode = component;
	}

	@Override
	protected void execute() {
		
		// update cache
		ChordRoutingTable crt = ((ChordRoutingTable)masterNode.getChordRoutingTable());
		if(crt != null) {
			crt.getChordCache().update();
		}
		
		// reschedule
		UpdateChordCacheOperation op = new UpdateChordCacheOperation(
				super.getComponent());
		op.scheduleWithDelay(EpiChordConfiguration.CHORD_CACHE_UPDATE_INTERVAL);
		operationFinished(true);
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

}
