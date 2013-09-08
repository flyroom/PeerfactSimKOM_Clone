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

package org.peerfact.impl.overlay.dht.kademlia.hkademlia.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractNodeFactory;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
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
 * Abstract hierarchy-aware node factory. Nodes are, in addition to
 * AbstractNodeFactory, saved in clusters and each node obtains its initial
 * routing table contents from its own cluster only. Thus, the cluster
 * assignment should be chosen such that each cluster contains at least as much
 * contacts as the number of initial routing table contacts.
 * <p>
 * The cluster assignment is currently done via the Host's (group) identifier. A
 * mapping from (group) identifier to cluster suffix is read from a
 * configuration file. If that fails or the (group) identifier is unknown, new
 * Nodes are created in cluster 0.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class HAbstractNodeFactory extends AbstractNodeFactory {

	private final static Logger log = SimLogger
			.getLogger(HAbstractNodeFactory.class);

	/**
	 * A Map with the cluster suffixes as keys and Lists of the Nodes in that
	 * cluster as values.
	 */
	// private final Map<BigInteger, List<Node<HKademliaOverlayID>>>
	// clusterLists;
	/**
	 * An estimate of the number of nodes per cluster.
	 */
	// private final int expectedNodesPerCluster;
	/**
	 * Permits to retrieve the (Host) group identifier - cluster suffix mapping.
	 * This reference might be null if reading the file failed!
	 */
	private final ResourceBundle clusterMapping;

	/**
	 * Constructs a new HAbstractNodeFactory, gets configuration values from
	 * KademliaSetup.
	 */
	public HAbstractNodeFactory() {
		// final int numOfClusters = (int) Math.ceil(Math.pow(2, config
		// .getHierarchyDepth()
		// * config.getHierarchyTreeOrder()));
		// clusterLists = new LinkedHashMap<BigInteger,
		// List<Node<HKademliaOverlayID>>>(
		// numOfClusters, 1.0f);
		// expectedNodesPerCluster = (int) (1.35 * config.getNumberOfPeers() /
		// numOfClusters);
		clusterMapping = getProperties(config.getClusterMappingFilePath());
	}

	private static ResourceBundle getProperties(final String filePath) {
		ResourceBundle props = null;
		try {
			final File propertiesFile = new File(filePath);
			final InputStream in = new FileInputStream(propertiesFile);
			props = new PropertyResourceBundle(in);
			in.close();
			log.info("Using cluster mapping from '" + filePath + "'.");
		} catch (final FileNotFoundException ex) {
			log.error("Could not find the given cluster mapping file '"
					+ filePath + "' - using default suffix 0 for all nodes. "
					+ "(This will cause further exceptions.)", ex);
		} catch (final IOException ex) {
			log.error("Could not read from given cluster mapping file '"
					+ filePath + "' - using default suffix 0 for all nodes. "
					+ "(This will cause further exceptions.)", ex);
		} catch (final Exception ex) {
			log.error(
					"An exception occurred while reading the cluster mapping '"
							+ filePath
							+ "' - using default suffix 0 for all nodes. "
							+ "(This will cause further exceptions.)", ex);
		}
		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Template method for construction of Nodes in clusters: add to cluster
	 * membership list, construct Node using abstract method
	 * buildHierarchicalNode.
	 */
	@Override
	protected final Node<HKademliaOverlayID> buildNode(
			final HKademliaOverlayID id, final short port, final Host host) {
		final TransLayer msgMgr = host.getTransLayer();
		final Node<HKademliaOverlayID> newNode = buildHierarchicalNode(id,
				port, msgMgr);
		// addNodeToCluster(newNode);

		return newNode;
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Get ID with cluster suffix.
	 */
	@Override
	protected final HKademliaOverlayID getRandomHKademliaOverlayID(
			final Host host) {
		final HKademliaOverlayID baseID = super
				.getRandomHKademliaOverlayID(host);
		final String groupID = host.getProperties().getGroupID();
		final HKademliaOverlayID clusteredID = baseID
				.setCluster(getClusterSuffix(groupID));
		return clusteredID;
	}

	/**
	 * Constructs a new Node with the given initialisation data.
	 * 
	 * @param id
	 *            the HKademliaOverlayID of the new Node (the cluster suffix has
	 *            already been set).
	 * @param port
	 *            the port on which the new Node will listen for incoming
	 *            messages.
	 * @param msgMgr
	 *            the TransLayer that the new Node will use for network
	 *            connectivity.
	 * @return a new Node with the given HKademliaOverlayID, port, and transport
	 *         layer.
	 */
	protected abstract Node<HKademliaOverlayID> buildHierarchicalNode(
			HKademliaOverlayID id, short port, TransLayer msgMgr);

	/**
	 * Determines the cluster that a node belongs to via its group identifier.
	 * 
	 * @param groupID
	 *            a String that contains the group identifier.
	 * @return a BigInteger to be used as a cluster suffix when creating a
	 *         HKademliaOverlayID.
	 */
	public final BigInteger getClusterSuffix(final String groupID) {
		try {
			return new BigInteger(clusterMapping.getString(groupID), 2);
		} catch (final Exception ex) {
			log.error("An exception occurred while reading the cluster "
					+ "mapping for group '" + groupID
					+ "' - using the default cluster suffix 0.", ex);
			return BigInteger.ZERO;
		}
	}

	/**
	 * Adds the given node to its cluster membership list, according to the
	 * cluster suffix derived from its HKademliaOverlayID. In order to avoid
	 * duplicate entries, this method should be called only once for each
	 * constructed Node.
	 * 
	 * @param newNode
	 *            the new Node that is to be added to its cluster.
	 */
	// protected final void addNodeToCluster(final Node<HKademliaOverlayID>
	// newNode) {
	// final BigInteger itsClusterID = newNode.getOverlayID().getCluster();
	// List<Node<HKademliaOverlayID>> itsClusterList = clusterLists
	// .get(itsClusterID);
	// if (itsClusterList == null) {
	// itsClusterList = new ArrayList<Node<HKademliaOverlayID>>(
	// expectedNodesPerCluster);
	// clusterLists.put(itsClusterID, itsClusterList);
	// }
	// itsClusterList.add(newNode);
	// }
	/**
	 * {@inheritDoc}
	 */
	/*
	 * This implementation returns random contacts only from the cluster of the
	 * node that requests them.
	 */
	// @Override
	// protected final Collection<KademliaOverlayContact<HKademliaOverlayID>>
	// getRandomInitialRoutingTableContents(
	// final Node<HKademliaOverlayID> target) {
	// return
	// getRandomInitialRoutingTableContentsFromList(getClusterList(target));
	// }
	/**
	 * Returns a List containing all nodes from the cluster of Node
	 * <code>fromCluster</code>.
	 * 
	 * @param fromCluster
	 *            a Node from the wanted cluster.
	 * @return a List containing all Nodes from that cluster, or an empty list
	 *         if that cluster is not known.
	 */
	// private List<Node<HKademliaOverlayID>> getClusterList(
	// final Node<HKademliaOverlayID> fromCluster) {
	// final BigInteger itsClusterID = fromCluster.getOverlayID().getCluster();
	// final List<Node<HKademliaOverlayID>> itsClusterList = clusterLists
	// .get(itsClusterID);
	// if (itsClusterList == null) {
	// return Collections.emptyList();
	// }
	// return itsClusterList;
	// }
}
