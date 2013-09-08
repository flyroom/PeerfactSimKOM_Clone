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

package org.peerfact.impl.analyzer;

import org.peerfact.api.analyzer.UnstructuredOverlayAnalyzer;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.analyzer.metric.CounterMetric;
import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;

/**
 * Analyzer to collect data about the unstructured overlays.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class DefaultUnstructuredOverlayAnalyzer extends
		AbstractFileMetricAnalyzer<OverlayContact<?>>
		implements UnstructuredOverlayAnalyzer {

	CounterMetric<OverlayContact<?>> connectionStarted = new CounterMetric<OverlayContact<?>>(
			"connections started", "number");

	CounterMetric<OverlayContact<?>> connectionSucceeded = new CounterMetric<OverlayContact<?>>(
			"connections succeeded", "number");

	CounterMetric<OverlayContact<?>> connectionDenied = new CounterMetric<OverlayContact<?>>(
			"connections denied", "number");

	CounterMetric<OverlayContact<?>> connectionTimeout = new CounterMetric<OverlayContact<?>>(
			"connections timeout", "number");

	CounterMetric<OverlayContact<?>> connectionBreakCancel = new CounterMetric<OverlayContact<?>>(
			"connection breaks (cancel)", "number");

	CounterMetric<OverlayContact<?>> connectionBreakTimeout = new CounterMetric<OverlayContact<?>>(
			"connection breaks (timeout)", "number");

	CounterMetric<OverlayContact<?>> pingTimeouted = new CounterMetric<OverlayContact<?>>(
			"pings timeouted", "number");

	CounterMetric<OverlayContact<?>> queryStarted = new CounterMetric<OverlayContact<?>>(
			"queries started", "number");

	CounterMetric<OverlayContact<?>> querySucceeded = new CounterMetric<OverlayContact<?>>(
			"queries succeeded", "number");

	StatisticMetric<OverlayContact<?>, Integer> queryHits = new StatisticMetric<OverlayContact<?>, Integer>(
			"query hits", "number");

	StatisticMetric<OverlayContact<?>, Double> queryHops = new StatisticMetric<OverlayContact<?>, Double>(
			"query hops", "number");

	CounterMetric<OverlayContact<?>> queryFailed = new CounterMetric<OverlayContact<?>>(
			"queries failed", "number");

	CounterMetric<OverlayContact<?>> queryMadeHop = new CounterMetric<OverlayContact<?>>(
			"made hops", "number");

	CounterMetric<OverlayContact<?>> reBootstraps = new CounterMetric<OverlayContact<?>>(
			"reBootstraps", "number");

	public DefaultUnstructuredOverlayAnalyzer() {
		setOutputFileName("UnstructuredOverlay");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(connectionStarted);
		addMetric(connectionSucceeded);
		addMetric(connectionDenied);
		addMetric(connectionTimeout);
		addMetric(connectionBreakCancel);
		addMetric(connectionBreakTimeout);
		addMetric(pingTimeouted);
		addMetric(queryStarted);
		addMetric(querySucceeded);
		addMetric(queryHits);
		addMetric(queryHops);
		addMetric(queryFailed);
		addMetric(queryMadeHop);
		addMetric(reBootstraps);
	}

	@Override
	public void connectionStarted(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		addPeer(invoker);
		connectionStarted.increment(invoker);
	}

	@Override
	public void connectionSucceeded(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		addPeer(invoker);
		connectionSucceeded.increment(invoker);
	}

	@Override
	public void connectionDenied(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		addPeer(invoker);
		connectionDenied.increment(invoker);
	}

	@Override
	public void connectionTimeout(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		addPeer(invoker);
		connectionTimeout.increment(invoker);
	}

	@Override
	public void connectionBreakCancel(OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent) {
		addPeer(opponent);
		connectionBreakCancel.increment(opponent);
	}

	@Override
	public void connectionBreakTimeout(OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent) {
		addPeer(opponent);
		connectionBreakTimeout.increment(opponent);
	}

	@Override
	public void pingTimeouted(OverlayContact<?> invoker,
			OverlayContact<?> receiver) {
		addPeer(invoker);
		pingTimeouted.increment(invoker);
	}

	@Override
	public void queryStarted(OverlayContact<?> initiator, Query query) {
		addPeer(initiator);
		queryStarted.increment(initiator);
	}

	@Override
	public void querySucceeded(OverlayContact<?> initiator, Query query,
			int hits, double averageHops) {
		addPeer(initiator);
		querySucceeded.increment(initiator);
		queryHits.addValue(initiator, hits);
		queryHops.addValue(initiator, averageHops);
	}

	@Override
	public void queryFailed(OverlayContact<?> initiator, Query query, int hits,
			double averageHops) {
		addPeer(initiator);
		queryFailed.increment(initiator);
	}

	@Override
	public void queryMadeHop(int queryUID, OverlayContact<?> hopContact) {
		addPeer(hopContact);
		queryMadeHop.increment(hopContact);
	}

	@Override
	public void reBootstrapped(OverlayContact<?> contact) {
		addPeer(contact);
		reBootstraps.increment(contact);
	}
}
