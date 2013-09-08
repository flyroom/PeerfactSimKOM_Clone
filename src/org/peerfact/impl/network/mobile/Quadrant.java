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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.network.NetID;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Quadrant {
	private final static Logger log = SimLogger.getLogger(Quadrant.class);

	private LinkedHashMap<MobileNetID, MobileNetLayer> NodeList;

	/**
	 * Constructor for a new quadrant
	 */
	public Quadrant() {
		this.NodeList = new LinkedHashMap<MobileNetID, MobileNetLayer>();
	}

	/**
	 * 
	 * Adds a node to this quadrant
	 * 
	 * @param id
	 *            : MobileNetId
	 * @param p
	 * @return True: if added successfully.
	 */
	public void addNode(MobileNetID id, MobileNetLayer p) {
		NodeList.put(id, p);
	}

	/**
	 * Delete this node from this quadrant
	 * 
	 * @param id
	 * @return Boolean
	 */
	public boolean deleteNode(MobileNetID id) {
		MobileNetLayer temp = NodeList.remove(id);
		if (temp == null) {
			return false;
		} else {
			return true;
		}
	}

	public void deleteNode(LinkedList<MobileNetLayer> list) {
		for (Iterator<MobileNetLayer> iterator = list.iterator(); iterator
				.hasNext();) {
			MobileNetLayer t1 = iterator.next();
			NodeList.remove(t1.getNetID());
		}
	}

	/**
	 * 
	 * @return How many nodes are present in this quadrant
	 */
	public int getNodeCount() {
		return NodeList.size();
	}

	/**
	 * Execute the method "moveNode" for all nodes which are present in this
	 * network. If the node has moved out of this quadrant, add it to a list and
	 * return it to the manager.
	 * 
	 * @param last_changed
	 *            : Time to calculate the distance for. Current time minus the
	 *            last update time
	 * 
	 * @return LinkedList: List with all the nodes, which have been moved out of
	 *         the array and were already deleted.
	 */

	public LinkedList<MobileNetLayer> updateMovement() {
		LinkedList<MobileNetLayer> l = new LinkedList<MobileNetLayer>();
		for (Map.Entry<MobileNetID, MobileNetLayer> e : NodeList.entrySet()) {
			MobileNetLayer elem = e.getValue();
			MobileNode n = (MobileNode) e.getValue().getNetPosition();
			double x = n.getXPos();
			double y = n.getYPos();
			n.updateMovement();
			// If the node moved out of my quadrant, add it to the returned List
			// and remove it from my list
			if ((x != n.getXPos()) || (y != n.getYPos())) {
				l.add(elem);
			}
		}
		this.deleteNode(l);
		return l;
	}

	public void outputNodes()
	{
		for (Map.Entry<MobileNetID, MobileNetLayer> e : NodeList.entrySet()) {
			MobileNode n = (MobileNode) e.getValue().getNetPosition();
			double x = n.getXPos();
			double y = n.getYPos();
			if (Math.abs(x - 0.8151173344625606D) < 0.00000001) {
				log.debug(e.getKey() + ";" + x + ";" + y);
			}
		}
	}

	public LinkedHashMap<MobileNetID, MobileNetLayer> getNodes() {
		return NodeList;
	}

	public boolean contains(NetID netID) {
		return NodeList.containsKey(netID);
	}
}
