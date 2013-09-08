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

package org.peerfact.impl.analyzer.visualization2d.gnuplot;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Results Table: Any Object as a column heading.
 * 
 * Time, Object1, Object2 ..... Object n t1,
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */

public class ResultTable {

	long[] t;

	LinkedHashMap<Object, String[]> metric_values = new LinkedHashMap<Object, String[]>();

	int length;

	public ResultTable(int length, final Collection<Object> obj) {
		this.length = length;
		t = new long[length];
		for (Object o : obj) {
			metric_values.put(o, new String[length]);
		}
	}

	/**
	 * Returns the objects that define the column heading.
	 * 
	 * @return
	 */
	public Collection<Object> getObjects() {
		return metric_values.keySet();
	}

	public void setTimeAt(int pos, long time) {
		t[pos] = time;
	}

	public long getTimeAt(int pos) {
		return t[pos];
	}

	public void setValueForAt(Object o, int pos, String value) {
		if (metric_values.get(o) == null) {
			throw new IllegalArgumentException(o
					+ " is not available in the table");
		}
		metric_values.get(o)[pos] = value;
	}

	public String getValueForAt(Object o, int pos) {
		return metric_values.get(o)[pos];
	}

}
