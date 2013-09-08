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
 *
 */
package org.peerfact.impl.overlay.informationdissemination.evaluation;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.ido.IDONode;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.api.overlay.ido.IDOOracle;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.analyzer.dbevaluation.AnalyzerOutputEntry;
import org.peerfact.impl.analyzer.dbevaluation.IAnalyzerOutputWriter;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.informationdissemination.NodeInfo;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulationEvent.Type;
import org.peerfact.impl.util.logging.SimLogger;
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
 * 
 * This class controls the statistic gathering process. It holds the reference
 * for the writers and is concerned with writing the statistic data on the one
 * hand and for scheduling new statistic generation events after the time that
 * is set in the constructor.
 * 
 * @author Christoph Muenker
 * @version 01/20/2011
 **/
public class StatisticGenerationEvent implements SimulationEventHandler {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger
			.getLogger(StatisticGenerationEvent.class);

	// table name
	private String TABLE_NAME = "statistics";

	/**
	 * The time interval between two samples.
	 */
	private long sampleStatisticRate;

	/**
	 * The analyzer of the received and sent message of the peers
	 */
	private EvaluationControlAnalyzer analyzer;

	/**
	 * Whether the generation of the statistic is active
	 */
	private boolean isActive = false;

	/**
	 * The IDO oracle for the IDO, that can be used to determined the ideal set
	 * of neighbors
	 */
	private IDOOracle oracle;

	/**
	 * The output writer for the metrics
	 */
	private IAnalyzerOutputWriter outputWriter;

	/**
	 * Assignment of IDs for the Host.
	 * <p>
	 * Is used from other classes
	 */
	public static Map<Host, Integer> hostIDs = new Hashtable<Host, Integer>();

	/**
	 * Sets the given parameter.
	 * 
	 * @param sampleStatisticRate
	 *            The time interval between two samples.
	 * @param analyzer
	 *            The analyzer of the received and sent message of the peers
	 * @param oracle
	 *            The IDO oracle for the IDO, that can be used to determined the
	 *            ideal set of neighbors
	 * @param outputWriter
	 *            The output writer for the metrics
	 */
	protected StatisticGenerationEvent(long sampleStatisticRate,
			EvaluationControlAnalyzer analyzer, IDOOracle oracle,
			IAnalyzerOutputWriter outputWriter) {
		this.sampleStatisticRate = sampleStatisticRate;
		this.analyzer = analyzer;
		this.oracle = oracle;
		this.outputWriter = outputWriter;
	}

	/**
	 * Add this event to the scheduler of the simulator.
	 */
	public void scheduleImmediatly() {
		Simulator.scheduleEvent(this, Simulator.getCurrentTime(), this,
				Type.STATUS);
	}

	/**
	 * Starts the measure of the metrics and initialize the output writer
	 */
	public void writerStarted() {
		String[] table = { TABLE_NAME };
		outputWriter.initialize(table);
		isActive = true;
	}

	/**
	 * Stops the measure of the metrics and close the output writer
	 */
	public void writerStopped() {
		isActive = false;
		outputWriter.flush();
	}

