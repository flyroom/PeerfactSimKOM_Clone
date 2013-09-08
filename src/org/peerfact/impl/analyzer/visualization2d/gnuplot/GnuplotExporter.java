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

package org.peerfact.impl.analyzer.visualization2d.gnuplot;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.controller.player.Player;
import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;


/**
 * Generated results tables from the data model.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnuplotExporter {

	/**
	 * Generated results tables from the specified bonded metrics towards time.
	 * Thereby determine start and end of the start and end point, interval the
	 * interval of sampling for the selected metrics.
	 * 
	 * @param metrics
	 *            specified bonded metrics towards time
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @param interval
	 *            the interval of sampling for the selected metrics
	 * @return
	 */
	public static ResultTable generateResultTable(
			Collection<BoundMetric> metrics,
			long start, long end, long interval) {

		VisDataModel.mute(true);

		Player pl = Controller.getPlayer();
		EventTimeline tl = Controller.getTimeline();
		VisDataModel mdl = Controller.getModel();

		if (pl.isPlaying()) {
			pl.pause();
		}

		long originalPosition = tl.getActualTime();

		int list_size = (int) ((end - start) / interval);

		Collection<Object> result_caption = new Vector<Object>();
		result_caption.addAll(metrics);
		ResultTable values = new ResultTable(list_size, result_caption);

		mdl.reset();
		tl.reset();

		long pos = start;

		for (int i = 0; i < list_size; i++) {
			// log.debug("Position: " + pos);
			tl.jumpToTime(pos);

			values.setTimeAt(i, pos / Player.TIME_UNIT_MULTIPLICATOR);

			for (BoundMetric m : metrics) {

				String mValue = m.getValue();
				if (mValue == null) {
					mValue = "0";
				}

				values.setValueForAt(m, i, mValue);
			}

			pos += interval;
		}

		mdl.reset();
		tl.reset();

		tl.jumpToTime(originalPosition);

		VisDataModel.mute(false);

		return values;
	}

	/**
	 * Generates a result table for the specified nodes towards time concerning
	 * the node metric. Thereby determine start and end of the start and end
	 * point, interval the interval of sampling for the selected metrics.
	 * 
	 * @param nodes
	 *            the specified nodes
	 * @param metric
	 *            the node metric
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @param interval
	 *            the interval of sampling for the selected metrics
	 * @return
	 */
	public static ResultTable generateOneMetricPeersVsTime(
			List<VisOverlayNode> nodes,
			OverlayNodeMetric metric, long start, long end, long interval) {

		VisDataModel.mute(true);

		Player pl = Controller.getPlayer();
		EventTimeline tl = Controller.getTimeline();
		VisDataModel mdl = Controller.getModel();

		if (pl.isPlaying()) {
			pl.pause();
		}

		long originalPosition = tl.getActualTime();

		int list_size = (int) ((end - start) / interval);

		Collection<Object> result_caption = new Vector<Object>();
		result_caption.addAll(nodes);
		ResultTable values = new ResultTable(list_size, result_caption);

		mdl.reset();
		tl.reset();

		long pos = start;

		for (int i = 0; i < list_size; i++) {
			tl.jumpToTime(pos);

			values.setTimeAt(i, pos / Player.TIME_UNIT_MULTIPLICATOR);

			for (VisOverlayNode n : nodes) {
				String mValue = metric.getValue(n);

				if (mValue == null)
				{
					mValue = "0"; // Behavior when a value is not defined: it is
					// set to 0.
				}

				values.setValueForAt(n, i, metric.getValue(n));
			}

			pos += interval;
		}

		mdl.reset();
		tl.reset();

		tl.jumpToTime(originalPosition);

		VisDataModel.mute(false);

		return values;
	}

	/**
	 * Generates a result table for the specified edges towards time concerning
	 * the edge metric. Thereby determine start and end of the start and end
	 * point, interval the interval of sampling for the selected metrics.
	 * 
	 * @param edges
	 *            the specified edges
	 * @param metric
	 *            the edge metric
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @param interval
	 *            the interval of sampling for the selected metrics
	 * @return
	 */
	public static ResultTable generateOneMetricConnVsTime(
			List<VisOverlayEdge> edges,
			OverlayEdgeMetric metric, long start, long end, long interval) {

		VisDataModel.mute(true);

		Player pl = Controller.getPlayer();
		EventTimeline tl = Controller.getTimeline();
		VisDataModel mdl = Controller.getModel();

		if (pl.isPlaying()) {
			pl.pause();
		}

		long originalPosition = tl.getActualTime();

		int list_size = (int) ((end - start) / interval);

		Collection<Object> result_caption = new Vector<Object>();
		result_caption.addAll(edges);
		ResultTable values = new ResultTable(list_size, result_caption);

		mdl.reset();
		tl.reset();

		long pos = start;

		for (int i = 0; i < list_size; i++) {
			tl.jumpToTime(pos);

			values.setTimeAt(i, pos / Player.TIME_UNIT_MULTIPLICATOR);

			for (VisOverlayEdge e : edges) {

				String mValue = metric.getValue(e);

				if (mValue == null)
				{
					mValue = "0"; // Behavior when a value is not defined: it is
					// set to 0.
				}

				values.setValueForAt(e, i, mValue);
			}

			pos += interval;
		}

		mdl.reset();
		tl.reset();

		tl.jumpToTime(originalPosition);

		VisDataModel.mute(false);

		return values;
	}

}
