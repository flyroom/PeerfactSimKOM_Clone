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

package org.peerfact.impl.isolation;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.isolation.IsolationGenerator;
import org.peerfact.api.isolation.IsolationModel;
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
 * Provides a default implementation for the isolation generator.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 04/19/2013
 */
public class DefaultIsolationGenerator implements SimulationEventHandler,
		IsolationGenerator {

	private static final Logger log = SimLogger
			.getLogger(DefaultIsolationGenerator.class);

	private IsolationModel isolationModel;

	private HostBuilder hostBuilder;

	private long endTime = Long.MAX_VALUE;

	private Map<String, List<Host>> groups;

	@Override
	public void setIsolationModel(IsolationModel model) {
		this.isolationModel = model;
	}

	public IsolationModel getIsolationModel() {
		return this.isolationModel;
	}

	@Override
	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.ISOLATION_START);
	}

	@Override
	public void setStop(long time) {
		this.endTime = time;
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.ISOLATION_STOP);
	}

	@Override
	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {

		if (se.getType().equals(SimulationEvent.Type.ISOLATION_EVENT)) {
			IsolationEvent isolationEvent = (IsolationEvent) se.getData();
			if (isolationEvent.goIsolation) {
				for (Host host : groups.get(isolationEvent.group)) {
					host.getNetLayer().startIsolation();
				}
			} else {
				for (Host host : groups.get(isolationEvent.group)) {
					host.getNetLayer().stopIsolation();
				}
			}
			scheduleIsolationEvent(isolationEvent.group);
		} else if (se.getType().equals(SimulationEvent.Type.ISOLATION_START)) {
			this.groups = this.filterHosts();
			this.activate();
		} else { // ISOLATION_STOP
			if (groups == null) {
				throw new RuntimeException(
						"Tried to stop isolation at "
								+ Simulator.getCurrentTime()
								+ ", but it never started. Maybe you should correct your isolation start and stop.");
			}
			for (List<Host> hosts : this.groups.values()) {
				for (Host host : hosts) {
					if (host.getNetLayer().isIsolated()) {
						host.getNetLayer().stopIsolation();
					}
				}
			}
		}
	}

	void activate() {
		for (String group : groups.keySet()) {
			scheduleIsolationEvent(group);
		}

		log.info("Scheduled " + groups.size() + " isolation events");
	}

	private Map<String, List<Host>> filterHosts() {
		List<Host> tmp = hostBuilder.getAllHosts();
		Map<String, List<Host>> newGroups = new LinkedHashMap<String, List<Host>>();
		List<Host> filteredHosts = new LinkedList<Host>();
		CollectionHelpers.filter(tmp, filteredHosts,
				Predicates.IS_ISOLATION_AFFECTED);

		for (Host host : filteredHosts) {
			String group = host.getProperties().getGroupID();
			if (!newGroups.containsKey(group)) {
				newGroups.put(group, new LinkedList<Host>());
			}
			newGroups.get(group).add(host);
		}
		return newGroups;
	}

	private void scheduleIsolationEvent(String group) {
		NetLayer net = groups.get(group).get(0).getNetLayer();
		long offset;
		boolean goIsolation;
		long currentTime = Simulator.getCurrentTime();

		if (net.isIsolated()) {
			offset = this.isolationModel.getNextGlobalTime(group);
			goIsolation = false;
		} else {
			offset = this.isolationModel.getNextIsolationTime(group);
			goIsolation = true;
		}

		long timepoint = currentTime + offset;

		if (currentTime < endTime && timepoint < endTime) {
			Simulator.scheduleEvent(new IsolationEvent(group, goIsolation),
					timepoint, this, SimulationEvent.Type.ISOLATION_EVENT);
		}
	}

	private static class IsolationEvent {

		String group;

		boolean goIsolation;

		IsolationEvent(String group, boolean goIsolation) {
			this.group = group;
			this.goIsolation = goIsolation;
		}
	}

}
