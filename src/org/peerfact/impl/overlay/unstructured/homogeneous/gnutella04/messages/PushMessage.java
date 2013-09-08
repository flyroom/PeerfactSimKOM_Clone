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

import java.math.BigInteger;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing.FilesharingKey;


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
public class PushMessage extends BaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4924805726117276898L;

	private static final long GNUTELLA_PUSH_MESSAGE_SIZE = 9999;

	private GnutellaOverlayID pushTarget;

	private FilesharingKey key;

	private OverlayContact<GnutellaOverlayID> pushInitiator;

	public PushMessage(GnutellaOverlayID sender, GnutellaOverlayID receiver,
			int ttl, int hops, BigInteger descriptor,
			OverlayContact<GnutellaOverlayID> pushInitiator,
			GnutellaOverlayID pushTarget, FilesharingKey key) {
		super(sender, receiver, ttl, hops, descriptor);
		this.pushInitiator = pushInitiator;
		this.pushTarget = pushTarget;
		this.key = key;
	}

	public OverlayContact<GnutellaOverlayID> getPushSender() {
		return this.pushInitiator;
	}

	public GnutellaOverlayID getPushReceiver() {
		return this.pushTarget;
	}

	public FilesharingKey getKey() {
		return this.key;
	}

	@Override
	public long getSize() {
		return super.getSize() + GNUTELLA_PUSH_MESSAGE_SIZE
				+ key.getTransmissionSize();
	}

}
