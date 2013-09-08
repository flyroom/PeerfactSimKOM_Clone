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

/**
 * Unified interface for Position-Distributions based on PositionVectors. When
 * used, it has to be initialized with the correct number of dimensions used by
 * the vectors.
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/26/2011
 */
public abstract class PositionDistribution {

	private int dimensions;

	/**
	 * Define the dimensions this coordinate-Space should use
	 * 
	 * @param dim
	 */
	public void setDimensions(int dim) {
		this.dimensions = dim;
	}

	protected int getDimensions() {
		return dimensions;
	}

	/**
	 * Returns a n-Dimensional PositionVector, where n is the number of
	 * dimensions set with setDimensions()
	 * 
	 * @return
	 */
	public abstract PositionVector getNextPosition();

}
