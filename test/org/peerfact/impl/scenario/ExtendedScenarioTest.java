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

package org.peerfact.impl.scenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;

/**
 * @author Konstantin Pussep
 * @version 0.1, 20.08.2007
 * 
 */

public class ExtendedScenarioTest extends SimulatorTest {
	private static final String GROUP_ID = "host";

	static class OIDDummy implements OverlayID<Object> {

		private String value;

		OIDDummy(String value) {
			this.value = value;
		}

		@Override
		public byte[] getBytes() {
			return value.getBytes();
		}

		@Override
		public Object getUniqueValue() {
			return value;
		}

		@Override
		public int compareTo(OverlayID<Object> arg0) {
			return 0;
		}

		@Override
		public String toString() {
			return "OID value =" + value;
		}

		@Override
		public long getTransmissionSize() {
			return 0;
		}

	}

	static class OIDParser implements Parser {

		@Override
		public Object parse(String stringValue) {
			return new OIDDummy(stringValue);
		}

		@Override
		public Class<?> getType() {
			return OverlayID.class;
		}

	}

	private ExtendedScenario scenario;

	private List<DefaultHost> hosts;

	/**
	 * Create new ExtendedScenarioTest.
	 */
	public ExtendedScenarioTest() {
		super();
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		List<Parser> parsers = new LinkedList<Parser>();
		parsers.add(new OIDParser());
		List<Class<? extends Component>> additionalCompClasses = new ArrayList<Class<? extends Component>>();
		additionalCompClasses.add(ComponentDummy2.class);
		scenario = new ExtendedScenario(ComponentDummy.class,
				additionalCompClasses, parsers);

	}

	private void createHosts(int number) {
		Map<String, List<Host>> hostsMap = new LinkedHashMap<String, List<Host>>();
		hosts = new LinkedList<DefaultHost>();

		for (int i = 0; i < number; i++) {
			DefaultHost host = new DefaultHost();
			host.setComponent(new ComponentDummy());
			host.setComponent(new ComponentDummy2());
			hosts.add(host);
		}
		hostsMap.put(GROUP_ID, new LinkedList<Host>(hosts));
		scenario.setHosts(hostsMap);
	}

	/**
	 * Primitive implementation of a component.
	 */
	public class ComponentDummy implements Component {

		@Override
		public Host getHost() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setHost(Host host) {
			// TODO Auto-generated method stub

		}

		public void foo0() {
			log.info("method foo0 was called at " + Simulator.getCurrentTime());
		}

		public void foo1(double d) {
			log.info("method foo1 was called with param d=" + d + " at "
					+ Simulator.getCurrentTime());
		}

		public void foo2(int i, long l) {
			log.info("method foo2 was called with params i=" + i + " and l="
					+ l + " at " + Simulator.getCurrentTime());
		}

		public void fooWithOID(OverlayID<?> id) {
			log.info("method fooWithOID was called with params id=" + id
					+ " at " + Simulator.getCurrentTime());
		}

		public void fooWithOIDandInt(OverlayID<?> id, int i) {
			log.info("method fooWithOID was called with params id=" + id
					+ " and i=" + i + " at " + Simulator.getCurrentTime());
		}

	}

	/**
	 * Second primitive implementation of a component.
	 */
	public class ComponentDummy2 implements Component,
			Comparable<ComponentDummy2> {

		@Override
		public Host getHost() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setHost(Host host) {
			// TODO Auto-generated method stub

		}

		public void foo0() {
			log.info("method foo0 was called at " + Simulator.getCurrentTime());
		}

		@Override
		public int compareTo(ComponentDummy2 arg0) {
			// unused
			return 0;
		}
	}

	/**
	 * Test creation of different actions with different number and type of
	 * parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAction() throws Exception {

		createHosts(1);
		DefaultHost host = hosts.get(0);

		assertEquals(0, scenario.actions.size());

		// try a method without params
		scenario.createAction(host, ComponentDummy.class, seconds(5), "foo0",
				new String[0]);
		assertEquals(1, scenario.actions.size());

		// try a method with one simple param
		String[] params = new String[1];
		params[0] = "10.2";
		scenario.createAction(host, ComponentDummy.class, seconds(10), "foo1",
				params);
		assertEquals(2, scenario.actions.size());

		// try a method with two simple params
		params = new String[2];
		params[0] = "10";
		params[1] = "-10";
		scenario.createAction(host, ComponentDummy.class, seconds(12), "foo2",
				params);
		assertEquals(3, scenario.actions.size());

		// try a method with one complex param
		params = new String[1];
		params[0] = "someID";
		scenario.createAction(host, ComponentDummy.class, seconds(10),
				"fooWithOID", params);
		assertEquals(4, scenario.actions.size());

		// try a method with one complex and one simple param
		params = new String[2];
		params[0] = "someID";
		params[1] = "127";
		scenario.createAction(host, ComponentDummy.class, seconds(10),
				"fooWithOIDandInt", params);
		assertEquals(5, scenario.actions.size());

		// now schedule actions and run the simulation
		scenario.prepare();

		runSimulation(seconds(10000));
	}

	/**
	 * Test creating an action for a group of hosts.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateActionForGroup() throws Exception {
		createHosts(3);

		String[] params;

		// try a method
		params = new String[0];
		scenario.createActions(GROUP_ID, "10s-20s", "foo0", params);

		assertEquals(hosts.size(), scenario.actions.size());

		// now schedule actions and run the simulation
		scenario.prepare();

		runSimulation(seconds(10000));
	}

	/**
	 * Test creating an action for a non-default component.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateActionForNotDefaultComponent() throws Exception {
		createHosts(1);

		String[] params;

		assertEquals(0, scenario.actions.size());

		// try a method
		params = new String[0];
		scenario.createActions(GROUP_ID, "10s", "Comparable:foo0", params);

		assertEquals(hosts.size(), scenario.actions.size());

		// now schedule actions and run the simulation
		scenario.prepare();

		runSimulation(seconds(10000));
	}

	/**
	 * Test one configuration file for consistency and format correctness.
	 * 
	 */
	@Test
	public void testTimeInterval() {
		long start = 10;
		int size = 20;
		long delta = 5;
		List<Long> expected = new ArrayList<Long>(size);
		for (int i = 0; i < size; i++) {
			expected.add(start + i * delta);
		}
		String range = expected.get(0).toString() + "-"
				+ expected.get(expected.size() - 1).toString();
		long[] times = ExtendedScenario
				.createTimePoints(expected.size(), range);

		List<Long> got = new ArrayList<Long>(size);
		for (int i = 0; i < times.length; i++) {
			got.add(times[i]);
		}
		Assert.assertEquals(expected, got);

	}
}