	/**
	 * Generates the metrics and add a new event to the scheduler.
	 */
	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getData() instanceof StatisticGenerationEvent) {

			log.debug(Simulator.getSimulatedRealtime()
					+ " Triggered statistic generation.");

			generateMetrics();
			outputWriter.flush();

			/*
			 * Schedule new STATUS event
			 */
			long scheduleAtTime = Simulator.getCurrentTime()
					+ sampleStatisticRate;
			Simulator.scheduleEvent(this, scheduleAtTime, this, Type.STATUS);

		}
	}

	/**
	 * Measure the metrics and write out the data.
	 */
	private void generateMetrics() {
		if (isActive) {
			LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> onlineNodes = getAllOnlineNodes();
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> onlineNodesIDMap = getOverlayIDMap(onlineNodes);

			// reset the oracle and fill it with the actually information
			oracle.reset();
			oracle.insertNodeInfos(getNodeInfos(onlineNodesIDMap));

			int numberOnlinePeers = MetricsComputation
					.computeNumberOfOnlinePeers(onlineNodes);
			double globaleConnectivity = MetricsComputation
					.computeGlobalConnectivity(onlineNodesIDMap);
			double globalConnectedComponentFactor = MetricsComputation
					.computeGlobalConnectedComponentFactor(onlineNodesIDMap);

			writeOut("numberOnlinePeers", numberOnlinePeers);
			writeOut("globaleConnectivity", globaleConnectivity);
			writeOut("globalConnectedComponentFactor",
					globalConnectedComponentFactor);

			for (Host host : onlineNodes.keySet()) {
				IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node = onlineNodes
						.get(host);

				Integer hostID = StatisticGenerationEvent.hostIDs.get(host);
				if (hostID == null) {
					StatisticGenerationEvent.hostIDs.put(host,
							StatisticGenerationEvent.hostIDs.size());
					hostID = StatisticGenerationEvent.hostIDs.get(host);
					log.info("No host identifiere for this host! The new hostID for this host is: "
							+ hostID);
				}

				int visionRange = MetricsComputation.computeVisionRange(node);
				Point2D centroid = MetricsComputation.computeCentroid(node,
						onlineNodesIDMap);
				Point ownPosition = MetricsComputation.computeOwnPosition(node);
				double nodeSpeed = MetricsComputation.computeNodeSpeed(node);

				double trafficDown = MetricsComputation.computeTraffic(node,
						analyzer.getReceivedMsgsPerPeer());
				double trafficUp = MetricsComputation.computeTraffic(node,
						analyzer.getSentMsgsPerPeer());
				double traffic = trafficDown + trafficUp;
				int msgCountDown = MetricsComputation.computeMsgsCount(node,
						analyzer.getReceivedMsgsPerPeer());
				int msgCountUp = MetricsComputation.computeMsgsCount(node,
						analyzer.getSentMsgsPerPeer());

				int[] confusionMatrix = MetricsComputation
						.computeConfusionMatrix(node, oracle);
				int truePositive = confusionMatrix[0];
				int falsePositive = confusionMatrix[1];
				int falseNegative = confusionMatrix[2];
				double precision = MetricsComputation.computePrecision(
						truePositive, falsePositive);
				double recall = MetricsComputation.computeRecall(truePositive,
						falseNegative);

				double positionError = MetricsComputation.computePositionError(
						node, onlineNodesIDMap);

				double dispersion = MetricsComputation.computeDispersion(node,
						centroid, onlineNodesIDMap);

				writeOut(hostID, "visionRange", visionRange);
				writeOut(hostID, "centroid_x", centroid.getX());
				writeOut(hostID, "centroid_y", centroid.getY());
				writeOut(hostID, "position_x", ownPosition.x);
				writeOut(hostID, "position_y", ownPosition.y);
				writeOut(hostID, "nodeSpeed", nodeSpeed);
				writeOut(hostID, "trafficDown", trafficDown);
				writeOut(hostID, "trafficUp", trafficUp);
				writeOut(hostID, "traffic", traffic);
				writeOut(hostID, "msgCountDown", msgCountDown);
				writeOut(hostID, "msgCountUp", msgCountUp);
				writeOut(hostID, "truePositive", truePositive);
				writeOut(hostID, "falsePositive", falsePositive);
				writeOut(hostID, "falseNegative", falseNegative);
				writeOut(hostID, "precision", precision);
				writeOut(hostID, "recall", recall);
				writeOut(hostID, "positionError", positionError);
				writeOut(hostID, "dispersion", dispersion);

			}
			// clear Msgs-storage
			analyzer.getReceivedMsgsPerPeer().clear();
			analyzer.getSentMsgsPerPeer().clear();
		}
	}

	/**
	 * Write the given data to the output writer.
	 * 
	 * @param hostId
	 *            An unique identifier for the peer.
	 * @param metric
	 *            The name of the metric
	 * @param value
	 *            The value of the metric
	 */
	private void writeOut(int hostId, String metric, Object value) {

		long time = Simulator.getCurrentTime();

		AnalyzerOutputEntry entry = new AnalyzerOutputEntry(hostId, time,
				metric, value);

		outputWriter.persist(TABLE_NAME, entry);
	}

	/**
	 * Write the given data to the output writer as general metric.
	 * 
	 * @param metric
	 *            The name of the metric
	 * @param value
	 *            The value of the metric
	 */
	private void writeOut(String metric, Object value) {

		long time = Simulator.getCurrentTime();

		AnalyzerOutputEntry entry = new AnalyzerOutputEntry(time, metric, value);

		outputWriter.persist(TABLE_NAME, entry);
	}

	/**
	 * Creates to every node in the given list, a {@link NodeInfo}.
	 * 
	 * @param nodes
	 *            A map of nodes.
	 * @return A list of NodeInfos.
	 */
	private static List<IDONodeInfo> getNodeInfos(
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodes) {
		List<IDONodeInfo> result = new Vector<IDONodeInfo>();
		for (OverlayID<?> id : nodes.keySet()) {
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node = nodes
					.get(id);
			if (node.getPeerStatus() == PeerStatus.PRESENT
					&& node.getHost().getNetLayer().isOnline()) {

				Point position = node.getPosition();
				int aoi = node.getAOI();

				result.add(new NodeInfo(position, aoi,
						(OverlayID<BigInteger>) id));
			}
		}
		return result;
	}

	/**
	 * Gets a map of all {@link IDONode}s, which are created in this simulation.
	 * 
	 * @return A map of {@link IDONode}.
	 */
	private static LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> getAllNodes() {

		LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> result = new LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>>();

		for (Host h : GlobalOracle.getHosts()) {
			OverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>> node =
					(OverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>>) h
							.getOverlay(IDONode.class);

			if (!hostIDs.containsKey(h)) {
				hostIDs.put(h, hostIDs.size());
			}

			if (node != null) {
				IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> idoNode = (IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>) node;
				result.put(h, idoNode);
			}
		}
		return result;
	}

	/**
	 * Gets {@link IDONode}s back, that think, they are connected with the
	 * Overlay.
	 * 
	 * @return A map of {@link IDONode}s.
	 */
	private static LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> getAllOnlineNodes() {

		LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> result = new LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>>();
		for (Host h : getAllNodes().keySet()) {
			OverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>> node = (OverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>>) h
					.getOverlay(IDONode.class);

			if (node != null) {
				IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> idoNode = (IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>) node;
				if (idoNode.isPresent()) {
					result.put(
							h,
							idoNode);
				}
			}
		}
		return result;
	}

	/**
	 * Change the key of the map. From {@link Host} to the {@link OverlayID}.
	 * 
	 * @param nodes
	 *            A map with key {@link Host} and value {@link IDONode}.
	 * @return A map with key {@link OverlayID} and value {@link IDONode}.
	 */
	private static LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> getOverlayIDMap(
			LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodes) {
		LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> result = new LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>>();
		for (Host h : nodes.keySet()) {
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node = nodes
					.get(h);
			result.put(node.getOverlayID(), node);
		}
		return result;
	}
}
