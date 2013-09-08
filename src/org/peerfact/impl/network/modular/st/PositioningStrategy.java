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

package org.peerfact.impl.network.modular.st;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;

/**
 * This strategy determines an abstract network position of a given host (like
 * 2-dimensional, geographical or GNP)
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface PositioningStrategy extends ModNetLayerStrategy {

	/**
	 * Returns the position of the host. Can be any object extending
	 * NetPosition. If the host is mobile, a position stub object may be
	 * returned, where the current position can be requested at any simulation
	 * time.
	 * 
	 * @param host
	 * @param db
	 * @param hostMeta
	 * @return
	 */
	public NetPosition getPosition(
			Host host,
			NetMeasurementDB db,
			NetMeasurementDB.Host hostMeta);

}
