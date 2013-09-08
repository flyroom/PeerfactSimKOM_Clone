/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.util.positioning;

import java.awt.Point;

/**
 * N-Dimensional Vector containing a Position
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/25/2011
 */
public class PositionVector {

	private int dimensions;

	private double[] values;

	/**
	 * 
	 * @param dimensions
	 */
	public PositionVector(int dimensions) {
		if (dimensions < 2) {
			throw new AssertionError("Less than 2 Dimensions make no sense.");
		}
		this.dimensions = dimensions;
		this.values = new double[dimensions];
	}

	public PositionVector(PositionVector vec) {
		this(vec.getDimensions());
		for (int i = 0; i < vec.getDimensions(); i++) {
			setEntry(i, vec.getEntry(i));
		}
	}

	/**
	 * Convenience Constructor, intializes a Vector with values.length
	 * Dimensions and sets Entries, using the callback setEntry
	 * 
	 * @param values
	 */
	public PositionVector(double[] values) {
		this(values.length);
		for (int i = 0; i < values.length; i++) {
			setEntry(i, values[i]);
		}
	}

	/**
	 * Number of Dimensions
	 * 
	 * @return
	 */
	public final int getDimensions() {
		return dimensions;
	}

	/**
	 * returns the nth position in the coord-Vector, starting with 0
	 * 
	 * @param dim
	 * @return
	 */
	public double getEntry(int dim) {
		return values[dim];
	}

	/**
	 * Saves a new value. Implementations might perform error control or
	 * additional scaling/translation
	 * 
	 * @param dim
	 * @param value
	 */
	public void setEntry(int dim, double value) {
		values[dim] = value;
	}

	/**
	 * This is called whenever a Movement-Model calculated a Movement, it
	 * contains the delta-vector for this step. Implementations may perform some
	 * kind of scaling or error control
	 * 
	 * @param vec
	 */
	public void add(PositionVector delta) {
		for (int i = 0; i < dimensions; i++) {
			setEntry(i, getEntry(i) + delta.getEntry(i));
		}
	}

	/**
	 * Calculate a Distance (Euclidean Space), can be overwritten
	 * 
	 * @param pv
	 * @return
	 */
	protected double calculateDistance(PositionVector pv) {
		if (pv.getDimensions() == getDimensions()) {
			double dist = 0;
			for (int i = 0; i < dimensions; i++) {
				dist += Math.pow((pv.getEntry(i) - getEntry(i)), 2);
			}
			return Math.sqrt(dist);
		} else {
			throw new AssertionError(
					"Can not compute distance between Vectors of different length!");
		}
	}

	/**
	 * Mainly for drawing purposes, Representation of the first two dimensions
	 * as a Point
	 * 
	 * @return
	 */
	public Point asPoint() {
		return new Point((int) getEntry(0), (int) getEntry(1));
	}

	/**
	 * Representation of the Vector as a Double-Array
	 * 
	 * @return
	 */
	public double[] asDoubleArray() {
		double[] ret = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			ret[i] = getEntry(i);
		}
		return ret;
	}

}
