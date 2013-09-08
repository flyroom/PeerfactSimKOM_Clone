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

package org.peerfact.impl.analyzer;

import java.io.IOException;
import java.util.List;

import org.peerfact.Constants;
import org.peerfact.impl.simengine.Simulator;

/**
 * Abstract analyzer class as superclass for all string related analyzers. It
 * handles the printing and formatting of the output.
 * 
 * The concrete analyzers have to implement the abstract methods
 * generateHeadlineForMetrics() to define the headline, resetEvaluationMetrics()
 * to reset measurements and generateEvaluationMetrics() to get the
 * measurements.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 07/20/2011
 */
public abstract class AbstractFileStringAnalyzer extends AbstractFileAnalyzer {

	@Override
	protected void initialize() throws IOException {
		// Write head line
		List<String> headlines = generateHeadlineForMetrics();
		output.write(Constants.COMMENT_LINE);
		output.write("time [sec]");
		output.write("time [min]");
		for (String field : headlines) {
			output.write(field + Constants.SEPARATOR);
		}
		output.write(Constants.LINE_END);
		// Flush output after each line if needed
		if (flushEveryLine) {
			output.flush();
		}

		resetEvaluationMetrics();
	}

	@Override
	protected void doEvaluation(long currentTime) throws IOException {
		// Write the time
		output.write(Long.valueOf(currentTime
				/ Simulator.SECOND_UNIT).toString() + Constants.SEPARATOR);
		output.write(Long.valueOf(Long.valueOf(currentTime
				/ Simulator.MINUTE_UNIT).toString()) + Constants.SEPARATOR);

		// Output the generated line string
		List<String> measurements = generateEvaluationMetrics(currentTime);

		for (String field : measurements) {
			output.write(field + Constants.SEPARATOR);
		}
		output.write(Constants.LINE_END);

		// Flush output after each line if needed
		if (flushEveryLine) {
			output.flush();
		}

		resetEvaluationMetrics();
	}

	@Override
	protected void doFinalEvaluation() throws IOException {
		// nothing to do
	}

	/**
	 * Generates a string that is prepended to the metric file
	 * 
	 * NOTE: Could be used to include a description of the data columns.
	 * 
	 * @return a list of strings to be prepended
	 */
	protected abstract List<String> generateHeadlineForMetrics();

	/**
	 * Resets all data for next evaluation round.
	 */
	protected abstract void resetEvaluationMetrics();

	/**
	 * Generates a string, containing the metrics to be plotted.
	 * 
	 * NOTE: Values should be converted to string and they should not contain
	 * any separators or line breaks.
	 * 
	 * @param currentTime
	 *            the current simulation time
	 * @return a list of metrics
	 */
	protected abstract List<String> generateEvaluationMetrics(long currentTime);

}
