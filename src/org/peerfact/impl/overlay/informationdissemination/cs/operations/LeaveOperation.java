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

package org.peerfact.impl.overlay.informationdissemination.cs.operations;

import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.cs.ClientNode;
import org.peerfact.impl.overlay.informationdissemination.cs.messages.LeaveMessage;
import org.peerfact.impl.overlay.informationdissemination.cs.util.CSConfiguration;

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
 * Leave Operation of a client. It sends to the server a {@link LeaveMessage}.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class LeaveOperation extends AbstractOperation<ClientNode, Object> {

	ClientNode node;

	public LeaveOperation(ClientNode node) {
		super(node);
		this.node = node;
	}

	@Override
	protected void execute() {
		LeaveMessage msg = new LeaveMessage(node.getOverlayID());

		node.getTransLayer().send(msg, node.getServerTransInfo(),
				node.getPort(), CSConfiguration.TRANSPORT_PROTOCOL);
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// Nothing to get back.
		return null;
	}

}
