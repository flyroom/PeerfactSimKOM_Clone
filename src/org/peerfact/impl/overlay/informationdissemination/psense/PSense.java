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

package org.peerfact.impl.overlay.informationdissemination.psense;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Configuration;
import org.peerfact.impl.simengine.Simulator;
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
 * Stores the node information about the other nodes for the local nodes. The
 * information will be store in the <code>nodeStorage</code>. The
 * <code>nearNodes, sensorNodes</code> and <code>localNode</code> contains the
 * keys for the nodeStorage.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 */
public class PSense {

	/**
	 * The number of the degree of a circle. It is used for the computing of the
	 * sectors.
	 */
	private final static double DEGREE_OF_CIRCLE = 360;

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(PSense.class);

	private final Hashtable<PSenseID, PSenseNodeInfo> nodeStorage;

	private final Vector<PSenseID> nearNodes;

	private final PSenseID[] sensorNodes;

	private final PSenseID localNode;

	public PSense(PSenseID localNodeID) {
		this.nodeStorage = new Hashtable<PSenseID, PSenseNodeInfo>();
		this.nearNodes = new Vector<PSenseID>();
		this.sensorNodes = new PSenseID[Configuration.NUMBER_SECTORS];
		this.localNode = localNodeID;
	}

	/**
	 * Update the node storage with the given information. The given information
	 * will only store, if the information has a newer content.
	 * 
	 * @param id
	 *            The ID of the node.
	 * @param nodeInfo
	 *            The {@link PSenseNodeInfo} with the new information
	 * @return If the content of the nodeInfo is new then <code>true</code>,
	 *         otherwise <code>false</code>. A nodeInfo is new, if the sequence
	 *         number is greater than the old sequence number or the nodeInfo is
	 *         not in {@link #nodeStorage}.
	 * @throws IllegalArgumentException
	 *             If a parameter is null.
	 */
	public boolean updateNodeStorage(PSenseID id, PSenseNodeInfo nodeInfo) {
		if (id == null || nodeInfo == null) {
			throw new IllegalArgumentException("An argument is null");
		}
		PSenseNodeInfo oldNodeInfo = nodeStorage.get(id);

		if (oldNodeInfo != null) {
			if (oldNodeInfo.getSequenceNr().equals(nodeInfo.getSequenceNr())) {
				// it is possible that this nodeInfo has new receivers
				oldNodeInfo.updateReceiversList(nodeInfo.getReceiversList());
				return false;
			} else if (oldNodeInfo.getSequenceNr().isNewerAs(
					nodeInfo.getSequenceNr())) {
				// do nothing, because nodeInfo is old
				return false;
			}
		}
		// if nodeInfo newer or not in nodeStorage, replace nodeInfo!
		nodeStorage.put(id, nodeInfo);
		return true;
	}

	public void updateSensorNodeList(PSenseID[] newSensorNodes) {
		if (newSensorNodes != null && sensorNodes != null) {
			if (newSensorNodes.length == sensorNodes.length) {
				// iterate over sensorNodes and Update
				for (int i = 0; i < sensorNodes.length; i++) {
					if (newSensorNodes[i] != null) {
						// replace old value
						sensorNodes[i] = newSensorNodes[i];
					} else {
						/** If no newer sensor node returned, then search a new **/
						// if (sensorNodes[i] != null) {
						// keep the old node
						// sensorNodes[i] = sensorNodes[i];
						// } else {
						// search new sensor node
						PSenseNodeInfo localNodeInfo = nodeStorage
								.get(localNode);

						List<PSenseID> toIgnore = new Vector<PSenseID>();
						toIgnore.add(localNode);

						sensorNodes[i] = findSensorNode(
								localNodeInfo.getPosition(),
								localNodeInfo.getVisionRangeRadius(), (byte) i,
								toIgnore);
						// }
					}
				}
			} else {
				log.error("The stored list for sensorNodes has a different length as the new delivered list");
			}
		}

	}

