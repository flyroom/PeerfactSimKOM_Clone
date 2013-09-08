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

package org.peerfact.impl.network.gnp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.network.gnp.GeoLocationOracle;
import org.peerfact.impl.network.gnp.GnpLatencyModel;
import org.peerfact.impl.network.gnp.GnpNetLayer;
import org.peerfact.impl.network.gnp.GnpNetLayerFactory;

public class GeoLocationOracleTest extends Assert {

	GnpNetLayer CH, IT, US1, US2, CN, ISP_City_1, ISP_City_2, Region_1,
	Region_2, Hessen_DA1, Hessen_DA2, Hessen_KA, Berlin_1, Berlin_2,
	Johannesburg, California, Oregon, North_Carolina_1,
	North_Carolina_2;

	GnpNetLayerFactory netLayerFactoryGnp;

	GnpLatencyModel latencyModelGnp;

	@Before
	public void setUp() {
		try {
			latencyModelGnp = new GnpLatencyModel();
			latencyModelGnp.setUsePingErJitter(true);

			netLayerFactoryGnp = new GnpNetLayerFactory();
			netLayerFactoryGnp
			.setGnpFile("test/de/tud/kom/p2psim/impl/network/gnp/testISP.xml");
			netLayerFactoryGnp.setLatencyModel(latencyModelGnp);

			// fake entries (not realistic)
			CH = netLayerFactoryGnp.newNetLayer("Switzerland");
			IT = netLayerFactoryGnp.newNetLayer("Italy");
			US1 = netLayerFactoryGnp.newNetLayer("Massachusetts");
			US2 = netLayerFactoryGnp.newNetLayer("NewYork");
			CN = netLayerFactoryGnp.newNetLayer("EastAsia");
			ISP_City_1 = netLayerFactoryGnp.newNetLayer("ISP_City_Test1");
			ISP_City_2 = netLayerFactoryGnp.newNetLayer("ISP_City_Test2");
			Region_1 = netLayerFactoryGnp.newNetLayer("Region_Test1");
			Region_2 = netLayerFactoryGnp.newNetLayer("Region_Test2");

			// real measured entries (derived from measured data)
			Hessen_DA1 = netLayerFactoryGnp.newNetLayer("Hessen_DA1");
			Hessen_DA2 = netLayerFactoryGnp.newNetLayer("Hessen_DA2");
			Hessen_KA = netLayerFactoryGnp.newNetLayer("Hessen_KA");
			Berlin_1 = netLayerFactoryGnp.newNetLayer("Berlin_1");
			Berlin_2 = netLayerFactoryGnp.newNetLayer("Berlin_2");
			Johannesburg = netLayerFactoryGnp.newNetLayer("Johannesburg");
			California = netLayerFactoryGnp.newNetLayer("California");
			Oregon = netLayerFactoryGnp.newNetLayer("Oregon");
			North_Carolina_1 = netLayerFactoryGnp
					.newNetLayer("North_Carolina_1");
			North_Carolina_2 = netLayerFactoryGnp
					.newNetLayer("North_Carolina_2");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGeoPriority() {
		assertTrue(GeoLocationOracle.getGeoPriority(CN.getNetID(), US1
				.getNetID()) == 0);
		assertTrue(GeoLocationOracle.getGeoPriority(CH.getNetID(), IT
				.getNetID()) == 1);
		assertTrue(GeoLocationOracle.getGeoPriority(US1.getNetID(), US2
				.getNetID()) == 2);
		assertTrue(GeoLocationOracle.getGeoPriority(Region_1.getNetID(),
				Region_2.getNetID()) == 3);
		assertTrue(GeoLocationOracle.getGeoPriority(ISP_City_1.getNetID(),
				ISP_City_2.getNetID()) == 4);
	}

	// @Test
	// public void testGetPropagationDelay() {
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(Hessen_DA1.getNetID(),
	// Hessen_DA2.getNetID()));
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(Hessen_DA1.getNetID(),
	// Hessen_KA.getNetID()));
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(Hessen_DA1.getNetID(),
	// Berlin_1.getNetID()));
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(Hessen_DA1.getNetID(),
	// Berlin_2.getNetID()));
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(Berlin_2.getNetID(),
	// Berlin_1.getNetID()));
	//
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(Oregon.getNetID(),
	// California.getNetID()));
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(North_Carolina_1.getNetID(),
	// California.getNetID()));
	// log.debug(GeoLocationOracle.getInternetPropagationDelay(California.getNetID(),
	// North_Carolina_1.getNetID()));
	// }

	// @Test
	// public void testGeoDistance() {
	// log.debug(GeoLocationOracle.getGeographicalDistance(Hessen_DA1.getNetID(),
	// Hessen_KA.getNetID()));
	// log.debug(GeoLocationOracle.getGeographicalDistance(Hessen_DA1.getNetID(),
	// Berlin_2.getNetID()));
	// log.debug(GeoLocationOracle.getGeographicalDistance(Hessen_DA1.getNetID(),
	// North_Carolina_1.getNetID()));
	// log.debug(GeoLocationOracle.getGeographicalDistance(Hessen_DA1.getNetID(),
	// Johannesburg.getNetID()));
	// }

	@After
	public void tearDown() {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.
	}

}
