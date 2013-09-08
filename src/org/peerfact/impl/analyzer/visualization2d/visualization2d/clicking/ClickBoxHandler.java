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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ClickBoxHandler {

	List<ClickBox> boxes = new LinkedList<ClickBox>();

	public void addBox(ClickBox box) {
		boxes.add(box);
	}

	public MetricObject getClickedObject(Point clickPoint) {
		ListIterator<ClickBox> it = boxes.listIterator(boxes.size());

		while (it.hasPrevious()) {
			ClickBox elem = it.previous();
			if (elem.clicked(clickPoint)) {
				return elem.getLinkedObj();
			}
		}
		return null;
	}

	public boolean clickedOnABox(Point clickPoint) {
		for (ClickBox box : boxes) {
			if (box.clicked(clickPoint)) {
				return true;
			}
		}
		return false;
	}

}
