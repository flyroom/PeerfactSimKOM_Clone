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

package org.peerfact.impl.analyzer.csvevaluation.metrics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;


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
public class QuerySuccessAndNHops {

	static final Logger logger = Logger.getLogger(QuerySuccessAndNHops.class);

	static final long TIME_UNIT = 1000000;

	static final long TIMEOUT = 70 * TIME_UNIT; // 15s Timeout

	static final boolean CONSIDER_FAILED_FOR_HOPS = false;

	List<QueryTimeoutListener> queryListeners = new ArrayList<QueryTimeoutListener>();

	final QuerySuccess qsuccess = this.new QuerySuccess();

	final QueryNHops qnhops = new QueryNHops();

	final AvgRespTime avgRespTime = new AvgRespTime();

	Queue<QueryHandle> queries = new LinkedBlockingQueue<QueryHandle>();

	Map<Object, QueryHandle> queriesMap = new LinkedHashMap<Object, QueryHandle>();

	public void addListener(QueryTimeoutListener listener) {
		queryListeners.add(listener);
	}

	public void removeListener(QueryTimeoutListener listener) {
		queryListeners.remove(listener);
	}

	protected void queryTimeouted(Object queryIdentifier) {
		for (QueryTimeoutListener l : queryListeners) {
			l.queryTimeouted(queryIdentifier);
		}
	}

	public QueryHandle queryStarted(Object identifier, long time) {
		cleanupQueue(Simulator.getCurrentTime());
		if (identifier == null) {
			throw new IllegalArgumentException(
					"The identifier of a query must not be null.");
		}
		QueryHandle q = new QueryHandle(identifier, time);
		if (queriesMap.containsKey(identifier)) {
			logger.warn("Query " + identifier
					+ "already started. Query timeout will be rescheduled.");
			queries.remove(q);
			queriesMap.remove(identifier);
		}
		queries.add(q);
		queriesMap.put(identifier, q);
		assertListsSize();
		return q;
	}

	public boolean querySucceeded(Object identifier, int hops) {
		if (identifier == null) {
			throw new IllegalArgumentException(
					"The identifier of a query must not be null.");
		}
		cleanupQueue(Simulator.getCurrentTime());
		if (queries.remove(new QueryHandle(identifier, -1))) {
			int nhops = 0;
			QueryHandle succQ = queriesMap.remove(identifier);
			nhops = succQ.getNhops();
			nhops += hops;
			qnhops.considerNewValue(nhops);
			avgRespTime.considerSimulationTime(Simulator.getCurrentTime()
					- succQ.getTimeStarted());
			// if (nhops <= 0) logger.warn("Hop Count: " + nhops +
			// " for succeeded query " + identifier +
			// ", this could be a result of a broken hop counter.");
			qsuccess.addPositive();
			assertListsSize();
			return true;
		} else {
			logger
					.warn("Query was marked as succeeded that was never marked as started, or it finished beyond timeout interval"
							+ TIMEOUT + ", at " + Simulator.getCurrentTime());
			assertListsSize();
			return false;
		}
	}

	/**
	 * Adds a hop to a query that is denoted by identifier.
	 * 
	 * @param identifier
	 * @return if the query existed.
	 */
	public boolean addHopToQuery(Object identifier) {
		cleanupQueue(Simulator.getCurrentTime());
		QueryHandle q = queriesMap.get(identifier);
		if (q != null) {
			q.addHop();
			return true;
		} else {
			return false;
		}
	}

	void cleanupQueue(long time) {
		try {
			while (queries.element().getTimeStarted() <= time - TIMEOUT) {
				QueryHandle q2drop = queries.remove();
				queriesMap.remove(q2drop.getIdentifier());
				qsuccess.addNegative();
				if (CONSIDER_FAILED_FOR_HOPS) {
					int hopCount = q2drop.getNhops();
					qnhops.considerNewValue(hopCount);
				}
				queryTimeouted(q2drop.getIdentifier());
			}
		} catch (NoSuchElementException e) {
			// Queue is empty, fine.
		}
		assertListsSize();
	}

	void assertListsSize() {
		if (queriesMap.size() != queries.size()) {
			throw new IllegalStateException("The queue and map sizes differ: "
					+ queriesMap.size() + "!=" + queries.size());
		}
	}

	public class QuerySuccess extends BinaryRatioMetric {

		QuerySuccess() {
			// Protected
		}

		@Override
		public String getMeasurementFor(long time) {
			cleanupQueue(time);
			return super.getMeasurementFor(time);
		}

		@Override
		public String getName() {
			return "QuerySuccess(%)";
		}
	}

	public static class QueryNHops extends AverageMetric {

		QueryNHops() {
			// Protected
		}

		@Override
		public String getName() {
			return "AvgNHops";
		}
	}

	public static class AvgRespTime extends AverageMetric {
		AvgRespTime() {
			// Protected
		}

		public void considerSimulationTime(long simTime) {
			this.considerNewValue((double) simTime / TIME_UNIT);
		}

		@Override
		public String getName() {
			return "AvgQueryTime";
		}
	}

	public static class QueryHandle {
		Object identifier;

		long timeStarted;

		private int hops = 0;

		protected QueryHandle(Object identifier, long timeStarted) {
			this.timeStarted = timeStarted;
			this.identifier = identifier;
		}

		protected long getTimeStarted() {
			return timeStarted;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof QueryHandle)) {
				return false;
			}
			QueryHandle other = (QueryHandle) o;
			return identifier.equals(other.identifier);
		}

		public void addHop() {
			hops++;
		}

		public int getNhops() {
			return hops;
		}

		public Object getIdentifier() {
			return identifier;
		}

		@Override
		public int hashCode() {
			return identifier.hashCode();
		}

	}

	public interface QueryTimeoutListener {
		public void queryTimeouted(Object queryIdentifier);
	}

	public Metric getQuerySuccess() {
		return qsuccess;
	}

	public Metric getQueryNHops() {
		return qnhops;
	}

	public Metric getAvgRespTime() {
		return avgRespTime;
	}
}
