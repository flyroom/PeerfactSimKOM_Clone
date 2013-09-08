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

package org.peerfact.impl.analyzer.visualization2d.visualization2d.clicking;

import java.awt.Point;
import java.awt.Rectangle;

import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ClickBox {

	Rectangle bounds;

	MetricObject linkedObj = null;

	public ClickBox(Rectangle bounds) {
		super();
		this.bounds = bounds;
	}

	public ClickBox(int startX, int startY, int endX, int endY) {
		this(new Rectangle(startX, startY, endX, endY));
	}

	public boolean clicked(Point clickPoint) {
		return bounds.contains(clickPoint);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public MetricObject getLinkedObj() {
		return linkedObj;
	}

	public void setLinkedObj(MetricObject linkedObj) {
		this.linkedObj = linkedObj;
	}

}
