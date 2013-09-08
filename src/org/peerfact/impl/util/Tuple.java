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

package org.peerfact.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Tuple<T1, T2> {

	T1 a;

	T2 b;

	boolean changed;

	public Tuple(T1 a, T2 b) {
		super();
		this.a = a;
		this.b = b;
		this.changed = false;
	}

	public T1 getA() {
		return a;
	}

	public void setA(T1 a) {
		this.a = a;
		this.changed = true;
	}

	public T2 getB() {
		return b;
	}

	public void setB(T2 b) {
		this.b = b;
		this.changed = true;
	}

	public boolean isChanged() {
		return changed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Tuple<?, ?> other = (Tuple<?, ?>) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		return true;
	}

	public static <K, V> List<Tuple<K, V>> tupleListFromMap(
			Map<K, V> map) {
		List<Tuple<K, V>> l = new ArrayList<Tuple<K, V>>(map.size());
		for (Entry<K, V> e : map.entrySet()) {
			l.add(new Tuple<K, V>(e.getKey(), e.getValue()));
		}
		return l;
	}

	public static <K, V1, V2> List<Tuple<K, V2>> transformSecondArgumentInList(
			List<Tuple<K, V1>> sourceList, Transformer<V1, V2> tr) {

		List<Tuple<K, V2>> newLst = new ArrayList<Tuple<K, V2>>(
				sourceList.size());
		for (Tuple<K, V1> s : sourceList) {
			newLst.add(new Tuple<K, V2>(s.getA(), tr.transform(s.getB())));
		}
		return newLst;
	}

}
