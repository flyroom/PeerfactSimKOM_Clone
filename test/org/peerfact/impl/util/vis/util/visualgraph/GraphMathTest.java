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
 *
 */

package org.peerfact.impl.util.vis.util.visualgraph;

import java.awt.Point;
import java.awt.Rectangle;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.GraphMath;
import org.peerfact.impl.util.logging.SimLogger;


public class GraphMathTest {
	private static Logger log = SimLogger.getLogger(GraphMathTest.class);

	public static void main(String[] args) {

		// testA (50,100)
		doPoint(new Rectangle(0, 0, 100, 100), new Point(0, 50), new Point(100,
				150));
		// testB (0, 25)
		doPoint(new Rectangle(0, 0, 100, 100), new Point(-50, 0), new Point(50,
				50));
		// testC (50, 0)
		doPoint(new Rectangle(0, 0, 100, 100), new Point(50, -50), new Point(
				50, 50));
		// testD (100, 25)
		doPoint(new Rectangle(0, 0, 100, 100), new Point(200, 0), new Point(0,
				50));

		// testNoIntersect (null)
		doPoint(new Rectangle(0, 0, 100, 100), new Point(50, -100), new Point(
				200, 50));
		// testNoIntersect (null)
		doPoint(new Rectangle(0, 0, 100, 100), new Point(50, 200), new Point(
				200, 50));

	}

	public static void doPoint(Rectangle r, Point lineStart, Point lineEnd) {
		log.debug(GraphMath.getLineRectIntersection(lineStart,
				lineEnd, r));
	}

}
