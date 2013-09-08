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

package org.peerfact.impl.churn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.churn.ChurnGenerator;
import org.peerfact.api.churn.ChurnModel;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.CollectionHelpers;
import org.peerfact.impl.util.toolkits.Predicates;

/**
 * Provides a random number generator for times. do sth
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class DefaultChurnGenerator implements SimulationEventHandler,
		ChurnGenerator {
	private static final Logger log = SimLogger
			.getLogger(DefaultChurnGenerator.class);

	HostBuilder hostBuilder;

	ChurnModel churnModel;

	List<Host> hosts = null;

	long endTime = Long.MAX_VALUE;

	boolean testMode = false;

	@Override
	public void setChurnModel(ChurnModel model) {
		this.churnModel = model;
	}

	public ChurnModel getChurnModel() {
		return this.churnModel;
	}

	@Override
	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.CHURN_START);
	}

	@Override
	public void setStop(long time) {
		this.endTime = time;
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.CHURN_STOP);
	}

	@Override
	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {

		if (se.getType().equals(SimulationEvent.Type.CHURN_EVENT)) {

			ChurnEvent churnEvent = (ChurnEvent) se.getData();
			NetLayer net = churnEvent.host.getNetLayer();
			if (churnEvent.goOnline) {
				net.goOnline();
			} else {
				net.goOffline();
			}
			scheduleChurnEvent(churnEvent.host);
		} else if (se.getType().equals(SimulationEvent.Type.CHURN_START)) {
			this.hosts = new ArrayList<Host>(this.filterHosts());
			this.churnModel.prepare(this.hosts);
			this.activate();
		} else { // CHURN_STOP
			if (hosts == null) {
				throw new RuntimeException(
						"Tried to stop churn at "
								+ Simulator.getCurrentTime()
								+ ", but it never started. Maybe you should correct your churn start and stop.");
			}
			for (Host host : this.hosts) {
				if (host.getNetLayer().isOffline()) {
					host.getNetLayer().goOnline();
				}
			}
		}
	}

	void activate() {
		for (Host host : hosts) {
			scheduleChurnEvent(host);
		}

		log.info("Scheduled " + hosts.size() + " churn events");
	}

	private List<Host> filterHosts() {
		List<Host> tmp = hostBuilder.getAllHosts();
		List<Host> filteredHosts = new LinkedList<Host>();

		CollectionHelpers.filter(tmp, filteredHosts,
				Predicates.IS_CHURN_AFFECTED);
		return filteredHosts;
	}

	private void scheduleChurnEvent(Host host) {
		NetLayer net = host.getNetLayer();
		long offset;
		boolean goOnline;
		long currentTime = Simulator.getCurrentTime();

		if (net.isOnline()) {
			offset = this.churnModel.getNextDowntime(host);
			Simulator.getMonitor().nextSessionTime(offset);
			goOnline = false;
			// if (testMode)
			// this.testStub.onlineEvent(host, currentTime);
		} else {
			offset = this.churnModel.getNextUptime(host);
			Simulator.getMonitor().nextInterSessionTime(offset);
			goOnline = true;
			// if (testMode)
			// this.testStub.offlineEvent(host, currentTime);
		}

		long timepoint = currentTime + offset;

		if (currentTime < endTime && timepoint < endTime) {
			Simulator.scheduleEvent(new ChurnEvent(host, goOnline), timepoint,
					this, SimulationEvent.Type.CHURN_EVENT);
		}
	}

	private static class ChurnEvent {
		Host host;

		boolean goOnline;

		ChurnEvent(Host host, boolean goOnline) {
			this.host = host;
			this.goOnline = goOnline;
		}
	}

	// void setTestStub(ChurnTestStub testStub) {
	// this.testStub = testStub;
	// this.testMode = true;
	// }

}
