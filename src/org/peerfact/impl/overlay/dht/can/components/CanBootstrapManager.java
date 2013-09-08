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

package org.peerfact.impl.overlay.dht.can.components;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * CanBootstrpManager know about every present peer
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanBootstrapManager implements BootstrapManager<CanNode> {

	/*
	 * all active nodes
	 */

	private static Logger log = SimLogger.getLogger(CanNode.class);

	private List<CanNode> activeNodes;

	public CanBootstrapManager() {
		this.activeNodes = new LinkedList<CanNode>();

	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> list = new LinkedList<TransInfo>();
		for (CanNode node : this.activeNodes) {
			list.add(node.getLocalOverlayContact().getTransInfo());
		}
		return list;
	}

	@Override
	public void registerNode(CanNode node) {
		this.activeNodes.add(node);

	}

	@Override
	public void unregisterNode(CanNode node) {
		this.activeNodes.remove(node);

	}

	public void unregisterNode(CanOverlayContact node) {
		log.debug("unregister Node: " + node.getOverlayID().toString());
		for (int i = 0; i < activeNodes.size(); i++) {
			if (activeNodes.get(i).getOverlayID().toString()
					.equals(node.getOverlayID().toString())) {
				activeNodes.remove(i);
			}
		}
	}

	/**
	 * give one node for the join operation. Either the CAN is uniform
	 * distributed (CanConfig.distribution==0) or the nodes are picked randomly.
	 * Randomly gives problems in scalling the CAN
	 * 
	 * @return CanOverlayContact
	 */
	@SuppressWarnings("unused")
	public CanNode pick() {
		if (activeNodes.isEmpty()) {
			return null;
		}

		CanNode pickedNode = null;
		if (CanConfig.distribution == 0) {
			pickedNode = activeNodes.get(0);
			int i = 0;
			while (pickedNode.getPeerStatus() != PeerStatus.PRESENT) {
				pickedNode = activeNodes.get(i);
				i++;
			}

			for (i = 0; i < activeNodes.size(); i++) {
				int squarePicked = (pickedNode.getLocalOverlayContact()
						.getArea()
						.getArea()[1] -
						pickedNode.getLocalOverlayContact().getArea().getArea()[0])
						*
						(pickedNode.getLocalOverlayContact().getArea()
								.getArea()[3] -
						pickedNode.getLocalOverlayContact().getArea().getArea()[2]);

				CanNode newTry = activeNodes.get(i);
				int newTrySquare = (newTry.getLocalOverlayContact().getArea()
						.getArea()[1] -
						newTry.getLocalOverlayContact().getArea().getArea()[0])
						*
						(newTry.getLocalOverlayContact().getArea().getArea()[3] -
						newTry.getLocalOverlayContact().getArea().getArea()[2]);
				if ((newTrySquare > squarePicked)
						&& newTry.getPeerStatus() == PeerStatus.PRESENT) {
					pickedNode = newTry;
				}
			}
		}
		else {
			pickedNode = activeNodes.get(Simulator.getRandom()
					.nextInt(activeNodes.size()));
			while (((pickedNode.getLocalOverlayContact().getArea().getArea()[1] - pickedNode
					.getLocalOverlayContact().getArea().getArea()[1]) == 1
					|| (pickedNode.getLocalOverlayContact().getArea().getArea()[3] - pickedNode
					.getLocalOverlayContact().getArea().getArea()[2]) == 1)
					|| pickedNode.getPeerStatus() != PeerStatus.PRESENT) {
				pickedNode = activeNodes.get(Simulator.getRandom()
						.nextInt(activeNodes.size()));
			}
		}

		return pickedNode;
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < activeNodes.size(); i++) {
			output.append((" "
					+ activeNodes.get(i).getOverlayID().toString()));

		}
		return output.toString();
	}

	public List<CanNode> getBootstrap() {
		List<CanNode> output = new LinkedList<CanNode>();
		for (int i = 0; i < activeNodes.size(); i++) {
			output.add(activeNodes.get(i));
		}
		return output;
	}

	public void update(CanNode node) {
		for (int i = 0; i < this.activeNodes.size(); i++) {
			if (activeNodes.get(i).getOverlayID().toString()
					.equals(node.getOverlayID().toString())) {
				activeNodes.remove(i);
			}
		}
		activeNodes.add(node);
		for (int i = 0; i < this.activeNodes.size(); i++) {
			if (activeNodes.get(i).getOverlayID().toString()
					.equals(node.getOverlayID().toString())) {
				activeNodes.get(i).setArea(activeNodes
						.get(i).getLocalOverlayContact().getArea());
				activeNodes.get(i)
						.getLocalOverlayContact()
						.getArea()
						.setVid(activeNodes.get(i)
								.getLocalOverlayContact().getArea().getVid());
			}
		}
	}

	public void visualize() {
		for (int j = 0; j < activeNodes.size(); j++) {
			log.debug("Own ID: "
					+ activeNodes.get(j).getLocalOverlayContact()
							.getOverlayID().toString()
					+ " own VID "
					+ activeNodes.get(j).getLocalOverlayContact()
							.getArea().getVid().toString()
					+ " own area "
					+ activeNodes.get(j).getLocalOverlayContact()
							.getArea().toString()
					+ " is allive: "
					+ activeNodes.get(j).getLocalOverlayContact()
							.isAlive()
					+ " Neighbours: ");
			try {
				for (int i = 0; i < activeNodes.get(j)
						.getNeighbours().size(); i++) {
					log.debug(activeNodes.get(j).getNeighbours()
							.get(i).getOverlayID().toString());
				}
				log.debug("VID Neighbours "
						+ activeNodes.get(j).getVIDNeighbours()[0]
								.getArea().getVid().toString()
						+ " "
						+ activeNodes.get(j).getVIDNeighbours()[0]
								.getOverlayID().toString()
						+ " "
						+ activeNodes.get(j).getVIDNeighbours()[1]
								.getArea().getVid().toString()
						+ " "
						+ activeNodes.get(j).getVIDNeighbours()[1]
								.getOverlayID().toString());
			} catch (Exception e) {
				log.error("Exception in CanBootstrapManager occured", e);
			}
		}
	}
}
