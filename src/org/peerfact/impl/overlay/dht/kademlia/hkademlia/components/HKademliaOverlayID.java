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

import java.math.BigInteger;

import org.peerfact.impl.overlay.dht.kademlia.base.TypesConfig;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.util.toolkits.BigIntegerHelpers;

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
 * Hierarchy support for KademliaOverlayIDs. IDs are organised in hierarchical
 * clusters formed by a BTREE with degree 2^
 * {@link TypesConfig#getHierarchyTreeOrder()} and maximum depth
 * {@link TypesConfig#getHierarchyDepth()}. The root node, the topmost cluster,
 * contains all nodes, that is the nodes contained in its children clusters.
 * Generally speaking, the clusters in each level of the cluster tree partition
 * the node identifier space, that is each node identifier belongs to exactly
 * one cluster at each level.<br />
 * 
 * The cluster identifier of a HKademliaOverlayID is embedded in the BigInteger
 * bit representation of a KademliaOverayID, namely the
 * <code>{@link TypesConfig#getHierarchyTreeOrder()} * {@link TypesConfig#getHierarchyDepth()}</code>
 * rightmost bits, where the "most significant" cluster identifier is at the
 * left.<br />
 * 
 * For example, with ID_LENGTH=8, HIERARCHY_DEPTH=2, HIERARCHY_BTREE=2:
 * <ul>
 * <li>The nodes 01001100 and 01001011 only share the topmost cluster, that is
 * the root of the cluster hierarchy that contains all nodes.</li>
 * <li>The nodes 01010111 and 11010110 share the cluster 01 (depth 1),
 * <li>and the nodes 01010111 and 11010111 share the cluster 0111 (depth 2).</li>
 * </ul>
 * Obviously, if two nodes share a common cluster at a certain depth in the
 * cluster hierarchy, they also share a cluster in the next levels towards the
 * root.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HKademliaOverlayID extends KademliaOverlayID {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5997297758201664969L;

	/**
	 * Constructs a HKademliaOverlayID from the specified Integer. The hierarchy
	 * is derived from the last bits at the right.
	 * 
	 * @param id
	 *            the ID of this object
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public HKademliaOverlayID(final Integer id, final TypesConfig conf) {
		super(id, conf);
	}

	/**
	 * Constructs a HKademliaOverlayID from the specified binary representation.
	 * The hierarchy is derived from the last bits at the right.
	 * 
	 * @param id
	 *            a String that contains the binary representation of the
	 *            HKademliaOverlayID. Alphabet is {0, 1}.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public HKademliaOverlayID(final String id, final TypesConfig conf) {
		super(id, conf);
	}

	/**
	 * Constructs a HKademliaOverlayID from the specified byte array which is
	 * interpreted as a positive integer in big-endian order (most significant
	 * byte is in the zeroth element). The hierarchy is derived from the last
	 * bits at the right.
	 * 
	 * @param val
	 *            the specified byte array
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	// public HKademliaOverlayID(final byte[] val, final TypesConfig conf) {
	// super(val, conf);
	// }
	/**
	 * Constructs a HKademliaOverlayID from a BigInteger. The hierarchy is
	 * derived from the last bits at the right.
	 * 
	 * @param id
	 *            the identifier of this KademliaOverlayID.
	 * @param conf
	 *            a TypesConfig reference that permits to retrieve configuration
	 *            "constants".
	 */
	public HKademliaOverlayID(final BigInteger id, final TypesConfig conf) {
		super(id, conf);
	}

	/**
	 * Calculates the depth of the deepest common cluster of this
	 * HKademliaOverlayID and the given HKademliaOverlayID. This metric is
	 * symmetric.
	 * 
	 * @param test
	 *            the HKademliaOverlayID that is to be compared to this ID.
	 * @return the depth of the deepest common cluster of the two identifiers.
	 *         The topmost cluster, that is the one that contains all nodes, has
	 *         depth 0.
	 */
	public final int getCommonClusterDepth(final HKademliaOverlayID test) {
		final int clusterBitLength = config.getHierarchyTreeOrder()
				* config.getHierarchyDepth();
		BigInteger myCluster = BigIntegerHelpers.getNRightmostBits(this
				.getBigInt(), clusterBitLength);
		BigInteger hisCluster = BigIntegerHelpers.getNRightmostBits(test
				.getBigInt(), clusterBitLength);
		for (int i = config.getHierarchyDepth(); i >= 0; i--) {
			if (myCluster.equals(hisCluster)) {
				return i;
			}
			// TODO: maybe possible with less effort?
			myCluster = BigIntegerHelpers.getNRightmostBits(myCluster
					.shiftRight(config.getHierarchyTreeOrder()), (i - 1)
					* config.getHierarchyTreeOrder());
			hisCluster = BigIntegerHelpers.getNRightmostBits(hisCluster
					.shiftRight(config.getHierarchyTreeOrder()), (i - 1)
					* config.getHierarchyTreeOrder());

		}
		return 0;
	}

	/**
	 * Returns a new HKademliaOverlayID with the cluster ID set to
	 * <code>clusterID</code>. This modifies the last
	 * <code>{@link TypesConfig#getHierarchyTreeOrder()} * {@link TypesConfig#getHierarchyDepth()}</code>
	 * bits of the current identifier (but this HKademliaOverlayID object itself
	 * is not modified).
	 * 
	 * @param clusterID
	 *            the new cluster ID for this HKademliaOverlayID.
	 * @return the new HKademliaOverlayID with the modified cluster ID.
	 */
	public final HKademliaOverlayID setCluster(final BigInteger clusterID) {
		final int clusterBitLength = config.getHierarchyTreeOrder()
				* config.getHierarchyDepth();
		final BigInteger cutClusterID = BigIntegerHelpers.getNRightmostBits(
				clusterID, clusterBitLength);
		final BigInteger ownIDEmptyCluster = getBigInt().shiftRight(
				clusterBitLength).shiftLeft(clusterBitLength);
		return new HKademliaOverlayID(ownIDEmptyCluster.or(cutClusterID),
				config);
	}

	/**
	 * @return a BigInteger that contains only the suffix of this
	 *         HKademliaOverlayID that stands for the cluster.
	 */
	public final BigInteger getCluster() {
		final int clusterBitLength = config.getHierarchyTreeOrder()
				* config.getHierarchyDepth();
		final BigInteger myCluster = BigIntegerHelpers.getNRightmostBits(this
				.getBigInt(), clusterBitLength);
		return myCluster;
	}

}
