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

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.controller.player.Player;
import org.peerfact.impl.analyzer.visualization2d.model.events.Event;
import org.peerfact.impl.analyzer.visualization2d.model.events.MessageSent;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;
import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * Messages per second for nodes, with a determined look-back strategy.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class NodeMessagesPerSecond extends OverlayNodeMetric {

	protected final String look_back_path = base_path + "/SecondsLookBack";

	/**
	 * Seconds, which looking into the past to calculate the number of past
	 * messages.
	 */
	final int SECONDS_LOOK_BACK = Config.getValue(look_back_path, 50);

	public NodeMessagesPerSecond() {
		this.setColor(new Color(240, 0, 0));
	}

	@Override
	public String getValue(VisOverlayNode node) {
		long t = Controller.getTimeline().getActualTime();

		return String.valueOf((float) NodeMessagesPerSecond.getEventCount(t
				- Player.TIME_UNIT_MULTIPLICATOR * SECONDS_LOOK_BACK, t, node)
				/ (float) SECONDS_LOOK_BACK);
	}

	protected static long getEventCount(long begin, long end,
			VisOverlayNode node) {

		int count = 0;

		for (ArrayList<Event> l : Controller.getTimeline()
				.getMappedEventsBetween(begin, end).values()) {
			for (Event e : l) {
				if (e instanceof MessageSent) {
					MessageSent ms = (MessageSent) e;
					if (ms.getFrom() == node || ms.getTo() == node) {
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
	public String getUnit() {
		return "msg/s";
	}

	@Override
	public boolean isNumeric() {
		return true;
	}

}
