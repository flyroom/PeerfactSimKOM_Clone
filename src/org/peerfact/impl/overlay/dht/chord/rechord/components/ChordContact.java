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

import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;

/**
 * ChordContact encapsulates ChordId and Transport Address. This information is
 * used to contact between overlay nodes.
 * 
 * @author Minh Hoang Nguyen
 * 
 */
public class ChordContact extends AbstractChordContact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2807120460579662080L;

	private boolean realNode = true;

	public ChordContact(ChordID id, TransInfo transInfo, boolean realNode) {
		super(id, transInfo);
		this.realNode = realNode;
	}

	public boolean isRealNode() {
		return this.realNode;
	}

	public void setIsReal(boolean b) {
		this.realNode = b;
	}

	@Override
	public ChordContact clone() {
		ChordContact newContact = new ChordContact(super.getOverlayID(),
				super.getTransInfo(),
				this.realNode);
		newContact.setAlive(super.isAlive());
		return newContact;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ChordContact) {
			return equals((ChordContact) o);
		}
		return super.equals(o);
	}

}
