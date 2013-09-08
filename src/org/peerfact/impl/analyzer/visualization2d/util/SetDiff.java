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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Compares two collections and returns the difference as a set.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 07.11.2008
 * 
 * @param <T>
 */
public class SetDiff<T> {

	Collection<T> a;

	Collection<T> b;

	/**
	 * Default constructor
	 * 
	 * @param setA
	 * @param setB
	 */
	public SetDiff(Collection<T> setA, Collection<T> setB) {
		this.a = setA;
		this.b = setB;
	}

	/**
	 * Returns all items that are in B but not in A. If B is null, an empty list
	 * is returned, if A is null, B is returned
	 * 
	 * @return
	 */
	public Collection<T> getItemsNotInA() {
		Set<T> diff = new LinkedHashSet<T>();
		if (b == null) {
			return diff;
		}
		if (a == null) {
			return b;
		}

		for (T item : b) {
			if (!a.contains(item)) {
				diff.add(item);
			}
		}
		return diff;
	}

	/**
	 * Returns all items that are in A but not in B. If A is null, an empty list
	 * is returned, if B is null, A is returned
	 * 
	 * @return
	 */
	public Collection<T> getItemsNotInB() {
		Set<T> diff = new LinkedHashSet<T>();
		if (a == null) {
			return diff;
		}
		if (b == null) {
			return a;
		}
		for (T item : a) {
			if (!b.contains(item)) {
				diff.add(item);
			}
		}
		return diff;
	}

}
