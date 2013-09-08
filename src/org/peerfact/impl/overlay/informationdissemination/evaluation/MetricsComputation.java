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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONode;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.api.overlay.ido.IDOOracle;
import org.peerfact.impl.overlay.informationdissemination.AbstractIDONode;
import org.peerfact.impl.util.logging.SimLogger;


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
 * This class is meant to encapsulate the computation of metrics. This class is
 * not concerned with the information gathering for the computation by means of
 * analyzers. The methods of this class are static and are provided with data
 * via parameters.
 * 
 * @author Christoph Muenker
 * @version 01/20/2011
 * 
 */
public class MetricsComputation {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(MetricsComputation.class);

	/**
	 * Determine the number of online peers.
	 * 
	 * @param nodes
	 *            A map with online nodes.
	 * @return The number of online peers.
	 */
	public static int computeNumberOfOnlinePeers(
			LinkedHashMap<Host, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodes) {
		return nodes.size();
	}

	/**
	 * Determine the AOI radius of an {@link IDONode}.
	 * 
	 * @param node
	 *            An {@link IDONode}.
	 * @return The AOI radius of the given {@link IDONode};
	 */
	public static int computeVisionRange(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node) {
		return node.getAOI();
	}

	/**
	 * Determine the centroid of the given node. The centroid describes the
	 * average position of a node, which is perceived of the neighbors of the
	 * node.
	 * 
	 * @param node
	 *            The node, for which is determined the centroid
	 * @param nodesID
	 *            A map with online nodes.
	 * @return The average position, which is perceived of the neighbors of the
	 *         node.
	 */
	public static Point2D computeCentroid(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodesID) {

		Point2D result = new Point2D.Double(0, 0);
		double positionX = 0;
		double positionY = 0;
		int countPositions = 0;
		// M(i,t)
		for (IDONodeInfo neighborInfo : node.getNeighborsNodeInfo()) {
			Point position_ij = getPosition(nodesID, node.getOverlayID(),
					neighborInfo.getID());
			if (position_ij != null) {
				positionX += position_ij.x;
				positionY += position_ij.y;
				countPositions++;
			}
		}
		if (countPositions != 0) {
			result = new Point2D.Double(positionX / countPositions, positionY
					/ countPositions);
		}
		return result;
	}

