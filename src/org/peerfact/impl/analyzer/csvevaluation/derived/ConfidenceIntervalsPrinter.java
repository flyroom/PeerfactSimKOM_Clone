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

package org.peerfact.impl.analyzer.csvevaluation.derived;

import java.io.File;
import java.io.IOException;

import org.peerfact.impl.analyzer.csvevaluation.derived.lib.ConfidenceIntervals;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.IScale;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.IYValueSetFactory;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.Parser;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.Printer;
import org.peerfact.impl.analyzer.csvevaluation.derived.lib.Sorter;


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
public class ConfidenceIntervalsPrinter {

	public static void printToFile(File input, File output, int colX, int colY,
			IScale scale) throws IOException {

		Parser parser = new Parser(input, colX, colY);

		IYValueSetFactory<ConfidenceIntervals> factory = new ConfidenceIntervals.ConfidenceIntervalsFactory(
				0.95f, 4);

		Sorter<ConfidenceIntervals> s = new Sorter<ConfidenceIntervals>(parser,
				scale, factory);

		s.sortIn();

		Printer printer = new Printer(s.getResultStream());

		printer.print(output);

	}

}
