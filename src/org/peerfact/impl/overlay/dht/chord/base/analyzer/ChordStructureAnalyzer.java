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

package org.peerfact.impl.overlay.dht.chord.base.analyzer;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.AbstractFileStringAnalyzer;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordRoutingTable;
import org.peerfact.impl.util.oracle.GlobalOracle;


/**
 * Analyzer to regularly check the structure of the Chord ring
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class ChordStructureAnalyzer extends AbstractFileStringAnalyzer {

	public ChordStructureAnalyzer() {
		setFlushEveryLine(true);
		setOutputFileName("ChordStructure");
	}

	@Override
	protected List<String> generateHeadlineForMetrics() {
		List<String> fieldNames = new LinkedList<String>();
		fieldNames.add("PRESENT peers");
		fieldNames.add("TO_JOIN peers");
		fieldNames.add("CHURN peers");
		fieldNames.add("Succ ring size");
		fieldNames.add("Succ ring connected?");
		fieldNames.add("Succ num succ ring breaks");
		fieldNames.add("Succ ring connected (using backups)?");
		fieldNames.add("Succ ring includes all?");
		fieldNames.add("Succ num not included nodes");
		fieldNames.add("Pred ring size");
		fieldNames.add("Pred ring connected?");
		fieldNames.add("Pred num pred ring breaks");
		fieldNames.add("Pred ring connected (using backups)?");
		fieldNames.add("Pred ring includes all?");
		fieldNames.add("Pred num not included nodes");
		fieldNames.add("average cache size (EpiChord)");
		return fieldNames;
	}

	@Override
	protected void resetEvaluationMetrics() {
		// nothing to do
	}

	@Override
	protected List<String> generateEvaluationMetrics(long currentTime) {
		Map<ChordID, AbstractChordNode> cNodes = getAllPresentChordNodes();
		int nodeCount = cNodes.size();

		boolean succBasicRingConnected = true;
		int succBasicRingBreaks = 0;
		boolean succRingIncludesAll = true;
		int succNotIncluded = 0;
		boolean succExtendedRingConnected = true;
		int succCountedRingSize = 0;

		if (cNodes.size() > 0) {
			AbstractChordNode startNode = getStartingNode(cNodes);
			if (startNode != null) {
				succCountedRingSize++;
				cNodes.remove(startNode.getOverlayID());
				AbstractChordNode currentNode = startNode;

				while (succExtendedRingConnected && succRingIncludesAll
						&& !cNodes.isEmpty()) {
					if (currentNode != null) {
						AbstractChordContact nextContact = currentNode
								.getChordRoutingTable().getSuccessor();

						List<AbstractChordContact> allContacts = currentNode
								.getChordRoutingTable()
								.getAllDistantSuccessor();

						if (nextContact != null) {
							ChordID nextID = nextContact.getOverlayID();

							if (nextID.compareTo(startNode.getOverlayID()) == 0) {
								// Ring is closed

								if (!cNodes.isEmpty()) {
									succNotIncluded = cNodes.size();
									succRingIncludesAll = false;
								}

							} else {
								currentNode = cNodes.remove(nextID);

								if (currentNode == null) {
									succBasicRingConnected = false;
									succBasicRingBreaks++;

									for (AbstractChordContact alternativeSucc : allContacts) {
										currentNode = cNodes
												.remove(alternativeSucc
														.getOverlayID());
										if (currentNode != null) {
											break;
										}
									}

									if (currentNode == null) {
										succExtendedRingConnected = false;
									}
								}

								if (currentNode != null) {
									succCountedRingSize++;
								}
							}
						}
					}
				}
			}
		}

		cNodes = getAllPresentChordNodes();

		boolean predBasicRingConnected = true;
		int predBasicRingBreaks = 0;
		boolean predRingIncludesAll = true;
		int predNotIncluded = 0;
		boolean predExtendedRingConnected = true;
		int predCountedRingSize = 0;

		if (cNodes.size() > 0) {
			AbstractChordNode startNode = getStartingNode(cNodes);
			if (startNode != null) {
				predCountedRingSize++;
				cNodes.remove(startNode.getOverlayID());
				AbstractChordNode currentNode = startNode;

				while (predExtendedRingConnected && predRingIncludesAll
						&& !cNodes.isEmpty()) {
					if (currentNode != null) {
						AbstractChordContact nextContact = currentNode
								.getChordRoutingTable().getPredecessor();

						List<AbstractChordContact> allContacts = currentNode
								.getChordRoutingTable()
								.getAllDistantPredecessor();

						if (nextContact != null) {
							ChordID nextID = nextContact.getOverlayID();

							if (nextID.compareTo(startNode.getOverlayID()) == 0) {
								// Ring is closed

								if (!cNodes.isEmpty()) {
									predNotIncluded = cNodes.size();
									predRingIncludesAll = false;
								}

							} else {
								currentNode = cNodes.remove(nextID);

								if (currentNode == null) {
									predBasicRingConnected = false;
									predBasicRingBreaks++;

									for (AbstractChordContact alternativeSucc : allContacts) {
										if (alternativeSucc != null) {
											currentNode = cNodes
													.remove(alternativeSucc
															.getOverlayID());
										}
										if (currentNode != null) {
											break;
										}
									}

									if (currentNode == null) {
										predExtendedRingConnected = false;
									}
								}

								if (currentNode != null) {
									predCountedRingSize++;
								}
							}
						}
					}
				}
			}
		}

		List<String> measurements = new LinkedList<String>();
		measurements.add(Integer.valueOf(nodeCount).toString());
		measurements.add(Integer.valueOf(getNumOfJoiningNodes()).toString());
		measurements.add(Integer.valueOf(getNumOfChurnAffectedNodes())
				.toString());
		measurements.add(Integer.valueOf(succCountedRingSize).toString());
		measurements.add(Boolean.valueOf(succBasicRingConnected).toString());
		measurements.add(Integer.valueOf(succBasicRingBreaks).toString());
		measurements.add(Boolean.valueOf(succExtendedRingConnected).toString());
		measurements.add(Boolean.valueOf(succRingIncludesAll).toString());
		measurements.add(Integer.valueOf(succNotIncluded).toString());
		measurements.add(Integer.valueOf(predCountedRingSize).toString());
		measurements.add(Boolean.valueOf(predBasicRingConnected).toString());
		measurements.add(Integer.valueOf(predBasicRingBreaks).toString());
		measurements.add(Boolean.valueOf(predExtendedRingConnected).toString());
		measurements.add(Boolean.valueOf(predRingIncludesAll).toString());
		measurements.add(Integer.valueOf(predNotIncluded).toString());
		measurements.add(Double.valueOf(getAvgCacheSize()).toString());
		return measurements;
	}

	private static double getAvgCacheSize() {
		Map<ChordID, AbstractChordNode> nodes = getAllPresentChordNodes();
		int sum = 0;
		for (AbstractChordNode node : nodes.values()) {
			if (node instanceof ChordNode) {
				// only for EpiChord
				sum += ((ChordRoutingTable) node.getChordRoutingTable())
						.getChordCache().entries().size();
			}
		}
		if (nodes.size() > 0) {
			return Double.valueOf(sum) / Double.valueOf(nodes.size());
		} else {
			return 0;
		}
	}

	private static Map<ChordID, AbstractChordNode> getAllPresentChordNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		LinkedHashMap<ChordID, AbstractChordNode> chordNodes = new LinkedHashMap<ChordID, AbstractChordNode>();

		for (Host host : hosts) {
			OverlayNode<ChordID, AbstractChordContact> olNode = (OverlayNode<ChordID, AbstractChordContact>) host
					.getOverlay(AbstractChordNode.class);
			if (olNode != null && olNode instanceof AbstractChordNode) {
				AbstractChordNode cNode = (AbstractChordNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.PRESENT) {
					chordNodes.put(cNode.getOverlayID(), cNode);
				}
			}
		}
		return chordNodes;
	}

	private static AbstractChordNode getStartingNode(
			Map<ChordID, AbstractChordNode> nodes) {
		if (nodes.isEmpty()) {
			return null;
		}
		return nodes.entrySet().iterator().next().getValue();
	}

	private static int getNumOfChurnAffectedNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfChurnAffecteedNodes = 0;

		for (Host host : hosts) {
			OverlayNode<ChordID, AbstractChordContact> olNode = (OverlayNode<ChordID, AbstractChordContact>) host
					.getOverlay(AbstractChordNode.class);
			if (olNode != null && olNode instanceof AbstractChordNode) {
				AbstractChordNode cNode = (AbstractChordNode) olNode;

				if (cNode.absentCausedByChurn()) {
					numOfChurnAffecteedNodes++;
				}
			}
		}
		return numOfChurnAffecteedNodes;
	}

	private static int getNumOfJoiningNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfJoiningNodes = 0;

		for (Host host : hosts) {
			OverlayNode<ChordID, AbstractChordContact> olNode = (OverlayNode<ChordID, AbstractChordContact>) host
					.getOverlay(AbstractChordNode.class);
			if (olNode != null && olNode instanceof AbstractChordNode) {
				AbstractChordNode cNode = (AbstractChordNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.TO_JOIN) {
					numOfJoiningNodes++;
				}
			}
		}
		return numOfJoiningNodes;
	}

}