	/**
	 * The Position of peer i perceived by peer j. If i=j then is this the
	 * position of peer i;
	 * 
	 * @param nodes
	 *            All nodes, that are online in the overlay
	 * @param i
	 *            The OverlayID<?> for peer i
	 * @param j
	 *            The OverlayID<?> for peer j
	 * @return The position of peer i perceived by peer j. If no position can
	 *         determined, then return null.
	 */
	private static Point getPosition(
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodes,
			OverlayID<?> i, OverlayID<?> j) {
		IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> nodeJ = nodes
				.get(j);

		if (nodeJ != null) {
			if (i == j) {
				return nodeJ.getPosition();
			} else {
				for (IDONodeInfo nodeInfo : nodeJ.getNeighborsNodeInfo()) {
					if (nodeInfo.getID().equals(i)) {
						return nodeInfo.getPosition();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Determine the traffic for given node.
	 * 
	 * @param node
	 *            The node, for which the traffic is determined.
	 * @param msgsPerPeer
	 *            A map for all nodes, with all messages of the peers.
	 * @return The traffic for the given node.
	 */
	public static double computeTraffic(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			LinkedHashMap<OverlayID<?>, LinkedList<Message>> msgsPerPeer) {

		double traffic = 0;
		if (msgsPerPeer.containsKey(node.getOverlayID())) {
			for (Message message : msgsPerPeer.get(node.getOverlayID())) {
				traffic += message.getSize();
			}

		}
		return traffic;
	}

	/**
	 * Determine the number of messages of a node.
	 * 
	 * @param node
	 *            The node, for which the number of message is determined.
	 * @param msgsPerPeer
	 *            A map for all nodes, with all messages of the peers.
	 * @return The number of messages for a node.
	 */
	public static int computeMsgsCount(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			LinkedHashMap<OverlayID<?>, LinkedList<Message>> msgsPerPeer) {

		int countMsgs = 0;
		if (msgsPerPeer.containsKey(node.getOverlayID())) {
			countMsgs = msgsPerPeer.get(node.getOverlayID()).size();
		}
		return countMsgs;
	}

	/**
	 * Compute a confusion matrix. It contains true positive, false positive and
	 * false negative.<br>
	 * true positive describes: should see and see the node <br>
	 * false positive describes: should see, but not see the node <br>
	 * false negative describes: should not see, but see the node
	 * 
	 * @param node
	 *            The node, for which the confusion matrix should be compute.
	 * @return Returns an array.The first entry in the array contains the true
	 *         positive. The second entry the false positive. The third entry
	 *         the false negative.
	 */
	public static int[] computeConfusionMatrix(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			IDOOracle oracle) {
		// TODO: falsePos und falsNeg ist verdreht.
		OverlayID<?> id = node.getOverlayID();

		// should see and see
		int truePosAwarness = 0;
		// should see, but not see
		int falsePosAwarness = 0;
		// should not see, but see
		int falseNegAwarness = 0;

		List<IDONodeInfo> allKnownNodes = node.getNeighborsNodeInfo();

		List<IDONodeInfo> nodesInAOI = oracle
				.getAllNeighbors(id, node.getAOI());
		List<OverlayID<?>> nodeInAoiID = extractID(nodesInAOI);

		for (IDONodeInfo knownNodeInfo : allKnownNodes) {
			if (nodeInAoiID.contains(knownNodeInfo.getID())) {
				nodeInAoiID.remove(knownNodeInfo.getID());
				truePosAwarness++;
			} else {
				falseNegAwarness++;
			}
		}
		falsePosAwarness = nodeInAoiID.size();

		int[] result = { truePosAwarness, falsePosAwarness, falseNegAwarness };
		return result;
	}

	/**
	 * Extract the ids of a list with {@link IDONodeInfo}s.
	 * 
	 * @param nodesInAOI
	 *            A list of {@link IDONodeInfo}s
	 * @return A list with {@link OverlayID}s, which are extracted from the
	 *         given list.
	 */
	private static List<OverlayID<?>> extractID(List<IDONodeInfo> nodesInAOI) {
		List<OverlayID<?>> result = new Vector<OverlayID<?>>();
		for (IDONodeInfo nodeInfo : nodesInAOI) {
			result.add(nodeInfo.getID());
		}
		return result;
	}

	/**
	 * Determine the precision.
	 * 
	 * @param truePositive
	 *            The number of true positives.
	 * @param falsePositive
	 *            The number of false positives.
	 * @return The factor of truePositive / (truePositive + falsePositive). If
	 *         (truePositive+falsePositive) == 0 then will be return
	 *         <code>1</code>.
	 */
	public static double computePrecision(int truePositive, int falsePositive) {
		if ((truePositive + falsePositive) == 0) {
			return 1;
		}
		return (((double) truePositive) / ((double) (truePositive + falsePositive)));
	}

	/**
	 * Determine the recall.
	 * 
	 * @param truePositive
	 *            The number of true positives.
	 * @param falseNegative
	 *            The number of false negative.
	 * @return The factor of truePositive / (truePositive + falseNegative). If
	 *         (truePositive+falseNegative) == 0 then will be return
	 *         <code>1</code>.
	 */
	public static double computeRecall(int truePositive, int falseNegative) {
		if ((truePositive + falseNegative) == 0) {
			return 1;
		}
		return (((double) truePositive) / ((double) (truePositive + falseNegative)));
	}

	/**
	 * Determine the position error for a node. The position error describes the
	 * average distance between real position and perceived position by the
	 * neighbors.
	 * 
	 * @param node
	 *            The node, for which the position error is determined.
	 * @param nodesID
	 *            A map with all online nodes.
	 * @return The average distance between real position and perceived position
	 *         by the neighbors.
	 */
	public static double computePositionError(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodesID) {

		double positionError = 0;

		double sumDistance = 0;
		int countPositions = 0;
		for (IDONodeInfo nodeInfo : node.getNeighborsNodeInfo()) {
			OverlayID<?> i = node.getOverlayID();
			OverlayID<?> j = nodeInfo.getID();

			Point position_ij = getPosition(nodesID, i, j);
			Point position_ii = getPosition(nodesID, i, i);
			if (position_ij != null && position_ii != null) {
				sumDistance += position_ii.distance(position_ij);
				countPositions++;
			}
		}
		if (countPositions != 0) {
			positionError = sumDistance / countPositions;
		}
		return positionError;
	}

	/**
	 * Determine the dispersion for a node. The dispersion describes
	 * 
	 * @param node
	 *            The node, for which the dispersion is determined.
	 * @param centroid
	 *            The centroid for the node.
	 * @param nodesID
	 *            A map with all online nodes.
	 * @return The dispersion to the node.
	 */
	public static double computeDispersion(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			Point2D centroid,
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodesID) {

		double dispersion = 0;
		double sumDispersion = 0;
		int countPositions = 0;
		for (IDONodeInfo nodeInfo : node.getNeighborsNodeInfo()) {
			OverlayID<?> i = node.getOverlayID();
			OverlayID<?> j = nodeInfo.getID();

			Point position_ij = getPosition(nodesID, i, j);
			if (position_ij != null && centroid != null) {
				sumDispersion += centroid.distanceSq(position_ij);
				countPositions++;
			}
		}

		if (countPositions > 1) {
			dispersion = Math.sqrt(sumDispersion / (countPositions - 1));
		}

		return dispersion;
	}

	public static Point computeOwnPosition(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node) {
		return node.getPosition();
	}

	/**
	 * Determine the global connectivity.
	 * 
	 * @param nodes
	 *            A map of all online nodes.
	 * @return The global connectivity.
	 */
	public static double computeGlobalConnectivity(
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodes) {

		double overallGlobalConnectivitySum = 0;
		int numOfOverallNodes = nodes.size();

		for (OverlayID<?> id : nodes.keySet()) {
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node = nodes
					.get(id);
			Point centerPoint = node.getPosition();

			List<IDONodeInfo> neighbors = node.getNeighborsNodeInfo();

			if (neighbors.size() == 0) {
				continue;
			}

			double localMaxClockwiseAngle = 0.0;

			/*
			 * Compute the maximum clockwise angle between two neighbor that are
			 * next to each other
			 */
			for (int i = 0; i < neighbors.size(); i++) {

				IDONodeInfo nInfo1 = neighbors.get(i);
				Point nPoint1 = nInfo1.getPosition();

				double minClockwiseAngle = 2 * Math.PI;

				for (int j = 0; j < neighbors.size(); j++) {

					if (i == j) {
						continue;
					}

					IDONodeInfo nInfo2 = neighbors.get(j);
					Point nPoint2 = nInfo2.getPosition();

					double angle1 = Math.atan2(nPoint1.y - centerPoint.y,
							nPoint1.x - centerPoint.x);
					double angle2 = Math.atan2(nPoint2.y - centerPoint.y,
							nPoint2.x - centerPoint.x);

					double localAngle = angle2 - angle1;

					if (localAngle >= 0 && localAngle < minClockwiseAngle) {
						minClockwiseAngle = localAngle;
					}
				}

				if (minClockwiseAngle < 2 * Math.PI
						&& minClockwiseAngle > localMaxClockwiseAngle) {
					localMaxClockwiseAngle = minClockwiseAngle;
				}
			}

			if (localMaxClockwiseAngle < Math.PI) {
				overallGlobalConnectivitySum++;
			}

		}

		double overallConnectivity = 1;
		if (numOfOverallNodes > 0) {
			overallConnectivity = overallGlobalConnectivitySum
					/ numOfOverallNodes;
		}

		return overallConnectivity;
	}

	/**
	 * Determined the global connected component factor. For that is determined
	 * the maximal weak connected component on the neighborhood graph.
	 * 
	 * @param nodes
	 *            A map with all online nodes.
	 * @return The factor of the number of the maximal weak connected component
	 *         and the number of online nodes.
	 */
	public static double computeGlobalConnectedComponentFactor(
			LinkedHashMap<OverlayID<?>, IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>> nodes) {

		List<Set<OverlayID<?>>> connectedComponents = new Vector<Set<OverlayID<?>>>();

		for (OverlayID<?> id : nodes.keySet()) {
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node = nodes
					.get(id);
			LinkedHashSet<OverlayID<?>> tree = new LinkedHashSet<OverlayID<?>>();
			tree.add(id);
			for (IDONodeInfo nodeInfo : node.getNeighborsNodeInfo()) {
				tree.add(nodeInfo.getID());
			}

			// For components, which are merged with tree and later merged
			// together
			List<Set<OverlayID<?>>> toJoin = new Vector<Set<OverlayID<?>>>();
			// add tree to the component. It's possible, that a tree is add to
			// many components.
			for (Set<OverlayID<?>> component : connectedComponents) {
				if (!Collections.disjoint(component, tree)) {
					component.addAll(tree);
					toJoin.add(component);
				}
			}
			// is disjoint, add tree to the connectedComponents
			if (toJoin.size() == 0) {
				connectedComponents.add(tree);
				// repair connectedComponents
			} else if (toJoin.size() >= 2) {
				connectedComponents.removeAll(toJoin);
				Set<OverlayID<?>> joined = new LinkedHashSet<OverlayID<?>>();
				for (Set<OverlayID<?>> component : toJoin) {
					joined.addAll(component);
				}
				connectedComponents.add(joined);
			}

		}
		double maxSizeConnectedComponent = 0;
		for (Set<OverlayID<?>> connectedComponent : connectedComponents) {
			if (connectedComponent.size() > maxSizeConnectedComponent) {
				maxSizeConnectedComponent = connectedComponent.size();
			}
		}

		double globalConnectedComponentFactor = 1;
		if (nodes.size() > 0) {
			globalConnectedComponentFactor = (maxSizeConnectedComponent / nodes
					.size());
		}
		return globalConnectedComponentFactor;
	}

	/**
	 * Determine the nodeSpeed for a node. The nodeSpeed describes the distance
	 * between two computations of a new position. <br>
	 * ATTENTION: The speed is not per second!
	 * 
	 * @param node
	 *            The node, for that should be compute the speed.
	 * @return The speed of a node, between two computations of a new position.
	 */
	public static double computeNodeSpeed(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node) {

		int[] moveVec = { 0, 0 };

		if (node instanceof AbstractIDONode) {
			moveVec = ((AbstractIDONode<OverlayID<?>, OverlayContact<OverlayID<?>>>) node)
					.getApplication()
					.getCurrentMoveVector();
		}

		double speed = Math.sqrt(Math.pow(moveVec[0], 2)
				+ Math.pow(moveVec[1], 2));
		return speed;
	}

}
