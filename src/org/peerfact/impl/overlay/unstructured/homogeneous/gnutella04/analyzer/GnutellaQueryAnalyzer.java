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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaBootstrapManager;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryHitMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryMessage;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnutellaQueryAnalyzer implements TransAnalyzer {

	private Map<Integer, List<BigInteger>> queryTimeSteps = new LinkedHashMap<Integer, List<BigInteger>>();

	private Map<Integer, List<BigInteger>> queryHitTimeSteps = new LinkedHashMap<Integer, List<BigInteger>>();

	private Map<Integer, List<BigInteger>> queriesFailedTimeSteps = new LinkedHashMap<Integer, List<BigInteger>>();

	private Map<Integer, List<BigInteger>> queriesSuccededTimeSteps = new LinkedHashMap<Integer, List<BigInteger>>();

	private Map<Integer, List<Integer>> aktiveNodesTimeSteps = new LinkedHashMap<Integer, List<Integer>>();

	private Map<BigInteger, Integer> queriesWithTime = new LinkedHashMap<BigInteger, Integer>();

	private Map<BigInteger, Integer> queryHitsWithTime = new LinkedHashMap<BigInteger, Integer>();

	private Map<BigInteger, Integer> queryHops = new LinkedHashMap<BigInteger, Integer>();

	private List<Integer> timeSteps = new LinkedList<Integer>();

	private List<BigInteger> queryIds = new LinkedList<BigInteger>();

	private List<BigInteger> queryHitIds = new LinkedList<BigInteger>();

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(Writer output) {
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(1);

		/**
		 * for (int i=0; i< timeSteps.size(); i++){ int zeitRaum =
		 * timeSteps.get(i); List<BigInteger> ids = new
		 * LinkedList<BigInteger>(); log.debug(ids.size()); ids =
		 * queriesFailedTimeSteps.get(zeitRaum); log.debug(ids.size()); for (int
		 * q = 0; q< ids.size(); q++){ BigInteger id = ids.get(q);
		 * if(queryHitIds.contains(id)){ int hop = queryHops.get(id);
		 * queryHitHops.put(id, hop); ids.remove(id);
		 * queriesFailedTimeSteps.put(zeitRaum, ids);
		 * if(!queriesSuccededTimeSteps.containsKey(zeitRaum)){
		 * queriesSuccededTimeSteps.put(zeitRaum, new LinkedList<BigInteger>());
		 * } queriesSuccededTimeSteps.get(zeitRaum).add(id); } }
		 * 
		 * }
		 **/
		for (int i = 0; i < queryIds.size(); i++) {
			BigInteger descriptor = queryIds.get(i);
			int timeStep = queriesWithTime.get(descriptor);
			queryTimeSteps.get(timeStep).add(descriptor);
			if (queryHitIds.contains(descriptor)) {
				queriesSuccededTimeSteps.get(timeStep).add(descriptor);
			}
			if (!queryHitIds.contains(descriptor)) {
				queriesFailedTimeSteps.get(timeStep).add(descriptor);
			}
		}
		for (int i = 0; i < queryHitIds.size(); i++) {
			BigInteger descriptor = queryHitIds.get(i);
			int timeStep = queryHitsWithTime.get(descriptor);
			queryHitTimeSteps.get(timeStep).add(descriptor);
		}

		int queryTotal = 0;
		int querySuccededTotal = 0;
		int queryFailedTotal = 0;

		String f = Simulator.getOuputDir().getAbsolutePath() + File.separator
				+ "gnutella04-GnutellaQuery.dat";
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("	Gnutella-Analyzer	\n");
			out.write("	-----------------------	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("Output of Queries and QueryHits for periods	\n");
			out.write("	\n");
			out
					.write("time[sec]		queries		successfully		Failed		Hops	active nodes	\n");
			for (int i = 0; i < timeSteps.size(); i++) {
				int step1 = timeSteps.get(i);
				int step2 = step1 * 10 + 10;

				int activeTotal = 0;
				int secCounter = 0;
				int aktiveDurchschnitt = 0;
				double hitHops = 0;
				double zaehler = 0;
				double hopsDurchschnitt = 0;
				List<BigInteger> hits = new LinkedList<BigInteger>();
				hits = queriesSuccededTimeSteps.get(step1);
				for (int j = 0; j < hits.size(); j++) {
					BigInteger descriptor = hits.get(j);
					if (queryHops.containsKey(descriptor)) {
						hitHops += queryHops.get(descriptor);
						zaehler++;
					}
				}
				if (zaehler != 0) {
					hopsDurchschnitt = hitHops / zaehler;
				}

				List<Integer> activeNodes = aktiveNodesTimeSteps.get(step1);
				for (int z = 0; z < activeNodes.size(); z++) {
					activeTotal += activeNodes.get(z);
					secCounter++;
				}
				if (secCounter != 0) {
					aktiveDurchschnitt = activeTotal / secCounter;
				}
				queryTotal += queryTimeSteps.get(step1).size();
				querySuccededTotal += queriesSuccededTimeSteps.get(step1)
						.size();
				queryFailedTotal += queriesFailedTimeSteps.get(step1).size();

				// List<Integer> activeNodes = aktiveNodesTimeSteps.get(step1);

				// int queriesSucceded = queryTimeSteps.get(step1).size() -
				// queriesFailedTimeSteps.get(step1).size();
				out.write(step1 * 10 + "-" + step2 + "			"
						+ queryTimeSteps.get(step1).size() + "			"
						+ queriesSuccededTimeSteps.get(step1).size() + "				"
						+ queriesFailedTimeSteps.get(step1).size() + "			"
						+ n.format(hopsDurchschnitt) + "			"
						+ aktiveDurchschnitt + "	\n");
			}
			out.write("sum: 			" + queryTotal + "				" + querySuccededTotal
					+ "				" + queryFailedTotal + "	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("time[sec]	number of queryHits	\n");
			int queryHitsTotal = 0;
			for (int i = 0; i < timeSteps.size(); i++) {
				int temp = timeSteps.get(i);
				if (queryHitTimeSteps.containsKey(temp)) {
					int zeit = temp * 10 + 10;
					queryHitsTotal += queryHitTimeSteps.get(temp).size();
					out.write(temp * 10 + "-" + zeit + "				"
							+ queryHitTimeSteps.get(temp).size() + "	\n");
				}
			}

			out.write("	\n");
			out.write("sum:				" + queryHitsTotal + " \n");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {
		Simulator.getInstance();
		long time = Simulator.getCurrentTime();
		int timeStep = (int) (time / 10000000);
		if (!timeSteps.contains(timeStep)) {
			timeSteps.add(timeStep);
		}
		if (!queriesFailedTimeSteps.containsKey(timeStep)) {
			queriesFailedTimeSteps.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!queryTimeSteps.containsKey(timeStep)) {
			queryTimeSteps.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!queryHitTimeSteps.containsKey(timeStep)) {
			queryHitTimeSteps.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!queriesSuccededTimeSteps.containsKey(timeStep)) {
			queriesSuccededTimeSteps
					.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!aktiveNodesTimeSteps.containsKey(timeStep)) {
			aktiveNodesTimeSteps.put(timeStep, new LinkedList<Integer>());
		}

		Message message = msg.getPayload();

		if (message instanceof QueryMessage) {
			QueryMessage query = (QueryMessage) message.getPayload();
			BigInteger descriptor = query.getDescriptor();
			if (!queriesWithTime.containsKey(descriptor)) {
				queriesWithTime.put(descriptor, timeStep);
			}
			if (!queryIds.contains(descriptor)) {
				queryIds.add(descriptor);
			}
			if (!queryHops.containsKey(descriptor)) {
				queryHops.put(descriptor, query.getHops());
			} else if (queryHops.containsKey(descriptor)) {
				int oldHops = queryHops.get(descriptor);
				if (query.getHops() > oldHops) {
					queryHops.put(descriptor, query.getHops());
				}
			}
			if (!queryTimeSteps.containsKey(timeStep)) {
				queryTimeSteps.put(timeStep, new LinkedList<BigInteger>());
			}
			// queryTimeSteps.get(timeStep).add(descriptor);

			if (!queriesFailedTimeSteps.containsKey(timeStep)) {
				queriesFailedTimeSteps.put(timeStep,
						new LinkedList<BigInteger>());
			}
			// queriesFailedTimeSteps.get(timeStep).add(descriptor);

			if (!aktiveNodesTimeSteps.containsKey(timeStep)) {
				aktiveNodesTimeSteps.put(timeStep, new LinkedList<Integer>());
			}
			aktiveNodesTimeSteps.get(timeStep).add(
					GnutellaBootstrapManager.getInstance().getSize());

		} else if (message instanceof QueryHitMessage) {
			QueryHitMessage queryHit = (QueryHitMessage) message.getPayload();
			BigInteger descriptor = queryHit.getDescriptor();

			if (!queryHitsWithTime.containsKey(descriptor)) {
				queryHitsWithTime.put(descriptor, timeStep);
			}
			if (!queryHitIds.contains(descriptor)) {
				queryHitIds.add(descriptor);
			}
			if (!queryHitTimeSteps.containsKey(timeStep)) {
				queryHitTimeSteps.put(timeStep, new LinkedList<BigInteger>());
			}
			// queryHitTimeSteps.get(timeStep).add(descriptor);

		}
	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		Simulator.getInstance();
		long time = Simulator.getCurrentTime();
		int timeStep = (int) (time / 10000000);
		if (!timeSteps.contains(timeStep)) {
			timeSteps.add(timeStep);
		}
		if (!queriesFailedTimeSteps.containsKey(timeStep)) {
			queriesFailedTimeSteps.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!queryTimeSteps.containsKey(timeStep)) {
			queryTimeSteps.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!queryHitTimeSteps.containsKey(timeStep)) {
			queryHitTimeSteps.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!queriesSuccededTimeSteps.containsKey(timeStep)) {
			queriesSuccededTimeSteps
					.put(timeStep, new LinkedList<BigInteger>());
		}
		if (!aktiveNodesTimeSteps.containsKey(timeStep)) {
			aktiveNodesTimeSteps.put(timeStep, new LinkedList<Integer>());
		}

	}

}
