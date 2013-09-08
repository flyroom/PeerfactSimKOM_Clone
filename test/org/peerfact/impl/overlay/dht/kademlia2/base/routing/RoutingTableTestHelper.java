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

package org.peerfact.impl.overlay.dht.kademlia2.base.routing;

import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.RoutingTableEntry;

import junitx.util.PrivateAccessor;


/**
 * Helper methods for routing table tests.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class RoutingTableTestHelper {

	/**
	 * Returns the stale counter of RoutingTableEntry <code>e</code>.
	 * 
	 * @param e
	 *            the RoutingTableEntry whose stale counter is of interest.
	 * @return the stale counter of <code>e</code>.
	 * @throws NoSuchFieldException
	 *             if the name of the stale counter variable has changed and has
	 *             been forgotten here.
	 */
	public static <T extends KademliaOverlayID> int getStaleCounter(
			RoutingTableEntry<T> e) throws NoSuchFieldException {
		return (Integer) PrivateAccessor.getField(e, "staleCounter");
	}
}
