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

package org.peerfact.impl.overlay.unstructured.zeroaccess.operation;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.unstructured.zeroaccess.components.ZeroAccessOverlayContact;
import org.peerfact.impl.overlay.unstructured.zeroaccess.components.ZeroAccessOverlayNode;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.GetLMessage;

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
public class GetLOperation extends
		AbstractOperation<ZeroAccessOverlayNode, Object> {

	private ZeroAccessOverlayNode node;

	private TransInfo connectInfo;

	public GetLOperation(ZeroAccessOverlayNode node, TransInfo connectInfo,
			OperationCallback<Object> callback) {
		super(node, callback);
		this.connectInfo = connectInfo;
		this.node = node;
	}

	@Override
	protected void execute() {
		ZeroAccessOverlayContact contact = new ZeroAccessOverlayContact(
				node.getOverlayID(), node.getTransLayer()
						.getLocalTransInfo(node.getPort()));
		GetLMessage message = new GetLMessage(
				this.node.getOverlayID(), null, contact);
		node.getTransLayer().send(message, connectInfo, node.getPort(),
				TransProtocol.UDP);
	}

	@Override
	public Object getResult() {
		return this;
	}

}
