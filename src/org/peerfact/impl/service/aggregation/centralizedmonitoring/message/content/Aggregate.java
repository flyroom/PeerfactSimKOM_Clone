package org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content;

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
 * Aggregate of all values to given identifier. This class provides functions to
 * return maximum, minimum and average over all values.
 * 
 * @author Alexander Nigl
 * 
 * @param <ID>
 *            class of identifier
 */
public class Aggregate<ID extends Object> implements AggregationResult {
	protected ID identifier;

	protected double max;

	protected double min;

	protected int counter;

	protected double sum;

	protected double sumQuad;

	protected long minTime;

	protected long maxTime;

	protected long sumTime;

	/**
	 * 
	 * @param identifier
	 *            of aggregated value
	 */
	public Aggregate(ID identifier) {
		this.identifier = identifier;
		this.counter = 0;
		this.max = Double.NEGATIVE_INFINITY;
		this.min = Double.POSITIVE_INFINITY;
		this.sum = 0;
		this.sumQuad = 0;
		this.minTime = Long.MAX_VALUE;
		this.maxTime = Long.MIN_VALUE;
		this.sumTime = 0;
	}

	/**
	 * Adds aggregates together
	 * 
	 * @param agg
	 *            aggregate to add
	 */
	public void add(Aggregate<ID> agg) {
		// FIXME: throw correct exception
		assert (agg.identifier == this.identifier);
		this.sum += agg.sum;
		this.sumQuad += agg.sumQuad;
		this.counter += agg.counter;
		this.max = Math.max(this.max, agg.max);
		this.min = Math.min(this.min, agg.min);
	}

	/**
	 * Adds the given value.
	 * 
	 * @param value
	 *            of client
	 */
	public void addValue(double value, long updateTime) {
		this.counter++;
		this.sum += value;
		this.sumQuad += Math.pow(value, 2);
		this.max = Math.max(this.max, value);
		this.min = Math.min(this.min, value);
		this.maxTime = Math.max(this.maxTime, updateTime);
		this.minTime = Math.min(this.minTime, updateTime);
		this.sumTime += updateTime;
	}

	/**
	 * Returns the identifier of the aggregate
	 * 
	 * @return identifier
	 */
	public ID getIdentifier() {
		return this.identifier;
	}

	/**
	 * Returns the maximum of all collected values
	 * 
	 * @return maximum value of identifier
	 */
	@Override
	public double getMaximum() {
		if (this.counter == 0) {
			return Double.NaN;
		}
		return this.max;
	}

	/**
	 * Returns the minimum of all collected values
	 * 
	 * @return minimum value of identifier
	 */
	@Override
	public double getMinimum() {
		if (this.counter == 0) {
			return Double.NaN;
		}
		return this.min;
	}

	/**
	 * Returns number of values
	 * 
	 * @return number of values
	 */
	@Override
	public int getNodeCount() {
		return this.counter;
	}

	/**
	 * Returns sum over all values
	 * 
	 * @return sum of values
	 */
	public double getSum() {
		return this.sum;
	}

	/**
	 * Returns the size of the Information
	 * 
	 * @return Information Size in Bytes
	 */
	public static long getSize() {
		return (Integer.SIZE + 5 * Double.SIZE + 3 * Long.SIZE) / 8;
	}

	/**
	 * Calculates the average of all collected values
	 * 
	 * @return average value of identifier
	 */
	@Override
	public double getAverage() {
		if (this.counter > 0) {
			return this.sum / this.counter;
		} else {
			return Double.NaN;
		}
	}

	/**
	 * Returns standard deviation
	 * 
	 * @return standard deviation
	 */
	@Override
	public double getVariance() {
		return this.sumQuad - 2 * this.getAverage() * this.sum + this.counter
				* Math.pow(this.getAverage(), 2);

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
		return sumTime / counter;
	}

}
