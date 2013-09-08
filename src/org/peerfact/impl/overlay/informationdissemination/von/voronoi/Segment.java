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
public class Segment {
	public Point2d p1 = new Point2d();

	public Point2d p2 = new Point2d();

	public Segment(double x1, double y1, double x2, double y2) {
		p1.x = x1;
		p1.y = y1;
		p2.x = x2;
		p2.y = y2;
	}

	public Segment(Point2d a, Point2d b) {
		p1.x = a.x;
		p1.y = a.y;
		p2.x = b.x;
		p2.y = b.y;
	}

	public void set(Segment s) {
		p1 = s.p1;
		p2 = s.p2;
	}

	boolean is_inside(Point2d p) {
		double xmax, xmin, ymax, ymin;

		if (p1.x > p2.x) {
			xmax = p1.x;
			xmin = p2.x;
		} else {
			xmax = p2.x;
			xmin = p1.x;
		}
		if (p1.y > p2.y) {
			ymax = p1.y;
			ymin = p2.y;
		} else {
			ymax = p2.y;
			ymin = p1.y;
		}
		if (xmin <= p.x && p.x <= xmax && ymin <= p.y && p.y <= ymax) {
			return true;
		} else {
			return false;
		}
	}

	boolean intersects(Point2d p3, int radius) {
		double u;

		// we should re-order p1 and p2's position such that p2 > p1
		double x1, x2, y1, y2;
		if (p2.x > p1.x) {
			x1 = p1.x;
			y1 = p1.y;
			x2 = p2.x;
			y2 = p2.y;
		} else {
			x1 = p2.x;
			y1 = p2.y;
			x2 = p1.x;
			y2 = p1.y;
		}

		// formula from
		// http://astronomy.swin.edu.au/~pbourke/geometry/sphereline/
		u = ((p3.x - x1) * (x2 - x1) + (p3.y - y1) * (y2 - y1))
				/ ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		if (u >= 0 && u <= 1) {
			double x = x1 + (x2 - x1) * u;
			double y = y1 + (y2 - y1) * u;

			boolean result = ((new Point2d(x, y).distance(p3) <= radius) ? true
					: false);

			return result;
		} else {
			return false;
		}
	}
}
