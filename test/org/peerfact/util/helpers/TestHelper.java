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

package org.peerfact.util.helpers;

import org.peerfact.api.network.NetID;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.network.simple.SimpleNetID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableConfig;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
import org.peerfact.impl.simengine.Scheduler;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;

import junitx.util.PrivateAccessor;

/**
 * This class is used by JUnit-Tests to set up some minimally necessary
 * simulator-wide variables.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 */
public class TestHelper {

	/**
	 * Makes sure the simulator is in a state in which basic unit tests can be
	 * carried out, for instance, a scheduler is registered.
	 */
	public static void initSimulator() {
		Simulator.getInstance();
	}

	/**
	 * Dirty hack to set the simulation time (only for testing purposes where
	 * the simulation engine itself is not running).
	 * 
	 * @param time
	 *            the simulation time to be set.
	 * @throws NoSuchFieldException
	 *             if the variable name in the scheduler implementation holding
	 *             the current simulation time has been modified but not been
	 *             adapted in this method.
	 */
	public static void setSimulationTime(long time) throws NoSuchFieldException {
		Scheduler sched = (Scheduler) PrivateAccessor.getField(Simulator.class,
				"scheduler");

		PrivateAccessor.setField(sched, "currentTime", time);
	}

	/**
	 * Creates a KademliaOverlayContact with the given <code>id</code>, and
	 * pseudo TransportAddress.
	 * 
	 * @param id
	 *            the KademliaOverlayID to be used in the new contact.
	 * @param randomNumber
	 *            some random number used to generate NetIDs and ports needed in
	 *            TransportAddresses needed for the returned
	 *            KademliaOverlayContact.
	 * @return a new contact with the given <code>id</code>.
	 */
	public static <T extends KademliaOverlayID> KademliaOverlayContact<T> createContact(
			T id, int randomNumber) {
		NetID someNetID;
		TransInfo someTransportAddress;
		KademliaOverlayContact<T> result;

		someNetID = new SimpleNetID(randomNumber);
		someTransportAddress = DefaultTransInfo.getTransInfo(someNetID,
				(short) (2 * randomNumber));
		result = new KademliaOverlayContact<T>(id, someTransportAddress);
		return result;
	}

	/**
	 * Creates a KademliaOverlayContact with the given <code>id</code> as a
	 * HKademliaOverlayID.
	 * 
	 * @param id
	 *            a String that contains the binary representation of the ID.
	 * @param conf
	 *            a RoutingTableConfig with constants for ID length etc.
	 * @return a new contact with the given <code>id</code>.
	 */
	public static KademliaOverlayContact<HKademliaOverlayID> createContact(
			String id, RoutingTableConfig conf) {
		HKademliaOverlayID oid = new HKademliaOverlayID(id, conf);
		return createContact(oid, oid.hashCode());
	}

	/**
	 * Creates a KademliaOverlayContact with the given <code>id</code> as a
	 * KademliaOverlayID.
	 * 
	 * @param id
	 *            a String that contains the binary representation of the ID.
	 * @param conf
	 *            a RoutingTableConfig with constants for ID length etc.
	 * @return a new contact with the given <code>id</code>.
	 */
	public static KademliaOverlayContact<KademliaOverlayID> createStdContact(
			String id, RoutingTableConfig conf) {
		KademliaOverlayID oid = new KademliaOverlayID(id, conf);
		return createContact(oid, oid.hashCode());
	}

}
