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

package org.peerfact.impl.service.aggregation.skyeye.metrics;

import org.peerfact.api.service.aggr.AggregationResult;

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
 * This class defines the functionality and the appearance of an aggregate. As
 * the aggregates are mainly used in the context of metrics, we refer to the
 * class of an aggregate as <code>MetricsAggregate</code>. Within the
 * <code>MetricsAggregate</code>, we specify the name of the metric as well as
 * the different aggregate functions (e.g. min, max, sum etc).
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class MetricsAggregate implements AggregationResult {

	private final String aggregateName;

	private final double minValue;

	private final double maxValue;

	private final double sumOfAggregates;

	private final double sumOfSquares;

	private final int numberOfAggrElem;

	private final long minTime;

	private final long maxTime;

	private final long avgTime;

	/**
	 * Dummy, sets all values = 0
	 * 
	 * @param aggregateName
	 * @param time
	 */
	public MetricsAggregate(String aggregateName, Long time) {
		this.aggregateName = aggregateName;
		this.minValue = Double.MAX_VALUE;
		this.maxValue = 0;
		this.sumOfAggregates = 0;
		this.sumOfSquares = 0;
		this.numberOfAggrElem = 1;
		this.minTime = time;
		this.maxTime = time;
		this.avgTime = time;
	}

	public MetricsAggregate(String aggregateName, double minValue,
			double maxValue, double sumOfAggregates, double sumOfSquarres,
			int numberOfAggrElem, long minTime, long maxTime, long avgTime) {
		this.aggregateName = aggregateName;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.sumOfAggregates = sumOfAggregates;
		this.sumOfSquares = sumOfSquarres;
		this.numberOfAggrElem = numberOfAggrElem;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.avgTime = avgTime;
	}

	/**
	 * This method returns the name of the metric, which is represented by this
	 * <code>MetricsAggregate</code>.
	 * 
	 * @return the name of the represented metric
	 */
	public String getAggregateName() {
		return aggregateName;
	}

	/**
	 * This method returns the minimal value of the aggregated metric.
	 * 
	 * @return the minimal value
	 */
	@Override
	public double getMinimum() {
		return minValue;
	}

	/**
	 * This method returns the maximal value of the aggregated metric.
	 * 
	 * @return the maximal value
	 */
	@Override
	public double getMaximum() {
		return maxValue;
	}

	/**
	 * This method returns the sum of all values of the aggregated metric.
	 * 
	 * @return the sum of all values
	 */
	public Double getSumOfAggregates() {
		return sumOfAggregates;
	}

	/**
	 * This method returns the sum of squares of all values of the aggregated
	 * metric.
	 * 
	 * @return the sum of squares of all values
	 */
	public Double getSumOfSquares() {
		return sumOfSquares;
	}

	/**
	 * This method returns the counted amount of values, which are integrated
	 * within the metric.
	 * 
	 * @return the counted amount of aggregated values
	 */
	@Override
	public int getNodeCount() {
		return numberOfAggrElem;
	}

	/**
	 * This method returns the average value of the aggregated metric.
	 * 
	 * @return the average value
	 */
	@Override
	public double getAverage() {
		if (numberOfAggrElem == 0) {
			return 0d;
		}
		return sumOfAggregates / numberOfAggrElem;
	}

	/**
	 * This method returns the standard deviation of the aggregated metric.
	 * 
	 * @return the standard deviation
	 */
	public Double getStandardDeviation() {
		double count = numberOfAggrElem;
		double sumSquares = sumOfSquares;

		if (count == 0) {
			return 0d;
		}
		double avg = getAverage();
		double squareAverage = avg * avg;
		double averageSquare = sumSquares / count;
		return Math.sqrt((averageSquare - squareAverage));
	}

	@Override
	public double getVariance() {
		double count = numberOfAggrElem;
		double sumSquares = sumOfSquares;
		if (count == 0) {
			return 0d;
		}
		double avg = getAverage();
		double squareAverage = avg * avg;
		double averageSquare = sumSquares / count;
		return (averageSquare - squareAverage);
	}

	@Override
	public long getMinTime() {
		return minTime;
	}

	@Override
	public long getMaxTime() {
		return maxTime;
	}

	@Override
	public long getAvgTime() {
		return avgTime;
	}

}
