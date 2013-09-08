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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.api.overlay.ido.IDOOracle;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Configuration;
import org.peerfact.impl.overlay.informationdissemination.psense.util.SequenceNumber;


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
 * An Oracle for pSense. It computes the ideal set of to knowing peers for a
 * node. Therefore is used global knowledge, which must insert before using the
 * oracle.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class PSenseOracle implements IDOOracle {

	private PSense globalPSense;

	public PSenseOracle() {
		globalPSense = new PSense(PSenseID.EMPTY_PSENSE_ID);
	}

	@Override
	public void insertNodeInfos(List<IDONodeInfo> nodeInfos) {
		for (IDONodeInfo nodeInfo : nodeInfos) {
			PSenseNodeInfo temp = createPSenseNodeInfo(nodeInfo);
			globalPSense.updateNodeStorage((PSenseID) nodeInfo.getID(), temp);
		}
	}

	/**
	 * Create from the {@link IDONodeInfo} a {@link PSenseNodeInfo}.
	 * 
	 * @param nodeInfo
	 *            A nodeInfo.
	 * @return A nodeInfo of type PSenseNodeInfo
	 */
	private static PSenseNodeInfo createPSenseNodeInfo(IDONodeInfo nodeInfo) {
		int aoi = nodeInfo.getAoiRadius();
		Point position = nodeInfo.getPosition();
		PSenseID id = (PSenseID) nodeInfo.getID();
		PSenseContact contact = new PSenseContact(id, null);
		SequenceNumber seqNr = new SequenceNumber();
		List<PSenseID> receiverList = new Vector<PSenseID>();
		byte hops = 5;
		return new PSenseNodeInfo(aoi, position, contact, seqNr, receiverList,
				hops);
	}

	@Override
	public void reset() {
		globalPSense = new PSense(PSenseID.EMPTY_PSENSE_ID);
	}

	@Override
	public List<IDONodeInfo> getAllNeighbors(OverlayID<?> id, int aoi) {
		IDONodeInfo nodeInfoCenterNode = globalPSense
				.getNodeInfo((PSenseID) id);
		List<PSenseID> ignoreNodes = new Vector<PSenseID>();
		ignoreNodes.add((PSenseID) nodeInfoCenterNode.getID());

		Point position = nodeInfoCenterNode.getPosition();

		return getAllNeighbors(position, aoi, ignoreNodes);
	}

	/**
	 * Determine all Neighbors to one position and the given AOI for the
	 * globalPSense. The ignoreNodes contains the node for this is calculated.
	 * 
	 * @param position
	 *            The position of the node
	 * @param aoi
	 *            the AOI of a node.
	 * @param ignoreNodes
	 *            The node, which is on this position and should not be used as
	 *            result.
	 * @return A list of Neighbors, that are found in globalPSense for the AOI.
	 */
	private List<IDONodeInfo> getAllNeighbors(Point position, int aoi,
			List<PSenseID> ignoreNodes) {
		// The Set is used, for an easy handling of no duplicate entries
		Set<PSenseID> idSet = new LinkedHashSet<PSenseID>();

		// adds all nodes in AOI to the idSet
		List<PSenseID> inAOI = globalPSense.getAllNodesInArea(position, aoi,
				ignoreNodes);
		idSet.addAll(inAOI);

		// adds all sensor nodes to the idSet
		for (int sectorId = 0; sectorId < Configuration.NUMBER_SECTORS; sectorId++) {
			PSenseID temp = globalPSense.findSensorNode(position, aoi,
					(byte) sectorId, ignoreNodes);
			if (temp != null) {
				idSet.add(temp);
			}
		}

		// to all ids in idSets, build a list with IDONodeInfo.
		Iterator<PSenseID> iter = idSet.iterator();
		List<IDONodeInfo> result = new Vector<IDONodeInfo>();
		while (iter.hasNext()) {
			PSenseID id = iter.next();
			if (id != null) {
				result.add(globalPSense.getNodeInfo(id));
			}
		}

		return result;
	}
}
