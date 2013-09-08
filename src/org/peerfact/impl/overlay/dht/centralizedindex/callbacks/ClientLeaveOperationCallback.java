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

package org.peerfact.impl.overlay.dht.centralizedindex.callbacks;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIClientNode;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.AbstractCIClientOperation;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;

/**
 * Implementing a centralized DHT overlay, whose organization of the centralized
 * index is similar to the distributed index of Chord
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 */
public class ClientLeaveOperationCallback extends
		AbstractCIClientOperation<Object> implements OperationCallback<Object> {

	private CIClientNode client;

	private int retry;

	public ClientLeaveOperationCallback(CIClientNode client, int retry) {
		super(client);
		this.client = client;
		this.retry = retry;
	}

	@Override
	public void calledOperationFailed(Operation<Object> op) {
		log.info("ClientLeaveOperation with id " + op.getOperationID()
				+ " failed");
		if (retry < 3) {
			retry = retry + 1;
			log.info(retry + ". Retry of ClientLeaveOperation");
			client.leave(new ClientLeaveOperationCallback(client, retry));
		} else {
			retry = 0;
			log.error("------NO CHANCE TO LEAVE------");
		}

	}

	@Override
	public void calledOperationSucceeded(Operation<Object> op) {
		retry = 0;
		client.setPeerStatus(PeerStatus.ABSENT);

		log.debug("ClientLeaveOperation with id " + op.getOperationID()
				+ " succeeded. The client left the overlay ");

		// reset the setting of the overlays
		SkyNetNode skyNetNode = (SkyNetNode) client.getHost().getOverlay(
				AbstractSkyNetNode.class);
		client.resetNapsterClient();
		skyNetNode.resetSkyNetNode(Simulator.getCurrentTime());

	}

	@Override
	protected void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
