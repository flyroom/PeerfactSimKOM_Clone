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

package org.peerfact.impl.analyzer.visualization2d.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the behavior of a map that uses two keys to identify a
 * value.
 * 
 * @param <S>
 *            the class used as keys
 * @param <T>
 *            the class used as value
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MultiKeyMap<S, T> {

	/**
	 * The stored values.
	 * 
	 * Explanation: I used a <code>LinkedHashMap</code> to internally allow an
	 * efficient iteration of the sub-maps.
	 */
	LinkedHashMap<S, LinkedHashMap<S, T>> values;

	public MultiKeyMap() {
		values = new LinkedHashMap<S, LinkedHashMap<S, T>>();
	}

	/**
	 * Add a value to the map
	 * 
	 * @param k1
	 *            first key
	 * @param k2
	 *            second key
	 * @param value
	 */
	public void put(S k1, S k2, T value) {
		if (!values.containsKey(k1)) {
			values.put(k1, new LinkedHashMap<S, T>());
		}

		values.get(k1).put(k2, value);
	}

	/**
	 * Retrieve a value from the map
	 * 
	 * @param k1
	 *            the first key
	 * @param k2
	 *            the second key
	 * @return the value associated to the keys or <code>null</code> if there is
	 *         none
	 */
	public T get(S k1, S k2) {
		Map<S, T> m;
		if ((m = values.get(k1)) != null) {
			T v;
			if ((v = m.get(k2)) != null) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Remove a value from the map
	 * 
	 * @param k1
	 *            the first key
	 * @param k2
	 *            the second key
	 * @return the value that was associated to the keys or <code>null</code> if
	 *         there was none
	 */
	public T remove(S k1, S k2) {
		Map<S, T> m;
		if ((m = values.get(k1)) != null) {
			if (m.containsKey(k2)) {
				return m.remove(k2);
			}
		}
		return null;
	}

	/**
	 * Tells whether a value associated to two keys are contained within the map
	 * 
	 * @param k1
	 *            the first key
	 * @param k2
	 *            the second key
	 * @return <code>true</code> if there is a value associated to the keys,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(S k1, S k2) {
		Map<S, T> m;
		if ((m = values.get(k1)) != null) {
			if (m.containsKey(k2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes all entries from the map that match the first key and do not
	 * match any second key from the given set.
	 * 
	 * @param k1
	 *            the first key to match
	 * @param toKeep
	 *            the set of second keys not to match
	 * @return the set of values that where removed from the map
	 */
	public Set<T> removeComplementarySet(S k1, Set<S> toKeep) {
		List<S> toRemoveKeys = new LinkedList<S>();
		Set<T> removed = new LinkedHashSet<T>();
		LinkedHashMap<S, T> subMap = values.get(k1);

		if (subMap == null) {
			return removed;
		}

		// Find all entries that have to be removed
		for (Map.Entry<S, T> entry : subMap.entrySet()) {
			S key = entry.getKey();
			if (!toKeep.contains(key)) {
				toRemoveKeys.add(key);
			}
		}

		// Remove the entries
		for (S k : toRemoveKeys) {
			removed.add(subMap.remove(k));
		}

		return removed;
	}

}
