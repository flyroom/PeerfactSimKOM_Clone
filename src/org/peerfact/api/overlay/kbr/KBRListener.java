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

package org.peerfact.api.overlay.kbr;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;

/**
 * The Common API for Upcalls from Paper
 * "Towards a Common API for Structured Peer-to-Peer Overlays"
 * 
 * @author Eser <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface KBRListener<T extends OverlayID<?>, S extends OverlayContact<T>, K extends OverlayKey<?>> {

	/**
	 * This upcall is invoked at each OverlayAgent that forwards message
	 * <code>msg</code>, including the source OverlayAgent, and the key's root
	 * node (before deliver is invoked). The upcall informs the application that
	 * message <code>msg</code> with OverlayKey <code>key</code> is about to be
	 * forwarded to nextHopAgent. The application may modify the
	 * <code>msg</code>, <code>key</code>, or nextHopNode parameters or
	 * terminate the message by setting nextHopAgent to <code>null</code>.
	 * 
	 * @param information
	 *            , contains msg, key and nextHopAgent
	 */
	public void forward(KBRForwardInformation<T, S, K> information);

	/**
	 * This function is invoked on the OverlayAgent that is the root for
	 * OverlayKey <code>key</code> upon the arrival of Message <code>msg</code>.
	 * 
	 * @param key
	 * @param msg
	 */
	public void deliver(K key, Message msg);

	/**
	 * This upcall is invoked to inform the application that OverlayContact
	 * contact has either joined or left the neighbor set of the local node as
	 * that set would be returned by the neighborSet call.
	 * 
	 * @param contact
	 *            info about that contact
	 * @param joined
	 *            joined the network?
	 */
	public void update(S contact, boolean joined);
}
