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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations;

import java.math.BigInteger;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaConfiguration;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayNode;


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
public abstract class BaseOperation extends
		AbstractOperation<GnutellaOverlayNode, Object> {

	protected int ttl;

	protected int hops;

	protected BigInteger descriptor;

	protected BaseOperation(GnutellaOverlayNode component, int maxTTL, int ttl,
			int hops, BigInteger descriptor, OperationCallback<Object> callback) {
		super(component, callback);
		this.ttl = ttl;
		this.hops = hops;
		this.descriptor = descriptor;

		if (this.descriptor == null) {
			this.descriptor = GnutellaConfiguration
					.generateDescriptor(component.getOverlayID()
							.getUniqueValue());
		}

		// don't process if negative hop count
		if (this.hops < 0) {
			this.hops = 0;
			this.ttl = 0;
		}

		// decrease ttl, if higher than maximal ttl
		if (this.hops + this.ttl > maxTTL) {
			this.ttl = maxTTL - this.hops;
		}
	}

	public BigInteger getDescriptor() {
		return this.descriptor;
	}

	@Override
	public Object getResult() {
		return this;
	}
}
