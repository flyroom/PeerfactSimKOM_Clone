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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers;

import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.AbstractSkyNetAnalyzer;
import org.peerfact.impl.simengine.SimulationEvent;
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
 * This class implements {@link OperationAnalyzer} and is therefore responsible
 * of monitoring the started and finished operations. Besides this
 * monitoring-functionality, <code>OPAnalyzer</code> allows every host to inform
 * itself about the completed operations, that it started. This information
 * comprises the number of finished operations, the duration to finish the
 * operations as well as the information if the operation was successfully
 * finished or not.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class OPAnalyzer extends AbstractSkyNetAnalyzer implements
		OperationAnalyzer {

	private static Logger log = SimLogger.getLogger(OPAnalyzer.class);

	private LinkedHashMap<Integer, Long> startedOperations;

	private LinkedHashMap<Long, Vector<OPAnalyzerEntry>> completedOperations;

	// private LinkedHashMap<Long, Long> succeededOperations;

	// private LinkedHashMap<Long, Long> failedOperations;

	private long succeededOps;

	private long failedOps;

	private static IPv4NetID serverNetId;

	public OPAnalyzer() {
		super();
		succeededOps = 0;
		failedOps = 0;
	}

	@Override
	protected void initialize() {
		// not needed within this analyzer
	}

	@Override
	protected void finish() {
		log.fatal("Unfinished Operations:" + startedOperations.size());
		/*
		 * Iterator<Long> iter = succeededOperations.keySet().iterator(); long
		 * counter = 0; while (iter.hasNext()) { counter = counter +
		 * succeededOperations.get(iter.next()); }
		 */
		log.fatal("Succeeded Operations: " + succeededOps);
		log.fatal("Failed Operations: " + failedOps);
	}

	@Override
	public void operationFinished(Operation<?> op) {
		if (runningAnalyzer) {
			long time = Simulator.getCurrentTime();
			Long startTime = startedOperations.remove(op.getOperationID());

			if (startTime == null) {
				return;
			}

			IPv4NetID ip = (IPv4NetID) op.getComponent().getHost()
					.getNetLayer().getNetID();
			if (compareIPWithServerIP(ip)) {
				if (op.isSuccessful()) {
					if (completedOperations.containsKey(ip.getID())) {
						Vector<OPAnalyzerEntry> vec = completedOperations
								.remove(ip.getID());
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										true));
						completedOperations.put(ip.getID(), vec);
					} else {
						Vector<OPAnalyzerEntry> vec = new Vector<OPAnalyzerEntry>();
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										true));
						completedOperations.put(ip.getID(), vec);
					}
					/*
					 * Long temp = succeededOperations.remove(ip.getID()); if
					 * (temp == null) { succeededOperations.put(ip.getID(), 1l);
					 * } else { succeededOperations.put(ip.getID(),
					 * temp.longValue() + 1); }
					 */
					succeededOps++;
				} else {
					if (completedOperations.containsKey(ip.getID())) {
						Vector<OPAnalyzerEntry> vec = completedOperations
								.remove(ip.getID());
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										false));
						completedOperations.put(ip.getID(), vec);
					} else {
						Vector<OPAnalyzerEntry> vec = new Vector<OPAnalyzerEntry>();
						vec
								.add(new OPAnalyzerEntry(op, time - startTime,
										false));
						completedOperations.put(ip.getID(), vec);
					}
					/*
					 * Long temp = failedOperations.remove(ip.getID()); if (temp
					 * == null) { failedOperations.put(ip.getID(), 1l); } else {
					 * failedOperations.put(ip.getID(), temp.longValue() + 1); }
					 */
					failedOps++;
				}
			} else {
				log.warn("ServerIP-hit");
			}
		}
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		if (runningAnalyzer) {
			if (startedOperations.containsKey(op.getOperationID())) {
				log.fatal("Should not happen");
			} else {
				startedOperations.put(op.getOperationID(), Simulator
						.getCurrentTime());
			}
		}

	}

	// ----------------------------------------------------------------------
	// methods for getting the collected data of this analyzer
	// ----------------------------------------------------------------------

	/**
	 * This method returns all finished operations, which the specified host
	 * started during a predefined interval.
	 * 
	 * @param id
	 *            contains the {@link NetID} of the host
	 * @return a <code>Vector</code> with all finished operations
	 */
	public Vector<OPAnalyzerEntry> getCompletedOperations(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		return completedOperations.remove(ip.getID());
	}

	public void setSimulationSize(int size) {
		double capacity = Math.ceil(size / 0.75d);
		startedOperations = new LinkedHashMap<Integer, Long>();
		completedOperations = new LinkedHashMap<Long, Vector<OPAnalyzerEntry>>(
				(int) capacity);
		// succeededOperations = new LinkedHashMap<Long, Long>((int) capacity);
		// failedOperations = new LinkedHashMap<Long, Long>((int) capacity);
	}

	public static void setServerNetId(IPv4NetID serverID) {
		serverNetId = serverID;
	}

	private static boolean compareIPWithServerIP(NetID id) {
		if (serverNetId != null) {
			if (!((IPv4NetID) id).getID().equals(serverNetId.getID())) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		// not needed within this analyzer
	}

}
