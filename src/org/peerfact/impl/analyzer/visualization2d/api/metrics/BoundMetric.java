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

package org.peerfact.impl.analyzer.visualization2d.api.metrics;

/**
 * A bound metric is a metric that returns its value for a node / edge one to
 * which it was bound, and thus does not require an explicit specification of a
 * node / an edge.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class BoundMetric extends Metric {

	protected Metric m;

	public BoundMetric(Metric m) {
		this.m = m;
	}

	/**
	 * Returns the value of the metric at the current time, depending on the
	 * object to which it was bound.
	 * 
	 * @return
	 */
	public abstract String getValue();

	@Override
	public String getName() {
		return m.getName();
	}

	@Override
	public boolean isActivated() {
		return m.isActivated();
	}

	@Override
	public void setActivated(boolean activated) {
		m.setActivated(activated);
	}

	@Override
	public String getUnit() {
		return m.getUnit();
	}

	@Override
	public boolean isNumeric() {
		return m.isNumeric();
	}

}
