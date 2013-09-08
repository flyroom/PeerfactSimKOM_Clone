/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.overlay.informationdissemination.evaluation;

import java.awt.Point;
import java.io.Writer;
import java.util.LinkedHashMap;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.ido.IDONode;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.analyzer.dbevaluation.AnalyzerOutputEntry;
import org.peerfact.impl.analyzer.dbevaluation.IAnalyzerOutputWriter;
import org.peerfact.impl.analyzer.dbevaluation.IOutputWriterDelegator;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.oracle.GlobalOracle;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class SinglePointMovementAnalyzer implements Analyzer,
		SimulationEventHandler, IOutputWriterDelegator {

	private static String TABLE_NAME = "statistics";

	private static String DISTANCE_SINGLE_POINT = "distance_single_point";

	/**
	 * The measurement interval
	 */
	private long measurementInterval = -1;

	/**
	 * The output writer
	 */
	private IAnalyzerOutputWriter outputWriter;

	/**
	 * Flag for the analyzer for activity.
	 */
	private boolean active = false;

	/**
	 * X coordinate for the single point for Movement
	 */
	private int pointX = -1;

	/**
	 * Y coordinate for the single point for Movement
	 */
	private int pointY = -1;

	//
	// Setter for Configuration
	//

	public void setMeasurementInterval(long interval) {
		this.measurementInterval = interval;
	}

	public void setPointX(int x) {
		this.pointX = x;
	}

	public void setPointY(int y) {
		this.pointY = y;
	}

	//
	// For Collecting data and write out
	//

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (active) {

			writeDistanceToMiddlePoint();
			outputWriter.flush();

			/*
			 * Schedule new STATUS event
			 */
			long scheduleAtTime = Simulator.getCurrentTime()
					+ measurementInterval;
			Simulator.scheduleEvent(this, scheduleAtTime, this, null);
		}
	}

	private void writeDistanceToMiddlePoint() {
		if (pointX == -1 || pointY == -1) {
			throw new RuntimeException("pointX or pointY is not set!");
		}

		Point singlePoint = new Point(pointX, pointY);
		LinkedHashMap<Host, IDONode<?, ?>> onlineNodes = getAllOnlineNodes();
		long time = Simulator.getCurrentTime();

		for (Host host : onlineNodes.keySet()) {
			IDONode<?, ?> node = onlineNodes.get(host);
			double dist = singlePoint.distance(node.getPosition());
			outputWriter.persist(TABLE_NAME, new AnalyzerOutputEntry(
					StatisticGenerationEvent.hostIDs.get(host), time,
					DISTANCE_SINGLE_POINT, dist));
		}
	}

	/**
	 * Gets {@link IDONode}s back, that think, they are connected with the
	 * Overlay.
	 * 
	 * @return A map of {@link IDONode}s.
	 */
	private static LinkedHashMap<Host, IDONode<?, ?>> getAllOnlineNodes() {

		LinkedHashMap<Host, IDONode<?, ?>> result = new LinkedHashMap<Host, IDONode<?, ?>>();
		for (Host h : getAllNodes().keySet()) {
			OverlayNode<?, ?> node = h.getOverlay(IDONode.class);

			if (node != null) {
				IDONode<?, ?> idoNode = (IDONode<?, ?>) node;
				if (idoNode.isPresent()) {
					result.put(h, idoNode);
				}
			}
		}
		return result;
	}

	/**
	 * Gets a map of all {@link IDONode}s, which are created in this simulation.
	 * 
	 * @return A map of {@link IDONode}.
	 */
	private static LinkedHashMap<Host, IDONode<?, ?>> getAllNodes() {

		LinkedHashMap<Host, IDONode<?, ?>> result = new LinkedHashMap<Host, IDONode<?, ?>>();

		for (Host h : GlobalOracle.getHosts()) {
			OverlayNode<?, ?> node = h.getOverlay(IDONode.class);

			if (!StatisticGenerationEvent.hostIDs.containsKey(h)) {
				StatisticGenerationEvent.hostIDs.put(h,
						StatisticGenerationEvent.hostIDs.size());
			}

			if (node != null) {
				IDONode<?, ?> idoNode = (IDONode<?, ?>) node;
				result.put(h, idoNode);
			}
		}
		return result;
	}

	@Override
	public void start() {
		if (measurementInterval == -1) {
			throw new RuntimeException("measurementInterval is not set!");
		}

		outputWriter.initialize(TABLE_NAME);
		active = true;
		// schedule immediatly
		Simulator.scheduleEvent(this, Simulator.getCurrentTime(), this, null);
	}

	@Override
	public void stop(Writer output) {
		active = false;
		outputWriter.flush();
	}

	/**
	 * Will be set from Monitor!
	 */
	@Override
	public void setAnalyzerOutputWriter(
			IAnalyzerOutputWriter analyzerOutputWriter) {
		this.outputWriter = analyzerOutputWriter;
	}
}
