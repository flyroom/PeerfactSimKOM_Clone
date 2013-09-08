/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

/**
 * 
 */
package org.peerfact.impl.overlay.dht.chord.maliciouschord.operations;

import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;


/**
 * @author Tobias Wybranietz
 * @author Thim Strothmann
 */
public abstract class AbstractMaliciousLookupOperation extends LookupOperation {

	public AbstractMaliciousLookupOperation(AbstractChordNode component,
			ChordID target,
			OperationCallback<List<AbstractChordContact>> callback, int lookupId) {
		super(component, target, callback, lookupId);
	}

	@Override
	public void deliverResult(AbstractChordContact responsibleContact1,
			ChordID targetKey, int lookupOperationID, int hopCount) {
		lookupHopCount = hopCount;

		log.debug("lookup finish id = " + getLookupId() + " redo = "
				+ redoCounter);

		this.responsibleContact = responsibleContact1;

		if (!isFinished()) {
			masterNode.removeLookupOperation(getLookupId());
			operationFinished(true);
		}
	}

}
