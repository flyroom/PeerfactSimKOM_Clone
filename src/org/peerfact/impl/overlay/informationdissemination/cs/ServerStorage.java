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

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.informationdissemination.cs.exceptions.FullServerException;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


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
 * The storage of the server. It stores the incoming information of the clients.
 * Additionally it provides methods to derive the neighbors to a stored ID.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class ServerStorage {
	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(ServerStorage.class);

	/**
	 * Stores to every Client ID a server Storage, that contains all needed
	 * Information to a client.
	 */
	private LinkedHashMap<ClientID, ServerStorageContainer> storage;

	private int maxClients;

	/**
	 * The constructor of the Server. It sets the maximal Client Size.
	 * 
	 * @param maxClients
	 *            The maximal number of Clients, which can the server handle.
	 *            This value should not be 0 or negative!
	 */
	public ServerStorage(int maxClients) {
		if (maxClients <= 0) {
			throw new IllegalArgumentException(
					"The maximal Clients are 0 or negative");
		}
		storage = new LinkedHashMap<ClientID, ServerStorageContainer>();
		this.maxClients = maxClients;
	}

	/**
	 * Add the client to the storage. If the Server is full, then throw an
	 * exception.
	 * 
	 * @param id
	 *            The id of the client.
	 * @param contact
	 *            The contact information of the client.
	 * @param nodeInfo
	 *            The client node information of the client.
	 * @throws FullServerException
	 *             If the server has reached the maximum size, then throw this
	 *             exception
	 */
	public void addClient(ClientID id, ClientContact contact,
			ClientNodeInfo nodeInfo) throws FullServerException {
		if (!storage.containsKey(id)) {
			if (storage.size() <= maxClients) {
				ServerStorageContainer container = new ServerStorageContainer(
						id, contact, nodeInfo);
				storage.put(id, container);
			} else {
				throw new FullServerException("Server is Full" + storage.size()
						+ "/" + maxClients);
			}
		} else {
			log.warn("The clientID " + id + " already exists.");
		}
	}

	/**
	 * Removes the client from the storage.
	 * 
	 * @param id
	 *            The id of the client.
	 */
	public void removeClient(ClientID id) {
		if (storage.containsKey(id)) {
			storage.remove(id);
		} else {
			log.warn("Node with clientID " + id
					+ " cannot removed. It is not contained in storage.");
		}
	}

	/**
	 * Updates the {@link ClientNodeInfo} for a client.
	 * 
	 * @param id
	 *            The id of the client.
	 * @param nodeInfo
	 *            The {@link ClientNodeInfo} of a client.
	 */
	public void updateClientInfo(ClientID id, ClientNodeInfo nodeInfo) {
		if (storage.containsKey(id)) {
			storage.get(id).updateClientNodeInfo(nodeInfo);
		} else {
			log.info("Node with clientID " + id
					+ " not exists. It cannot be updated!");
		}
	}

	/**
	 * Find the neighbors to a given ID. If the ID not in storage, it returns
	 * null. The area of radius is taken from the {@link ClientNodeInfo}.
	 * 
	 * @param id
	 *            The id of the client.
	 * @return The {@link ClientNodeInfo}s that are the neighbors. If the ID not
	 *         in storage, it returns null.
	 */
	public List<ClientNodeInfo> findNeighbors(ClientID id) {
		if (storage.containsKey(id)) {
			ClientNodeInfo nodeInfoCenter = storage.get(id).getClientNodeInfo();
			Point center = nodeInfoCenter.getPosition();
			int aoi = nodeInfoCenter.getAoiRadius();
			if (center != null) {
				return findNeighbors(center, aoi, id);
			} else {
				log.warn("The nodeInfo from node " + id + " is wrong ("
						+ nodeInfoCenter);
			}
		}
		return null;
	}

	/**
	 * Find the neighbors to a given ID and area of interest radius. If the ID
	 * not in storage, it returns null.
	 * 
	 * @param id
	 *            The id of the client.
	 * @param aoi
	 *            The radius for the area of interest.
	 * @return The {@link ClientNodeInfo}s that are in the area of interest. If
	 *         the ID not in storage, it returns null.
	 */
	public List<ClientNodeInfo> findNeighbors(ClientID id, int aoi) {
		if (storage.containsKey(id)) {
			ClientNodeInfo nodeInfoCenter = storage.get(id).getClientNodeInfo();
			Point center = nodeInfoCenter.getPosition();
			if (center != null) {
				return findNeighbors(center, aoi, id);
			} else {
				log.warn("The nodeInfo from node " + id + " is wrong ("
						+ nodeInfoCenter);
			}
		}
		return null;
	}

	private List<ClientNodeInfo> findNeighbors(Point center, int aoi,
			ClientID ignore) {
		List<ClientNodeInfo> result = new Vector<ClientNodeInfo>();
		for (ClientID id : storage.keySet()) {
			ClientNodeInfo nodeInfo = storage.get(id).getClientNodeInfo();
			if (!id.equals(ignore)) {
				if (nodeInfo.getPosition() != null) {
					if (inAOI(center, nodeInfo.getPosition(), aoi)) {
						result.add(nodeInfo);
					}
				} else {
					log.warn("The nodeInfo from node " + id + " is wrong ("
							+ nodeInfo);
				}
			}
		}
		return result;
	}

	private static boolean inAOI(Point center, Point point, int aoi) {
		if (center == null || point == null)
		{
			log.debug("lui");
			return false;
		}
		return (center.distance(point) <= aoi);
	}

	/**
	 * The actually number of stored client informations.
	 * 
	 * @return The size of the storage.
	 */
	public int getClientSize() {
		return storage.size();
	}

	/**
	 * Gets the {@link ClientContact} for the given client.
	 * 
	 * @param id
	 *            The id of the client.
	 * @return The {@link ClientContact} for the given id.
	 */
	public ClientContact getClientContact(ClientID id) {
		if (storage.containsKey(id)) {
			return storage.get(id).getContact();
		}
		return null;
	}

	/**
	 * Gets the last Update time of a client.
	 * 
	 * @param id
	 *            The id of the client.
	 * @return The last update time of a client
	 */
	public long getLastUpdate(ClientID id) {
		if (storage.containsKey(id)) {
			return storage.get(id).getLastUpdate();
		}
		return 0;
	}

	/**
	 * Returns all stored client IDs
	 * 
	 * @return A list of client IDs
	 */
	public List<ClientID> getAllStoredClientIDs() {
		List<ClientID> result = new Vector<ClientID>(storage.keySet());
		return result;
	}

	/**
	 * Container for information of a client. It stores the id, contact
	 * information, clientNodeInfo and the last received Update of a client.
	 * 
	 * @author Christoph Muenker
	 * @version 01/06/2011
	 * 
	 */
	private static class ServerStorageContainer {

		/**
		 * The contact information of a client
		 */
		private ClientContact contact;

		/**
		 * Time for the last update of the clientNodeInfo
		 */
		private long lastUpdate;

		/**
		 * The received clientNodeInfo of a client.
		 */
		private ClientNodeInfo clientNodeInfo;

		/**
		 * Create a container for the Information of a client. Additionally it
		 * sets the lastUpdate to the currently simulation time.
		 * 
		 * @param id
		 *            The {@link ClientID} of the client.
		 * @param contact
		 *            The {@link ClientContact} information of the client.
		 * @param clientNodeInfo
		 *            The {@link ClientNodeInfo} of a client.
		 */
		public ServerStorageContainer(ClientID id, ClientContact contact,
				ClientNodeInfo clientNodeInfo) {
			super();

			this.contact = contact;
			this.clientNodeInfo = clientNodeInfo;
			this.lastUpdate = Simulator.getCurrentTime();
		}

		/**
		 * Update the {@link ClientNodeInfo} of a client. Additionally, it
		 * update the lastUpdate to the currently simulation time.
		 * 
		 * @param nodeInfo
		 */
		public void updateClientNodeInfo(ClientNodeInfo nodeInfo) {
			this.clientNodeInfo = nodeInfo;
			this.lastUpdate = Simulator.getCurrentTime();
		}

		public ClientContact getContact() {
			return contact;
		}

		public ClientNodeInfo getClientNodeInfo() {
			return clientNodeInfo;
		}

		public long getLastUpdate() {
			return lastUpdate;
		}

	}

}
