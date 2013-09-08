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

package org.peerfact.impl.analyzer.visualization2d.metrics.overlay.generic;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;

/**
 * Metric, which simply displays an attribute.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 27.11.2008
 * 
 */
public abstract class ShowNodeAttrM extends OverlayNodeMetric {

	@Override
	public String getValue(VisOverlayNode node) {
		Object attr = node.getAttribute(getAttrIdentifier());

		if (attr != null) {
			return attr.toString();
		} else {
			return null;
		}
	}

	@Override
	public abstract String getName();

	protected abstract String getAttrIdentifier();

	@Override
	public abstract String getUnit();

	@Override
	public abstract boolean isNumeric();

}
