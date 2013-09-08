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
import java.util.List;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing.FilesharingKey;
import org.peerfact.impl.simengine.Simulator;


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
public class QueryHitMessage extends BaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1822985966642833219L;

	private List<FilesharingKey> keys;

	private final static long QUERY_HIT_MESSAGE_SIZE = 18;

	// TODO file description
	private OverlayContact<GnutellaOverlayID> contact;

	private long searchStringSize;

	public QueryHitMessage(GnutellaOverlayID sender,
			GnutellaOverlayID receiver, int ttl, int hops,
			BigInteger descriptor, OverlayContact<GnutellaOverlayID> contact,
			List<FilesharingKey> keys) {
		super(sender, receiver, ttl, hops, descriptor);
		this.contact = contact;
		this.keys = keys;
		searchStringSize = (long) (Simulator.getRandom().nextDouble() * 40);
	}

	public OverlayContact<GnutellaOverlayID> getContact() {
		return contact;
	}

	public List<FilesharingKey> getKeys() {
		return keys;
	}

	@Override
	public long getSize() {
		// TODO low priority: correct message size
		return super.getSize() + QUERY_HIT_MESSAGE_SIZE + searchStringSize;
	}
}
