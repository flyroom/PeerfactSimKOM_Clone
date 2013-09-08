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

package org.peerfact.impl.analyzer.visualization2d.model;

import java.util.Vector;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;
import org.peerfact.impl.analyzer.visualization2d.util.gui.IconObject;


/**
 * An object of the data model, such as a node or an edge that has metrics.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public interface MetricObject extends IconObject {

	/**
	 * Returns a set of metrics for this object in the form attached to it.
	 * 
	 * @return
	 */
	public Vector<BoundMetric> getBoundMetrics();

	public void iterate(ModelIterator it);

}