	/**
	 * Find a sensor node for the given sectorID. The node can a node in the
	 * sector or a node in the near of the sector.
	 * 
	 * @param position
	 *            The position of the node, that search a sensor node.
	 * @param visionRangeRadius
	 *            The vision range radius of the node
	 * @param sectorID
	 *            The sectorID, for that is to find a sensor node.
	 * @param ignoreNodes
	 *            Nodes that are to ignore. Normal the nodeID of the node, that
	 *            belongs the position and visionRangeRadius.
	 * @return The PSenseID, of the node that is the best sensor node for this
	 *         the given sector. If no node found, then returned
	 *         <code>null</code>.
	 */
	public PSenseID findSensorNode(Point position, int visionRangeRadius,
			byte sectorID, List<PSenseID> ignoreNodes) {

		// ################################################
		// FIRST METHOD
		// search nodes between the sector
		// ################################################
		if (log.isTraceEnabled()) {
			log.trace("First Method to find nearSensorNode");
		}
		double startAngle = getSectorSize() * sectorID;
		double endAngle = startAngle + getSectorSize();
		List<PSenseID> foundNodes = findNodesBetweenAngles(position,
				startAngle, endAngle, ignoreNodes);

		PSenseID node = getSmallestDistance(foundNodes, position,
				visionRangeRadius);
		if (node != null) {
			return node;
		}

		// ################################################
		// SECOND METHOD
		// search nodes between the enlarged sector
		// ################################################
		if (log.isTraceEnabled()) {
			log.trace("Second Method to find nearSensorNode");
		}
		double enlargingAngle = getSectorSize()
				* Configuration.ENLARGING_SECTOR_FACTOR;
		startAngle = modulo(startAngle - enlargingAngle / 2, DEGREE_OF_CIRCLE);
		endAngle = modulo(endAngle + enlargingAngle / 2, DEGREE_OF_CIRCLE);

		foundNodes = findNodesBetweenAngles(position, startAngle, endAngle,
				ignoreNodes);

		node = getSmallestDistance(foundNodes, position, visionRangeRadius);
		if (node != null) {
			return node;
		}

		// ################################################
		// THIRD METHOD
		// search node that is closest to the position on the vision range
		// circle.
		// Position on circle is the center of the sectorAngle and on the
		// circle.
		// ################################################
		if (log.isTraceEnabled()) {
			log.trace("Third Method to find nearSensorNode");
		}
		double centerAngle = modulo(getSectorSize() * sectorID
				+ (getSectorSize() / 2), DEGREE_OF_CIRCLE);
		int transX = (int) (Math.cos(Math.toRadians(centerAngle)) * visionRangeRadius);
		int transY = (int) (Math.sin(Math.toRadians(centerAngle)) * visionRangeRadius);
		Point pointOnCircle = new Point(position.x + transX, position.y
				+ transY);

		foundNodes = new Vector<PSenseID>(nodeStorage.keySet());
		if (ignoreNodes != null) {
			foundNodes.removeAll(ignoreNodes);
		}

		node = getSmallestDistance(foundNodes, pointOnCircle, -1);
		if (node != null) {
			return node;
		}
		return null;
	}

	private PSenseID getSmallestDistance(List<PSenseID> nodeIDs,
			Point position, int minDistance) {
		if (nodeIDs == null || nodeIDs.size() == 0) {
			return null;
		}
		PSenseID minID = null;
		double min = Double.MAX_VALUE;
		for (PSenseID id : nodeIDs) {
			PSenseNodeInfo nodeInfo = nodeStorage.get(id);
			double distance = position.distance(nodeInfo.getPosition());
			if (minDistance < distance && distance < min) {
				min = distance;
				minID = id;
			}
		}
		return minID;
	}

	/**
	 * Get the size of one sector back.
	 * 
	 * @return Size of one sector.
	 */
	private static double getSectorSize() {
		return DEGREE_OF_CIRCLE / Configuration.NUMBER_SECTORS;
	}

