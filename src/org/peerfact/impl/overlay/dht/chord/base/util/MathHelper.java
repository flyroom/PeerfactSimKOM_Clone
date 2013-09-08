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

import java.math.BigInteger;

import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;


/**
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MathHelper {

	private static final BigInteger maxValue = new BigInteger("2")
			.pow(ChordID.KEY_BIT_LENGTH);

	/**
	 * @param nodeId
	 * @param entryIndex
	 * @return finger point start value
	 */
	public static BigInteger getFingerStartValue(BigInteger nodeId,
			int entryIndex) {
		// return n + 2^(k)
		BigInteger powIndex = new BigInteger("2").pow(entryIndex);
		BigInteger result = powIndex.add(nodeId);
		result = result.mod(maxValue);
		return result;
	}
}
