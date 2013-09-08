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

package org.peerfact.impl.overlay.informationdissemination.mercury.operation;

import java.awt.Point;
import java.util.Vector;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.mercury.MercuryIDONode;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryService;
import org.peerfact.impl.service.publishsubscribe.mercury.filter.IMercuryFilter;
import org.peerfact.impl.service.publishsubscribe.mercury.filter.IMercuryFilter.OPERATOR_TYPE;


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
 * This operation execute in time intervals a subscription for the given node.<br>
 * 
 * This is needed, because the node change his position. Therefore it change the
 * subscription.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class SubscriptionOperation extends
		AbstractOperation<MercuryIDONode, Object> {
	/**
	 * Node, which started this operation
	 */
	private MercuryIDONode node;

	public SubscriptionOperation(MercuryIDONode node,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.node = node;
	}

	@Override
	protected void execute() {
		if (node.isPresent()) {
			Point position = node.getPosition();
			Vector<IMercuryFilter> filters = new Vector<IMercuryFilter>();
			node.getService();
			filters.add(MercuryService.createFilter(
					node.getService().getAttributeByName("x"),
					position.x - node.getAOI(), OPERATOR_TYPE.greater));
			filters.add(MercuryService.createFilter(
					node.getService().getAttributeByName("x"),
					position.x + node.getAOI(), OPERATOR_TYPE.smaller));
			filters.add(MercuryService.createFilter(
					node.getService().getAttributeByName("y"),
					position.y - node.getAOI(), OPERATOR_TYPE.greater));
			filters.add(MercuryService.createFilter(
					node.getService().getAttributeByName("y"),
					position.y + node.getAOI(), OPERATOR_TYPE.smaller));
			node.getService().subscribe(filters);
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// do nothing
		return null;
	}

	/**
	 * Stop this operation.
	 */
	public void stop() {
		operationFinished(false);
	}
}
