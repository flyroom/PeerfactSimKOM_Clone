/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

package org.peerfact.impl.service.dhtstorage.past.analyzer;

import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.analyzer.IOldFilesharingAnalyzer;
import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Transmitable;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.analyzer.csvevaluation.AbstractGnuplotAnalyzer;
import org.peerfact.impl.analyzer.csvevaluation.metrics.Metric;

public class ReplicationAnalyzer extends AbstractGnuplotAnalyzer implements
		ConnectivityAnalyzer, KBROverlayAnalyzer, IOldFilesharingAnalyzer {

	/**
	 * Base class for all metrics that just count something.
	 */
	public class SimpleCounterMetric implements Metric {

		private String name;

		private int servedCount = 0;

		public SimpleCounterMetric(String name) {
			super();
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getMeasurementFor(long time) {
			return String.valueOf(servedCount);
		}

		public void inc() {
			servedCount++;
		}

	}

	/**
	 * Counts how often a file was not present at the responsible node.
	 */
	private SimpleCounterMetric fileNotPresent = new SimpleCounterMetric(
			"FileNotPresent");

	/**
	 * Counts how often a value lookup was answered by the responsible node.
	 */
	private SimpleCounterMetric ownDocServedMetric = new SimpleCounterMetric(
			"OriginalFilePresent");

	/**
	 * Counts how often a value lookup was answered by a predecessor of the
	 * responsible node.
	 */
	private SimpleCounterMetric mirroredDocServedPredMetric = new SimpleCounterMetric(
			"ReplicatedFileAtPredPresent");

	/**
	 * Counts how often a value lookup was answered by a successor the
	 * responsible node.
	 */
	private SimpleCounterMetric mirroredDocServedSuccMetric = new SimpleCounterMetric(
			"ReplicatedFileAtSuccPresent");

	/**
	 * Counts how many value lookups failed.
	 */
	private SimpleCounterMetric failedQueries = new SimpleCounterMetric(
			"FailedQueries");

	/**
	 * Counts how many value lookups were started.
	 */
	private SimpleCounterMetric startedQueries = new SimpleCounterMetric(
			"StartedQueries");

	private ReplicationAnalyzerMetric metrics = new ReplicationAnalyzerMetric();

	@Override
	protected void declareMetrics() {
		addMetric(fileNotPresent);
		addMetric(ownDocServedMetric);
		addMetric(mirroredDocServedPredMetric);
		addMetric(mirroredDocServedSuccMetric);
		addMetric(metrics.getAvgHops());
		addMetric(metrics.getAvgReplica());
		addMetric(metrics.getMinReplica());
		addMetric(metrics.getMaxReplica());
		addMetric(metrics.getMinReplicaList());
		addMetric(metrics.getMaxReplicaList());
		addMetric(metrics.getResponsibleNode());
		addMetric(metrics.getFileCount());
	}

	@Override
	public void queryStarted(OverlayContact<?> contact, Message appMsg) {
		checkTimeProgress();
		startedQueries.inc();

	}

	@Override
	public void queryFailed(OverlayContact<?> failedHop, Message appMsg) {
		checkTimeProgress();
		failedQueries.inc();

	}

	@Deprecated
	@Override
	public void mirrorAssigned(OverlayContact<?> host, Transmitable document) {
		checkTimeProgress();
		if (document == null) {
			throw new RuntimeException("should not be null");
		}

	}

	@Deprecated
	@Override
	public void ownDocumentServed(OverlayContact<?> server,
			Transmitable document,
			boolean success) {
		checkTimeProgress();
		if (success) {
			ownDocServedMetric.inc();
		} else {
			fileNotPresent.inc();
		}

	}

	@Deprecated
	@Override
	public void mirroredDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean source) {
		checkTimeProgress();
		if (source) {
			mirroredDocServedPredMetric.inc();
		} else {
			mirroredDocServedSuccMetric.inc();
		}

	}

	@Deprecated
	@Override
	public void mirrorDeleted(OverlayContact<?> server, Transmitable document) {
		checkTimeProgress();
	}

	@Override
	public void offlineEvent(Host host) {
		checkTimeProgress();
	}

	@Override
	public void onlineEvent(Host host) {
		checkTimeProgress();
	}

	@Override
	public void overlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int hops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void overlayMessageDelivered(OverlayContact<?> contact, Message msg,
			int hops) {
		// TODO Auto-generated method stub

	}
}
