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

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.util.logging.SimLogger;


public class CoordsTest {
	private static Logger log = SimLogger.getLogger(CoordsTest.class);

	public static void main(String[] args) {
		Coords a = new Coords(1, 0);
		Coords b = new Coords(1, 0);

		log.debug("Erster Test: " + !(a.equals(b)));

		Coords c = new Coords(1, 0);
		Coords d = new Coords(2, 0);

		log.debug("Zweiter Test: " + !(c.equals(d)));
	}

}
