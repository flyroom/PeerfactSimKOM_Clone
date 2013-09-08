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

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
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
 * A base type for messages that are exchanged in a Kademlia overlay network. It
 * allows access to the sender's and destination's KademliaOverlayID.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class KademliaMsg<T extends KademliaOverlayID> implements
		Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3403469168558867655L;

	/**
	 * The sender of this message.
	 */
	private final T sender;

	/**
	 * The receiver of this message.
	 */
	private final T destination;

	/**
	 * Why this message has been sent.
	 */
	private final Reason reason;

	/**
	 * Configuration values ("constants").
	 */
	protected final TypesConfig config;

	/**
	 * Constructs a new KademliaMessage which is a message exchanged between two
	 * peers in a Kademlia overlay network.
	 * 
	 * @param sender
	 *            the KademliaOverlayID of the sender of this message.
	 * @param destination
	 *            the KademliaOverlayID of the destination of this message.
	 * @param why
	 *            the reason why this message will be sent.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public KademliaMsg(final T sender, final T destination, final Reason why,
			final TypesConfig conf) {
		this.config = conf;
		this.sender = sender;
		this.destination = destination;
		reason = why;
	}

	/**
	 * @return the KademliaOverlayID of the sender of this message.
	 */
	public final T getSender() {
		return sender;
	}

	/**
	 * @return the KademliaOverlayID of the receiver of this message.
	 */
	public final T getDestination() {
		return destination;
	}

	/**
	 * Creates a StringBuilder that contains the header of this messages. It can
	 * be used to conveniently implement the {@link toString()} method.
	 * 
	 * @return a StringBuilder that contains the header of this message, that is
	 *         its destination and its sender.
	 */
	// protected StringBuilder getHeaderStringBuilder() {
	// StringBuilder header = new StringBuilder();
	// header.append("Destination: ").append(this.destination).append(" / ");
	// header.append("Sender: ").append(this.sender).append(" / ");
	// return header;
	// }
	/**
	 * @return the size of this message in bytes, that is the size of this
	 *         message's payload and of the headers.
	 */
	@Override
	public final long getSize() {
		return getOtherFieldSize() + 2 * (config.getIDLength() / 8);
	}

	/**
	 * @return the size of the other fields (everything except the headers
	 *         defined in KademliaMsg) in bytes.
	 */
	protected abstract long getOtherFieldSize();

	/**
	 * Returns the payload of this message. KademliaMsgs do not allow nesting
	 * and allow access to their payload (a lookup result, for instance) via
	 * special getter methods, hence this method always returns
	 * <code>null</code>.
	 * 
	 * @return <code>null</code>
	 */
	@Override
	public final KademliaMsg<T> getPayload() {
		return null;
	}

	/**
	 * @return the reason why this message has been sent.
	 */
	public final Reason getReason() {
		return reason;
	}
}
