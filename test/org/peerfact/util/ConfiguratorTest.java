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

package org.peerfact.util;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.peerfact.api.churn.ChurnGenerator;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.HostProperties;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;
import org.peerfact.api.scenario.Scenario;
import org.peerfact.api.scenario.ScenarioFactory;
import org.peerfact.impl.network.simple.SimpleNetFactory;
import org.peerfact.impl.scenario.DefaultConfigurator;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;

/**
 * This class tests the configuration fuctionality of the simulator. It involves
 * the configurator itself and DefaultHostBuilder. TODO split in Configurator
 * test and HostBuilderTest (and general FactoryConfTest)
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 30.11.2007
 * 
 */
public class ConfiguratorTest extends SimulatorTest {

	@Override
	@Before
	public void setUp() {
		/*
		 * Intentionally don't reuse scheduler super.setUp(); As these tests
		 * should create everything from the scratch as defined in configuration
		 * files.
		 */
	}

	/**
	 * Test the creation of the network.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNetwork() throws Exception {
		DefaultConfigurator conf = new DefaultConfigurator(
				new File("config/config-test.xml"));
		conf.configureAll();
		Configurable nf = conf.getConfigurable("NetLayer");
		// Collection<Configurable> clients = conf.configureAll();
		assertTrue(nf instanceof SimpleNetFactory);
		// assertTrue(clients.iterator().next() instanceof
		// SimpleNetFactory);
	}

	/**
	 * Test the variables.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVariables() throws Exception {
		DefaultConfigurator conf = new DefaultConfigurator(new File(
				"config/config-test.xml"));
		conf.configureAll();

		assertEquals("0", conf.parseValue("$seed"));

	}

	/**
	 * Test the creation and setting of host properties..
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHostProperties() throws Exception {
		DefaultConfigurator conf = new DefaultConfigurator(new File(
				"config/config-test.xml"));
		// conf.register("Scheduler", Scheduler.getDefaultScheduler());
		Collection<Configurable> clients = conf.configureAll();
		log.info("configured " + clients);
		HostBuilder builder = (HostBuilder) conf
				.getConfigurable(Configurator.HOST_BUILDER);
		assertNotNull(builder);
		Map<String, List<Host>> hosts = builder.getAllHostsWithIDs();
		log.debug(hosts);

		// test single host
		List<Host> list = hosts.get("server");
		assertEquals(1, list.size());
		Host host = list.get(0);
		HostProperties props = host.getProperties();
		assertNotNull(props);
		// assertNotNull(props.getGroupID());
		assertFalse(props.isChurnAffected());

		// test a group of hosts
		list = hosts.get("eu_clients");
		assertEquals(5, list.size());
		Host prev = null;
		for (Host host2 : list) {
			HostProperties props2 = host2.getProperties();
			assertNotNull(props2);
			// assertNotNull(props2.getGroupID());
			assertTrue(props2.isChurnAffected());// default value for churn is
													// true
			// check that all hosts get different properties
			if (prev != null) {
				assertTrue(host2.getProperties() != prev.getProperties());
			}
			prev = host2;
		}

	}

	/**
	 * TODO implement a proper test
	 * 
	 */
	@Test
	public void testWithHosts() {
		DefaultConfigurator conf = new DefaultConfigurator(new File(
				"config/napster.xml"));
		Collection<Configurable> clients = conf.configureAll();
		log.debug("Configured: " + clients);

		ComponentFactory nf = (ComponentFactory) conf
				.getConfigurable("NetLayer");
		assertTrue(nf != null);

		ComponentFactory af = (ComponentFactory) conf
				.getConfigurable("ApplicationLayer");
		assertTrue(af == null);

		HostBuilder builder = (HostBuilder) conf
				.getConfigurable(Configurator.HOST_BUILDER);
		assertNotNull(builder);

		assertTrue(builder.getAllHosts().size() > 0);
		// TODO test scenario factory?
		// ScenarioFactory sf = (ScenarioFactory)
		// conf.getComponent(ScenarioFactory.SCENARIO_TAG);
		// assertNotNull(null);
		// Scenario scenario = sf.getScenario();
		//
	}

	/**
	 * TODO reimplement
	 * 
	 */
	@Test
	// currently no churn is used here
	@Ignore
	public void testChurn() {
		DefaultConfigurator conf = new DefaultConfigurator(new File(
				"config/config-test.xml"));
		Collection<Configurable> clients = conf.configureAll();
		log.debug("Configured: " + clients);

		ComponentFactory nf = (ComponentFactory) conf
				.getConfigurable("NetLayer");
		assertTrue(nf != null);

		ComponentFactory af = (ComponentFactory) conf
				.getConfigurable("ApplicationLayer");
		assertTrue(af == null);

		HostBuilder builder = (HostBuilder) conf
				.getConfigurable(Configurator.HOST_BUILDER);
		assertNotNull(builder);

		ScenarioFactory sf = (ScenarioFactory) conf
				.getConfigurable(Configurator.SCENARIO_TAG);
		assertNotNull(sf);
		Scenario scenario = sf.createScenario();

		ChurnGenerator churn = (ChurnGenerator) conf
				.getConfigurable("ChurnGenerator");
		assertNotNull(churn);

		scenario.prepare();

		// runSimulation(-1);

	}

	/**
	 * Test how specified time is parsed by the configurator.
	 * 
	 */
	@Test
	public void testParseTime() {
		String pattern = "\\d+(ms|s|m|h)";
		boolean b = Pattern.matches(pattern, "100m");
		Assert.assertEquals(true, b);
		Assert.assertFalse(Pattern.matches(pattern, "s"));
		Assert.assertFalse(Pattern.matches(pattern, "123"));
		Assert.assertFalse(Pattern.matches(pattern, "bsdf"));
		Assert.assertFalse(Pattern.matches(pattern, "111s1"));

		Assert.assertTrue(Pattern.matches(pattern, "12345s"));
		Assert.assertTrue(Pattern.matches(pattern, "1234m"));
		Assert.assertTrue(Pattern.matches(pattern, "12345h"));

		Assert.assertEquals(0l, DefaultConfigurator.parseTime("0ms"));
		Assert.assertEquals(Simulator.MILLISECOND_UNIT,
				DefaultConfigurator.parseTime("1ms"));
		Assert.assertEquals(12345 * Simulator.MILLISECOND_UNIT,
				DefaultConfigurator.parseTime("12345ms"));
		Assert.assertEquals(Simulator.SECOND_UNIT,
				DefaultConfigurator.parseTime("1s"));
		Assert.assertEquals(15 * Simulator.MINUTE_UNIT,
				DefaultConfigurator.parseTime("15m"));
		Assert.assertEquals(5 * Simulator.HOUR_UNIT,
				DefaultConfigurator.parseTime("5h"));
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}
}
