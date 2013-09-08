/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.kademlia.base.messages;

import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.Reason;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * A message format for transferring data items in the Kademlia overlay network.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Sebastian Kaune
 * 
 * @param <T>
 *            the concrete type of KademliaOverlayID that is being used in this
 *            message.
 * @version 05/06/2011
 */
public final class DataMsg<T extends KademliaOverlayID> extends KademliaMsg<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7479944171598434079L;

	/**
	 * The key of the data item that is being transfered.
	 */
	private final KademliaOverlayKey key;

	/**
	 * The data item that is being transfered.
	 */
	private final DHTObject value;

	/**
	 * Constructs a new DataMsg that is used to transfer data items (either
	 * after a data lookup or when data is stored on nodes in the DHT).
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param key
	 *            the key of the data item to be transfered.
	 * @param value
	 *            the data to be transfered.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public DataMsg(final T sender, final T destination,
			final KademliaOverlayKey key, final DHTObject value,
			final Reason why, final TypesConfig conf) {
		super(sender, destination, why, conf);
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key associated with the data item returned by {@link
	 *         getValue()}.
	 */
	public final KademliaOverlayKey getKey() {
		return this.key;
	}

	/**
	 * @return the data item associated with the key returned by {@link
	 *         getKey()}.
	 */
	public final DHTObject getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final long getOtherFieldSize() {
		return (config.getIDLength() / 8) + config.getDataSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[DataMsg|from:" + getSender() + "; to:" + getDestination()
				+ "; reason:" + getReason() + "; data:" + value + "/" + key
				+ "]";
	}
}
