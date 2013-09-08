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

package org.peerfact.impl.overlay.dht.chord.rechord.vis;

import java.awt.Color;

import org.peerfact.impl.overlay.dht.chord.base.vis.AbstractChordIDM;


public class ChordIDM extends AbstractChordIDM {

	public ChordIDM() {
		setColor(new Color(255, 50, 100));
	}

	@Override
	protected String getAttrIdentifier() {
		return "ReChordID";
	}

	@Override
	public String getName() {
		return "ReChord-ID";
	}

	@Override
	public String getUnit() {
		return null;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

}
