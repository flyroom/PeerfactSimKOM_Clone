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

package org.peerfact.impl.analyzer.csvevaluation.distribution;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
 * Efficient implementation of IDistribution
 * 
 * @author <info@peerfact.org>
 * 
 * @param <Identifier>
 * @version 05/06/2011
 */
public class Distribution<Identifier> implements IDistribution<Identifier> {

	public SortedMap<Long, ValueAmount> valueAmounts = new TreeMap<Long, ValueAmount>();

	public Map<Identifier, Long> values = new LinkedHashMap<Identifier, Long>();

	@Override
	public void setValue(Identifier id, long value) {
		if (id == null) {
			throw new IllegalArgumentException(
					"The identifier must not be null.");
		}
		if (values.containsKey(id)) {
			long oldVal = values.get(id);
			values.put(id, value);
			changeValueAmount(oldVal, false);
			changeValueAmount(value, true);
		} else {
			values.put(id, value);
			changeValueAmount(value, true);
		}
	}

	private void changeValueAmount(long value, boolean increase) {

		ValueAmount valAmount = valueAmounts.get(value);

		if (valAmount != null) {
			if (increase) {
				valAmount.increase();
			} else {
				if (valAmount.getAmount() == 1) {
					valueAmounts.remove(value);
				} else {
					valAmount.decrease();
				}
			}
		} else {
			if (increase) {
				valueAmounts.put(value, new ValueAmount());
			} else {
				throw new IllegalStateException(
						"ValueAmount may not be decreased since it is 0");
			}
		}
	}

	@Override
	public void remove(Identifier id) {
		if (values.containsKey(id)) {
			changeValueAmount(values.get(id), false);
			values.remove(id);

		}
	}

	/*
	 * public void getAllDistributionParameters(IDistributionReceiver rcv) { int
	 * j = 0; for (int val : valueAmounts.keySet()) { ValueAmount amount =
	 * valueAmounts.get(val); for (int i = 0; i < amount.getAmount(); i++){
	 * rcv.receiveDistributionValue((double)j/values.size(), val); j++; } }
	 */

	@Override
	public DistResultStream getResultStream() {
		return new DistResultStream();
	}

	@Override
	public int getDistributionSize() {
		return values.size();
	}

	public class DistResultStream implements IDistResultStream {

		Iterator<Long> valuesIt;

		int valuesSize;

		long actualValue;

		int actualValueRemaining = -1;

		public DistResultStream() {
			valuesIt = valueAmounts.keySet().iterator();
			valuesSize = values.size();
		}

		@Override
		public int getDistSize() {
			return valuesSize;
		}

		@Override
		public long getNextValue() {
			if (actualValueRemaining > 0) {
				actualValueRemaining--;
				return actualValue;
			}
			actualValue = valuesIt.next();
			actualValueRemaining = valueAmounts.get(actualValue).getAmount() - 1;
			return actualValue;
		}

	}

	protected class ValueAmount {
		int amount = 1;

		public void increase() {
			amount++;
		}

		public void decrease() {
			amount--;
		}

		public int getAmount() {
			return amount;
		}
	}

}
