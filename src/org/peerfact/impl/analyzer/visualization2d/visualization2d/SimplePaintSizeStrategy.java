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

package org.peerfact.impl.analyzer.visualization2d.visualization2d;

import java.util.LinkedHashMap;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.Metric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SimplePaintSizeStrategy implements IPaintSizeStrategy {

	public LinkedHashMap<Metric, Float> maxValueLookAhead = new LinkedHashMap<Metric, Float>();

	public LinkedHashMap<Metric, Float> minValueLookAhead = new LinkedHashMap<Metric, Float>();

	@Override
	public float computeStrokeFor(OverlayEdgeMetric m, VisOverlayEdge e,
			float normalSize, float maxSize) {

		if (m != null) {

			try {

				String mValue = m.getValue(e);

				if (mValue == null) {
					return normalSize;
				}

				float size = normalSize + getSizeFromMetricValue(m, mValue)
						* maxSize;

				return size;

			} catch (NumberFormatException ex) {
				return normalSize;
			}
		} else {
			return normalSize;
		}
	}

	@Override
	public int computeNodeSizeFor(OverlayNodeMetric m, VisOverlayNode n,
			int minSize, int maxSize) {

		if (m != null) {

			try {

				String mValue = m.getValue(n);

				if (mValue == null) {
					return minSize;
				}

				return (int) Math.rint(minSize
						+ getSizeFromMetricValue(m, mValue) * maxSize);

			} catch (NumberFormatException ex) {
				// Is no numeric value.
				return minSize;
			}
		} else {
			return minSize;
		}

	}

	public float getSizeFromMetricValue(Metric m, String metricValue)
			throws NumberFormatException {
		float float_val = Float.valueOf(metricValue);
		updateMaxValue(m, float_val);
		updateMinValue(m, float_val);

		float minVal = minValueLookAhead.get(m);
		float maxVal = maxValueLookAhead.get(m);

		return (float_val - minVal) / (maxVal - minVal);
	}

	private void updateMinValue(Metric m, float float_val) {
		if (!minValueLookAhead.containsKey(m)
				|| minValueLookAhead.get(m) > float_val) {
			minValueLookAhead.put(m, float_val);
		}
	}

	protected void updateMaxValue(Metric m, float float_val) {
		if (!maxValueLookAhead.containsKey(m)
				|| maxValueLookAhead.get(m) < float_val) {
			maxValueLookAhead.put(m, float_val);
		}
	}

}
