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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;

/**
 * Sent to a node to notify it that the connection shall be closed There is no
 * response following this message.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaClose<TContact extends GnutellaLikeOverlayContact> extends
		AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7055112058313181190L;

	private TContact forContact;

	/**
	 * Creates a new close message
	 * 
	 * @param sender
	 *            : the node that sends this message.
	 * @param forContact
	 *            : the contact that caused the sender to close the connection.
	 *            null if no contact caused it.
	 */
	public GnutellaClose(TContact sender,
			TContact forContact) {
		this.sndr = sender;
		this.forContact = forContact;
	}

	/**
	 * Returns the sender of this message.
	 * 
	 * @return
	 */
	public TContact getSndr() {
		return sndr;
	}

	/**
	 * Returns the contact that caused the sender to close the connection.
	 * 
	 * @return
	 */
	public TContact getCausedContact() {
		return forContact;
	}

	public TContact sndr;

	@Override
	public long getGnutellaPayloadSize() {
		return (forContact != null) ? GnutellaLikeOverlayContact.getSize() : 0;
	}

	@Override
	public String toString() {
		return "Close: sender=" + sndr;
	}

}
