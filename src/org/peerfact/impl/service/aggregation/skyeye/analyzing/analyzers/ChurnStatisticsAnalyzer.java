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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.api.analyzer.ChurnAnalyzer;
import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.SkyNetSimulationType;
import org.peerfact.api.service.skyeye.SkyNetSimulationType.SimulationType;
import org.peerfact.api.service.skyeye.overlay2SkyNet.AnalyzerDelegator;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIServerNode;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetBatchSimulator;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SupportPeerInfo;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.AbstractSkyNetAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing.StatusPostProcessor;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeEntry;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsEntry;
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
 * This class performs two tasks. Since it implements {@link ChurnAnalyzer} and
 * {@link ConnectivityAnalyzer}, it keeps track of the absent and present peers
 * in the overlay as well as of the connected and unconnected hosts. Every
 * class, which needs the aforementioned information can access this analyzer
 * and obtain the required information about the amount of present peers etc.<br>
 * Besides this functionality, this class monitors the number of nodes, support
 * peers, sub-coordinators, free capacities and leafs at each level within the
 * SkyNet-tree during a simulation. For this reason, the amount of peers within
 * the tree is periodically checked (the period is determined by
 * <code>updateInterval</code>) and written into <code>idLevelMatrix</code>. The
 * collected data is also periodically written in files via Serialization to
 * avoid a large amount of data. The files can be found in the
 * statusData-directory. The interval for the Serialization is determined by
 * <code>writeInterval</code>.<br>
 * In case of the Serialization, it is important to maintain the writing-order
 * of the objects to avoid, that the corresponding post-processor-class (
 * {@link StatusPostProcessor} in this case) cannot read the objects and throws
 * an exception.<br>
 * The order for the Serialization is as follows: <li>
 * <code>oos.writeObject(idLevelMatrix);</code> <li>
 * <code>oos.writeInt(yMax);</code>
 * 
 * Further requirements led to the addition of another functionality for this
 * class. It now also traces the root changes over time. This data is summed up
 * during a update interval and directly written to a .dat file. Since the trace
 * of the root changes only include a small amount of data, I decided not to use
 * serialization and post processing this time. (Julius Rueckert)
 * 
 * @author Dominik Stingl (extended by Julius Rueckert)
 *         <peerfact@kom.tu-darmstadt.de>
 * @version 1.1, 01.02.2010
 * 
 */
