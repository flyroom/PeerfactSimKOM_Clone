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

package org.peerfact.impl.overlay.dht.chord.base.util;

import java.io.Serializable;
import java.util.Comparator;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;


public class ChordContactGreaterThanComparator implements
		Comparator<OverlayContact<ChordID>>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4576567602433205795L;

	@Override
	public int compare(OverlayContact<ChordID> arg0,
			OverlayContact<ChordID> arg1) {

		if (arg0 == null) {
			return -1;
		}
		if (arg1 == null) {
			return 1;
		}

		ChordID x = arg0.getOverlayID();
		ChordID y = arg1.getOverlayID();

		return y.compareTo(x);
	}

}
