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

package org.peerfact.impl.application.filesharing.analyzer;

import java.util.LinkedHashMap;
import java.util.Set;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.analyzer.AbstractFileMetricAnalyzer;
import org.peerfact.impl.analyzer.metric.CounterMetric;
import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.analyzer.metric.SumMetric;
import org.peerfact.impl.simengine.Simulator;

/**
 * Analyzer to generate statistics regarding the file sharing application.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class DefaultFileSharingAnalyzer extends
		AbstractFileMetricAnalyzer<OverlayContact<?>>
		implements FileSharingAnalyzer {

	CounterMetric<OverlayContact<?>> numPublishedDocuments = new CounterMetric<OverlayContact<?>>(
			"Published Documents", "number");

	CounterMetric<OverlayContact<?>> numStartedDownloads = new CounterMetric<OverlayContact<?>>(
			"Started Downloads", "number");

	CounterMetric<OverlayContact<?>> numSucceededDownloads = new CounterMetric<OverlayContact<?>>(
			"Finished Downloads", "number");

	CounterMetric<OverlayContact<?>> numFailedDownloads = new CounterMetric<OverlayContact<?>>(
			"Failed Downloads", "number");

	CounterMetric<OverlayContact<?>> numFailedPublishes = new CounterMetric<OverlayContact<?>>(
			"Failed Publishes", "number");

	SumMetric<OverlayContact<?>, Long> filesizeDocuments = new SumMetric<OverlayContact<?>, Long>(
			"Overall Filesize", "bytes");

	StatisticMetric<OverlayContact<?>, Long> downloadFilesizes = new StatisticMetric<OverlayContact<?>, Long>(
			"Filesize per Download", "bytes");

	StatisticMetric<OverlayContact<?>, Long> publishFilesizes = new StatisticMetric<OverlayContact<?>, Long>(
			"Filesize per Publish", "bytes");

	LinkedHashMap<Object, Long> downloadStarttimes = new LinkedHashMap<Object, Long>();

	StatisticMetric<OverlayContact<?>, Long> downloadTimes = new StatisticMetric<OverlayContact<?>, Long>(
			"Time per Download", "seconds");

	LinkedHashMap<Object, Long> publishStarttimes = new LinkedHashMap<Object, Long>();

	StatisticMetric<OverlayContact<?>, Long> publishTimes = new StatisticMetric<OverlayContact<?>, Long>(
			"Time per Publish", "seconds");

	public DefaultFileSharingAnalyzer() {
		setOutputFileName("FileSharing");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(numPublishedDocuments);
		addMetric(numFailedPublishes);
		addMetric(numStartedDownloads);
		addMetric(numSucceededDownloads);
		addMetric(numFailedDownloads);
		addMetric(filesizeDocuments);
		addMetric(downloadFilesizes);
		addMetric(publishFilesizes);
		addMetric(downloadTimes);
		addMetric(publishTimes);
	}

	@Override
	public void downloadStarted(OverlayContact<OverlayID<?>> initiator,
			Object queryUID) {
		addPeer(initiator);
		numStartedDownloads.increment(initiator);
		downloadStarttimes.put(queryUID, Simulator.getCurrentTime());
	}

	@Override
	public void downloadSucceeded(OverlayContact<OverlayID<?>> initiator,
			Object queryUID,
			long filesize) {
		addPeer(initiator);
		numSucceededDownloads.increment(initiator);
		downloadFilesizes.addValue(initiator, filesize);
		downloadTimes.addValue(initiator, (Simulator.getCurrentTime()
				- downloadStarttimes.get(queryUID)) / Simulator.SECOND_UNIT);
		downloadStarttimes.remove(queryUID);
	}

	@Override
	public void downloadFailed(OverlayContact<OverlayID<?>> initiator,
			Object queryUID) {
		addPeer(initiator);
		numFailedDownloads.increment(initiator);
		downloadStarttimes.remove(queryUID);
	}

	@Override
	public void publishStarted(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish, Object queryUID) {
		addPeer(initiator);
		publishStarttimes.put(queryUID, Simulator.getCurrentTime());
	}

	@Override
	public void publishSucceeded(OverlayContact<OverlayID<?>> initiator,
			Set<OverlayContact<OverlayID<?>>> holder, int keyPublished,
			Object queryUID, long filesize) {
		addPeer(initiator);
		numPublishedDocuments.increment(initiator);
		publishFilesizes.addValue(initiator, filesize);
		publishTimes.addValue(initiator, (Simulator.getCurrentTime()
				- publishStarttimes.get(queryUID)) / Simulator.SECOND_UNIT);
		publishStarttimes.remove(queryUID);
		filesizeDocuments.addValue(initiator, filesize);
	}

	@Override
	public void publishFailed(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish, Object queryUID) {
		addPeer(initiator);
		numFailedPublishes.increment(initiator);
		publishStarttimes.remove(queryUID);
	}

}
