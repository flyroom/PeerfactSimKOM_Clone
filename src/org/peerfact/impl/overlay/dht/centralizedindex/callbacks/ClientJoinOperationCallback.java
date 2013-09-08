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
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIClientNode;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayContact;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayID;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.AbstractCIClientOperation;
import org.peerfact.impl.overlay.dht.centralizedindex.operations.ClientJoinOperation;
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
public class ClientJoinOperationCallback extends
		AbstractCIClientOperation<Object> implements
		OperationCallback<Object> {

	private CIClientNode client;

	private int retry;

	public ClientJoinOperationCallback(CIClientNode client, int retry) {
		super(client);
		this.client = client;
		this.retry = retry;
	}

	@Override
	public void calledOperationFailed(Operation<Object> op) {
		log.info(retry + ". ClientJoinOperation with id " + op.getOperationID()
				+ " failed");

		// FIXME Just for workaround. the outer if-else-block is only a
		// workaround, since the server does not refresh its entries
		if (client.getHost().getNetLayer().isOffline()) {
			log
					.warn(Simulator
							.getFormattedTime(Simulator.getCurrentTime())
							+ " Scruffy Access to the DHT to remove "
							+ client.getOwnOverlayContact().toString()
							+ ". This is needed, since the DHT does not refresh its entries");
			client.getServer().getDHT().removeContact(
					client.getHost().getNetLayer().getNetID());
		} else {
			if (retry < 3) {
				retry = retry + 1;
				log.info(retry + ". Retry of ClientJoinOperation");
				((ClientJoinOperation) op).execute();
			} else {
				log.error(Simulator
						.getFormattedTime(Simulator.getCurrentTime())
						+ " "
						+ client.getOwnOverlayContact().getTransInfo()
								.getNetId().toString()
						+ " ----NO CHANCE TO JOIN, so trying a rejoin----");
				client.tryJoin();
			}
		}
	}

	@Override
	public void calledOperationSucceeded(Operation<Object> op) {
		CIOverlayID oid = (CIOverlayID) op.getResult();

		// initialize everything at the CIClientNode, what is needed
		client.setOverlayID(oid);
		client.getOwnOverlayContact().setOverlayID(oid);
		client.setPeerStatus(PeerStatus.PRESENT);

		// Create the serverOverlayContact
		CIOverlayID serverOID = ((ClientJoinOperation) op).getReplyMsg()
				.getSender();
		CIOverlayContact serverOverlayContact = new CIOverlayContact(
				serverOID, client.getServerTransInfo());
		client.setServerOverlayContact(serverOverlayContact);

		// start the SkyNetNode
		OverlayNode<?, ?> skyNetOverlay = client.getHost().getOverlay(
				AbstractSkyNetNode.class);
		if (skyNetOverlay != null) {
			long time = Simulator.getCurrentTime();
			((SkyNetNode) skyNetOverlay).startSkyNetNode(time);
		}
		// Create the SkyNetID and start the timers

		/*
		 * AddressResolutionImpl ari = AddressResolutionImpl .getInstance((int)
		 * SkyNetConstants.OVERLAY_ID_SIZE); SkyNetBigDecID skyNetID =
		 * ari.getSkyNetID(oid); AbstractSkyNetNode skyNetNode =
		 * (AbstractSkyNetNode) client.getHost()
		 * .getOverlay(AbstractSkyNetNode.class);
		 * skyNetNode.getSkyNetNodeInfo().setSkyNetID(skyNetID);
		 * skyNetNode.getSkyNetMessageHandler().setTryingJoin(false);
		 * log.info(skyNetNode.getSkyNetNodeInfo().toString()); long time =
		 * Simulator.getCurrentTime(); ((SkyNetNode)
		 * skyNetNode).setPresentTime(time); // Schedule next metric-update
		 * skyNetNode.getMetricUpdateStrategy().setSendingTime(time); long
		 * metricsTime = time +
		 * (skyNetNode.getMetricUpdateStrategy().getUpdateInterval());
		 * Simulator.scheduleEvent(new SkyNetEventObject(
		 * SkyNetEventType.METRICS_UPDATE, time), metricsTime, skyNetNode,
		 * null); // Schedule next attribute-update
		 * skyNetNode.getAttributeUpdateStrategy().setSendingTime(time); long
		 * attributeTime = time +
		 * (skyNetNode.getAttributeUpdateStrategy().getUpdateInterval());
		 * Simulator.scheduleEvent(new SkyNetEventObject(
		 * SkyNetEventType.ATTRIBUTE_UPDATE, time), attributeTime, skyNetNode,
		 * null); // other inits ((SkyNetHostProperties)
		 * skyNetNode.getHost().getProperties()).init();
		 */

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
