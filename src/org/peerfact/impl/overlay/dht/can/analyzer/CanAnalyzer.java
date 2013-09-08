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

package org.peerfact.impl.overlay.dht.can.analyzer;

import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.AbstractFileStringAnalyzer;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.components.CanConfig;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayID;
import org.peerfact.impl.overlay.dht.can.messages.CanMessage;
import org.peerfact.impl.overlay.dht.can.messages.LookupMsg;
import org.peerfact.impl.overlay.dht.can.messages.LookupReplyMsg;
import org.peerfact.impl.overlay.dht.can.messages.PingMsg;
import org.peerfact.impl.overlay.dht.can.messages.PongMsg;
import org.peerfact.impl.overlay.dht.can.messages.StoreMsg;
import org.peerfact.impl.overlay.dht.can.messages.StoreReplyMsg;
import org.peerfact.impl.overlay.dht.can.operations.LookupOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.oracle.GlobalOracle;
import org.peerfact.impl.util.stats.StatHelper;


public class CanAnalyzer extends AbstractFileStringAnalyzer implements
		NetAnalyzer, OperationAnalyzer, TransAnalyzer {

	final static Logger log = SimLogger
			.getLogger(CanAnalyzer.class);

	private long receivedBytes, sentBytes;

	private int receivedMsg, sentMsg;

	private List<Integer> hopCount = new LinkedList<Integer>();

	private List<Long> timeForHops = new LinkedList<Long>();

	private int lookupStoreMsg;

	private int leaveJoinTakeoverMsg;

	private int stabilizeMsg;

	private int startedLookup;

	private int receivedBackLookup;

	private LinkedHashMap<BigInteger, Integer> lookupValues = new LinkedHashMap<BigInteger, Integer>();

	StatHelper<Double> stats = new StatHelper<Double>();

	public CanAnalyzer() {
		setFlushEveryLine(true);
		setOutputFileName("Can");
	}

	@Override
	protected List<String> generateHeadlineForMetrics() {
		List<String> fieldNames = new LinkedList<String>();
		fieldNames.add("NumOfOnlinePeers");
		fieldNames.add("AverageDriftDistanceAvg");
		fieldNames.add("AverageDriftDistanceMin");
		fieldNames.add("AverageDriftDistanceMax");
		fieldNames.add("AverageDriftDistanceStDev");
		fieldNames.add("AverageDriftDistanceStDevUnderAvg");
		fieldNames.add("AverageDriftDistanceStDevOverAvg");
		fieldNames.add("AverageDriftDistanceMedian");
		fieldNames.add("AverageStaleNeighborRatio ");
		fieldNames.add("NumerLeaveJoinTakeoverPerMin");
		fieldNames.add("NumerLeaveJoinTakeoverPerMinPerNode");
		fieldNames.add("NumerStabilizePerMin");
		fieldNames.add("NumerStabilizePerMinPerNode");
		fieldNames.add("NumerLookupStorePerMin");
		fieldNames.add("NumerLookupStorePerMinPerNode");
		fieldNames.add("hopsAvg");
		fieldNames.add("medianHops");
		fieldNames.add("hopsNumberstandardDeviation");
		fieldNames.add("hopsNumberstandardDeviationMinus");
		fieldNames.add("hopsNumberstandardDeviationPlus");
		fieldNames.add("hopsTimeAvg all in ms");
		fieldNames.add("medianHopsTime");
		fieldNames.add("hopsTimeStandardDeviation");
		fieldNames.add("hopsTimestandardDeviationMinus");
		fieldNames.add("hopsTimestandardDeviationPlus");
		fieldNames.add("sentMsg");
		fieldNames.add("receivedMsg");
		fieldNames.add("sendDataPerMin");
		fieldNames.add("sendDataPerMinPerNode");
		fieldNames.add("receivedDataPerMin");
		fieldNames.add("receivedDataPerMin");
		fieldNames.add("NeighboursAvg");
		fieldNames.add("medianNeighboursTime");
		fieldNames.add("NeighboursTimeStandardDeviation");
		fieldNames.add("NeighboursTimestandardDeviationMinus");
		fieldNames.add("NeighboursTimestandardDeviationPlus");
		fieldNames.add("NeighboursAvgWithoutEdge");
		fieldNames.add("sendMsgPerMin");
		fieldNames.add("sendMsgPerMinPerNode");
		fieldNames.add("receivedMsgPerMin");
		fieldNames.add("receivedMsgPerMinPerNode");
		return fieldNames;
	}

	@Override
	protected void resetEvaluationMetrics() {
		receivedBytes = 0;
		sentBytes = 0;
		receivedMsg = 0;
		sentMsg = 0;
		hopCount.clear();
		timeForHops.clear();
		lookupStoreMsg = 0;
		leaveJoinTakeoverMsg = 0;
		stabilizeMsg = 0;
		startedLookup = 0;
		receivedBackLookup = 0;
		lookupValues.clear();
		receivedBytes = 0;

	}

	@Override
	protected List<String> generateEvaluationMetrics(long currentTime) {
		LinkedHashMap<CanOverlayID, CanNode> nodes = getAllCanNodes();

		double[] driftDistance = computeCurrentDriftDistance(nodes);

		double[] leaveJoinTakeover = numberLeaveJoinTakeover(nodes);

		double[] stabilizeMsgArray = numberStabilizeMsg(nodes);

		double[] LookupStore = numberLookupStoreMsg(nodes);

		double[] hops = hops(nodes);

		double[] hopsTime = transferTimeAvg(nodes);

		double sentLookupMsg = sendLookupMsg(nodes);

		double receivedLookupMsg = receivedLookupMsg(nodes);

		double[] receivedBytes2 = getReceivedBytes(nodes);

		double[] sendBytes = getSentBytes(nodes);

		double[] numberNeighbours = numberNeighbours(nodes);

		double[] sentMsgs = getSentMsg(nodes);

		double[] receivedMsgs = getReceivedMsg(nodes);

		List<String> measurements = new LinkedList<String>();
		measurements.add(Double.valueOf(
				computeNumOfOnlinePeers(getAllCanNodes()))
				.toString());
		measurements.add(Double.valueOf(driftDistance[0]).toString());
		measurements.add(Double.valueOf(driftDistance[1]).toString());
		measurements.add(Double.valueOf(driftDistance[2]).toString());
		measurements.add(Double.valueOf(driftDistance[3]).toString());
		measurements.add(Double.valueOf(driftDistance[4]).toString());
		measurements.add(Double.valueOf(driftDistance[5]).toString());
		measurements.add(Double.valueOf(driftDistance[6]).toString());
		measurements.add(Double.valueOf(
				computeStaleContactRatio(getAllCanNodes())).toString());
		measurements.add(Double.valueOf(leaveJoinTakeover[0]).toString());
		measurements.add(Double.valueOf(leaveJoinTakeover[1]).toString());
		measurements.add(Double.valueOf(stabilizeMsgArray[0]).toString());
		measurements.add(Double.valueOf(stabilizeMsgArray[1]).toString());
		measurements.add(Double.valueOf(LookupStore[0]).toString());
		measurements.add(Double.valueOf(LookupStore[1]).toString());
		measurements.add(Double.valueOf(hops[0]).toString());
		measurements.add(Double.valueOf(hops[1]).toString());
		measurements.add(Double.valueOf(hops[2]).toString());
		measurements.add(Double.valueOf(hops[3]).toString());
		measurements.add(Double.valueOf(hops[4]).toString());
		measurements.add(Double.valueOf(hopsTime[0]).toString());
		measurements.add(Double.valueOf(hopsTime[1]).toString());
		measurements.add(Double.valueOf(hopsTime[2]).toString());
		measurements.add(Double.valueOf(hopsTime[3]).toString());
		measurements.add(Double.valueOf(hopsTime[4]).toString());
		measurements.add(Double.valueOf(sentLookupMsg).toString());
		measurements.add(Double.valueOf(receivedLookupMsg).toString());
		measurements.add(Double.valueOf(sendBytes[0]).toString());
		measurements.add(Double.valueOf(sendBytes[1]).toString());
		measurements.add(Double.valueOf(receivedBytes2[0]).toString());
		measurements.add(Double.valueOf(receivedBytes2[1]).toString());
		measurements.add(Double.valueOf(numberNeighbours[0]).toString());
		measurements.add(Double.valueOf(numberNeighbours[1]).toString());
		measurements.add(Double.valueOf(numberNeighbours[2]).toString());
		measurements.add(Double.valueOf(numberNeighbours[3]).toString());
		measurements.add(Double.valueOf(numberNeighbours[4]).toString());
		measurements.add(Double.valueOf(numberNeighbours[5]).toString());
		measurements.add(Double.valueOf(sentMsgs[0]).toString());
		measurements.add(Double.valueOf(sentMsgs[1]).toString());
		measurements.add(Double.valueOf(receivedMsgs[0]).toString());
		measurements.add(Double.valueOf(receivedMsgs[1]).toString());

		return measurements;
	}

	/**
	 * Gets all peers from the globalOracle
	 * 
	 * @return all peers
	 */
	private static LinkedHashMap<CanOverlayID, CanNode> getAllCanNodes() {

		LinkedHashMap<CanOverlayID, CanNode> allCanNodes = new LinkedHashMap<CanOverlayID, CanNode>();

		for (Host h : GlobalOracle.getHosts()) {
			OverlayNode<?, ?> node = h.getOverlay(CanNode.class);

			if (node != null) {
				CanNode cNode = (CanNode) node;
				allCanNodes.put(cNode.getCanOverlayID(), cNode);
			}
		}
		return allCanNodes;
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		sentBytes += msg.getSize();
		sentMsg++;
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		receivedBytes += msg.getSize();
		receivedMsg++;
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// not used
	}

	/**
	 * gives the bytes of all received messages
	 * 
	 * @param globalNodes
	 *            all nodes in the CAN
	 * @return double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getReceivedBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		double outputPerMin = receivedBytes * Simulator.MINUTE_UNIT
				/ timeBetweenAnalyzeSteps;
		double outputPerMinPerNode = outputPerMin / globalNodes.size();
		receivedBytes = 0;
		double[] returnvalue = { outputPerMin, outputPerMinPerNode };
		return returnvalue;
	}

	/**
	 * gives the number of all received messages
	 * 
	 * @param globalNodes
	 *            all nodes in the CAN
	 * @return double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getReceivedMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		double outputPerMin = receivedMsg * Simulator.MINUTE_UNIT
				/ timeBetweenAnalyzeSteps;
		double outputPerMinPerNode = outputPerMin / globalNodes.size();
		receivedMsg = 0;

		double[] returnvalue = { outputPerMin, outputPerMinPerNode };
		return returnvalue;
	}

	/**
	 * gives the bytes of all received messages
	 * 
	 * @param globalNodes
	 *            all nodes in the CAN
	 * @return double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getSentBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		double outputPerMin = sentBytes * Simulator.MINUTE_UNIT
				/ timeBetweenAnalyzeSteps;
		double outputPerMinPerNode = outputPerMin / globalNodes.size();
		sentBytes = 0;

		double[] returnvalue = { outputPerMin, outputPerMinPerNode };
		return returnvalue;
	}

	/**
	 * gives the number of all send messages
	 * 
	 * @param globalNodes
	 *            all nodes in the CAN
	 * @return double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getSentMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		double outputPerMin = sentMsg * Simulator.MINUTE_UNIT
				/ timeBetweenAnalyzeSteps;
		double outputPerMinPerNode = outputPerMin / globalNodes.size();
		sentMsg = 0;

		double[] returnvalue = { outputPerMin, outputPerMinPerNode };
		return returnvalue;
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		if (op instanceof LookupOperation) {
			LookupOperation lookupOp = (LookupOperation) op;
			BigInteger value = lookupOp.getTarget().getId();
			boolean saved = false;
			for (BigInteger request : lookupValues.keySet()) {
				if (request == value) {
					int lookups = lookupValues.get(request);
					lookupValues.remove(request);
					lookupValues.put(request, lookups + 1);
					saved = true;
					break;
				}
			}
			if (saved == false) {
				lookupValues.put(value, 1);
			}
		}

	}

	@Override
	public void operationFinished(Operation<?> op) {
		if (op instanceof LookupOperation) {
			LookupOperation lookupOp = (LookupOperation) op;
			this.hopCount.add(lookupOp.getLookupHopCount());
			this.timeForHops.add(lookupOp.getDuration());
		}
	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		// not used

	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {
		Message message = msg.getPayload();
		if (message instanceof CanMessage) {
			CanMessage canMessage = (CanMessage) message;
			if (canMessage instanceof LookupMsg
					|| canMessage instanceof LookupReplyMsg
					|| canMessage instanceof StoreMsg
					|| canMessage instanceof StoreReplyMsg) {
				lookupStoreMsg++;
			} else if (canMessage instanceof PingMsg
					|| canMessage instanceof PongMsg) {
				stabilizeMsg++;
			} else {
				leaveJoinTakeoverMsg++;
			}
			if (canMessage instanceof LookupReplyMsg) {
				receivedBackLookup++;
			}

		}
	}

	/**
	 * Compute the drift distance. The difference between the observed postion
	 * and the real position.
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return double[] returnArr = { driftDistanceAvg, minDriftDistance,
	 *         maxDriftDistance, standardDeviation, standardDeviationMinus,
	 *         standardDeviationPlus, median };
	 */
	public double[] computeCurrentDriftDistance(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		int numOfNodesToConsider = globalNodes.size();
		double minDriftDistance = Double.MAX_VALUE;
		double maxDriftDistance = 0;

		double[] realNeighbor = new double[globalNodes.size()];
		for (int i = 0; i < realNeighbor.length; i++) {
			realNeighbor[i] = 0;
		}

		ArrayList<Double> driftDistances = new ArrayList<Double>();

		double driftDistanceAvgSum = 0;

		for (CanOverlayID id : globalNodes.keySet()) {
			CanNode node = globalNodes.get(id);

			if (node.getLocalOverlayContact() == null
					|| node.getPeerStatus() != PeerStatus.PRESENT
					|| node.getHost().getNetLayer().isOffline()) {
				/*
				 * We do not want to consider this node as it is not part of the
				 * overlay and therefore the computation of the drift distance
				 * does not make any sense.
				 */
				numOfNodesToConsider--;
				continue;
			}
			List<CanOverlayContact> neighborsInfo = node.getNeighbours();

			double localDriftDistanceSum = 0;

			int numOfNeighbors = neighborsInfo.size();

			for (CanOverlayContact neighbor : neighborsInfo) {
				CanOverlayID nId = neighbor.getOverlayID();

				if (globalNodes.get(nId) == null
						|| globalNodes.get(nId).getPeerStatus() != PeerStatus.PRESENT
						|| globalNodes.get(nId).getHost().getNetLayer()
								.isOffline()) {
					/*
					 * We do not want to consider this neighbor as it is not
					 * there and a drift distance can not be computed.
					 */
					numOfNeighbors--;
					continue;
				}

				int[] observedPos = neighbor.getArea().getArea();
				Point observerdPosPoint = new Point(observedPos[1]
						- observedPos[0], observedPos[3] - observedPos[2]);

				int[] realPos = globalNodes.get(nId).getLocalOverlayContact()
						.getArea().getArea();
				Point realPosPoint = new Point(realPos[1] - realPos[0],
						realPos[3] - realPos[2]);

				double distance = observerdPosPoint.distance(realPosPoint.x,
						realPosPoint.y);

				if (distance < minDriftDistance) {
					minDriftDistance = distance;
				}

				if (distance > maxDriftDistance) {
					maxDriftDistance = distance;
				}

				localDriftDistanceSum += distance;

				driftDistances.add(new Double(distance));
			}
			if (numOfNeighbors != 0) {
				double localDriftDistanceAvg = localDriftDistanceSum
						/ numOfNeighbors;

				driftDistanceAvgSum += localDriftDistanceAvg;
			}
		}

		double driftDistanceAvg = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;
		double median = 0;

		if (numOfNodesToConsider != 0) {
			driftDistanceAvg = driftDistanceAvgSum / numOfNodesToConsider;

			if (driftDistances != null && !driftDistances.isEmpty()) {
				median = stats.median(driftDistances);
			}

			/*
			 * Compute standard deviation
			 */
			double sumSquareDDMinusAvg = 0;
			if (driftDistances.size() > 0) {
				for (Double dd : driftDistances) {

					double ddMinusAvg = dd - driftDistanceAvg;
					sumSquareDDMinusAvg += ddMinusAvg * ddMinusAvg;
				}
				standardDeviation = Math.sqrt(sumSquareDDMinusAvg
						/ driftDistances.size());
			}

			Collections.sort(driftDistances);
			for (int i = 0; i < driftDistances.size(); i++) {
				Double d = driftDistances.get(i);

				if (d >= driftDistanceAvg) {
					List<Double> underAvg = driftDistances.subList(0, i);
					List<Double> overAvg = driftDistances.subList(i + 1,
							driftDistances.size());

					/*
					 * Compute standard deviation for values under and over the
					 * average separately.
					 */
					double sumOfSquares = 0;
					for (Double dUnder : underAvg) {
						sumOfSquares += (dUnder - driftDistanceAvg)
								* (dUnder - driftDistanceAvg);
					}
					if (underAvg.size() > 0) {
						standardDeviationMinus = Math.sqrt(sumOfSquares
								/ underAvg.size());
					}

					sumOfSquares = 0;
					for (Double dOver : overAvg) {
						sumOfSquares += (dOver - driftDistanceAvg)
								* (dOver - driftDistanceAvg);
					}
					if (overAvg.size() > 0) {
						standardDeviationPlus = Math.sqrt(sumOfSquares
								/ overAvg.size());
					}

					break;
				}

			}

		}

		else {
			maxDriftDistance = 0;
			minDriftDistance = 0;
		}

		standardDeviationMinus = driftDistanceAvg - standardDeviationMinus;
		standardDeviationPlus += driftDistanceAvg;

		double[] returnArr = { driftDistanceAvg, minDriftDistance,
				maxDriftDistance, standardDeviation, standardDeviationMinus,
				standardDeviationPlus, median };

		return returnArr;
	}

	/**
	 * Computes the stale contact ration. Therefore it measure the non valid
	 * neighbours and give the ration between the valid and the non valid.
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return ratio between non valid and valid neighbours. max=1
	 */
	public static double computeStaleContactRatio(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		globalNodes.size();
		int overallStaleNeighborSum = 0;
		int overallNeighborSum = 0;

		for (CanOverlayID id : globalNodes.keySet()) {
			CanNode node = globalNodes.get(id);

			if (node.getPeerStatus() != PeerStatus.PRESENT) {
				continue;
			}

			if (node.getLocalOverlayContact() == null) {
				continue;
			}

			List<CanOverlayContact> neighbors = node.getNeighbours();

			if (neighbors.size() == 0) {
				continue;
			}

			overallNeighborSum += neighbors.size();

			for (CanOverlayContact neighbour : node.getNeighbours()) {
				for (CanOverlayID id2 : globalNodes.keySet()) {
					CanNode node2 = globalNodes.get(id2);

					if (neighbour
							.getOverlayID()
							.toString()
							.equals(
									node2.getLocalOverlayContact()
											.getOverlayID().toString())
							&& (!neighbour.getArea().toString().equals(
									node2.getLocalOverlayContact().getArea()
											.toString())
							|| (node2
									.getPeerStatus() != PeerStatus.PRESENT))) {
						overallStaleNeighborSum++;
					}
				}
			}

		}
		double overallStaleNeighborRatio = 0;

		if (overallNeighborSum > 0) {
			overallStaleNeighborSum = overallNeighborSum
					- overallStaleNeighborSum;
		}
		overallStaleNeighborRatio = (double) overallStaleNeighborSum
				/ overallNeighborSum;

		return overallStaleNeighborRatio;
	}

	/**
	 * Gives the number of online peers.
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return
	 */
	public static double computeNumOfOnlinePeers(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		int numOfOnlinePeers = 0;

		for (CanNode node : globalNodes.values()) {

			if (node.getPeerStatus() == PeerStatus.PRESENT
					&& node.getHost().getNetLayer().isOnline()) {
				numOfOnlinePeers++;
			}
		}
		return numOfOnlinePeers;
	}

	/**
	 * Gives the number of leave, join and takeover messages.
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return output[] = {outputPerMin, outputPerMinPerNode}
	 */
	public double[] numberLeaveJoinTakeover(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] returnvalue = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			outputPerMin = leaveJoinTakeoverMsg;
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ timeBetweenAnalyzeSteps;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			returnvalue[0] = outputPerMin;
			returnvalue[1] = outputPerMinPerNode;
		}
		return returnvalue;

	}

	/**
	 * Gives the number of stabilize messages (ping and pong).
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return output[] = {outputPerMin, outputPerMinPerNode}
	 */
	public double[] numberStabilizeMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] returnvalue = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			outputPerMin += stabilizeMsg;
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ timeBetweenAnalyzeSteps;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			returnvalue[0] = outputPerMin;
			returnvalue[1] = outputPerMinPerNode;
		}
		return returnvalue;

	}

	/**
	 * Gives the number of lookup and store messages as well as the replies.
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return output[] = {outputPerMin, outputPerMinPerNode}
	 */
	public double[] numberLookupStoreMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] returnvalue = { 0, 0 };
		if (globalNodes.size() != 0) {
			double outputPerMin = 0, outputPerMinPerNode = 0;
			outputPerMin += lookupStoreMsg;
			outputPerMin = outputPerMin * Simulator.MINUTE_UNIT
					/ timeBetweenAnalyzeSteps;
			outputPerMinPerNode = outputPerMin / globalNodes.size();
			returnvalue[0] = outputPerMin;
			returnvalue[1] = outputPerMinPerNode;
		}
		return returnvalue;

	}

	/**
	 * Gives the number of needed of hops of all lookup messages
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return output[] = {hopsAvg, median, standardDeviation,
	 *         standardDeviationMinus, standardDeviationPlus}
	 */
	public double[] hops(LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] returnvalue = { 0, 0, 0, 0, 0 };

		double hopsAvg = 0;
		double median = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;

		if (globalNodes.size() != 0) {
			ArrayList<Double> numberHops = new ArrayList<Double>();
			List<Integer> hops = hopCount;
			for (int i = 0; i < hops.size(); i++) {
				numberHops.add((double) hops.get(i));
			}

			if (numberHops.size() != 0) {
				double numberHopsSum = 0;
				for (int i = 0; i < numberHops.size(); i++) {
					numberHopsSum += numberHops.get(i);
				}

				hopsAvg = numberHopsSum / numberHops.size();

				median = stats.median(numberHops);

				Double[] deviation = stats.standardDeviationDetail(numberHops);
				returnvalue[0] = hopsAvg;
				returnvalue[1] = median;
				returnvalue[2] = deviation[0];
				returnvalue[3] = deviation[1];
				returnvalue[4] = deviation[2];

			} else {
				returnvalue[0] = hopsAvg;
				returnvalue[1] = median;
				returnvalue[2] = standardDeviation;
				returnvalue[3] = standardDeviationMinus;
				returnvalue[4] = standardDeviationPlus;
			}
		}

		return returnvalue;
	}

	/**
	 * Gives the time needed for a lookup to travel to the destination and back,
	 * for all lookups
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return output[] = {hopsAvg, median, standardDeviation,
	 *         standardDeviationMinus, standardDeviationPlus}
	 */
	public double[] transferTimeAvg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		double[] returnvalue = { 0, 0, 0, 0, 0 };

		double hopsAvg = 0;
		double median = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;

		if (globalNodes.size() != 0) {
			ArrayList<Double> timeHops = new ArrayList<Double>();
			List<Long> hopTimes = timeForHops;
			for (int i = 0; i < hopTimes.size(); i++) {
				timeHops
						.add(((double) hopTimes.get(i) / Simulator.MILLISECOND_UNIT));
			}

			if (timeHops.size() != 0) {
				double timeHopsSum = 0;
				for (int i = 0; i < timeHops.size(); i++) {
					timeHopsSum += timeHops.get(i);
				}

				hopsAvg = timeHopsSum / timeHops.size();

				median = stats.median(timeHops);

				Double[] deviation = stats.standardDeviationDetail(timeHops);
				returnvalue[0] = hopsAvg;
				returnvalue[1] = median;
				returnvalue[2] = deviation[0];
				returnvalue[3] = deviation[1];
				returnvalue[4] = deviation[2];
			}
		} else {
			returnvalue[0] = hopsAvg;
			returnvalue[1] = median;
			returnvalue[2] = standardDeviation;
			returnvalue[3] = standardDeviationMinus;
			returnvalue[4] = standardDeviationPlus;
		}

		return returnvalue;

	}

	public int sendLookupMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		int returnvalue = 0;

		if (globalNodes.size() != 0) {
			returnvalue = startedLookup;
		}
		return returnvalue;
	}

	public int receivedLookupMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {
		int returnvalue = 0;

		if (globalNodes.size() != 0) {
			returnvalue = receivedBackLookup;
		}
		return returnvalue;
	}

	/**
	 * Gives the number of neighbours.
	 * 
	 * @param globalNodes
	 *            all peers in the CAN
	 * @return output[] = {hopsAvg, median, standardDeviation,
	 *         standardDeviationMinus, standardDeviationPlus}
	 */
	public double[] numberNeighbours(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes) {

		double[] returnvalue = { 0, 0, 0, 0, 0, 0 };

		double neighboursAvg = 0;
		double median = 0;
		double standardDeviation = 0;
		double standardDeviationMinus = 0;
		double standardDeviationPlus = 0;
		double withoutEdge = 0;

		if (globalNodes.size() != 0) {
			ArrayList<Double> numberNeighbours = new ArrayList<Double>();
			ArrayList<Double> numberNeighboursWithoutEdge = new ArrayList<Double>();
			for (CanOverlayID id : globalNodes.keySet()) {
				numberNeighbours.add((double) globalNodes.get(id)
						.getNeighbours().size());

				if (globalNodes.get(id).getLocalOverlayContact().getArea()
						.getArea()[0] != 0
						&& globalNodes.get(id).getLocalOverlayContact()
								.getArea()
								.getArea()[1] != CanConfig.CanSize
						&& globalNodes.get(id).getLocalOverlayContact()
								.getArea()
								.getArea()[2] != 0
						&& globalNodes.get(id).getLocalOverlayContact()
								.getArea()
								.getArea()[3] != CanConfig.CanSize) {
					numberNeighboursWithoutEdge.add((double) globalNodes
							.get(id).getNeighbours().size());
				}
			}

			if (numberNeighbours.size() != 0) {
				double numberNeighboursSum = 0;
				double withoutEdgeSum = 0;
				for (int i = 0; i < numberNeighbours.size(); i++) {
					numberNeighboursSum += numberNeighbours.get(i);
				}

				for (int i = 0; i < numberNeighboursWithoutEdge.size(); i++) {
					withoutEdgeSum += numberNeighboursWithoutEdge
							.get(i);
				}

				neighboursAvg = numberNeighboursSum
						/ numberNeighbours.size();

				withoutEdge = withoutEdgeSum
						/ numberNeighboursWithoutEdge.size();

				median = stats.median(numberNeighbours);

				Double[] deviation = stats
						.standardDeviationDetail(numberNeighbours);
				returnvalue[0] = neighboursAvg;
				returnvalue[1] = median;
				returnvalue[2] = deviation[0];
				returnvalue[3] = deviation[1];
				returnvalue[4] = deviation[2];
				returnvalue[5] = withoutEdge;

			} else {
				returnvalue[0] = neighboursAvg;
				returnvalue[1] = median;
				returnvalue[2] = standardDeviation;
				returnvalue[3] = standardDeviationMinus;
				returnvalue[4] = standardDeviationPlus;
				returnvalue[5] = withoutEdge;
			}
		}

		return returnvalue;
	}

}
