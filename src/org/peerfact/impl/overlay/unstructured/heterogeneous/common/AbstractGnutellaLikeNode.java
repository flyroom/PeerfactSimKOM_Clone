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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.LocalClock;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTListener;
import org.peerfact.api.overlay.dht.DHTListenerSupported;
import org.peerfact.api.overlay.unstructured.HeterogeneousOverlayNode;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IQueryInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IResource;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.RankResource;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.SingleResourceQuery;
import org.peerfact.impl.service.dhtstorage.storage.SimpleDHTService;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * 
 * Abstract implementation of a Gnutella-like node.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractGnutellaLikeNode<TContact extends GnutellaLikeOverlayContact, TConfig extends IGnutellaConfig>
		implements ConnectivityListener,
		HeterogeneousOverlayNode<GnutellaOverlayID, TContact>,
		Comparable<AbstractGnutellaLikeNode<TContact, TConfig>>,
		DHTListenerSupported<GnutellaOverlayID, TContact, DHTKey<?>> {

	final static Logger log = SimLogger
			.getLogger(AbstractGnutellaLikeNode.class);

	/**
	 * The host this node is part of
	 */
	protected Host host;

	/**
	 * Simple DHTService provides a LinkedHashMap on the DHTListener Interface,
	 * allowing compatibility with older config files
	 */
	protected DHTListener<DHTKey<?>> dhtListener = new SimpleDHTService<DHTKey<?>>();

	/**
	 * The Overlay ID of this node
	 */
	protected GnutellaOverlayID id;

	TConfig config;

	/**
	 * The port where this node listens to incoming messages.
	 */
	protected short port;

	/**
	 * Bootstrap source this node uses.
	 */
	GnutellaBootstrap<TContact> bootstrap;

	public boolean bootstrapping = false;

	/**
	 * The sequence number can be used for arbitrary purposes, e.g. for
	 * assigning a number to messages. It is increased every time it is
	 * requested. This field should be only used by getNewSequenceNumber().
	 */
	private int sequenceNumberCounter = Simulator.getRandom().nextInt();

	/**
	 * The set of resources this node is currently sharing. On each change of
	 * the set, the method updateResources() should be called to trigger updates
	 * of replication mechanisms.
	 */
	private Set<IResource> resources = new LinkedHashSet<IResource>();

	/**
	 * If the node has joined the network
	 */
	private boolean joined = false;

	LocalClock localClock = new LocalClock() {
		@Override
		public long getCurrentLocalTime() {
			return Simulator.getCurrentTime(); // TODO: Should be biased for a
			// realistic simulation.
		}
	};

	/**
	 * Default constructor for this class.
	 * 
	 * @param host
	 *            : the host this node is part of
	 * @param id
	 *            : the overlay ID which was assigned to this node.
	 * @param config
	 *            : the configuration this node uses
	 * @param bootstrap
	 *            : the bootstrap source this node uses for connection.
	 * @param port
	 *            : the port where this node listens / shall listen to incoming
	 *            events.
	 */
	public AbstractGnutellaLikeNode(Host host, GnutellaOverlayID id,
			TConfig config, GnutellaBootstrap<TContact> bootstrap, short port) {
		this.host = host;
		this.id = id;
		this.config = config;
		this.port = port;
		this.bootstrap = bootstrap;
		host.getNetLayer().addConnectivityListener(this);
	}

	public GnutellaBootstrap<TContact> getBootstrap() {
		return bootstrap;
	}

	/**
	 * Just for global access to all entities for monitoring and debug purposes.
	 * No functional purpose.
	 */
	public static Set<AbstractGnutellaLikeNode<?, ?>> allInstances = new TreeSet<AbstractGnutellaLikeNode<?, ?>>();

	@Override
	public GnutellaOverlayID getOverlayID() {
		return id;
	}

	@Override
	public short getPort() {
		return port;
	}

	/**
	 * Returns the Gnutella configuration this node uses.
	 * 
	 * @return
	 */
	public TConfig getConfig() {
		return config;
	}

	/**
	 * Returns the own contact information of this node.
	 */
	@Override
	public abstract TContact getOwnContact();

	@Override
	public Host getHost() {
		return host;
	}

	/**
	 * Returns a sequence number that can be used e.g. in messages and
	 * operations.
	 * 
	 * @return
	 */
	public int getNewSequenceNumber() {
		int result = sequenceNumberCounter;
		sequenceNumberCounter++;
		return result;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (!joined) {
			return;
		}
		if (ce.isOffline()) {
			handleOfflineStatus();
			bootstrap.removeNode(getOwnContact());
		} else {
			handleOnlineStatus();
			this.new BootstrapOperation(Operations.getEmptyCallback())
					.scheduleImmediately();
		}
	}

	/**
	 * Called when the peer has gone offline
	 */
	protected abstract void handleOfflineStatus();

	/**
	 * Called when the peer has gone online
	 */
	protected abstract void handleOnlineStatus();

	@Override
	public boolean isPresent() {
		return this.getHost().getNetLayer().isOnline();
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		joined = true;
		allInstances.add(this);
		BootstrapOperation op = this.new BootstrapOperation(callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		// Not implemented yet.
		return -1;
	}

	@Override
	public void publishRanks(String docs,
			OperationCallback<Set<TContact>> callback) {

		String[] docIDStr = docs.split(",");
		Set<IResource> docIDs = new LinkedHashSet<IResource>();

		for (int i = 0; i < docIDStr.length; i++) {
			try {
				docIDs.add(new RankResource(Integer.parseInt(docIDStr[i])));
			} catch (NumberFormatException e) {
				log.warn(docIDStr[i]
						+ " is not a valid integer identifier. Skipping this document identifier.");
			}

		}

		if (docIDs.size() > 0) {
			publishSet(docIDs, callback);
		} else {
			log.warn("No valid identifiers were given in the publish action. Skipping this request.");
		}

	}

	@Override
	public void publishSet(Set<IResource> res,
			OperationCallback<Set<TContact>> callback) {
		new PublishOperation(res, callback).scheduleImmediately();
	}

	@Override
	public void queryRank(
			int rank,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		query(new SingleResourceQuery(new RankResource(rank)), hitsWanted,
				callback);
	}

	@Override
	public void query(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback) {
		startQuery(info, hitsWanted, callback);
	}

	/**
	 * Starts a query, given query information
	 * 
	 * @param info
	 */
	protected abstract void startQuery(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback);

	@Override
	public Set<IResource> getResources() {
		Set<IResource> ret = new LinkedHashSet<IResource>();
		for (DHTEntry<DHTKey<?>> entry : dhtListener.getDHTEntries()) {
			ret.add((IResource) entry.getValue());
		}
		return ret;
	}

	/**
	 * Initiates the connection to the overlay network by connecting to the
	 * given node.
	 * 
	 * @param contact
	 */
	protected abstract void initConnection(TContact contact);

	/**
	 * Updates all neighbors that need an update if e.g. the set of resources
	 * has changed.
	 */
	protected abstract void updateResources();

	/**
	 * If the node is connected to the overlay.
	 * 
	 * @return
	 */
	protected abstract boolean hasConnection();

	/**
	 * Publish operation for all nodes. Initiated after a publish to inform
	 * callback.
	 */
	public class PublishOperation
			extends
			AbstractOperation<AbstractGnutellaLikeNode<TContact, TConfig>, Set<TContact>> {

		private Set<IResource> res;

		protected PublishOperation(Set<IResource> res,
				OperationCallback<Set<TContact>> callback) {
			super(AbstractGnutellaLikeNode.this, callback);
			this.res = res;
		}

		@Override
		protected void execute() {
			resources.addAll(res);
			for (IResource r : res) {
				dhtListener.addDHTEntry(r.getKey(), r.getValue());
			}
			updateResources();
			this.operationFinished(true);
		}

		@Override
		public Set<TContact> getResult() {
			return Collections.singleton(getOwnContact());
		}
	}

	/**
	 * Bootstrap operation for all nodes. Initiated after a bootstrap and ended
	 * if the node has connected successfully.
	 * 
	 * @author
	 * 
	 */
	public class BootstrapOperation
			extends
			AbstractOperation<AbstractGnutellaLikeNode<TContact, TConfig>, Object> {

		public BootstrapOperation(OperationCallback<Object> callback) {
			super(AbstractGnutellaLikeNode.this, callback);
		}

		@Override
		protected void execute() {

			if (!hasConnection()) {
				// if (isBootstrapping()) {
				// AbstractGnutellaLikeNode.dumpStateOfAll();
				// log.debug("At " + getOwnContact());
				// log.debug("Redoing bootstrap");
				// }

				bootstrapping = true;
				doBootstrap();
			} else {
				bootstrapping = false;
				if (this.getComponent().canBeUsedForBootstrapping()) {
					bootstrap.addNode(getOwnContact());
				}
			}
		}

		/**
		 * Default bootstrap procedure
		 */
		private void doBootstrap() {
			TContact nodeForBootstrap = bootstrap.getANodeToConnectTo();
			if (nodeForBootstrap != null) {
				this.getComponent().initConnection(nodeForBootstrap);
				setWaitForConnection();
			} else if (this.getComponent().canBeUsedForBootstrapping()) {
				bootstrap.addNode(getOwnContact());
				bootstrapping = false;
				// Node is the only active bootstrapping-capable in the network.
			} else {
				setWaitForConnection();
				// Leaf must wait for other bootstrapping-capable peers to be
				// present in the network.
			}
		}

		/**
		 * Instructs the node to wait a little time until the bootstrap is
		 * rechecked.
		 */
		private void setWaitForConnection() {
			this.scheduleWithDelay(config.getBootstrapIntvl());
		}

		@Override
		public Object getResult() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public boolean isBootstrapping() {
		return bootstrapping;
	}

	protected abstract boolean canBeUsedForBootstrapping();

	/**
	 * Returns if the node has such a low connectivity that it has to be
	 * improved (configuration-dependent).
	 * 
	 * @return
	 */
	public abstract boolean hasLowConnectivity();

	/**
	 * For e.g. debugging purposes. In a list fo nodes, these ones are sorted by
	 * their id, but all ultrapeers are greater than the leaves.
	 */
	@Override
	public int compareTo(AbstractGnutellaLikeNode<TContact, TConfig> node) {
		return this.getOverlayID().compareTo(node.getOverlayID());
	}

	public LocalClock getLocalClock() {
		return localClock;
	}

	public static void dumpStateOfAll() {
		log.debug("===All instances");
		for (HeterogeneousOverlayNode<?, ?> node : AbstractGnutellaLikeNode.allInstances) {
			log.debug(node);
		}
		log.debug("===");
	}

	public abstract Collection<TContact> getConnectedContacts();

	@Override
	public NeighborDeterminator<TContact> getNeighbors() {
		return new NeighborDeterminator<TContact>() {

			@Override
			public Collection<TContact> getNeighbors() {
				Collection<TContact> coll = getConnectedContacts();
				return Collections.unmodifiableCollection(coll);
			}
		};
	}

	/**
	 * For unified access of DHT-related Services
	 */
	@Override
	public void registerDHTListener(DHTListener<DHTKey<?>> listener) {
		this.dhtListener = listener;
	}

}
