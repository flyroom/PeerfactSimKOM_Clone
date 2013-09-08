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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peerfact.api.analyzer.DHTOverlayAnalyzer;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.analyzer.metric.CounterMetric;
import org.peerfact.impl.analyzer.metric.StatisticMetric;
import org.peerfact.impl.simengine.Simulator;

/**
 * Analyzer to collect data about the overlays using the DHT interface for
 * lookups.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class DefaultDHTOverlayAnalyzer extends
		AbstractFileMetricAnalyzer<OverlayContact<?>>
		implements DHTOverlayAnalyzer {

	CounterMetric<OverlayContact<?>> numberOfInitiatedLookups = new CounterMetric<OverlayContact<?>>(
			"Initiated Lookups", "number");

	CounterMetric<OverlayContact<?>> numberOfFinishedLookups = new CounterMetric<OverlayContact<?>>(
			"Finished Lookups", "number");

	CounterMetric<OverlayContact<?>> numberOfFailedLookups = new CounterMetric<OverlayContact<?>>(
			"Failed Lookups", "number");

	CounterMetric<OverlayContact<?>> numberOfForwardedLookups = new CounterMetric<OverlayContact<?>>(
			"Forwarded Messages", "number");

	CounterMetric<OverlayContact<?>> numberOfServedLookups = new CounterMetric<OverlayContact<?>>(
			"Served Messages", "number");

	StatisticMetric<OverlayContact<?>, Integer> hops = new StatisticMetric<OverlayContact<?>, Integer>(
			"Hop Count", "number");

	StatisticMetric<OverlayContact<?>, Long> durations = new StatisticMetric<OverlayContact<?>, Long>(
			"Duration", "seconds");

	CounterMetric<OverlayContact<?>> additionalFinishedLookups = new CounterMetric<OverlayContact<?>>(
			"Additional Finished Lookups", "number");

	CounterMetric<OverlayContact<?>> additionalFailedLookups = new CounterMetric<OverlayContact<?>>(
			"Additional Failed Lookups", "number");

	Map<OverlayContact<?>, Map<DHTKey<?>, Long>> durationsMap = new LinkedHashMap<OverlayContact<?>, Map<DHTKey<?>, Long>>();

	public DefaultDHTOverlayAnalyzer() {
		setOutputFileName("DHTOverlay");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(numberOfInitiatedLookups);
		addMetric(numberOfFinishedLookups);
		addMetric(numberOfFailedLookups);
		addMetric(numberOfForwardedLookups);
		addMetric(numberOfServedLookups);
		addMetric(hops);
		addMetric(durations);
		addMetric(additionalFinishedLookups);
		addMetric(additionalFailedLookups);
	}

	@Override
	public void storeInitiated(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeFailed(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeFinished(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object, List<OverlayContact<?>> responsibleContacts) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lookupInitiated(OverlayContact<?> contact, DHTKey<?> key) {
		addPeer(contact);
		// store contact in map
		if (!durationsMap.containsKey(contact)) {
			durationsMap.put(contact, new LinkedHashMap<DHTKey<?>, Long>());
		}

		// save start time in map
		long startTime = Simulator.getCurrentTime();
		durationsMap.get(contact).put(key, startTime);
		numberOfInitiatedLookups.increment(contact);
	}

	@Override
	public void lookupForwarded(OverlayContact<?> contact, DHTKey<?> key,
			OverlayContact<?> currentHop, int neededHops) {
		if (durationsMap.containsKey(contact)
				&& durationsMap.get(contact).containsKey(key)) {
			addPeer(currentHop);
			numberOfForwardedLookups.increment(currentHop);
		}
	}

	@Override
	public void lookupFailed(OverlayContact<?> contact, DHTKey<?> key) {
		addPeer(contact);
		// remove lookup from map
		if (durationsMap.containsKey(contact)
				&& durationsMap.get(contact).containsKey(key)) {
			durationsMap.get(contact).remove(key);
			numberOfFailedLookups.increment(contact);
		} else {
			additionalFailedLookups.increment(contact);
		}
	}

	@Override
	public void lookupFinished(OverlayContact<?> contact, DHTKey<?> key,
			List<OverlayContact<?>> responsibleContacts, int neededHops) {
		addPeer(contact);
		// remove lookup from map
		if (durationsMap.containsKey(contact)
				&& durationsMap.get(contact).containsKey(key)) {
			long startTime = durationsMap.get(contact).remove(key);
			hops.addValue(contact, neededHops);
			long duration = (Simulator.getCurrentTime() - startTime)
					/ Simulator.SECOND_UNIT;
			durations.addValue(contact, duration);
			numberOfFinishedLookups.increment(contact);
			addPeer(responsibleContacts.get(0));
			numberOfServedLookups.increment(responsibleContacts.get(0));
		} else {
			additionalFinishedLookups.increment(contact);
		}
	}

	@Override
	public void lookupFinished(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object, int neededHops) {
		addPeer(contact);
		// remove lookup from map
		if (durationsMap.containsKey(contact)
				&& durationsMap.get(contact).containsKey(key)) {
			long startTime = durationsMap.get(contact).remove(key);
			hops.addValue(contact, neededHops);
			long duration = (Simulator.getCurrentTime() - startTime)
					/ Simulator.SECOND_UNIT;
			durations.addValue(contact, duration);
			numberOfFinishedLookups.increment(contact);
		} else {
			additionalFinishedLookups.increment(contact);
		}
	}

}
