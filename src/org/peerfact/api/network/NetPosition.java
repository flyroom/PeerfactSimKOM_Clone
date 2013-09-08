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

package org.peerfact.api.network;

/**
 * Abstract position of a peer in the space. Concrete implementations implement
 * different models, e.g. simple 2d space, geographical coordinates,
 * latitude/longitude etc.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */
public interface NetPosition {

	/**
	 * Returns the "distance" between the local NetPosition instance and a
	 * remote NetPosition. The exact meaning of the term "distance" depends on
	 * the context of the concrete implementation of this interface
	 * 
	 * @param netPosition
	 *            the given remote NetPosition
	 * @return the distance between the local NetPosition and the remote
	 *         NetPosition
	 */
	public double getDistance(NetPosition netPosition);
}
