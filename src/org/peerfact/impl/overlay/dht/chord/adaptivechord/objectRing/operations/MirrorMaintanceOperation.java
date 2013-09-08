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

import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNodeType;
import org.peerfact.impl.overlay.dht.chord.base.operations.AbstractChordOperation;

/**
 * This Operation is called periodically to maintain the mirrors: remote nodes
 * who hold mirrors of documents assigned to this node are updated and documents
 * that are mirrored on this node are deleted if no update was received for some
 * time.
 * 
 * @author wette
 * @version 1.0, 06/21/2011
 */
public class MirrorMaintanceOperation extends AbstractChordOperation<Boolean> {

	ChordNode owner;

	public MirrorMaintanceOperation(ChordNode component) {
		super(component);
		this.owner = component;
	}

	@Override
	protected void execute() {

		if (owner.getNodeType() != ChordNodeType.DATANETNODE) {
			return;
		}

		// check if the remaining current upload capabilitys are sufficient to
		// get some of the
		// mirrored objects back to this node:
		owner.getMirroredObjectsBack();

		// for each document i gave away tell the mirror that he has to keep the
		// document as
		// i am not capable of serving the load this document generates
		// should one of the mirrors not answer on my message i assume the
		// mirror is down - so i take
		// the document back and delete this mirror from my list.
		owner.contactAllMirrorHolders();

		// check for all documents i am holding a mirror for if i have heared
		// from the original node in the last time
		// if i did not hear from a node --> delete the mirrored object!
		owner.removeMirroredObjectsIfHostDown();

		this.operationFinished(true);

		// reschedule this operation.
		MirrorMaintanceOperation newop = new MirrorMaintanceOperation(owner);
		newop.scheduleWithDelay(ChordConfiguration.CHECK_MIRROR_INTERVAL);
	}

	@Override
	public Boolean getResult() {
		return this.isSuccessful();
	}

}
