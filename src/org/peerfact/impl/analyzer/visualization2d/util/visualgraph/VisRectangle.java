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

package org.peerfact.impl.analyzer.visualization2d.util.visualgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class VisRectangle implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2376709419959105951L;

	Color color;

	Point point1;

	Point point2;

	public VisRectangle(Point point1, Point point2, Color color) {
		this.point1 = point1;
		this.point2 = point2;
		this.color = color;
	}

	public Point getPoint1() {
		return point1;
	}

	/**
	 * @return the point2
	 */
	public Point getPoint2() {
		return point2;
	}

	public Dimension getDimension() {
		return new Dimension(point2.x - point1.x, point2.y - point1.y);
	}

	public Color getColor() {
		return color;
	}

	public void iterate(ModelIterator<?, ?, ?> it) {
		it.rectangleVisited(this);
	}

}
