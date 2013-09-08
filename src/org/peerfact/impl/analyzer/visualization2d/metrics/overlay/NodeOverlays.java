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

package org.peerfact.impl.analyzer.visualization2d.metrics.overlay;

import java.awt.Color;
import java.util.List;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;


/**
 * Name of the overlay / overlays, which uses the node and are visualized. (Use
 * node attribute "overlay")
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class NodeOverlays extends OverlayNodeMetric {

	public NodeOverlays() {
		this.setColor(new Color(100, 200, 0));
	}

	@SuppressWarnings("static-method")
	protected String getAttrKeyName() {
		return "overlay";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getValue(VisOverlayNode node) {
		Object attr = node.getAttribute(getAttrKeyName());

		if (attr != null && attr instanceof List) {
			return printCommaSeparated((List<Object>) attr);
		} else if (attr != null) {
			return attr.toString();
		} else {
			return null;
		}
	}

	/**
	 * Returns a list as a string, where the elements are separated by a comma.
	 * For this object is toString() is used.
	 * 
	 * @param attr
	 * @return
	 */
	private static String printCommaSeparated(List<Object> attr) {
		StringBuffer buf = new StringBuffer();
		boolean setComma = false;
		for (Object o : attr) {
			if (setComma) {
				buf.append(", ");
			}
			buf.append(o.toString());
			setComma = true;
		}
		return buf.toString();
	}

	@Override
	public String getName() {
		return "Used overlays";
	}

	@Override
	public String getUnit() {
		return "";
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

}
