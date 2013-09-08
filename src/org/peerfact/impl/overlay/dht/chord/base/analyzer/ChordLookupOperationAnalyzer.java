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

import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.AbstractFileStringAnalyzer;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.operations.LookupOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.oracle.GlobalOracle;
import org.peerfact.impl.util.stats.StatHelper;


/**
 * Analyzer to generate statistics for chord lookup operations.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @author Thim Striothmann
 * @version 1.0, 12/21/2011
 */
public class ChordLookupOperationAnalyzer extends AbstractFileStringAnalyzer
		implements OperationAnalyzer {

	private int sumOfStartedRequests;

	private int sumOfSuccesfullRequests;

	private int sumOfFailedRequests;

	private final List<Integer> hopCounts = new LinkedList<Integer>();

	private final List<Long> durations = new LinkedList<Long>();

	StatHelper<Integer> statsInteger = new StatHelper<Integer>();

	StatHelper<Long> statsLong = new StatHelper<Long>();

	public ChordLookupOperationAnalyzer() {
		setFlushEveryLine(true);
		setOutputFileName("ChordLookupOperation");
	}

	@Override
	protected List<String> generateHeadlineForMetrics() {
		List<String> fieldNames = new LinkedList<String>();
		fieldNames.add("PRESENT peers");
		fieldNames.add("TO_JOIN peers");
		fieldNames.add("CHURN peers");
		fieldNames.add("NumOfLookups");
		fieldNames.add("Lookup hops(avg)");
		fieldNames.add("Lookup hops(st.Dev.)");
		fieldNames.add("Lookup hops(st.Dev.Minus)");
		fieldNames.add("Lookup hops(st.Dev.Plus)");
		fieldNames.add("Lookup hops(median)");
		fieldNames.add("Lookup duration(avg)");
		fieldNames.add("Lookup duration(st.Dev.)");
		fieldNames.add("Lookup duration(st.Dev.Minus)");
		fieldNames.add("Lookup duration(st.Dev.Plus)");
		fieldNames.add("Lookup duration(median)");
		fieldNames.add("Number of started Requests(sum)");
		fieldNames.add("Number of successfull Requests(sum)");
		fieldNames.add("Number of failed Requests(sum)");
		return fieldNames;
	}

	@Override
	protected void resetEvaluationMetrics() {
		hopCounts.clear();
		durations.clear();
	}

	@Override
	protected List<String> generateEvaluationMetrics(long currentTime) {
		List<String> measurements = new LinkedList<String>();
		measurements.add(Integer.valueOf(getNumOfPresentNodes()).toString());
		measurements.add(Integer.valueOf(getNumOfJoiningNodes()).toString());
		measurements.add(Integer.valueOf(getNumOfChurnAffectedNodes())
				.toString());

		measurements.add(Integer.valueOf(hopCounts.size()).toString());

		Double[] avgAndStDev = statsInteger
				.computeAverageAndStandardDeviation(hopCounts);

		measurements
				.add(avgAndStDev != null ? avgAndStDev[0] != null ? new Double(
						avgAndStDev[0])
						.toString() : null : null);
		measurements
				.add(avgAndStDev != null ? avgAndStDev[1] != null ? new Double(
						avgAndStDev[1])
						.toString() : null : null);
		measurements
				.add(avgAndStDev != null ? avgAndStDev[2] != null ? new Double(
						avgAndStDev[2])
						.toString() : null : null);
		measurements
				.add(avgAndStDev != null ? avgAndStDev[3] != null ? new Double(
						avgAndStDev[3])
						.toString() : null : null);
		measurements.add(hopCounts.size() > 0 ? new Double(statsInteger
				.median(hopCounts)).toString() : null);

		avgAndStDev = statsLong.computeAverageAndStandardDeviation(durations);

		measurements
				.add(avgAndStDev != null ? avgAndStDev[0] != null ? Double
						.valueOf(
								avgAndStDev[0]
										/ Simulator.SECOND_UNIT).toString()
						: null
						: null);
		measurements
				.add(avgAndStDev != null ? avgAndStDev[1] != null ? new Double(
						avgAndStDev[1]
								/ Simulator.SECOND_UNIT).toString() : null
						: null);
		measurements
				.add(avgAndStDev != null ? avgAndStDev[2] != null ? new Double(
						avgAndStDev[2]
								/ Simulator.SECOND_UNIT).toString() : null
						: null);
		measurements
				.add(avgAndStDev != null ? avgAndStDev[3] != null ? new Double(
						avgAndStDev[3]
								/ Simulator.SECOND_UNIT).toString() : null
						: null);
		measurements.add(durations.size() > 0 ? new Double(statsLong
				.median(durations) / Simulator.SECOND_UNIT).toString() : null);
		measurements.add(Integer.valueOf(sumOfStartedRequests).toString());
		measurements.add(Integer.valueOf(sumOfSuccesfullRequests).toString());
		measurements.add(Integer.valueOf(sumOfFailedRequests).toString());

		hopCounts.clear();
		durations.clear();

		return measurements;
	}

	private static int getNumOfPresentNodes() {
		List<Host> hosts = GlobalOracle.getHosts();
		int numOfChurnAffecteedNodes = 0;

		for (Host host : hosts) {
			OverlayNode<ChordID, AbstractChordContact> olNode = (OverlayNode<ChordID, AbstractChordContact>) host
					.getOverlay(AbstractChordNode.class);
			if (olNode != null && olNode instanceof AbstractChordNode) {
				AbstractChordNode cNode = (AbstractChordNode) olNode;

				if (cNode.getPeerStatus() == PeerStatus.PRESENT) {
					numOfChurnAffecteedNodes++;
				}
			}
		}
		return numOfChurnAffecteedNodes;
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

	@Override
	public void operationInitiated(Operation<?> op) {
		if (op instanceof LookupOperation) {
			sumOfStartedRequests++;
		}
	}

	@Override
	public void operationFinished(Operation<?> op) {

		if (op instanceof LookupOperation) {

			if (op.isSuccessful()) {
				LookupOperation lookupOp = (LookupOperation) op;
				hopCounts.add(lookupOp.getLookupHopCount());
				durations.add(lookupOp.getDuration());
				sumOfSuccesfullRequests++;

			} else {
				sumOfFailedRequests++;
			}
		}
	}

}