public class ChurnStatisticsAnalyzer extends AbstractSkyNetAnalyzer implements
		ChurnAnalyzer, ConnectivityAnalyzer {

	private static Logger log = SimLogger
			.getLogger(ChurnStatisticsAnalyzer.class);

	private static String DATA_PATH = Constants.TMP_DIR + File.separator
			+ "statusData";

	private AnalyzerDelegator analyzerDelegator;

	private LinkedHashMap<Long, Host> offlinePeers;

	private static LinkedHashMap<Long, Host> onlinePeers;

	private int simulationSize;

	private static boolean activated = false;

	private final long writeInterval = 5 * Simulator.MINUTE_UNIT;

	private final long updateInterval = 60 * Simulator.SECOND_UNIT;

	private int periodeCounter;

	private final TreeMap<Integer, TreeMap<Integer, Double[]>> idLevelMatrix;

	private int yMax;

	/*
	 * Some variables for the tracing of root changes
	 */

	private int numOfRootPosLosses = 0;

	private int numOfRootPosTakeovers = 0;

	private int numOfCurrentRoots = 0;

	private final String PATH = "data";

	private final String DATA_FILENAME = "RootChanges.dat";

	private FileWriter rootChangeWriter;

	public ChurnStatisticsAnalyzer() {
		super();
		activated = true;
		idLevelMatrix = new TreeMap<Integer, TreeMap<Integer, Double[]>>();
		periodeCounter = 1;
		yMax = 0;
	}

	public static boolean isActivated() {
		return activated;
	}

	/**
	 * In the beginning of a simulation all hosts are online, so all hosts are
	 * delivered to this class through a call of this method.
	 * 
	 * @param list
	 *            contains the list of all hosts of a simulation.
	 */
	public static void setCreatedHost(List<Host> list) {
		Host host;
		for (int i = 0; i < list.size(); i++) {
			host = list.get(i);
			if (host.getOverlay(CIServerNode.class) == null) {
				onlinePeers.put(((IPv4NetID) host.getNetLayer().getNetID())
						.getID(), host);
			}
		}
	}

	@Override
	protected void initialize() {
		initWriteDirectory(DATA_PATH, true);
		long time = Simulator.getCurrentTime();
		Simulator.scheduleEvent("Update", time, this,
				SimulationEvent.Type.STATUS);
		Simulator.scheduleEvent("Status", time + writeInterval, this,
				SimulationEvent.Type.STATUS);

		// DEBUX only for debugging
		// long timePoint = 16200 * SkyNetConstants.DIVISOR_FOR_SECOND;
		// Simulator.scheduleEvent("Average", timePoint, this,
		// SimulationEvent.Type.STATUS);

		initRootChangeFile();
	}

	@Override
	protected void finish() {
		// writing down the data-Maps
		long time = Simulator.getCurrentTime();
		long delta = System.currentTimeMillis();
		File f = new File(DATA_PATH + File.separatorChar + "temp-"
				+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(f));
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Started to write the status-map");
			oos.writeObject(idLevelMatrix);
			oos.writeInt(yMax);

			oos.close();
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Finished to write the query-map in "
					+ (System.currentTimeMillis() - delta) + "ms");
			idLevelMatrix.clear();

			rootChangeWriter.close();
			rootChangeWriter = null;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextInterSessionTime(long time) {
		if (runningAnalyzer) {
			// not needed
		}
	}

	/**
	 * This method returns the amount of hosts, which are online.
	 * 
	 * @return the number of connected hosts
	 */
	public static int getNoOfOnlinePeers() {
		return onlinePeers.size();
	}

	/**
	 * This method returns the amount of hosts, which are offline.
	 * 
	 * @return the number of disconnected hosts
	 */
	public int getNoOfOfflinePeers() {
		return offlinePeers.size();
	}

	/**
	 * This method checks, if a peer is present, which means that it
	 * participates within the overlay.
	 * 
	 * @param id
	 *            contains the {@link NetID} of a peer, for which this check is
	 *            executed
	 * @return <code>true</code>, if the peer is present, <code>false</code>
	 *         otherwise
	 */
	public boolean isPeerPresent(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		if (onlinePeers.containsKey(ip.getID())) {
			AbstractOverlayNode<?, ?> node = analyzerDelegator
					.getAbstractOverlayNode(onlinePeers.get(ip.getID()));
			if (node.getPeerStatus().equals(PeerStatus.PRESENT)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * This method returns the amount of peers, which are present in the
	 * overlay.
	 * 
	 * @return the number of present peers
	 */
	public int getNoOfPresentPeers() {
		int counter = 0;
		Iterator<Long> iter = onlinePeers.keySet().iterator();
		while (iter.hasNext()) {
			Host nextHost = onlinePeers.get(iter.next());

			AbstractOverlayNode<?, ?> node = null;

			if (nextHost != null) {
				node = analyzerDelegator.getAbstractOverlayNode(nextHost);
			}
			if (node != null && node.getPeerStatus().equals(PeerStatus.PRESENT)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * This method returns the amount of peers, which are absent from the
	 * overlay.
	 * 
	 * @return the number of absent peers
	 */
	public int getNoOfAbsentPeers() {
		int counter = 0;
		Iterator<Long> iter = offlinePeers.keySet().iterator();
		while (iter.hasNext()) {
			Host nextHost = offlinePeers.get(iter.next());

			AbstractOverlayNode<?, ?> node = null;

			if (nextHost != null) {
				node = analyzerDelegator.getAbstractOverlayNode(nextHost);
			}
			if (node != null && node.getPeerStatus().equals(PeerStatus.ABSENT)) {
				counter++;
			}
		}
		return counter;
	}

	@Override
	public void nextSessionTime(long time) {
		if (runningAnalyzer) {
			//
		}

	}

	@Override
	public void offlineEvent(Host host) {
		if (runningAnalyzer) {
			if (host != null) {
				IPv4NetID ip = (IPv4NetID) host.getNetLayer().getNetID();
				Host h = onlinePeers.remove(ip.getID());

				offlinePeers.put(ip.getID(), h);
			} else {
				log.fatal("No peer with the desired is online to go offline");
			}
		}

	}

	@Override
	public void onlineEvent(Host host) {
		if (runningAnalyzer) {
			IPv4NetID ip = (IPv4NetID) host.getNetLayer().getNetID();
			Host h = offlinePeers.remove(ip.getID());
			if (h != null) {
				onlinePeers.put(ip.getID(), host);
			} else {
				log.fatal("No peer with ip " + ip.toString()
						+ "is offline to go online");
			}
		}

	}

	public void setAnalyzerDelegator(AnalyzerDelegator analyzerDelegator) {
		log.warn("Calling setAnalyzerDelegator() with "
				+ analyzerDelegator.toString());
		this.analyzerDelegator = analyzerDelegator;
	}

	public void setSimulationSize(int size) {
		simulationSize = size;
		double capacity = Math.ceil(size / 0.75d);
		onlinePeers = new LinkedHashMap<Long, Host>((int) capacity);
		offlinePeers = new LinkedHashMap<Long, Host>((int) capacity);
	}

	public int getSimulationSize() {
		if (SkyNetSimulationType.getSimulationType().equals(
				SimulationType.NAPSTER_SIMULATION)) {
			return simulationSize - 1;
		} else {
			return simulationSize;
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			// Status
			if (((String) se.getData()).equals("Status")) {
				// writing down the data-Maps
				long time = Simulator.getCurrentTime();
				long delta = System.currentTimeMillis();
				File f = new File(DATA_PATH + File.separatorChar + "temp-"
						+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");
				try {
					ObjectOutputStream oos = new ObjectOutputStream(
							new FileOutputStream(f));
					log.warn("@ " + Simulator.getFormattedTime(time)
							+ " Started to write the status-map");
					oos.writeObject(idLevelMatrix);
					oos.writeInt(yMax);

					oos.close();
					log.warn("@ " + Simulator.getFormattedTime(time)
							+ " Finished to write the status-map in "
							+ (System.currentTimeMillis() - delta) + "ms");
					idLevelMatrix.clear();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Simulator.scheduleEvent("Status", time + writeInterval, this,
						SimulationEvent.Type.STATUS);

				// Update
			} else if (((String) se.getData()).equals("Update")) {
				long time = Simulator.getCurrentTime();
				Iterator<Long> ipIter = onlinePeers.keySet().iterator();
				Long ip = null;
				TreeMap<Integer, Double[]> secondDim = null;
				Double[] value = null;
				int level = -1;
				while (ipIter.hasNext()) {
					ip = ipIter.next();
					AbstractOverlayNode<?, ?> node = analyzerDelegator
							.getAbstractOverlayNode(onlinePeers.get(ip));

					if (node != null
							&& node.getPeerStatus().equals(PeerStatus.PRESENT)) {
						SkyNetNode skynetNode = ((SkyNetNode) onlinePeers.get(
								ip).getOverlay(SkyNetNode.class));

						if (skynetNode.getSkyNetNodeInfo() != null) {
							level = skynetNode.getSkyNetNodeInfo().getLevel();
						}

						boolean isSupportPeer = skynetNode.isSupportPeer();

						int numOfLocalSubCoordinators = skynetNode
								.getMetricUpdateStrategy().getStorage()
								.getListOfSubCoordinators().size();

						boolean isLeaf = (numOfLocalSubCoordinators == 0);

						int observedLevelFromRoot = skynetNode
								.getSkyNetNodeInfo().getObservedLevelFromRoot();

						int numOfAttributesAsCoordinator = skynetNode
								.getAttributeInputStrategy()
								.getAttributeStorage()
								.getActualAmountOfEntriesOfCo();

						TreeMap<Double, AttributeEntry> coordinatorAttributes = skynetNode
								.getAttributeInputStrategy()
								.getAttributeStorage().getActualEntriesOfCo();

						/*
						 * Determine the number of Attributes the optional
						 * SupportPeer has
						 */

						SupportPeerInfo supportPeer = skynetNode
								.getAttributeInputStrategy().getSpHandler()
								.getActiveSupportPeer();

						if (supportPeer != null) {
							NetID netID = supportPeer.getNodeInfo()
									.getTransInfo().getNetId();
							if (netID instanceof IPv4NetID) {
								Long id = ((IPv4NetID) netID).getID();
								Host spHost = onlinePeers.get(id);
								OverlayNode<?, ?> spNode = null;
								if (spHost != null) {
									spNode = spHost
											.getOverlay(SkyNetNode.class);
								}
								if (spNode != null) {
									SkyNetNode spSkynetNode = (SkyNetNode) spNode;

									numOfAttributesAsCoordinator += spSkynetNode
											.getAttributeInputStrategy()
											.getAttributeStorage()
											.getActualAmountOfEntriesOfSP();

									coordinatorAttributes.putAll(spSkynetNode
											.getAttributeInputStrategy()
											.getAttributeStorage()
											.getActualEntriesOfCo());
								}
							}
						}

						/*
						 * Determine the total quality of the attributes
						 */
						Double totalRankOfAttributes = 0d;

						for (Double rank : coordinatorAttributes.keySet()) {
							totalRankOfAttributes += rank;
						}

						/*
						 * Only count this peer if it has a level
						 */
						if (observedLevelFromRoot == -1) {
							continue;
						}

						/*
						 * This changes the traced level to the observed level
						 * that is distributed via the ACKs of the metric
						 * updates.
						 */
						level = observedLevelFromRoot;

						int freeCapacity = SkyNetPropertiesReader.getInstance()
								.getIntProperty("SkyNetTreeBranchingFactor")
								- numOfLocalSubCoordinators;

						secondDim = idLevelMatrix.remove(periodeCounter);
						if (secondDim == null) {
							secondDim = new TreeMap<Integer, Double[]>();
							/*
							 * Format of dataArr:
							 * 
							 * #peers, #supportPeers,
							 * #subCoordinatorsOneLevelDeeper, #leafs,
							 * #FreeCapacities(BF-#SubCoordinators)
							 */
							Double[] dataArr = { 1d, isSupportPeer ? 1d : 0d,
									(double) numOfLocalSubCoordinators,
									isLeaf ? 1d : 0d, (double) freeCapacity,
									(double) numOfAttributesAsCoordinator,
									totalRankOfAttributes };
							secondDim.put(Integer.valueOf(level), dataArr);
							idLevelMatrix.put(Integer.valueOf(periodeCounter),
									secondDim);
							yMax = Math.max(yMax, secondDim.lastKey()
									.intValue());
						} else {
							value = secondDim.remove(level);
							if (value == null) {
								Double[] valueArr = { 0d, 0d, 0d, 0d, 0d, 0d,
										0d };
								value = valueArr;
							}
							Double numOfPeers = value[0] + 1;
							Double numOfSupportPeers = isSupportPeer ? (value[1] + 1)
									: value[1];
							Double numOfSubCoords = value[2]
									+ numOfLocalSubCoordinators;
							Double numOfLeafs = isLeaf ? (value[3] + 1)
									: value[3];
							Double numOfFreeCapacities = value[4]
									+ freeCapacity;
							Double numOfAttributes = value[5]
									+ numOfAttributesAsCoordinator;
							Double rankOfAttributes = value[6]
									+ totalRankOfAttributes;

							Double[] valueArr = { numOfPeers,
									numOfSupportPeers, numOfSubCoords,
									numOfLeafs, numOfFreeCapacities,
									numOfAttributes, rankOfAttributes };
							secondDim.put(Integer.valueOf(level), valueArr);
							yMax = Math.max(yMax, secondDim.lastKey()
									.intValue());
							idLevelMatrix.put(Integer.valueOf(periodeCounter),
									secondDim);
						}
					}
				}

				writeRootChanges(periodeCounter);

				periodeCounter++;
				Simulator.scheduleEvent("Update", time + updateInterval, this,
						SimulationEvent.Type.STATUS);
			} else if (((String) se.getData()).equals("Average")) {
				log
						.fatal(Simulator.getFormattedTime(Simulator
								.getCurrentTime())
								+ ": Writing a metric of ever peer for a real calculation"
								+ " of the average and standard-deviation!!!");
				writeFileForCalculation();
			}

		}
	}

	private void writeFileForCalculation() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("logging"
					+ File.separator + "avgStdDev.dat"));
			Iterator<Long> idIter = onlinePeers.keySet().iterator();
			Long id = null;
			Host host = null;
			SkyNetNode skyNetNode = null;
			MetricsEntry entry = null;
			String reference = "SentQueryTraffic";
			MetricsAggregate ag = null;
			boolean first = true;
			Vector<BigDecimal> vec = new Vector<BigDecimal>();
			while (idIter.hasNext()) {
				id = idIter.next();
				host = onlinePeers.get(id);
				if (isPeerPresent(host.getNetLayer().getNetID())) {
					skyNetNode = (SkyNetNode) host.getOverlay(SkyNetNode.class);
					vec.add(skyNetNode.getSkyNetNodeInfo().getSkyNetID()
							.getID());
					entry = skyNetNode.getMetricUpdateStrategy()
							.getOwnMetrics();
					if (entry != null) {
						if (first) {
							first = false;
							bw.write("AgName\tSum\tSumSquare\t#ofAgElements");
							bw.newLine();
						}
						ag = entry.getMetrics().get(reference);
						bw.write(ag.getAggregateName() + "\t"
								+ ag.getSumOfAggregates() + "\t"
								+ ag.getSumOfSquares() + "\t"
								+ ag.getNodeCount());
						bw.newLine();
					}
				}
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void lostRootPosition(SkyNetNodeInterface skyNetNode) {
		numOfRootPosLosses++;
		numOfCurrentRoots--;
	}

	public void gotRootPosition(SkyNetNodeInterface skyNetNode) {
		numOfRootPosTakeovers++;
		numOfCurrentRoots++;
	}

	private void initRootChangeFile() {
		try {
			rootChangeWriter = new FileWriter(new File(
					SkyNetConstants.COMMON_SIMULATIONS_PATH
							+ File.separator
							+ SkyNetBatchSimulator.getInstance()
									.getCurrentSimulationDir() + File.separator
							+ PATH + File.separator + DATA_FILENAME));

			rootChangeWriter.write("#Periode\n" + "#NumOfRootLosses\n"
					+ "#NumOfRootTakeovers\n" + "#NumOfCurrentRoots\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeRootChanges(int periode) {
		if (rootChangeWriter == null) {
			return;
		}

		try {
			rootChangeWriter.write(periode + "\t" + numOfRootPosLosses + "\t"
					+ numOfRootPosTakeovers + "\t" + numOfCurrentRoots + "\n");

			rootChangeWriter.flush();

			numOfRootPosLosses = 0;
			numOfRootPosTakeovers = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
