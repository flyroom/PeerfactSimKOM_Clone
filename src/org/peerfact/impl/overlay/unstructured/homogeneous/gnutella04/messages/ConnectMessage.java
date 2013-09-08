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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.AbstractOverlayMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayID;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ConnectMessage extends AbstractOverlayMessage<GnutellaOverlayID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 157531584545611643L;

	private final static long GNUTELLA_CONNECT_MESSAGE_SIZE = 23;

	private OverlayContact<GnutellaOverlayID> contact;

	public ConnectMessage(GnutellaOverlayID sender, GnutellaOverlayID receiver,
			OverlayContact<GnutellaOverlayID> contact) {
		super(sender, receiver);
		this.contact = contact;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	public OverlayContact<GnutellaOverlayID> getContact() {
		return contact;
	}

	@Override
	public long getSize() {
		return GNUTELLA_CONNECT_MESSAGE_SIZE;
	}

}