	private List<PSenseID> findNodesBetweenAngles(Point posCenter,
			double startAngle, double endAngle, List<PSenseID> ignoreNodes) {
		List<PSenseID> nodesBetweenAngle = new Vector<PSenseID>();
		for (PSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id)) {
				PSenseNodeInfo nodeInfo = nodeStorage.get(id);
				// transform nodeInfo.position to the origin on the basis of
				// posCenter
				int deltaX = nodeInfo.getPosition().x - posCenter.x;
				int deltaY = nodeInfo.getPosition().y - posCenter.y;
				try {
					// compute the angle on the basis of the unit circle
					double angle = computeAngle(deltaX, deltaY);

					if (isAngleBetween(angle, startAngle, endAngle)) {
						nodesBetweenAngle.add(id);
					}
				} catch (IllegalArgumentException e) {
					// do nothing.
					if (log.isDebugEnabled()) {
						log.debug(
								"Node position is on the same position for this request. It is not bad, because it gives no angle between this nodes.",
								e);
					}
				}
			}
		}
		return nodesBetweenAngle;
	}

	/**
	 * Is the given <code>angle</code>, between the <code>startAngle</code> and
	 * <code>endAngle</code>. It will be computing with 360 degrees. If endAngle
	 * smaller as startAngle, then will be handle a overflow of the angle.
	 * 
	 * @param angle
	 *            The angle, that is to check. The angle must between 0 and
	 *            {@value #DEGREE_OF_CIRCLE} degree.
	 * @param startAngle
	 *            The start angle.
	 * @param endAngle
	 *            The end angle.
	 * @return <code>true</code> if the angle is between <code>startAngle</code>
	 *         and <code>endAngle</code>, otherwise <code>false</code>
	 */
	private static boolean isAngleBetween(double angle, double startAngle,
			double endAngle) {
		// transform angle between 0 and 360 degree
		double newAngle = modulo(angle, DEGREE_OF_CIRCLE);
		double newStartAngle = modulo(startAngle, DEGREE_OF_CIRCLE);
		double newEndAngle = modulo(endAngle, DEGREE_OF_CIRCLE);

		if (newStartAngle <= newEndAngle) {
			return newStartAngle <= newAngle && newAngle < newEndAngle;
		} else {
			return 0 <= newAngle && newAngle < newEndAngle
					&& newStartAngle <= newAngle
					&& newAngle <= DEGREE_OF_CIRCLE;
		}
	}

	/**
	 * Gets a positive modulo back.
	 * 
	 * @param n
	 *            The number that is to calculate as modulo
	 * @param m
	 *            The factor for the modulo
	 * @return A positive number between 0 and m
	 */
	private static double modulo(double n, double m) {
		if (0 <= n && n < m) {
			return n;
		}
		double temp = n % m;
		if (temp < 0) {
			temp += m;
		}
		return temp;
	}

	/**
	 * Compute the angle on the basis of the unit circle in degree. It use only
	 * one point in the direction of the origin (point is transformed to the
	 * origin).
	 * 
	 * @param x
	 *            The value in direction of <code>x</code> of the point
	 * @param y
	 *            The value in direction of <code>y</code> of the point
	 * @return The angle in degree
	 * 
	 * @throws IllegalArgumentException
	 *             If x and y are 0. Because, an angle is not defined for this
	 *             position.
	 */
	private static double computeAngle(int x, int y) {
		// length from the origin to the point
		if (x == 0 && y == 0) {
			throw new IllegalArgumentException(
					"No Angle is defined for this position! (x=0 and y=0)");
		}
		double length = Math.sqrt(x * x + y * y);
		double cosAngleRadian = Math.acos(x / length);
		double cosAngle = Math.toDegrees(cosAngleRadian);
		if (y < 0) {
			return 360.0f - cosAngle;
		} else {
			return cosAngle;
		}
	}

	public void removeDeadNodes() {
		Enumeration<PSenseID> e = nodeStorage.keys();
		while (e.hasMoreElements()) {
			PSenseID id = e.nextElement();
			PSenseNodeInfo nodeInfo = nodeStorage.get(id);
			if (Simulator.getCurrentTime() - nodeInfo.getLastUpdate() > Configuration.DECLARE_NODE_DEATH_TIMEOUT) {
				if (id != localNode) {
					PSenseNodeInfo rmNodeInfo = nodeStorage.remove(id);

					// for consistent of the data structure
					for (int i = 0; i < sensorNodes.length; i++) {
						if (sensorNodes[i] != null && sensorNodes[i].equals(id)) {
							sensorNodes[i] = null;
						}
					}
					nearNodes.remove(id);

					if (log.isDebugEnabled()) {
						log.debug("Node: "
								+ rmNodeInfo.getContact().getOverlayID()
								+ " is declared dead and deleted from the nodeStorage.");
					}
				}
			}
		}
	}

	public void removeUnusedNodes() {
		Enumeration<PSenseID> e = nodeStorage.keys();
		while (e.hasMoreElements()) {
			PSenseID id = e.nextElement();
			if (localNode.equals(id) || nearNodes.contains(id)
					|| sensorNodesContains(id)) {
				// node is OK
			} else {
				nodeStorage.remove(id);
			}
		}
	}

	private boolean sensorNodesContains(PSenseID id) {
		for (PSenseID sensorNodeID : sensorNodes) {
			if (sensorNodeID != null && sensorNodeID.equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Update the nodeInfo for the local node.
	 * 
	 * @param nodeInfo
	 *            The nodeInfo, which is to store for the local node.
	 */
	public void updateLocalNodePosition(PSenseNodeInfo nodeInfo) {
		nodeStorage.put(localNode, nodeInfo);
	}

	/**
	 * Update the near node list. It looks, which nodes are in the area of the
	 * local node.
	 */
	public void updateNearNodeList() {
		// remove all ids from nearNodes list
		nearNodes.clear();

		PSenseNodeInfo localNodeInfo = nodeStorage.get(localNode);
		List<PSenseID> toIgnore = new Vector<PSenseID>();
		toIgnore.add(localNode);

		List<PSenseID> newNearNodes = getAllNodesInArea(
				localNodeInfo.getPosition(),
				localNodeInfo.getVisionRangeRadius(), toIgnore);

		nearNodes.addAll(newNearNodes);

	}

	/**
	 * Determines all known nodes in the given area. The ignoreNodes will be
	 * excluded.<br>
	 * Attention, the localNode can be in this area too.
	 * 
	 * @param position
	 *            The center of the area.
	 * @param radius
	 *            The radius of the area.
	 * @param ignoreNodes
	 *            The nodes that are to ignore.
	 * @return A list of {@link PSenseID}s, which are contain in this given
	 *         area.
	 */
	public List<PSenseID> getAllNodesInArea(Point position, int radius,
			List<PSenseID> ignoreNodes) {
		List<PSenseID> nodesInArea = new Vector<PSenseID>();

		for (PSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id)) {
				PSenseNodeInfo nodeInfo = nodeStorage.get(id);
				if (inVisionRange(position, nodeInfo.getPosition(), radius)) {
					nodesInArea.add(id);
				}
			}
		}
		return nodesInArea;
	}

	/**
	 * Check, whether point2 is in vision range of first point.
	 * 
	 * @param point
	 *            The first point.
	 * @param point2
	 *            The second point, which is to check, whether in the
	 *            visionRangeRadius of the first point.
	 * @param visionRangeRadius
	 *            The vision range radius of the first point.
	 * @return <code>true</code> if point2 is in the visionRangeRadius of point,
	 *         otherwise <code>false</code>
	 */
	private static boolean inVisionRange(Point point, Point point2,
			int visionRangeRadius) {
		double length = point.distance(point2);
		return (length <= visionRangeRadius);
	}

	/**
	 * Gets the {@link PSenseNodeInfo} to the associated id.
	 * 
	 * @param id
	 *            The {@link PSenseID} for the asked node info
	 * @return The {@link PSenseNodeInfo} to the associated id. If no
	 *         information stored to the id, then return <code>null</code>.
	 */
	public PSenseNodeInfo getNodeInfo(PSenseID id) {
		return nodeStorage.get(id);
	}

	/**
	 * Gets a copy of the list of the nearNodes back.
	 * 
	 * @return A list of all near nodes.
	 */
	public List<PSenseID> getNearNodes() {
		return new Vector<PSenseID>(nearNodes);
	}

	/**
	 * Gets a copy of sensor nodes back. The sectorID is coded in the array
	 * position.
	 * 
	 * @return A list of all sensor nodes.
	 */
	public PSenseID[] getSensorNodes() {
		return sensorNodes.clone();
	}

	/**
	 * Gets the @link {@link PSenseID} of the local node.
	 * 
	 * @return the local node PSenseID
	 */
	public PSenseID getLocalNode() {
		return localNode;
	}

	/**
	 * Look for known nodes in the given area. The ignoreNode is the node, that
	 * belong to the given area.
	 * 
	 * @param position
	 *            The center of the area, which is to check.
	 * @param radius
	 *            The radius around the position, for define the area
	 * @param ignoreNode
	 *            The node that is to ignore. Normal, that is the associated
	 *            node to the position and visionRangeRadius.
	 * @return <code>true</code> if a node exits in this area, otherwise
	 *         <code>false</code>.
	 */
	public boolean existsNodeInArea(Point position, int radius,
			PSenseID ignoreNode) {
		List<PSenseID> ignoreNodes = new Vector<PSenseID>();
		ignoreNodes.add(ignoreNode);
		List<PSenseID> nodes = getAllNodesInArea(position, radius, ignoreNodes);

		if (nodes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether the given id a sensor node is.
	 * 
	 * @param id
	 *            The ID, that is to check.
	 * @return <code>true</code> if the id is a sensor node, otherwise
	 *         <code>false</code>
	 */
	public boolean isSensorNode(PSenseID id) {
		for (PSenseID sensorID : sensorNodes) {
			if (sensorID != null && sensorID.equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets a node, that is closest to the position and is not a node from the
	 * ignoreNodes list.
	 * 
	 * @param position
	 *            The reference position for the closest node
	 * @param ignoreNodes
	 *            Nodes that are to ignore.
	 * @return The {@link PSenseID} of the closest node. If doesn't exist one
	 *         node, then return <code>null</code>
	 */
	public PSenseID getClosestNode(Point position, List<PSenseID> ignoreNodes) {
		List<PSenseID> nodes = new Vector<PSenseID>();
		// remove ignoreNodes from all known nodes.
		for (PSenseID id : nodeStorage.keySet()) {
			if (!ignoreNodes.contains(id)) {
				nodes.add(id);
			}
		}
		return getSmallestDistance(nodes, position, -1);
	}

	/**
	 * Gets all known {@link PSenseNodeInfo} back, that are stored in the
	 * {@link #nodeStorage}.
	 * 
	 * @return A list of all values from the {@link #nodeStorage}
	 */
	public List<PSenseNodeInfo> getAllKnownNodeInfos() {
		return new Vector<PSenseNodeInfo>(nodeStorage.values());
	}
}
