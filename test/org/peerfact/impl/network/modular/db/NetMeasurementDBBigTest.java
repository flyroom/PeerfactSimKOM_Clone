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

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.db.NetMeasurementDB.Group;
import org.peerfact.impl.util.logging.SimLogger;


public class NetMeasurementDBBigTest {
	private static Logger log = SimLogger
			.getLogger(NetMeasurementDBBigTest.class);

	@Test
	public void test1() throws Exception {

		NetMeasurementDB db = readFromFile();

		Group germanyGrp = db.getStringAddrObjFromStr(
				NetMeasurementDB.Group.class, "Germany");
		assertNotNull(germanyGrp);
		log.debug("Successfully found group Germany: "
				+ germanyGrp.getMembers().size() + " members.");
		NetMeasurementDB.Host testHost = germanyGrp.getMembers().get(0);
		log.debug(testHost);
		assertNotNull(testHost);

	}

	public static NetMeasurementDB readFromFile() throws Exception {
		NetMeasurementDB db = new NetMeasurementDB();
		db.readFromXMLFile(new File("data/mod_measured_data.xml"));
		return db;
	}

	public static void main(String[] args) {
		try {
			new NetMeasurementDBBigTest().test1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
