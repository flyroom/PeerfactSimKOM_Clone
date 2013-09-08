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

package org.peerfact.impl.overlay.dht.pastry;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.peerfact.impl.overlay.dht.pastry.components.PastryContact;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;
import org.peerfact.impl.overlay.dht.pastry.nodestate.StateHelpers;


public class StateHelpersTest {

	@Test
	public void testGetClosestContact() {

		PastryID id1 = new PastryID(BigInteger.valueOf(1));
		PastryID id2 = new PastryID(BigInteger.valueOf(2));
		PastryID id3 = new PastryID(BigInteger.valueOf(5));
		PastryID id4 = new PastryID(BigInteger.valueOf(9));
		PastryID id5 = new PastryID(BigInteger.valueOf(14));

		PastryContact c1 = new PastryContact(id1, null);
		PastryContact c2 = new PastryContact(id2, null);
		PastryContact c3 = new PastryContact(id3, null);
		PastryContact c4 = new PastryContact(id4, null);
		PastryContact c5 = new PastryContact(id5, null);

		List<PastryContact> contacts = new LinkedList<PastryContact>();
		contacts.add(c1);
		contacts.add(c2);
		contacts.add(c3);
		contacts.add(c4);
		contacts.add(c5);

		assertEquals(c1, StateHelpers.getClosestContact(id1, contacts));

		assertEquals(
				c2,
				StateHelpers.getClosestContact(
						new PastryID(BigInteger.valueOf(3)), contacts));

		assertEquals(
				c3,
				StateHelpers.getClosestContact(
						new PastryID(BigInteger.valueOf(4)), contacts));

		assertEquals(
				c5,
				StateHelpers.getClosestContact(
						new PastryID(BigInteger.valueOf(100)), contacts));

		assertEquals(c1, StateHelpers.getClosestContact(new PastryID(
				PastryID.MAX_ID_VALUE), contacts));
	}
}
