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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.mercury.MercuryIDOConfiguration;
import org.peerfact.impl.overlay.informationdissemination.mercury.MercuryIDONode;
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
 * This Operation execute a heartbeat. It disseminate the actually position to
 * the overlay, if in a defined time interval is no dissemination of the
 * position occurred.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class HeartbeatOperation extends
		AbstractOperation<MercuryIDONode, Object> {
	/**
	 * Node, which started this operation
	 */
	private MercuryIDONode node;

	public HeartbeatOperation(MercuryIDONode component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.node = component;
	}

	@Override
	protected void execute() {
		// FIXED >= leads to duplicate publications if Interval = n *
		// moveInterval
		if (Simulator.getCurrentTime() - node.getLastHeartbeat() > MercuryIDOConfiguration.INTERVAL_BETWEEN_HEARTBEATS) {
			node.disseminatePosition(node.getPosition());
			node.setLastHeartbeat(Simulator.getCurrentTime());
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Stop this operation.
	 */
	public void stop() {
		operationFinished(false);
	}

}
