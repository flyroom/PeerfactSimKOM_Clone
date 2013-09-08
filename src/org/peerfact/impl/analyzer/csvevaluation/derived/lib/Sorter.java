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

package org.peerfact.impl.analyzer.csvevaluation.derived.lib;

import java.io.IOException;
import java.util.Iterator;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Sorter<S extends IYValueSet> {

	private IXYStream stream;

	SortedMap<Integer, S> elements;

	private IYValueSetFactory<S> factory;

	int xValsNaN = 0;

	int yValsNaN = 0;

	IScale scale;

	public Sorter(IXYStream stream, IScale scale, IYValueSetFactory<S> factory) {
		this.stream = stream;
		this.factory = factory;
		this.scale = scale;
	}

	public void sortIn() throws IOException {
		elements = new TreeMap<Integer, S>();
		IXY xy;
		while ((xy = stream.nextXY()) != null) {
			if (Double.isNaN(xy.getX())) {
				xValsNaN++;
			} else if (Double.isNaN(xy.getY())) {
				yValsNaN++;
			}
			else {
				addElement(xy);
			}
		}

	}

	private void addElement(IXY xy) {
		int index = scale.indexFromX(xy.getX());
		S values = elements.get(index);
		if (values == null) {
			values = factory.newIYValueSet();
			elements.put(index, values);
		}
		values.addValue(xy.getY());
	}

	public IResultStream<S> getResultStream() {
		return new ResultStream();
	}

	public class ResultStream implements IResultStream<S> {

		Iterator<Integer> it;

		public ResultStream() {
			it = elements.keySet().iterator();
		}

		@Override
		public IResultField<S> getNextField() {
			if (!it.hasNext()) {
				return null;
			}
			int i = it.next();
			return new ResultField(scale.xFromIndex(i), elements.get(i));
		}

	}

	class ResultField implements IResultField<S> {

		public double x;

		public S values;

		public ResultField(double x, S values) {
			this.x = x;
			this.values = values;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public S getYVals() {
			return values;
		}

	}

	public String printStats() {
		return "Numbers that were NaN: X: " + xValsNaN + ", Y: " + yValsNaN;
	}

}
