/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.churn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.peerfact.api.common.Host;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;
import org.peerfact.impl.churn.DefaultChurnGenerator;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.util.ComponentTest;


public abstract class ChurnGeneratorTest extends ComponentTest {


	protected DefaultChurnGenerator churnGen;

	protected List<Host> churnHosts;

	List<Host> allHosts;

	protected static final long SIM_END = 2 * Simulator.HOUR_UNIT;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		churnHosts = new ArrayList<Host>();
		allHosts = new ArrayList<Host>();
		Host host;
		for (int i = 0; i < 10000; i++) {
			host = createChurnHost();
			churnHosts.add(host);
			allHosts.add(host);
		}

		for (int i = 0; i < 100; i++) {
			host = createWithoutChurnHost();
			allHosts.add(host);
		}
		HostBuilder builder = new TestBuilder(allHosts);
		churnGen = new DefaultChurnGenerator();
		churnGen.hostBuilder = builder;
		churnGen.setStart(0);
		churnGen.setStop(SIM_END);
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

	protected Host createChurnHost() {
		DefaultHost host = createEmptyHost();
		createHostProperties(host);
		host.getProperties().setEnableChurn(true);
		createNetworkWrapper(host);
		createTransLayer(host);
		return host;
	}

	protected Host createWithoutChurnHost() {
		DefaultHost host = createEmptyHost();
		createHostProperties(host);
		host.getProperties().setEnableChurn(false);
		createNetworkWrapper(host);
		createTransLayer(host);
		return host;
	}

	protected List<Host> getHosts() {
		return churnGen.hosts;
	}

	protected static class TestBuilder implements HostBuilder {

		List<Host> allHosts;

		public TestBuilder(List<Host> hosts) {
			this.allHosts = hosts;
		}

		@Override
		public List<Host> getAllHosts() {
			return this.allHosts;
		}

		@Override
		public Map<String, List<Host>> getAllHostsWithIDs() {
			return null;
		}

		@Override
		public List<Host> getHosts(String id) {
			return null;
		}

		@Override
		public void parse(Element elem, Configurator config) {
			// No implementation here (not sure why) by Thim
			// This text is here so that later on someone will find this.
		}

	}
}
