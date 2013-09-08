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

package org.peerfact.impl.overlay.dht.pastry.analyzer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.AbstractFileStringAnalyzer;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.pastry.components.PastryNode;
import org.peerfact.impl.util.oracle.GlobalOracle;


/**
 * Analyzer to regularly check the structure of the pastry overlay
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class PastryStructureAnalyzer extends AbstractFileStringAnalyzer {

	public PastryStructureAnalyzer() {
		setFlushEveryLine(true);
		setOutputFileName("PastryStructure");
	}

	@Override
	protected List<String> generateHeadlineForMetrics() {
		List<String> fieldNames = new LinkedList<String>();
		fieldNames.add("PRESENT nodes");
		fieldNames.add("TO_JOIN nodes");
		fieldNames.add("CHURN nodes");
		fieldNames.add("ABSENT nodes");
		fieldNames.add("Neighbors_Sum");
		fieldNames.add("Neighbors_AVG");
		fieldNames.add("Neighbors_MIN");
		fieldNames.add("Neighbors_MAX");
		fieldNames.add("Leafs_AVG");
		fieldNames.add("Leafs_MIN");
		fieldNames.add("Leafs_MAX");
		return fieldNames;
	}

	@Override
	protected void resetEvaluationMetrics() {
		// nothing to do
	}

	@Override
	protected List<String> generateEvaluationMetrics(long currentTime) {

		int present = 0;
		int toJoin = 0;
		int absent = 0;
		int churn = 0;

		int sumOfNeighbors = 0;
		int numOfNeighborsMin = Integer.MAX_VALUE;
		int numOfNeighborsMax = Integer.MIN_VALUE;

		int sumOfLeafs = 0;
		int numOfLeafsMin = Integer.MAX_VALUE;
		int numOfLeafsMax = Integer.MIN_VALUE;

		Collection<PastryNode> pNodes = getPastryNodes();

		for (PastryNode n : pNodes) {
			PeerStatus s = n.getPeerStatus();

			if (s == PeerStatus.PRESENT) {
				present++;
				int numOfNeighbors = n.getNumOfAllNeighbors();
				if (numOfNeighbors < numOfNeighborsMin) {
					numOfNeighborsMin = numOfNeighbors;
				}
				if (numOfNeighbors > numOfNeighborsMax) {
					numOfNeighborsMax = numOfNeighbors;
				}
				sumOfNeighbors += numOfNeighbors;

				int numOfLeafs = n.getLeafSetNodes().size();
				if (numOfLeafs < numOfLeafsMin) {
					numOfLeafsMin = numOfLeafs;
				}
				if (numOfLeafs > numOfLeafsMax) {
					numOfLeafsMax = numOfLeafs;
				}
				sumOfLeafs += numOfLeafs;

			} else if (s == PeerStatus.TO_JOIN) {
				toJoin++;
			} else if (s == PeerStatus.ABSENT) {
				if (n.wantsToBePresent()) {
					churn++;
				} else {
					absent++;
				}
			}
		}

		double avgNumOfNeighbors = 0;
		double avgNumOfLeafs = 0;
		if (pNodes.size() > 0) {
			avgNumOfNeighbors = (double) sumOfNeighbors / present;
			avgNumOfLeafs = (double) sumOfLeafs / present;
		}

		List<String> measurements = new LinkedList<String>();
		measurements.add(Integer.valueOf(present).toString());
		measurements.add(Integer.valueOf(toJoin).toString());
		measurements.add(Integer.valueOf(churn).toString());
		measurements.add(Integer.valueOf(absent).toString());
		measurements.add(Integer.valueOf(sumOfNeighbors).toString());
		measurements.add(new Double(avgNumOfNeighbors).toString());
		measurements
				.add(Integer.valueOf(
						(numOfNeighborsMin == Integer.MAX_VALUE ? 0
								: numOfNeighborsMin)).toString());
		measurements
				.add(Integer.valueOf(
						(numOfNeighborsMax == Integer.MIN_VALUE ? 0
								: numOfNeighborsMax)).toString());
		measurements.add(new Double(avgNumOfLeafs).toString());
		measurements.add(Integer.valueOf(
				(numOfLeafsMin == Integer.MAX_VALUE ? 0
						: numOfLeafsMin)).toString());
		measurements.add(Integer.valueOf(
				(numOfLeafsMax == Integer.MIN_VALUE ? 0
						: numOfLeafsMax)).toString());
		return measurements;
	}

	private static Collection<PastryNode> getPastryNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		Collection<PastryNode> pNodes = new LinkedList<PastryNode>();

		for (Host host : hosts) {
			OverlayNode<?, ?> olNode = host.getOverlay(PastryNode.class);
			if (olNode != null && olNode instanceof PastryNode) {
				pNodes.add((PastryNode) olNode);
			}
		}
		return pNodes;
	}

}
