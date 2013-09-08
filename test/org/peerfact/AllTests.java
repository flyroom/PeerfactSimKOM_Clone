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

package org.peerfact;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.peerfact.email.EmailTest;
import org.peerfact.impl.churn.model.ExponentialChurnModelTest;
import org.peerfact.impl.churn.model.StaticChurnModelTest;
import org.peerfact.impl.network.gnp.GnpLatencyModelTest;
import org.peerfact.impl.network.gnp.GnpNetBandwidthManagerTest;
import org.peerfact.impl.network.gnp.GnpNetLayerTest;
import org.peerfact.impl.network.simple.SimpleLatencyModelTest;
import org.peerfact.impl.network.simple.SimpleStaticLatencyTest;
import org.peerfact.impl.overlay.dht.centralized.CentralizedTest;
import org.peerfact.impl.scenario.ConfigurationFileTest;
import org.peerfact.impl.scenario.ExtendedScenarioTest;
import org.peerfact.impl.simengine.SchedulerTest;
import org.peerfact.impl.transport.DefaultTransLayerInOrderDeliveryTest;
import org.peerfact.impl.transport.DefaultTransLayerTest;
import org.peerfact.impl.util.vivaldi.VivaldiCoordinateTest;
import org.peerfact.util.ConfiguratorTest;


/**
 * This class is the test suite (as done in JUnit 4.0). All relevant test case
 * classes should be included here. Please run it ALWAYS before making a commit
 * to CVS and only perform a commit after all tests keep running.
 * 
 * @author Konstantin Pussep
 * @version 0.1, 21.08.2007
 * 
 */
@RunWith(value = Suite.class)
@SuiteClasses(value = {
		// ignore abstract tests: SimulatorTest.class and ComponentTest.class,
		// and the deprecated ModuleTest.class
		CentralizedTest.class,
		// TODO: revise ConfidenceIntervalTest.class,
		ConfigurationFileTest.class,
		ConfiguratorTest.class,
		ExtendedScenarioTest.class,
		DefaultTransLayerTest.class,
		EmailTest.class,
		ExponentialChurnModelTest.class,
		SchedulerTest.class,
		SimpleLatencyModelTest.class,
		SimpleStaticLatencyTest.class,
		StaticChurnModelTest.class,
		GnpLatencyModelTest.class,
		GnpNetBandwidthManagerTest.class,
		GnpNetLayerTest.class,
		DefaultTransLayerInOrderDeliveryTest.class,
		VivaldiCoordinateTest.class
})
public class AllTests {
	// intentionally left blank
}
