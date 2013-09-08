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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IResource;


/**
 * If its set of shared documents changes, this message is sent by a leaf to all
 * of its ultrapeers in order to let them know about the change.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaResources<TContact extends GnutellaLikeOverlayContact>
		extends AbstractGnutellaMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6370494917397158023L;

	Set<IResource> resources;

	private TContact sender;

	/**
	 * Creates a new resources message, given the sender and its resource set.
	 * 
	 * @param sender
	 * @param resources
	 */
	public GnutellaResources(TContact sender,
			Set<IResource> resources) {
		Set<IResource> resourcesSet = Collections
				.unmodifiableSet(new LinkedHashSet<IResource>(
						resources));
		this.sender = sender;
		this.resources = resourcesSet;
	}

	/**
	 * Returns the set of resources encapsulated in this message.
	 * 
	 * @return
	 */
	public Set<IResource> getResources() {
		return resources;
	}

	/**
	 * Returns the sender contact information of this message.
	 * 
	 * @return
	 */
	public TContact getSender() {
		return sender;
	}

	@Override
	public long getGnutellaPayloadSize() {
		int size = 0;
		for (IResource res : resources) {
			size += res.getSize();
		}

		return size;
	}

	@Override
	public String toString() {
		return "RESOURCES: resources=" + resources + ", sender=" + sender;
	}

}
