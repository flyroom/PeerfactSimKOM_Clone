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

package org.peerfact.impl.util.stats;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This class provides a statistical calculations for a collection of data.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 03/08/2011
 */
public class StatHelper<T extends Number & Comparable<T>> {

	/**
	 * Concats the given arrays of type Double to a new array.
	 * 
	 * @param arrays
	 *            the given arrays
	 * @return a new double array
	 */
	private static Double[] concat(Double[]... arrays) {
		int targetSize = 0;
		for (Double[] arr : arrays) {
			targetSize += arr.length;
		}

		Double[] newArr = new Double[targetSize];

		int nextStarting = 0;
		for (int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, newArr, nextStarting,
					arrays[i].length);

			nextStarting += arrays[i].length;
		}

		return newArr;
	}

	/**
	 * Derives the number of values for the given list of values.
	 * 
	 * @param values
	 *            A list of values
	 * @return The number of values. If the list null or the size is equals 0,
	 *         then return null.
	 */
	public static Integer count(List<?> values) {
		if (values == null || values.size() == 0) {
			return 0;
		}
		return values.size();
	}

	/**
	 * Derives the sum for the given list of values.
	 * 
	 * @param values
	 *            A list of values
	 * @return The sum. If the list null or the size is equals 0, then return
	 *         null.
	 */
	public Double sum(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}
		Double sum = new Double(0);
		for (T v : values) {
			sum += v.doubleValue();
		}
		return sum;
	}

	/**
	 * Derives the minimal value for the given list of values.
	 * 
	 * @param values
	 *            A list of values
	 * @return The minimal value. If the list null or the size is equals 0, then
	 *         return null.
	 */
	public T min(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}

		List<T> cpyValues = new Vector<T>(values);
		Collections.sort(cpyValues);
		return cpyValues.get(0);
	}

	/**
	 * Derives the maximal value for the given list of values.
	 * 
	 * @param values
	 *            A list of values
	 * @return The maximal value. If the list null or the size is equals 0, then
	 *         return null.
	 */
	public T max(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}

		List<T> cpyValues = new Vector<T>(values);
		Collections.sort(cpyValues);
		return cpyValues.get(cpyValues.size() - 1);
	}

	/**
	 * Derives the median for the given list of values.
	 * 
	 * @param values
	 *            A list of values.
	 * @return The median of the values. If the size of the list is equals 0 or
	 *         the list is null, then return null;
	 */
	public T median(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}

		List<T> cpyValues = new Vector<T>(values);
		Collections.sort(cpyValues);
		return cpyValues.get((int) Math.floor(cpyValues.size() / 2));
	}

	/**
	 * Derives the average for the list of values.
	 * 
	 * @param values
	 *            A list of doubles.
	 * @return The average of the values. If the size of the list equals 0, then
	 *         return null or the list is null
	 */
	public Double average(List<T> values) {
		return arithmeticMean(values);
	}

	/**
	 * Derives the average for the list of values.
	 * 
	 * @param values
	 *            A list of doubles.
	 * @return The average of the values. If the size of the list equals 0, then
	 *         return null or the list is null
	 */
	public Double arithmeticMean(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}
		double sum = sum(values);
		Double avg = sum / values.size();
		return avg;
	}

	/**
	 * Derives the standard deviation for the given list of values, with the
	 * arithmetic mean.
	 * 
	 * @param values
	 *            A list of values.
	 * @return The standard deviation of the values. If the size of the list is
	 *         smaller then 2 or the list is null, then return null;
	 */
	public Double standardDeviation(List<T> values) {
		if (values == null) {
			return null;
		}
		Double standardDeviation = computeStandardDeviation(values,
				arithmeticMean(values));
		return standardDeviation;
	}

	/**
	 * Derives the standard deviation for the given list of values to the given
	 * average. In detail the minus and plus deviation and the complete
	 * deviation are calculated.
	 * 
	 * @param values
	 *            A list of values.
	 * @param average
	 *            The average of the given list.
	 * @return The standard deviations of the values. If the size of the list is
	 *         smaller then 2 or the list is null, then return null;
	 */
	public Double[] standardDeviationDetail(List<T> values) {
		if (values == null) {
			return null;
		}
		Double[] standardDeviation = computeDetailStandardDeviation(values,
				arithmeticMean(values));
		return standardDeviation;
	}

	/**
	 * Derives the standard deviation for the given list of values to the given
	 * average.
	 * 
	 * @param values
	 *            A list of values.
	 * @param average
	 *            The average of the given list.
	 * @return The standard deviation of the values. If the size of the list is
	 *         smaller then 2 or the list is null, then return null;
	 */
	private Double computeStandardDeviation(List<T> values,
			Double average) {
		if (average == null) {
			return null;
		}
		double sumOfSquares = 0;
		Double standardDeviation = 0.0;
		if (values != null && values.size() > 1) {
			for (T dd : values) {
				Double ddMinusAvg = dd.doubleValue() - average;
				sumOfSquares += ddMinusAvg * ddMinusAvg;
			}
			standardDeviation = Math.sqrt(sumOfSquares / (values.size() - 1));
		} else {
			standardDeviation = null;
		}
		return standardDeviation;
	}

	/**
	 * Derives the standard deviation for the given list of values to the given
	 * average. In detail the minus and plus deviation and the complete
	 * deviation are calculated.
	 * 
	 * @param values
	 *            A list of values.
	 * @param average
	 *            The average of the given list.
	 * @return The standard deviations of the values. If the size of the list is
	 *         smaller then 2 or the list is null, then return null;
	 */
	private Double[] computeDetailStandardDeviation(List<T> values,
			Double average) {
		if (average == null) {
			return null;
		}

		Double standardDeviation = computeStandardDeviation(values, average);

		Double standardDeviationMinus = 0.0;
		Double standardDeviationPlus = 0.0;

		List<T> cpyValues = new Vector<T>(values);
		Collections.sort(cpyValues);

		for (int i = 0; i < cpyValues.size(); i++) {
			Double d = cpyValues.get(i).doubleValue();

			if (d >= average) {
				List<T> underAvg = cpyValues.subList(0, i);
				List<T> overAvg = cpyValues.subList(i + 1, cpyValues.size());

				/*
				 * Compute standard deviation for values under and over the
				 * average separately.
				 */
				double sumOfSquares = 0;
				for (T dUnder : underAvg) {
					sumOfSquares += (dUnder.doubleValue() - average)
							* (dUnder.doubleValue() - average);
				}
				if (underAvg.size() > 0) {
					standardDeviationMinus = Math.sqrt(sumOfSquares
							/ underAvg.size());
				}

				sumOfSquares = 0;
				for (T dOver : overAvg) {
					sumOfSquares += (dOver.doubleValue() - average)
							* (dOver.doubleValue() - average);
				}
				if (overAvg.size() > 0) {
					standardDeviationPlus = Math.sqrt(sumOfSquares
							/ overAvg.size());
				}

				break;
			}

		}

		return new Double[] { standardDeviation, standardDeviationMinus,
				standardDeviationPlus };
	}

	/**
	 * Derives the average and the detail standard deviation for the given list
	 * of values to the given average. In detail average, the minus and plus
	 * deviation and the complete deviation are calculated.
	 * 
	 * @param values
	 *            A list of values.
	 * @return The average and the standard deviations of the values. If the
	 *         size of the list is smaller then 2 or the list is null, then
	 *         return null;
	 */
	public Double[] computeAverageAndStandardDeviation(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}
		Double average = average(values);
		Double[] avgArr = { average };
		Double[] returnArr = concat(avgArr,
				computeDetailStandardDeviation(values, average));

		return returnArr;
	}

	/**
	 * Derives the truncated mean for the given list of values. For that it
	 * removes values which smaller then quantil alpha and greater then quantil
	 * (1-alpha). Over the other values will be derived the average.
	 * 
	 * @param values
	 *            A list of values.
	 * @param alpha
	 *            A value between 0 and 0.5.
	 * @return The truncated Mean of the values. If the list is null or it is
	 *         not possible to compute the truncated mean, then return null
	 */
	@SuppressWarnings("static-method")
	public Double truncatedMean(List<T> values, double alpha) {
		if (alpha < 0 && alpha > 0.5) {
			throw new IllegalArgumentException(
					"The alpha value isn't defined ("
							+ alpha
							+ "). The alpha value for truncated mean is defined for in interval [0,0.5] ∈ ℝ");
		}

		if (values == null || values.size() == 0) {
			return null;
		}

		List<T> cpyValues = new Vector<T>(values);
		Collections.sort(cpyValues);

		int k = (int) (cpyValues.size() * alpha);
		int start = k;
		int end = cpyValues.size() - k;
		Double sum = 0.0;
		for (int i = start; i < end; i++) {
			sum += cpyValues.get(i).doubleValue();
		}
		Double result = 0.0;
		if ((cpyValues.size() - 2 * k) == 0) {
			result = null;
		} else {
			result = sum / (cpyValues.size() - 2 * k);
		}
		return result;
	}

	/**
	 * Derives the quantil of the values, with the given p-quantil
	 * 
	 * @param values
	 *            A list of values
	 * @param p_quantil
	 *            The pth quantil. A value between [0,1]
	 * @return The p-quantil of the values. If the list is null or the size is
	 *         equals 0, then return null.
	 */
	public T quantile(List<T> values, double p_quantil) {
		if (p_quantil < 0 && p_quantil > 1) {
			throw new IllegalArgumentException(
					"The quantil isn't defined for the value "
							+ p_quantil
							+ ". The quantil is defined for in interval [0,1] ∈ ℝ");
		}

		if (values == null || values.size() == 0) {
			return null;
		}

		List<T> cpyValues = new Vector<T>(values);
		Collections.sort(cpyValues);

		T quantil = cpyValues.get((int) Math.floor(cpyValues.size()
				* p_quantil));
		return quantil;
	}

	/**
	 * Derives the geometric mean for the given values
	 * 
	 * @param values
	 *            A list of values
	 * @return The geometric mean for the values. If the list null or the size
	 *         is equals 0, then return null.
	 */
	public Double geometricMean(List<T> values) {
		if (values == null || values.size() == 0) {
			return null;
		}
		Double temp = 0.0;
		for (T v : values) {
			temp *= v.doubleValue();
		}
		return Math.pow(temp, 1.0 / values.size());
	}

	/**
	 * Derives the root mean square for the given values
	 * 
	 * @param values
	 *            A list of values
	 * @return The root mean square for the values. If the list null or the size
	 *         is equals 0, then return null.
	 */
	public Double rootMeanSquare(List<T> values) {
		return generalizedMean(values, 2);
	}

	/**
	 * Derives the cubic means for the given values
	 * 
	 * @param values
	 *            A list of values
	 * @return The cubic means for the values. If the list null or the size is
	 *         equals 0, then return null.
	 */
	public Double cubicMeans(List<T> values) {
		return generalizedMean(values, 3);
	}

	/**
	 * Derives the generalized mean, also known as power mean or Hölder mean for
	 * the given values. It computes root(sum(values^p)/values.size, p)
	 * 
	 * @param values
	 *            A list of values
	 * @param p
	 *            A real number, if not equals 0.
	 * @return The generalized mean for the values. If the list null or the size
	 *         is equals 0, then return null.
	 */
	public Double generalizedMean(List<T> values, double p) {
		if (p == 0) {
			throw new IllegalArgumentException(
					"The argument in p for generalizedMean cannot be 0.");
		}
		if (values == null || values.size() == 0) {
			return null;
		}
		Double squareSum = 0.0;
		for (T v : values) {
			squareSum += Math.pow(v.doubleValue(), p);
		}
		return Math.pow(squareSum / values.size(), 1.0 / p);
	}
}
