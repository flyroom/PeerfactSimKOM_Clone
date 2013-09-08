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

package org.peerfact.impl.churn.model;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.StatUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.peerfact.api.common.Host;
import org.peerfact.impl.churn.ChurnGeneratorTest;
import org.peerfact.impl.churn.model.ExponentialChurnModel;
import org.peerfact.impl.churn.model.ExponentialChurnModel.ChurnData;
import org.peerfact.impl.churn.model.ExponentialChurnModel.UserType;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;


public class ExponentialChurnModelTest extends ChurnGeneratorTest {

	ExponentialChurnModel model;

	ChurnTestStub testStub;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		model = new ExponentialChurnModel();
		model.setChurnFactor(0.5d);
		ExponentialChurnModel.setMeanSessionLength(1 * Simulator.HOUR_UNIT);
		testStub = new ChurnTestStub(this.churnHosts, this);
		// churnGen.setTestStub(testStub);
		churnGen.setChurnModel(model);

	}

	ChurnData getChurnData(Host host) {
		return model.hosts.get(host);
	}

	@Test
	public void testHostFiltering() {
		runSimulation(SIM_END);
		Assert.assertEquals(churnHosts, churnGen.getChurnModel());
	}

	@Test
	public void testFractions() {
		runSimulation(SIM_END);
		int longL = 0;
		int normal = 0;
		int trans = 0;

		for (ChurnData churnData : model.hosts.values()) {
			if (churnData.type.equals(UserType.LONG_LASTING)) {
				longL++;
			} else if (churnData.type.equals(UserType.NORMAL)) {
				normal++;
			} else {
				trans++;
			}
		}

		Assert
				.assertTrue(model.longLastingFraction * churnHosts.size()
						* 0.90 < longL
						&& longL < model.longLastingFraction
								* churnHosts.size() * 1.1);
		Assert
				.assertTrue(model.normalFraction * churnHosts.size() * 0.90 < normal
						&& normal < model.normalFraction * churnHosts.size()
								* 1.1);
		Assert
				.assertTrue(model.transientFraction * churnHosts.size() * 0.90 < trans
						&& trans < model.transientFraction * churnHosts.size()
								* 1.1);
	}

	@Ignore
	@Test
	public void testUptimeChurnfactor() {
		runSimulation(SIM_END);

		double[] sample = new double[testStub.uptimePeers.size()];
		int i = 0;
		for (Double samp : testStub.uptimePeers) {
			sample[i] = samp;
			i++;
		}
		double result = StatUtils.mean(sample);
		Assert.assertTrue(model.churnFactor * churnHosts.size() * 0.7 < result
				&& result < model.churnFactor * churnHosts.size() * 1.3);

	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

	@Ignore
	@Test
	public void testTransientUptimes() {
		SimulatorTest.runSimulation(SIM_END);

		double[] sample = new double[testStub.trans.size()];
		int i = 0;
		for (Double samp : testStub.trans) {
			sample[i] = samp;
			i++;
		}
		double result = StatUtils.mean(sample) / Simulator.MINUTE_UNIT;
		log.debug(result);
	}

	@Ignore
	@Test
	public void testNormalUptimes() {
		SimulatorTest.runSimulation(SIM_END);

		double[] sample = new double[testStub.normal.size()];
		int i = 0;
		for (Double samp : testStub.normal) {
			sample[i] = samp;
			i++;
		}
		double result = StatUtils.mean(sample) / Simulator.MINUTE_UNIT;
		log.debug(result);
	}

	@Ignore
	@Test
	public void testLongUptimes() {
		SimulatorTest.runSimulation(SIM_END);

		double[] sample = new double[testStub.longL.size()];
		int i = 0;
		for (Double samp : testStub.longL) {
			sample[i] = samp;
			i++;
		}
		double result = StatUtils.mean(sample) / Simulator.MINUTE_UNIT;
		log.debug(result);

	}

	@Ignore
	@Test
	public void testAllUptimes() {
		SimulatorTest.runSimulation(SIM_END);

		double[] sample = new double[testStub.all.size()];
		int i = 0;
		for (Double samp : testStub.all) {
			sample[i] = samp;
			i++;
		}
		double result = StatUtils.mean(sample) / Simulator.MINUTE_UNIT;

		log.debug(result);

	}

	public static class ChurnTestStub {
		List<Double> trans;

		List<Double> normal;

		List<Double> longL;

		List<Double> all;

		Map<Host, Long> online;

		Map<Host, Long> offline;

		ExponentialChurnModelTest testClass;

		List<Double> uptimePeers;

		int onlineCounter;

		public ChurnTestStub(List<Host> hosts,
				ExponentialChurnModelTest testClass) {
			online = new LinkedHashMap<Host, Long>();
			offline = new LinkedHashMap<Host, Long>();
			longL = new LinkedList<Double>();
			normal = new LinkedList<Double>();
			trans = new LinkedList<Double>();
			all = new LinkedList<Double>();
			uptimePeers = new LinkedList<Double>();
			this.testClass = testClass;
			for (Host host : hosts) {
				online.put(host, 0l);
			}
			uptimePeers.add(new Double(onlineCounter));

		}

		public void offlineEvent(Host host, long time) {
			onlineCounter--;
			log.debug(onlineCounter);
			uptimePeers.add(new Double(onlineCounter));
			Long online1 = this.online.remove(host);
			ChurnData data = testClass.getChurnData(host);
			if (data.type.equals(UserType.LONG_LASTING)) {
				this.longL.add(new Double(time - online1));
			} else if (data.type.equals(UserType.NORMAL)) {
				this.normal.add(new Double(time - online1));
			} else {
				this.trans.add(new Double(time - online1));
			}
			this.all.add(new Double(time - online1));
		}

		public void onlineEvent(Host host, long time) {
			this.online.put(host, time);
			onlineCounter++;
			uptimePeers.add(new Double(onlineCounter));
		}
	}
}
