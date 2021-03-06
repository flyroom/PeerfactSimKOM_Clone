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

package org.peerfact.impl.service.publishsubscribe.mercury.dht;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryAttributePrimitive;

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
 * This class maps an attribute value to the ID-Range of the given DHT-Overlay.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface MercuryIDMapping {

	/**
	 * get correct OverlayKey for a Attribute-Value-Combination
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public DHTKey<?> map(MercuryAttributePrimitive attribute, Object value);

	/**
	 * get the attribute Integer representation of a given OverlayID.
	 * 
	 * @param attribute
	 * @param id
	 * @return
	 */
	public Integer getInteger(MercuryAttributePrimitive attribute,
			OverlayID<?> id);

	/**
	 * id + 1, may be more complex for other Overlays?
	 * 
	 * @param id
	 * @return
	 */
	public OverlayID<?> getNextID(OverlayID<?> id);

}
