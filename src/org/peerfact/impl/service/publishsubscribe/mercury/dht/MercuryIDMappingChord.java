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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryAttributePrimitive;
import org.peerfact.impl.service.publishsubscribe.mercury.attribute.AttributeType;


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
 * ID-Mapping for Chord, translate between attribute value and OverlayID/Key
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryIDMappingChord implements MercuryIDMapping {

	BigDecimal maxValue = new BigDecimal("2").pow(ChordID.KEY_BIT_LENGTH);

	@Override
	public ChordKey map(MercuryAttributePrimitive attribute,
			Object value) {

		// TODO implement other types
		// FIXME wraparound for value == maxValue

		if (attribute.getType().equals(AttributeType.Integer)) {
			Integer range = (Integer) attribute.getMax()
					- (Integer) attribute.getMin();
			BigDecimal val = new BigDecimal(value.toString())
					.subtract(new BigDecimal(attribute.getMin().toString()));
			BigDecimal resultingKey = this.maxValue.multiply(val)
					.divideToIntegralValue(
							new BigDecimal(range.toString()));
			return new ChordKey(resultingKey.toBigInteger());
		} else {
			return null;
		}

	}

	@Override
	public Integer getInteger(MercuryAttributePrimitive attribute,
			OverlayID<?> id) {
		ChordID cid = (ChordID) id;
		if (attribute.getType().equals(AttributeType.Integer)) {
			Integer range = (Integer) attribute.getMax()
					- (Integer) attribute.getMin();
			BigDecimal resultingInt = new BigDecimal(range.toString())
					.multiply(new BigDecimal(cid.getUniqueValue()))
					.divideToIntegralValue(
							maxValue);
			return resultingInt.intValue();
		} else {
			return null;
		}
	}

	@Override
	public OverlayID<?> getNextID(OverlayID<?> id) {
		ChordID cid = (ChordID) id;
		return new ChordID(cid.getUniqueValue().add(new BigInteger("1")));
	}

}
