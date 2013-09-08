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
import java.util.ArrayList;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.controller.player.Player;
import org.peerfact.impl.analyzer.visualization2d.model.events.Event;
import org.peerfact.impl.analyzer.visualization2d.model.events.MessageSent;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * Messages per second for edges, determined using a look-back strategy.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class EdgeMessagesPerSecond extends OverlayEdgeMetric {

	protected final String look_back_path = base_path + "/SecondsLookBack";

	/**
	 * Seconds, which looking into the past to calculate the number of past
	 * messages.
	 */
	final int SECONDS_LOOK_BACK = Config.getValue(look_back_path, 50);

	public EdgeMessagesPerSecond() {
		this.setColor(new Color(0, 128, 0));
	}

	@Override
	public String getValue(VisOverlayEdge edge) {
		long t = Controller.getTimeline().getActualTime();

		return String.valueOf((float) EdgeMessagesPerSecond.getEventCount(t
				- Player.TIME_UNIT_MULTIPLICATOR * SECONDS_LOOK_BACK
				, t, edge)
				/ (float) SECONDS_LOOK_BACK);

	}

	protected static long getEventCount(long begin, long end,
			VisOverlayEdge edge) {

		int count = 0;

		for (ArrayList<Event> l : Controller.getTimeline()
				.getMappedEventsBetween(begin, end).values()) {
			for (Event e : l) {
				if (e instanceof MessageSent) {
					MessageSent ms = (MessageSent) e;
					if (ms.getFrom() == edge.getNodeA()
							&& ms.getTo() == edge.getNodeB()) {
						count++;
					} else if (ms.getTo() == edge.getNodeA()
							&& ms.getFrom() == edge.getNodeB()) {
						count++;
					}
				}
			}
		}

		return count;
	}

	@Override
	public String getName() {
		return "Messages per second";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getUnit() {
		return "msg/s";
	}

	@Override
	public boolean isNumeric() {
		return true;
	}
}
