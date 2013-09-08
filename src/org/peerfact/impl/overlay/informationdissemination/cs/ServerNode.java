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

package org.peerfact.impl.overlay.informationdissemination.cs;

import java.util.LinkedHashMap;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.informationdissemination.cs.operations.ServerDisseminationOperation;
import org.peerfact.impl.overlay.informationdissemination.cs.operations.ServerMaintenanceOperation;
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
 * This class is the main class for the server in the Client/Server IDO system.
 * With this server connects all clients. Additionally gives this server a
 * unique ID to a client back.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ServerNode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends AbstractOverlayNode<T, S> {

	/**
	 * A unique ID counter for the clientIDs
	 */
	private int uniqueIDCounter = 0;

	/**
	 * The {@link TransLayer} for this server
	 */
	private TransLayer translayer;

	/**
	 * The incoming Message Handler
	 */
	private ServerMessageHandler msgHandler;

	/**
	 * The storage of the server. It stores the incoming client node infos.
	 */
	private ServerStorage serverStorage;

	/**
	 * The dissemination operations.
	 */
	private LinkedHashMap<ClientID, ServerDisseminationOperation> disseminationOperations;

	protected ServerNode(TransLayer translayer, short port, int maxClients) {
		super(null, port);

		this.translayer = translayer;
		this.msgHandler = new ServerMessageHandler(this);
		this.getTransLayer().addTransMsgListener(this.msgHandler,
				this.getPort());

		this.serverStorage = new ServerStorage(maxClients);

		this.maintenanceEvent();

		this.disseminationOperations = new LinkedHashMap<ClientID, ServerDisseminationOperation>();

		if (getTransLayer().getHost().getNetLayer().isOnline()) {
			setPeerStatus(PeerStatus.PRESENT);
		} else {
			setPeerStatus(PeerStatus.ABSENT);
		}
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {
			setPeerStatus(PeerStatus.PRESENT);
		} else {
			setPeerStatus(PeerStatus.ABSENT);
		}
	}

	@Override
	public TransLayer getTransLayer() {
		return translayer;
	}

	/**
	 * Returns an unique ClientID.
	 * 
	 * @return A unique ClientID.
	 */
	public ClientID getUniqueClientID() {
		ClientID result = new ClientID(uniqueIDCounter);
		uniqueIDCounter++;
		return result;
	}

	/**
	 * Gets the {@link TransInfo} of this node
	 * 
	 * @return The {@link TransInfo} of this node
	 */
	public TransInfo getTransInfo() {
		return getTransLayer().getLocalTransInfo(this.getPort());
	}

	public ServerStorage getStorage() {
		return serverStorage;
	}

	/**
	 * Starts the dissemination operation of the given client ID.
	 * 
	 * @param id
	 *            The ID of the client.
	 */
	public void startDissemination(ClientID id) {
		disseminationOperations.put(id, null);
		disseminationEvent(id);
	}

	/**
	 * Stops the dissemination operation for the given client ID.
	 * 
	 * @param id
	 *            The ID of the client.
	 */
	public void stopDissemination(ClientID id) {
		if (disseminationOperations.containsKey(id)) {
			disseminationOperations.get(id).stop();
		}
		disseminationOperations.remove(id);
	}

	/**
	 * Adds a dissemination operation for a specific id to the scheduler. After
	 * successful, it reexecute the dissemination.
	 * 
	 * @param id
	 *            The id of a client.
	 */
	protected void disseminationEvent(ClientID id) {
		ServerDisseminationOperation op = new ServerDisseminationOperation(
				this, id, new OperationCallback<ClientID>() {

					@Override
					public void calledOperationFailed(
							Operation<ClientID> operation) {
						// do nothing, because it should be stopped!
					}

					@Override
					public void calledOperationSucceeded(
							Operation<ClientID> operation) {
						ClientID iD = operation.getResult();
						disseminationEvent(iD);
					}
				});
		if (disseminationOperations.containsKey(id)) {
			op.scheduleWithDelay(CSConfiguration.TIME_BETWEEN_DISSEMINATION_SERVER);
			disseminationOperations.put(id, op);
		}
	}

	/**
	 * Adds the maintenance operation to the scheduler. After successful
	 * execution, it reexecutes the operation.
	 */
	protected void maintenanceEvent() {
		ServerMaintenanceOperation op = new ServerMaintenanceOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(
							Operation<Object> operation) {
						// Do nothing
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Object> operation) {
						maintenanceEvent();
					}
				});
		op.scheduleWithDelay(CSConfiguration.TIME_BETWEEN_MAINTENANCE_SERVER);

	}

	@Override
	public NeighborDeterminator<S> getNeighbors() {
		return null;
	}
}
