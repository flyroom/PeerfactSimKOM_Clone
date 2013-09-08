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

package org.peerfact.impl.application.kbrapplication;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.Document;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Document used in the KBRApplication
 * 
 * @author Julius Ruckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KBRDocument implements Document<OverlayKey<?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8863273128882090836L;

	OverlayKey<?> key;

	/**
	 * @param key
	 *            the key of the document
	 */
	public KBRDocument(OverlayKey<?> key) {
		this.key = key;
	}

	@Override
	public OverlayKey<?> getKey() {
		return key;
	}

	@Override
	public int getPopularity() {
		return 0;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public State getState() {
		return null;
	}

	@Override
	public void setKey(OverlayKey<?> key) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void setPopularity(int popularity) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void setSize(long newSize) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void setState(State state) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public Document<OverlayKey<?>> copy() {
		return new KBRDocument(this.key);
	}

}
