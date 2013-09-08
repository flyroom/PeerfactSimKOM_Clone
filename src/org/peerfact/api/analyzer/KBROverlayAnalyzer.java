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

package org.peerfact.api.analyzer;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;

/**
 * KBROverlayAnalyzers receive notifications about events on the KBR layer.
 * This way it is possible to collect data independent of the used overlay.
 * To use this kind of analyzer in a meaningful way you have to use an
 * application that uses the KBR layer.
 * 
 * @author Julius Rueckert
 * 
 */
public interface KBROverlayAnalyzer extends Analyzer {

	/**
	 * Informs about the forward of a routed message on the KBR layer
	 * 
	 * @param sender
	 *            the contact of the forwarding peer
	 * @param receiver
	 *            the contact of the destination peer
	 * @param msg
	 *            the forwarded message
	 * @param hops
	 *            the current number of hops of the forwarded message
	 */
	public void overlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int hops);

	/**
	 * Informs about the delivery of a routed message on the KBR layer
	 * 
	 * @param contact
	 *            the contact of the peer that the message is delivered at
	 * @param msg
	 *            the delivered message
	 * @param hops
	 *            the final number of hops for the whole routing of the
	 *            message
	 */
	public void overlayMessageDelivered(OverlayContact<?> contact,
			Message msg,
			int hops);

	/**
	 * Informs about the start of a new query on the KBR layer. A query is
	 * started when a message is routed towards a key and not to a concrete
	 * receiver. These messages are sent to a next hop determined by the
	 * overlay.
	 * 
	 * @param contact
	 *            the contact of the peer that starts the query
	 * @param appMsg
	 *            the application message that is routed
	 */
	public void queryStarted(OverlayContact<?> contact, Message appMsg);

	/**
	 * Informs about the fail of a query on the KBR layer. This happens if a
	 * host can not determine a next hop during the routing process.
	 * 
	 * @param failedHop
	 *            the host that could not determine a next hop
	 * @param appMsg
	 *            the application message of the query
	 */
	public void queryFailed(OverlayContact<?> failedHop, Message appMsg);
}