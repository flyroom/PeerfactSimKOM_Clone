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

package org.peerfact.impl.util.evaluation.derived;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.ConfidenceIntervals;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.IScale;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.LogarithmicScale;
import org.peerfact.impl.util.logging.SimLogger;


public class ConfidenceIntervalsTest {
	private static Logger log = SimLogger
			.getLogger(ConfidenceIntervalsTest.class);

	public static void main(String[] args) {
		ConfidenceIntervals intvls = new ConfidenceIntervals(0.25d, 2);

		intvls.addValue(1);
		intvls.addValue(2);
		intvls.addValue(3);
		intvls.addValue(3);
		intvls.addValue(3);

		intvls.addValue(4);
		intvls.addValue(5);
		intvls.addValue(5);
		intvls.addValue(6);
		intvls.addValue(7);

		intvls.addValue(8);
		intvls.addValue(9);
		intvls.addValue(10);

		log.debug(intvls.printCaptionForFile());
		log.debug(intvls.printForFile());

		IScale sc = new LogarithmicScale(2);

		nearlyIdent(sc, 2);
		nearlyIdent(sc, 400);
		nearlyIdent(sc, 6000);
		nearlyIdent(sc, 250);
		nearlyIdent(sc, 200);
		nearlyIdent(sc, 60);
		nearlyIdent(sc, 288);
		nearlyIdent(sc, 800);
		nearlyIdent(sc, 20);
		nearlyIdent(sc, 222);
	}

	public static void nearlyIdent(IScale sc, double d) {
		log.debug(d + ", " + sc.xFromIndex(sc.indexFromX(d)));
	}

}
