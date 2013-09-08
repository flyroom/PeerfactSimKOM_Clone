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

package org.peerfact.impl.overlay.dht.chord.base.components;

import java.math.BigInteger;
import java.util.LinkedHashMap;

import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.HashToolkit;


/**
 * 
 * This class creates ChordId instance by using SHA-1 Hash function.
 * 
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordIDFactory {

	private static final ChordIDFactory INSTANCE = new ChordIDFactory();

	private int nextInt = 0;

	private LinkedHashMap<BigInteger, Integer> map = new LinkedHashMap<BigInteger, Integer>();

	private ChordIDFactory() {
		// Private constructor prevents instantiation from other classes
	}

	public static ChordIDFactory getInstance() {
		return INSTANCE;
	}

	public static BigInteger createNewID(TransInfo transInfo) {
		// use SHA-1 Hash value of transInfo as id
		return HashToolkit.getSHA1Hash(transInfo.getNetId().toString(),
				ChordID.KEY_BIT_LENGTH);
	}

	public static ChordID createRandomChordID() {
		// use SHA-1 Hash value of transInfo as id
		BigInteger id = HashToolkit.getSHA1Hash(
				Integer.valueOf(Simulator
						.getRandom().nextInt()).toString(),
						ChordID.KEY_BIT_LENGTH);
		return new ChordID(id);
	}

	public static ChordID getChordID(String s) {
		BigInteger id = HashToolkit.getSHA1Hash(s,
				ChordID.KEY_BIT_LENGTH);
		return new ChordID(id);
	}

	public int getNewInt() {
		return this.nextInt++;
	}

	public int getIntForBigInt(BigInteger i) {
		int ret = 0;
		if (map.containsKey(i)) {
			ret = map.get(i);
		} else {
			ret = getNewInt();
			map.put(i, ret);
		}
		return ret;
	}

	public static ChordID getChordID(BigInteger id) {
		return new ChordID(id);
	}
}