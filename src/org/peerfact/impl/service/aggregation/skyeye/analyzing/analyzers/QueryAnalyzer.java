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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.service.skyeye.ISkyNetMonitor;
import org.peerfact.api.service.skyeye.QueryAnalyzerInterface;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.AbstractSkyNetAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing.QueryPostProcessor;
import org.peerfact.impl.service.aggregation.skyeye.queries.Query;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryAddend;
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
 * This class implements <code>QueryAnalyzerInterface</code> and is used to
 * gather all collected information in one sink. To avoid, that the collected
 * data consumes too much memory of the PC, the data is written to files through
 * serialization. <code>DATA_PATH</code> defines the path for the files, while
 * <code>writeIntervall</code> determines the interval between the periodical
 * serialization. For the serialization of the data, we utilize the following
 * order of writing objects:<br>
 * <li><code>startedQueries</code> <li><code>failedQueries</code> <li>
 * <code>unsolvedQueries</code> <li><code>solvedQueries</code><br>
 * It is important to maintain this order, as changing this order would cause
 * the corresponding post-processing-class {@link QueryPostProcessor} to throw
 * an exception.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class QueryAnalyzer extends AbstractSkyNetAnalyzer implements
		Configurable, QueryAnalyzerInterface {

	private static Logger log = SimLogger.getLogger(QueryAnalyzer.class);

	private static String DATA_PATH = Constants.TMP_DIR + File.separator
			+ "queryData";

	private long writeIntervall = Simulator.MINUTE_UNIT * 5;

	private static QueryAnalyzer analyzer;

	private ChurnStatisticsAnalyzer csAnalyzer;

	// collections for storing the queries
	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> startedQueries;

	private int numberOfStartedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> failedQueries;

	private int numberOfFailedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> unsolvedQueries;

	private int numberOfUnsolvedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> solvedQueries;

	private int numberOfSolvedQueries;

	public static QueryAnalyzer getInstance() {
		return analyzer;
	}

	public QueryAnalyzer() {
		super();
		analyzer = this;
		ISkyNetMonitor monitor = (ISkyNetMonitor) Simulator.getMonitor();
		csAnalyzer = (ChurnStatisticsAnalyzer) monitor
				.getConnectivityAnalyzer(ChurnStatisticsAnalyzer.class);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.MONITOR_START)) {
			start();
		} else if (se.getType().equals(SimulationEvent.Type.MONITOR_STOP)) {
			stop(null);
		} else if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			// writing down the data-Maps
			long time = Simulator.getCurrentTime();
			long delta = System.currentTimeMillis();
			File f = new File(DATA_PATH + File.separatorChar + "temp-"
					+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(f));
				log.warn("@ " + Simulator.getFormattedTime(time)
						+ " Started to write the query-maps");
				oos.writeObject(startedQueries);
				oos.writeObject(failedQueries);
				oos.writeObject(unsolvedQueries);
				oos.writeObject(solvedQueries);
				oos.close();
				log.warn("@ " + Simulator.getFormattedTime(time)
						+ " Finished to write the query-maps in "
						+ (System.currentTimeMillis() - delta) + "ms");
				startedQueries.clear();
				failedQueries.clear();
				unsolvedQueries.clear();
				solvedQueries.clear();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Simulator.scheduleEvent(null, writeIntervall + time, this,
					SimulationEvent.Type.STATUS);
		}
	}

	@Override
	public void queryStarted(Query query) {
		if (runningAnalyzer) {
			numberOfStartedQueries++;
			LinkedHashMap<Integer, Query> queries = startedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			startedQueries.put(
					query.getQueryOriginator().getSkyNetID().getID(), queries);
		}
	}

	@Override
	public void queryLost(Query query) {
		if (runningAnalyzer) {
			numberOfFailedQueries++;
			LinkedHashMap<Integer, Query> queries = failedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			failedQueries.put(query.getQueryOriginator().getSkyNetID().getID(),
					queries);
		}
	}

	@Override
	public void unsolvedQueryReceived(Query query) {
		if (runningAnalyzer) {
			numberOfUnsolvedQueries++;
			LinkedHashMap<Integer, Query> queries = unsolvedQueries
					.remove(query
							.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			unsolvedQueries.put(query.getQueryOriginator().getSkyNetID()
					.getID(), queries);
		}
	}

	@Override
	public void solvedQueryReceived(Query query) {
		if (runningAnalyzer) {
			numberOfSolvedQueries++;
			float quality = determineAnswerQuality(query);
			query.setAnswerQuality(quality);
			LinkedHashMap<Integer, Query> queries = solvedQueries.remove(query
					.getQueryOriginator().getSkyNetID().getID());
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.put(query.getQueryID(), query);
			solvedQueries.put(query.getQueryOriginator().getSkyNetID().getID(),
					queries);
		}
	}

	private float determineAnswerQuality(Query query) {
		QueryAddend addend = query.getAddend(query.getIndexOfSolvedAddend());
		Vector<SkyNetNodeInfo> matches = addend.getMatches();
		int onlineMatches = 0;
		SkyNetNodeInfo node = null;
		for (int i = 0; i < matches.size(); i++) {
			node = matches.get(i);
			if (csAnalyzer.isPeerPresent(node.getTransInfo().getNetId())) {
				onlineMatches++;
			}
		}
		return ((float) onlineMatches) / ((float) matches.size());
	}

	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_START);
	}

	@Override
	protected void initialize() {
		initWriteDirectory(DATA_PATH, true);
		Simulator
				.scheduleEvent(null, writeIntervall
						+ Simulator.getCurrentTime(), this,
						SimulationEvent.Type.STATUS);
	}

	public void setStop(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_STOP);
	}

	@Override
	protected void finish() {
		log.fatal("Started queries = " + numberOfStartedQueries);
		log.fatal("Failed queries = " + numberOfFailedQueries);
		log.fatal("Unsolved queries = " + numberOfUnsolvedQueries);
		log.fatal("Solved queries = " + numberOfSolvedQueries);
		// writing down the data-Maps
		long time = Simulator.getCurrentTime();
		long delta = System.currentTimeMillis();
		File f = new File(DATA_PATH + File.separatorChar + "temp-"
				+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");

		File stats = new File(DATA_PATH + File.separatorChar + "stats.dat");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(f));
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Started to write the query-maps");
			oos.writeObject(startedQueries);
			oos.writeObject(failedQueries);
			oos.writeObject(unsolvedQueries);
			oos.writeObject(solvedQueries);
			oos.close();
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Finished to write the query-maps in "
					+ (System.currentTimeMillis() - delta) + "ms");
			startedQueries.clear();
			failedQueries.clear();
			unsolvedQueries.clear();
			solvedQueries.clear();

			oos = new ObjectOutputStream(new FileOutputStream(stats));
			oos.writeInt(numberOfStartedQueries);
			oos.writeInt(numberOfFailedQueries);
			oos.writeInt(numberOfUnsolvedQueries);
			oos.writeInt(numberOfSolvedQueries);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSimulationSize(int size) {
		double capacity = Math.ceil(size / 0.75d);
		startedQueries = new LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>(
				(int) capacity);
		numberOfStartedQueries = 0;
		failedQueries = new LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>(
				(int) capacity);
		numberOfFailedQueries = 0;
		unsolvedQueries = new LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>(
				(int) capacity);
		numberOfUnsolvedQueries = 0;
		solvedQueries = new LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>(
				(int) capacity);
		numberOfSolvedQueries = 0;
	}

}
