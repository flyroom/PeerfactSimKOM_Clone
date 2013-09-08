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

package org.peerfact.impl.overlay.unstructured.heterogeneous.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.messages.GnutellaPong;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations.ConnectCloseOperation;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations.ConnectOperation;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations.PeriodicPingOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.CollectionHelpers;
import org.peerfact.impl.util.toolkits.Predicate;


/**
 * 
 * General component that manages connections to other nodes
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class ConnectionManager<TConnectionMetadata, TContact extends GnutellaLikeOverlayContact, TConfig extends IGnutellaConfig, TPong extends GnutellaPong<TContact>> {

	public enum ConnBreakCause {
		Cancel, Timeout;
	}

	protected AbstractGnutellaLikeNode<TContact, TConfig> owner;

	TConfig config;

	protected int size;

	public int connectSemaphore = 3; // 3 max simultaneous connects possible;

	public Queue<Connection> waitingConns = new LinkedBlockingQueue<Connection>();

	IPongHandler<TContact, TPong> pongHdlr;

	public List<ConnectionManagerListener<TContact, TConnectionMetadata>> listeners = new ArrayList<ConnectionManagerListener<TContact, TConnectionMetadata>>();

	public Map<TContact, Connection> contacts = new LinkedHashMap<TContact, Connection>();

	/**
	 * Default constructor
	 * 
	 * @param owner
	 *            : the owner node of this connection manager
	 * @param config
	 *            : the configuration the connection manager is supposed to use
	 * @param size
	 *            : the maximum connection count the manager may hold
	 * @param pongHdlr
	 *            : the IPongHandler that handles pong information received by
	 *            this connection manager
	 * @param foreignAttemptKickProb
	 *            : the probability that a node is kicked by a foreign
	 *            connection attempt
	 */
	public ConnectionManager(AbstractGnutellaLikeNode<TContact, TConfig> owner,
			TConfig config, int size) {
		this.owner = owner;
		this.config = config;
		this.size = size;
	}

	/**
	 * Sets a pong handler that is used for the construction and evaulation of
	 * ping/pong messages.
	 */
	public void setPongHandler(IPongHandler<TContact, TPong> pongHdlr) {
		this.pongHdlr = pongHdlr;
	}

	@Override
	public String toString() {
		return "ConMgr:" + contacts.size() + "| succ: " +
				getNumberOfContactsInState(ConnectionState.Connected)
				+ "| tot:" +
				contacts.size() + " | " + contacts.values() + " sem: " +
				connectSemaphore + ", waiting=" + waitingConns.size();
		// return "ConMgr:" + contacts.size() + "| succ: "
		// + numberOfContactsInState(ConnectionState.Connected) + "| tot:"
		// + contacts.size() + " | sem: " + connectSemaphore;

	}

	/**
	 * Notifies the connection manager that the given peers have been
	 * discovered.
	 * 
	 * @param newContacts
	 */
	public void seenContacts(Collection<TContact> newContacts) {
		for (TContact c : newContacts) {
			if (contacts.size() >= size) {
				break;
			}
			seenContact(c);
		}
	}

	/**
	 * Puts connection metadata to a given peer. Returns true if this manager
	 * holds a connection to the given peer.
	 * 
	 * @param c
	 * @param data
	 * @return
	 */
	public boolean putMetadata(TContact c,
			TConnectionMetadata data) {
		Connection conn = contacts.get(c);
		if (conn != null) {
			conn.putMetadata(data);
			return true;
		}
		return false;
	}

	/**
	 * Returns the metadata for a given contact. Returns null if there is no
	 * connection to the given contact or no contact data was ever stored.
	 * 
	 * @param c
	 * @return
	 */
	public TConnectionMetadata getMetadata(TContact c) {
		Connection conn = contacts.get(c);
		if (conn != null) {
			return conn.getMetadata();
		}
		return null;
	}

	protected TConfig getConfig() {
		return config;
	}

	/**
	 * Closes the connecton to the peer c. forContact can be sent along with the
	 * disconnection attempt to suggest the kicked peer to connect to
	 * forContact, too.
	 * 
	 * Returns false if no connection to the peer is held or in progress.
	 * 
	 * @param c
	 * @param forContact
	 */
	public boolean closeConnection(TContact c,
			TContact forContact) {
		Connection conn = contacts.get(c);

		if (conn != null) {
			conn.close();
			new ConnectCloseOperation<TContact>(owner, c, forContact,
					Operations.getEmptyCallback()).scheduleImmediately();
			return true;
		}
		return false;
	}

	/**
	 * Tells the connection manager that an other peer c has triggered a close
	 * attempt.
	 * 
	 * @param c
	 */
	public void foreignCloseAttempt(TContact c) {
		Connection conn = contacts.get(c);
		if (conn != null) {
			conn.close();
		}
	}

	/**
	 * Tells the connection manager that a peer has been discovered.
	 * 
	 * @param c
	 */
	public abstract void seenContact(TContact c);

	/**
	 * Tells the node that a peer did not respond and is found dead.
	 * 
	 * Returns true if the node was finally kicked out or there is no connection
	 * to the node.
	 * 
	 * @param contact
	 * @return
	 */
	public boolean foundDeadContact(TContact contact) {
		Connection c = contacts.get(contact);
		if (c != null) {
			return c.markAsDead();
		} else {
			return true;
		}
	}

	/**
	 * Returns all contacts the peer has connected to.
	 * 
	 * @return
	 */
	public Set<TContact> getConnectedContacts() {

		Set<TContact> result = new LinkedHashSet<TContact>(
				contacts.size());
		for (Connection c : contacts.values()) {
			if (c.getState() == ConnectionState.Connected) {
				result.add(c.getContact());
			}
		}

		return Collections.unmodifiableSet(result);
	}

	/**
	 * Connection to another peer.
	 * 
	 * @author
	 * 
	 */
	public class Connection implements
			IManageableConnection<TContact, TConnectionMetadata> {

		private TConnectionMetadata metadata;

		long timeEstablished = Simulator.getCurrentTime();

		public Connection(TContact c, boolean foreignAttempt) {
			this.contact = c;
			contacts.put(c, this);
			if (foreignAttempt) {
				state = ConnectionState.Pending;
			} else {
				if (connectSemaphore > 0) {
					startConnectionProcedure();
				} else {
					waitingConns.add(this);
				}

			}
		}

		/**
		 * Puts metadata to this connection
		 * 
		 * @param data
		 */
		public void putMetadata(TConnectionMetadata data) {
			this.metadata = data;
		}

		/**
		 * Gets the metadata for this connection.
		 * 
		 * @return
		 */
		@Override
		public TConnectionMetadata getMetadata() {
			return this.metadata;
		}

		/**
		 * Initiates a connection procedure.
		 */
		public void startConnectionProcedure() {
			createNewConnectOperation(contact, this,
					Operations.getEmptyCallback()).scheduleImmediately();
			connectSemaphore--;
		}

		/**
		 * Called if an arbitrary component has found the connection as dead.
		 * Returns true if the connection was finally closed
		 */
		@Override
		public boolean markAsDead() {
			if (state != ConnectionState.Connected)
			{
				return false; // If connection establishment is in progress, do
			}
			// not harm them.
			deadCounter++;
			if (deadCounter >= config.getStaleConnAttempts()) {
				handleConnectionTimeout();
				ping.stop();
				return true;
			}
			return false;
		}

		/**
		 * Closes this connection.
		 */
		public void close() {
			// log.debug(state);
			if (ping != null) {
				ping.stop();
			}
			waitingConns.remove(this);
			state = ConnectionState.Closed;
			contacts.remove(contact);
			if (state == ConnectionState.Connected) {
				connectionEnded(contact, metadata);
			}
			lostAConnection(this, ConnBreakCause.Cancel);
		}

		/**
		 * Called if an arbitrary component has found the connection as alive.
		 */
		@Override
		public void markAsAlive() {
			deadCounter = 0;
		}

		@Override
		public String toString() {
			return "("
					+ contact
					+ ", "
					+ state
					+ ", "
					+ deadCounter
					+ ", "
					// + metadata + ")";
					+ ((getState() != ConnectionState.Connected) ? displayTime()
							+ ")"
							: ")");
		}

		String displayTime() {
			return "t=" + (Simulator.getCurrentTime() - timeEstablished) / 1000
					+ "ms";
		}

		/**
		 * Starts the periodic ping procedure for this connection.
		 */
		protected void startPing() {
			ping = new PeriodicPingOperation<TContact, TPong>(owner, contact,
					pongHdlr, this,
					Operations.getEmptyCallback());
			ping.scheduleImmediately();
		}

		@Override
		public void connectionSucceeded() {
			if (state == ConnectionState.Pending) {
				// Foreign Connection Attempt
				startPing();
				state = ConnectionState.Connected;
				newConnectionEstablished(contact, metadata);
				return;
			}
			state = ConnectionState.Connected;
			connectSemaphore++;
			startPing();
			continueQueue();
			newConnectionEstablished(contact, metadata);
		}

		@Override
		public void connectionTimeouted() {
			if (state != ConnectionState.Pending) {
				connectSemaphore++;
			}
			state = ConnectionState.Timeouted;
			contacts.remove(contact);
			lostAConnection(this, ConnBreakCause.Timeout);
			continueQueue();
		}

		/**
		 * Called if the connection was interrupted caused by a timeout.
		 */
		void handleConnectionTimeout() {
			// state = ConnectionState.Timeouted;
			contacts.remove(contact);
			connectionEnded(contact, metadata);
			lostAConnection(this, ConnBreakCause.Timeout);
		}

		@Override
		public void connectionFailed() {
			if (state == ConnectionState.Pending) {
				// Foreign Connection Attempt
				state = ConnectionState.Failed;
				contacts.remove(contact);
				return;
			}
			state = ConnectionState.Failed;
			connectSemaphore++;
			contacts.remove(contact);
			continueQueue();
			lostAConnection(this, ConnBreakCause.Cancel);
		}

		/**
		 * Returns the state of this connection.
		 * 
		 * @return
		 */
		ConnectionState getState() {
			return state;
		}

		/**
		 * Returns the contact to which this connection is established.
		 * 
		 * @return
		 */
		@Override
		public TContact getContact() {
			return contact;
		}

		/**
		 * Returns the failed attempts to contact the peer this connection is
		 * made to. Is reset on every successful attempt.
		 * 
		 * @return
		 */
		int getDeadCounter() {
			return deadCounter;
		}

		ConnectionState state = ConnectionState.Connecting;

		TContact contact;

		PeriodicPingOperation<?, ?> ping;

		int deadCounter = 0;

		/**
		 * Pops the next connection from the queue that waits for establishment.
		 */
		public void continueQueue() {
			try {
				Connection queuedConn = waitingConns.remove();
				if (connectSemaphore > 0) {
					queuedConn.startConnectionProcedure();
				}
			} catch (NoSuchElementException e) {
				// Fine, no element in connection queue.
			}
		}

	}

	public enum ConnectionState {
		Connecting, Pending, Failed, Timeouted, Connected, Closed;
	}

	/**
	 * Returns the number of connections that are in the given state.
	 * 
	 * @param state
	 * @return
	 */
	public int getNumberOfContactsInState(ConnectionState state) {
		int i = 0;
		for (Connection c : contacts.values()) {
			if (c.getState() == state) {
				i++;
			}
		}
		return i;
	}

	/**
	 * Returns maxSize connected peers randomly, or less if there are less than
	 * maxSize.
	 * 
	 * @param maxSize
	 * @return
	 */
	public List<TContact> getSomeConnectedPeers(int maxSize) {
		return CollectionHelpers.getRandomPartFrom(getConnectedContacts(),
				maxSize);
	}

	/**
	 * Returns maxSize known peers randomly, or less if there are less than
	 * maxSize.
	 * 
	 * @param maxSize
	 * @return
	 */
	public List<TContact> getSomeArbitraryPeers(int maxSize) {
		return CollectionHelpers.getRandomPartFrom(contacts.keySet(), maxSize);
	}

	/**
	 * Returns a random contact which is connected.
	 * 
	 * @return
	 */
	public TContact getRandomContact() {
		return CollectionHelpers.getRandomEntry(getConnectedContacts());
	}

	/**
	 * If peer c is connected.
	 * 
	 * @param c
	 * @return
	 */
	public boolean peerIsConnected(TContact c) {
		for (Connection conn : contacts.values()) {
			if (conn.getState() == ConnectionState.Connected
					&& conn.getContact().equals(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the total number of contacts used by this manager. Includes the
	 * number of peers where a connection attempt is currently in progress, not
	 * only the established connections.
	 * 
	 * @return
	 */
	public int getNumberOfContacts() {
		return contacts.size();
	}

	/**
	 * Adds a listener that listens to events of this connection manager.
	 * 
	 * @param l
	 */
	public void addListener(
			ConnectionManagerListener<TContact, TConnectionMetadata> l) {
		listeners.add(l);
	}

	/**
	 * Removes a listener
	 * 
	 * @param l
	 */
	public void removeListener(
			ConnectionManagerListener<TContact, TConnectionMetadata> l) {
		listeners.remove(l);
	}

	/**
	 * Called when a connection has been lost.
	 */
	protected void lostAConnection(
			IConnection<TContact, TConnectionMetadata> conn,
			ConnBreakCause cause) {
		if (cause.equals(ConnBreakCause.Cancel)) {
			Simulator.getMonitor().unstructuredConnectionBreakCancel(
					owner.getOwnContact(),
					conn.getContact());
		} else {
			Simulator.getMonitor().unstructuredConnectionBreakTimeout(
					owner.getOwnContact(),
					conn.getContact());
		}
		if (contacts.size() <= 0) {
			lostConnectivity();
		}
	}

	/**
	 * Called whene a connection to peer c has been established.
	 * 
	 * @param c
	 */
	void newConnectionEstablished(TContact c, TConnectionMetadata metadata) {
		for (ConnectionManagerListener<TContact, TConnectionMetadata> l : listeners) {
			l.newConnectionEstablished(c, metadata);
		}
	}

	/**
	 * Called when a connection to another peer ended.
	 * 
	 * @param c
	 * @param metadata
	 */
	void connectionEnded(TContact c, TConnectionMetadata metadata) {
		for (ConnectionManagerListener<TContact, TConnectionMetadata> l : listeners) {
			l.connectionEnded(c, metadata);
		}
	}

	/**
	 * Called when the whole connectivity has been lost.
	 */
	void lostConnectivity() {
		for (ConnectionManagerListener<TContact, TConnectionMetadata> l : listeners) {
			l.lostConnectivity();
		}
	}

	public interface ConnectionManagerListener<TContact extends GnutellaLikeOverlayContact, ConnectionMetadata> {

		public void newConnectionEstablished(TContact c,
				ConnectionMetadata metadata);

		public void connectionEnded(TContact c, ConnectionMetadata metadata);

		public void lostConnectivity();

	}

	protected Operation<Object> createNewConnectOperation(
			TContact contact,
			IManageableConnection<TContact, TConnectionMetadata> connection,
			OperationCallback<Object> callback) {
		return new ConnectOperation<TContact, TConfig>(owner, contact, this,
				connection, callback);
	}

	/**
	 * 
	 * Removes all connections. Does not send bye messages in order to notify
	 * the connected peers.
	 * 
	 */
	public void flushRaw() {
		for (Connection c : new ArrayList<Connection>(contacts.values())) {
			c.close();
		}
	}

	public Collection<IConnection<TContact, TConnectionMetadata>> getAllConnections() {
		ArrayList<IConnection<TContact, TConnectionMetadata>> result = new ArrayList<IConnection<TContact, TConnectionMetadata>>(
				contacts.size());
		result.addAll(contacts.values());
		return result;
	}

	public Collection<IConnection<TContact, TConnectionMetadata>> getEstablishedConnections() {
		Set<IConnection<TContact, TConnectionMetadata>> result = new LinkedHashSet<IConnection<TContact, TConnectionMetadata>>();

		CollectionHelpers.filter(contacts.values(), result,
				new Predicate<Connection>() {
					@Override
					public boolean isTrue(Connection object) {
						return object.getState() == ConnectionState.Connected;
					}
				});

		return result;
	}

}
