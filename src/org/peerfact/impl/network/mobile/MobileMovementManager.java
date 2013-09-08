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

package org.peerfact.impl.network.mobile;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Management class for all network nodes. The class creates a global array,
 * where all the network nodes are added to and manages the route searching and
 * updates the position for the movement of the nodes.
 * 
 * @author Carsten Snider <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MobileMovementManager {

	private final static Logger log = SimLogger
			.getLogger(MobileMovementManager.class);

	// Delay returned in case no path is found.
	private static final int error = 1;

	private Quadrant layer[][];

	private int resolution;

	private long last_changed;

	int steps;

	private LinkedList<Neighbor> distanceList;

	private LinkedList<Neighbor> deletedList;

	/**
	 * Constructor for this class. It initialize the array in dependence of the
	 * resolution set in the main class.
	 * 
	 * @param resolution
	 */

	public MobileMovementManager() {
		this.resolution = 0;
		last_changed = Simulator.getCurrentTime();
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
		log.debug("Aufl�sung: " + resolution);
		this.layer = new Quadrant[resolution][resolution];
		// Initialize the individual quadrants
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				layer[i][j] = new Quadrant();
			}
		}
	}

	/**
	 * Add a node to the global array. The array field is calculated based on
	 * the resolution (array size).
	 * 
	 * @param id
	 *            MobileNetID
	 * @param n
	 *            MobileNetLayer Object to be added to the Layer
	 */
	public void addNodeToLayer(MobileNetID id, MobileNetLayer n) {
		MobileNode e = (MobileNode) n.getNetPosition();
		layer[(int) Math.floor((e.getXPos() * resolution))][(int) Math.floor((e
				.getYPos() * resolution))].addNode(id, n);
	}

	public void addNodeToLayer(MobileNetLayer n) {
		MobileNode e = (MobileNode) n.getNetPosition();
		MobileNetID id = (MobileNetID) n.getNetID();
		layer[(int) Math.floor((e.getXPos() * resolution))][(int) Math.floor((e
				.getYPos() * resolution))].addNode(id, n);
	}

	/**
	 * Deletes a node from the global array.
	 * 
	 * @param n
	 *            MobileNetLayer, which will be deleted from the global array.
	 */
	public void deleteNodeFromLayer(MobileNetLayer n) {
		MobileNode e = (MobileNode) n.getNetPosition();
		MobileNetID id = (MobileNetID) n.getNetID();
		layer[(int) Math.floor((e.getXPos() * resolution))][(int) Math.floor((e
				.getYPos() * resolution))].deleteNode(id);
	}

	/**
	 * Searches the global array for a route between the sender and receiver.
	 * 
	 * @param sender
	 *            MobileNetLayer of the sending node
	 * @param receiver
	 *            MobileNetLayer of the receiving node
	 * @return HopCount (int) between the sender and receiver. If no path is
	 *         found, the sender is set offline
	 * 
	 */

	public int getHopCount(MobileNetLayer sender, MobileNetLayer receiver) {
		steps = 0;
		MobileNode s = (MobileNode) sender.getNetPosition();
		MobileNode r = (MobileNode) receiver.getNetPosition();
		// Check if the sender and receiver are in the same quadrant
		if (layer[(int) Math.floor(s.getXPos() * resolution)][(int) Math
		                                                      .floor(s.getYPos() * resolution)].contains(receiver.getNetID())) {
			return 1;
		}
		if (sender.getNetID() == receiver.getNetID()) {
			return 1;
		}
		// Check if there are network nodes near the sender or receiver
		if (check_neighboring_nodes(s) == false) {
			log.info("Fail. No network nodes near the sender");
			if (sender.isOnline()) {
				sender.goOffline();
			}
			return 1;
		}
		if (check_neighboring_nodes(r) == false) {
			log.info("Fail. No network nodes near the receiver");
			if (sender.isOnline()) {
				sender.goOffline();
			}
			return error;
		}
		this.distanceList = new LinkedList<Neighbor>();
		this.deletedList = new LinkedList<Neighbor>();
		// If there are node near the nodes, then start to search a path. First
		// create a Neighbour of the sender
		Neighbor ns = new Neighbor(Math.floor(s.getXPos() * resolution), Math
				.floor(s.getYPos() * resolution), Math.sqrt(Math.pow((Math
						.floor(s.getXPos() * resolution) - Math.floor(r.getXPos()
								* resolution)), 2)
								+ Math.pow(Math.floor(s.getYPos() * resolution)
										- Math.floor(r.getYPos() * resolution), 2)));
		Neighbor nr = new Neighbor(Math.floor(r.getXPos() * resolution), Math
				.floor(r.getYPos() * resolution), 0);
		selectNode(ns, nr);
		// Search for a path until the distance between the sender and receiver
		// is smaller than sqrt(2) or the list of all nodes is empty (
		// and no route is found ).
		while ((distanceList.size() > 0)
				&& (distanceList.getFirst().getDistanceToReceiver() > 1.5)) {
			log.debug("Selected Node: X: " + distanceList.getFirst().getXPos()
					+ " Y: " + distanceList.getFirst().getYPos() + " Distance:"
					+ distanceList.getFirst().getDistanceToReceiver());
			selectNode(distanceList.getFirst(), nr);
		}

		if (distanceList.size() > 0) {
			if (sender.isOffline()) {
				sender.goOnline();
			}
			if (distanceList.getFirst().getDistanceToReceiver() > 0) {
				// One additional step to the destination (distance=<sqrt(2);
				steps++;
			}
			log.debug("Found a connection. Delay:" + steps);
			return steps;
		} else {
			log.info("Fail. No connection found betwenn the network nodes.");
			if (sender.isOnline()) {
				sender.goOffline();
			}
			return error;
		}

	}

	/**
	 * Checks the neighboring cells of the sender node, if there are any nodes
	 * present. If there are nodes in the surrounding area, they are added to a
	 * list with the Euclidean distance to the receiver.
	 * 
	 * @param sender
	 *            MobileNode of the sender
	 * @param reiceiver
	 *            MobileNode of the receiver
	 * @return A list of all nodes which are neighbors of the sender.
	 * 
	 */

	private List<Neighbor> checkNeighboringNodes(Neighbor sender,
			Neighbor receiver) {
		List<Neighbor> nodeList = new LinkedList<Neighbor>();
		int xs = (int) sender.getXPos();
		int xr = (int) receiver.getXPos();
		int ys = (int) sender.getYPos();
		int yr = (int) receiver.getYPos();

		for (int i = -1; i <= 1; i++) {
			for (int f = -1; f <= 1; f++) {
				if ((f != 0) || (i != 0)) {
					try {
						if (layer[xs + i][ys + f].getNodeCount() > 0) {
							nodeList.add(new Neighbor(xs + i, ys + f, Math
									.sqrt(Math.pow((xs + i) - xr, 2)
											+ Math.pow((ys + f) - yr, 2))));
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// Ignore all out of bounds errors
						// For example, if the node is at the edge of the array
					}
				}
			}
		}
		return nodeList;
	}

	/**
	 * Searches a path between a sender and receiver. The results is saved into
	 * a linkedlist distanceList, which is sorted after the delay to the
	 * receiver. This method is called until the distance is smaller than
	 * sqrt(2). Also the global deletedList is used. Here the already searched
	 * nodes are saved.
	 * 
	 * @param sender
	 *            MobileNode of the sender
	 * @param receiver
	 *            MobileNode of the receiver
	 */
	private void selectNode(Neighbor sender, Neighbor receiver) {
		boolean new_points = false;
		List<Neighbor> nodeList = new LinkedList<Neighbor>();
		nodeList = checkNeighboringNodes(sender, receiver);
		if (nodeList.size() > 0) {
			ListIterator<Neighbor> itr = nodeList.listIterator();
			while (itr.hasNext()) {
				Neighbor m = itr.next();
				// Check for duplicates
				if ((!distanceList.contains(m)) && (!deletedList.contains(m))) {
					m.setSteps(steps);
					distanceList.add(m);
					new_points = true;
				}
			}

			// If anything was added to the array, resort it and select the
			// point with the shortest distance.
			if (new_points) {
				distanceList.remove(sender);
				deletedList.add(sender);
				Collections.sort(distanceList, new MECP());
				steps++;

			}
			// If not, then delete this node and select another one. The
			// distance is decreased.
			else {
				distanceList.remove(sender);
				if (distanceList.size() > 1) {
					steps = distanceList.getFirst().getSteps();
				}
				deletedList.add(sender);
			}
		}

	}

	/**
	 * Returns the number of nodes, which are in the radius of the x y
	 * parameters
	 * 
	 * @param x
	 *            X Position (between 0 - 100)
	 * @param y
	 *            Y Position (between 0 - 100)
	 * @return Number of nodes (int).
	 */

	private int check_nodes(int x, int y) {
		int nodeC = 0;
		for (int i = -1; i <= 1; i++) {
			for (int f = -1; f <= 1; f++) {
				try {
					nodeC += layer[x + i][y + f].getNodeCount();
				} catch (ArrayIndexOutOfBoundsException e) {
					// Ignore all outofbounds errors (if you the node is at the
					// edge of the array)
				}
			}
		}
		return nodeC;
	}

	private boolean check_neighboring_nodes(MobileNode s) {
		int x = (int) Math.floor(s.getXPos() * resolution);
		int y = (int) Math.floor(s.getYPos() * resolution);
		check_nodes(x, y);
		if (check_nodes(x, y) > 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Updates (move) the position of all network nodes, if the simulator time
	 * has changed. This class calls the method updateMovement of all quadrants.
	 */
	public boolean updateMovement() {
		long currentScaledTime = (Simulator.getCurrentTime() / Simulator.MINUTE_UNIT);

		if (last_changed != currentScaledTime) {
			LinkedList<MobileNetLayer> l = new LinkedList<MobileNetLayer>();
			for (int i = 0; i < layer.length; i++) {
				for (int f = 0; f < layer.length; f++) {
					// If there are network nodes in the quadrant call the
					// method updateMovement with the changed time
					if (layer[i][f].getNodeCount() != 0) {
						l = layer[i][f].updateMovement();

					}
					// If a node moved out of the current quadrant, add it to
					// the new quadrant
					if (l.size() != 0) {
						for (int g = 0; g < l.size(); g++) {
							this.addNodeToLayer(l.get(g));
						}
						l.clear();
					}
				}
			}
			last_changed = currentScaledTime;

			return true;
		}
		return false;
	}

}

// Comparator for the distance of the Network Nodes to the receiver
class MECP implements Comparator<Neighbor>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4428448460305426330L;

	@Override
	public int compare(Neighbor arg0, Neighbor arg1) {
		return (int) Math.round(arg0.getDistanceToReceiver() * 10
				- arg1.getDistanceToReceiver() * 10);

	}
}
