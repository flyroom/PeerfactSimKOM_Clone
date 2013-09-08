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

import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.impl.analyzer.metric.CounterMetric;

/**
 * Analyzer creates statistics for connectivity of peers.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 12/21/2011
 */
public class DefaultConnectivityAnalyzer extends
		AbstractFileMetricAnalyzer<Host>
		implements ConnectivityAnalyzer {

	private CounterMetric<Host> goOnline = new CounterMetric<Host>("Go Online",
			"number");

	private CounterMetric<Host> goOffline = new CounterMetric<Host>(
			"Go Offline", "number");

	public DefaultConnectivityAnalyzer() {
		setOutputFileName("Connectivity");
		setFlushEveryLine(true);
	}

	@Override
	protected void initializeMetrics() {
		addMetric(goOnline);
		addMetric(goOffline);
	}

	@Override
	public void offlineEvent(Host host) {
		goOffline.increment(host);
	}

	@Override
	public void onlineEvent(Host host) {
		goOnline.increment(host);
	}

}
