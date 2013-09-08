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

package org.peerfact.impl.network.modular.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.City;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Continent;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Country;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.CountryCountrySummaryRelation;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.CountryRegionSummaryRelation;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Group;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Host;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.PingErRegion;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Region;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.RegionRegionSummaryRelation;
import org.peerfact.impl.util.logging.SimLogger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class NetMeasurementDBSmallTest {

	private static Logger log = SimLogger
			.getLogger(NetMeasurementDBSmallTest.class);

	public static void main(String[] args) {

		NetMeasurementDB nmdb = new NetMeasurementDB();

		Continent europe = nmdb.new Continent("Europe");
		Continent america = nmdb.new Continent("America");
		Continent asia = nmdb.new Continent("Asia");

		PingErRegion BlaLand = nmdb.new PingErRegion("Bla-Land");
		PingErRegion BlaLand2 = nmdb.new PingErRegion("Bla-Land 2");

		Country china = nmdb.new Country("China", asia, BlaLand, "CH");
		Country mongolia = nmdb.new Country("Mongolia", asia, BlaLand, "MO");

		Country usa = nmdb.new Country("USA", america, BlaLand, "US");
		Country brazil = nmdb.new Country("Brazil", america, BlaLand, "BR");

		Country germany = nmdb.new Country("Germany", europe, BlaLand2, "DE");
		Country france = nmdb.new Country("France", europe, BlaLand2, "FR");

		Region hesse = nmdb.new Region("Hesse", germany);
		Region nrw = nmdb.new Region("North Rhine Westfalia", germany);

		Region utah = nmdb.new Region("utah", usa);
		Region texas = nmdb.new Region("Texas", usa);

		City kassel = nmdb.new City("Kassel", hesse);
		City darmstadt = nmdb.new City("Darmstadt", hesse);

		City austin = nmdb.new City("Austin", texas);
		City dallas = nmdb.new City("Dallas", texas);

		CountryCountrySummaryRelation ccrel = nmdb.new CountryCountrySummaryRelation(
				china, mongolia, 1, 2, 3, 4);
		CountryRegionSummaryRelation cregrel = nmdb.new CountryRegionSummaryRelation(
				germany, BlaLand, 1, 2, 3, 4);
		RegionRegionSummaryRelation regregrel = nmdb.new RegionRegionSummaryRelation(
				BlaLand, BlaLand2, 1, 2, 3, 4);

		List<Double> coordinates = new ArrayList<Double>(5);
		coordinates.add(123.4);
		coordinates.add(234.5);
		coordinates.add(345.6);
		coordinates.add(456.7);
		coordinates.add(567.8);

		Host host = nmdb.new Host(12345, kassel, 123.45, 678.9, coordinates);
		Host host2 = nmdb.new Host(23456, austin, 123.45, 678.9, coordinates);

		List<Host> hostsInGroup = new ArrayList<Host>(2);
		hostsInGroup.add(host);
		hostsInGroup.add(host2);
		Group group = nmdb.new Group("Gigagruppe", hostsInGroup);

		log.debug(nmdb.getStats());

		try {
			nmdb.writeToXMLFile(new File("NetworkMeasurementDBTest.xml"),
					"TestDB");
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		NetMeasurementDB dbResult = new NetMeasurementDB();
		DefaultHandler hdlr = dbResult.getXMLHandler(1);
		try {
			SAXParserFactory f = SAXParserFactory.newInstance();
			f.setNamespaceAware(true);
			SAXParser parser = f.newSAXParser();
			parser.parse(new File("NetworkMeasurementDBTest.xml"), hdlr);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.debug(dbResult.getStats());

		try {
			dbResult.writeToXMLFile(new File("NetworkMeasurementDBTest2.xml"),
					"TestDB");
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
