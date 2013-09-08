/*
 * VAST, a scalable peer-to-peer network for virtual environments
 * Copyright (C) 2006 Shun-Yun Hu (syhu@yahoo.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.peerfact.impl.overlay.informationdissemination.von.voronoi;

import java.awt.geom.Point2D;

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
 * This class is used for the generation of the voronoi diagrams
 */
public class Point2d extends Point2D implements Comparable<Object> {
	public double x;

	public double y;

	public Point2d() {
		x = y = 0.0;
	}

	public Point2d(double X, double Y) {
		x = X;
		y = Y;
	}

	public Point2d(Point2d a) {
		x = a.x;
		y = a.y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double X, double Y) {
		x = X;
		y = Y;
	}

	@Override
	public int compareTo(Object o) {
		Point2d p = (Point2d) o;
		if (y < p.y) {
			return (-1);
		}
		if (y > p.y) {
			return (1);
		}
		if (x < p.x) {
			return (-1);
		}
		if (x > p.x) {
			return (1);
		}
		return (0);
	}

	@Override
	public Object clone() {
		Point2d clone = new Point2d(this.x, this.y);

		return clone;
	}

}
