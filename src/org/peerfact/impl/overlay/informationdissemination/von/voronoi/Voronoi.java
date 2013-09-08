/*
 * The author of this software is Steven Fortune.
 * Copyright (c) 1994 by AT&T Bell Laboratories.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/*
 * VAST, a scalable peer-to-peer network for virtual environments
 * Copyright (C) 2004 Guan-Ming Liao (gm.liao@msa.hinet.net)    adpated from C   to C++
 * Copyright (C) 2006 Shun-Yun Hu    (syhu@yahoo.com)           adapted from C++ to Java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.peerfact.impl.overlay.informationdissemination.von.voronoi;

import java.awt.Point;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNodeInfo;
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
 * 
 * /* structure used both for sites and for vertices
 */

class Site {
	public Point2d coord = new Point2d();

	public int num; // originally 'sitenbr', now either the site id or vertex

	public int ref_count = 0; // originally 'refcnt', reference count

	public Vector<Integer> edge_idxlist = new Vector<Integer>();

	public Site(double x, double y) {
		coord.x = x;
		coord.y = y;
	}

	public double dist(Site s) {
		return coord.distance(s.coord);
	}
}

class Edge {
	public double a, b, c;

	public Site[] ep = new Site[2];

	public Site[] reg = new Site[2];

	public int num;
}

class Halfedge {
	public Halfedge ELleft, ELright;

	public Edge ELedge;

	public int ELref_count;

	public int ELpm;

	public Site vertex;

	public double ystar;

	public Halfedge PQnext;
}

/**
 * This class includes all logic for the handling of voronoi diagrams. It is
 * adapted to fit for the needs of the overlay VON.
 * 
 */
public class Voronoi {

	/**
	 * Tells whether the voronoi needs a recomputation or not
	 */
	private boolean invalidated = false;

	/**
	 * Used as semaphore to avoid interleaving calls of the method
	 * <code>recompute()</code>
	 */
	private boolean currentlyRecomputing = false;

	/**
	 * The overlay ID of the owning node
	 */
	private final VonID owningNode;

	/**
	 * The sites of this voronoi
	 */
	private final ConcurrentHashMap<Integer, Point2d> sites = new ConcurrentHashMap<Integer, Point2d>();

	/**
	 * Structure to store the <code>VonNodeInfo</code> for all sites in the
	 * voronoi
	 * 
	 * @author Julius Rueckert
	 */
	private final ConcurrentHashMap<Integer, VonNodeInfo> nodeInfos = new ConcurrentHashMap<Integer, VonNodeInfo>();

	/**
	 * The time stamps associated with the sites in this voronoi.
	 * 
	 * Note: There might not be a time stamp for every site in the voronoi.
	 */
	private final ConcurrentHashMap<Integer, Long> timestamps = new ConcurrentHashMap<Integer, Long>();

	/**
	 * NOTE: we use TreeMap for mSites as it is both sorted and also a map
	 */
	private TreeMap<Point2d, Site> mSites = new TreeMap<Point2d, Site>();

	private Vector<Line2d> mEdges = new Vector<Line2d>();

	private Vector<Point2d> mVertices = new Vector<Point2d>();

	public Voronoi(VonID owningNode) {

		this.owningNode = owningNode;

		DELETED = new Edge();
		DELETED.a = DELETED.b = DELETED.c = (-2);

		le = 0;
		re = 1;
	}

	/**
	 * Insert a new site, the first inserted is myself
	 * 
	 * @param id
	 * @param coord
	 */
	protected void insert(int id, Point coord) {

		// avoid duplicate insert
		if (sites.containsKey(Integer.valueOf(id)) == false) {
			invalidated = true;
			// System.err.println ("inserting [" + id + "] coord: " + coord);
			sites.put(Integer.valueOf(id), new Point2d(coord.x, coord.y));
		}
	}

	/**
	 * Insert a new site, the first inserted is myself
	 * 
	 * @author Julius Rueckert
	 * 
	 * @param olID
	 * @param nodeInfo
	 */
	private void insert(VonNodeInfo nodeInfo) {
		Integer id = Integer.valueOf(nodeInfo.getContact().getOverlayID()
				.getUniqueValue());

		nodeInfos.put(id, nodeInfo);
		insert(id, nodeInfo.getPosition());
	}

	/**
	 * Insert a new site, the first inserted is myself
	 * 
	 * @author Julius Rueckert
	 * 
	 * @param nodeInfo
	 * @param timestamp
	 *            usually given by the sender that informs about new neighbors
	 */
	public synchronized void insert(VonNodeInfo nodeInfo, long timestamp) {
		insert(nodeInfo);
		timestamps.put(nodeInfo.getContact().getOverlayID().getUniqueValue(),
				timestamp);
	}

	/**
	 * Sequentially insert new sites, the first inserted is myself
	 * 
	 * @author Julius Rueckert
	 * 
	 * @param nodeInfo
	 * @param timestampArray
	 * @return
	 */
	public LinkedList<VonNodeInfo> insertAll(VonNodeInfo[] nodeInfo,
			Long[] timestampArray) {
		LinkedList<VonNodeInfo> newContacts = new LinkedList<VonNodeInfo>();

		for (int i = 0; i < nodeInfo.length; i++) {
			if (!isPresent(nodeInfo[i])) {
				newContacts.add(nodeInfo[i]);
			}

			insert(nodeInfo[i], timestampArray[i]);
		}

		return newContacts;
	}

	private boolean isPresent(VonNodeInfo nodeInfo) {
		Integer id = Integer.valueOf(nodeInfo.getContact().getOverlayID()
				.getUniqueValue());

		return sites.containsKey(id);
	}

	public long getTimestamp(VonID id) {
		return timestamps.get(id.getUniqueValue());
	}

	public void updateTimestamp(VonID id, long newTimestamp) {
		timestamps.put(id.getUniqueValue(), newTimestamp);
	}

	public long getTimestamp(Integer id) {
		return timestamps.get(id);
	}

	/**
	 * Remove a site
	 * 
	 * @param id
	 */
	public synchronized VonNodeInfo remove(int id) {

		if (sites.remove(Integer.valueOf(id)) != null) {
			invalidated = true;
		}

		VonNodeInfo nodeInfo = nodeInfos.remove(Integer.valueOf(id));
		timestamps.remove(Integer.valueOf(id));

		return nodeInfo;
	}

	public synchronized VonNodeInfo remove(VonID id) {

		if (id.compareTo(owningNode) == 0) {
			return null;
		}
		return remove(id.getUniqueValue());
	}

	/**
	 * Update the entry of a site. This causes the voronoi to be marked as
	 * invalidated. Later operations may have to rebuild the voronoi.
	 * 
	 * @param id
	 * @param coord
	 */
	protected synchronized void update(int id, Point coord) {
		invalidated = true;

		// point2d pt =
		sites.put(Integer.valueOf(id), new Point2d(coord.x, coord.y));
		// if (pt == null)
		// insert(id, coord);
		// else {
		// pt.x = coord.x;
		// pt.y = coord.y;
		// }
	}

	/**
	 * Update the entry of a site. This causes the voronoi to be marked as
	 * invalidated. Later operations may have to rebuild the voronoi.
	 * 
	 * @param id
	 * @param coord
	 * @param timestamp
	 */
	public void update(VonID id, Point coord, int aoiRadius, long timestamp) {
		update(id.getUniqueValue(), coord);
		updateNodeInfo(id, coord);
		updateNodeInfo(id, aoiRadius);
		updateTimestamp(id, timestamp);

	}

	private synchronized void updateNodeInfo(VonID id, Point pos) {
		VonNodeInfo nInfo = nodeInfos.get(id.getUniqueValue());

		if (nInfo != null) {
			nInfo = new VonNodeInfo(nInfo.getContact(), pos, nInfo
					.getAoiRadius());
			nodeInfos.put(id.getUniqueValue(), nInfo);
		}
	}

	private synchronized void updateNodeInfo(VonID id, int aoiRadius) {
		VonNodeInfo nInfo = nodeInfos.get(id.getUniqueValue());

		if (nInfo != null) {
			nInfo = new VonNodeInfo(nInfo.getContact(), nInfo.getPosition(),
					aoiRadius);
			nodeInfos.put(id.getUniqueValue(), nInfo);
		}
	}

	/**
	 * Get the point of a site
	 * 
	 * @param id
	 * @return
	 */
	public Point get(int id) {

		Point2d p = sites.get(Integer.valueOf(id));
		return new Point((int) p.x, (int) p.y);
	}

	/**
	 * Get the node info of a site
	 * 
	 * @author Julius Rueckert
	 * 
	 * @param id
	 * @return
	 */
	public VonNodeInfo getNodeInfo(int id) {
		return nodeInfos.get(id);
	}

	/**
	 * Check if a point lies inside a particular region
	 * 
	 * @param id
	 * @param coord
	 * @return
	 */
	public boolean contains(int id, Point coord) {
		if (!sites.containsKey(Integer.valueOf(id))) {
			return false;
		}

		recompute();

		return insideRegion(id, new Point2d(coord.x, coord.y));
	}

	/**
	 * Check if the node is a boundary neighbor
	 * 
	 * 
	 * @param idToCheck
	 * @param center
	 * @param radius
	 * @return
	 */
	private boolean intersectsBoundary(int idToCheck, Point center, int radius) {

		Point2d coordOfNodeToCheck = sites.get(Integer.valueOf(idToCheck));
		if (coordOfNodeToCheck == null) {
			return false;
		}

		recompute();

		Point2d c = new Point2d(center.x, center.y);
		int idx1, idx2;

		TreeMap<Point2d, Site> sitesTreeMap = getmSites();
		Vector<Line2d> edges = getmEdges();
		Vector<Point2d> vertices = getmVertices();

		boolean oneInside = false;
		boolean oneOutside = false;

		if (sitesTreeMap.get(coordOfNodeToCheck) == null
				|| sitesTreeMap.get(coordOfNodeToCheck).edge_idxlist == null) {
			return false;
		}

		for (int edge_idx : sitesTreeMap.get(coordOfNodeToCheck).edge_idxlist) {

			if (edge_idx < edges.size()) {
				idx1 = (edges.elementAt(edge_idx)).vertexIndex[0];
				idx2 = (edges.elementAt(edge_idx)).vertexIndex[1];
			} else {
				continue;
			}

			if (idx1 != -1) {
				if (idx1 < vertices.size()
						&& c.distance(vertices.elementAt(idx1)) > radius) {
					oneOutside = true;
				} else {
					oneInside = true;
				}
			} else {
				oneOutside = true;
			}

			if (idx2 != -1) {

				if (idx2 < vertices.size()
						&& c.distance(vertices.elementAt(idx2)) > radius) {
					oneOutside = true;
				} else {
					oneInside = true;
				}
			} else {
				oneOutside = true;
			}

		}

		return oneInside && oneOutside;
	}

	public boolean isAoiNeighbor(int centerNode, int toCheckNode, int aoiRadius) {

		recompute();

		Point2d centerP = sites.get(Integer.valueOf(centerNode));
		Point2d toCheckP = sites.get(Integer.valueOf(toCheckNode));

		if (centerP == null || toCheckP == null) {
			return false;
		}

		return Math.floor(centerP.distance(toCheckP.x, toCheckP.y)) <= aoiRadius;
	}

	/**
	 * @author Julius Rueckert
	 * 
	 * @param centerNode
	 * @param toCheckNode
	 * @param aoiRadius
	 * @return
	 */
	public boolean isBoundaryNeighborOf(int centerNode, int toCheckNode,
			int aoiRadius) {

		boolean isAoiNeighbor = isAoiNeighbor(centerNode, toCheckNode,
				aoiRadius);

		boolean insideAoiAndIntersectingWithBoundary = isAoiNeighbor
				&& intersectsBoundary(toCheckNode, getNodeInfo(centerNode)
						.getPosition(), aoiRadius);

		boolean outsideAoiAndEnclosing = !isAoiNeighbor(centerNode,
				toCheckNode, aoiRadius)
				&& isEnclosingNeighbor(centerNode, toCheckNode);

		return insideAoiAndIntersectingWithBoundary || outsideAoiAndEnclosing;

	}

	/**
	 * @author Julius Rueckert
	 * 
	 * @param centerNode
	 * @param toCheckNode
	 * @param aoiRadius
	 * @return
	 */
	public boolean isBoundaryNeighborOf(VonID centerNode, VonID toCheckNode,
			int aoiRadius) {
		return isBoundaryNeighborOf(centerNode.getUniqueValue(), toCheckNode
				.getUniqueValue(), aoiRadius);
	}

	/**
	 * @author Julius Rueckert
	 * 
	 * @param centerNode
	 * @param toCheckNode
	 * @param aoiRadius
	 * @return
	 */
	public boolean isBoundaryOrEnclosingNeighbor(VonID centerNode,
			VonID toCheckNode, int aoiRadius) {
		return isBoundaryNeighborOf(centerNode, toCheckNode, aoiRadius)
				|| isEnclosingNeighbor(centerNode, toCheckNode);
	}

	private boolean isVonNeighbor(int centerNode, int toCheckNode, int aoiRadius) {
		recompute();

		if (centerNode == toCheckNode) {
			return false;
		}

		boolean isAoiNeighbor = isAoiNeighbor(centerNode, toCheckNode,
				aoiRadius);

		if (isAoiNeighbor) {
			return true;
		}

		boolean isEnclosingNeighbor = isEnclosingNeighbor(centerNode,
				toCheckNode);

		if (isEnclosingNeighbor) {
			return true;
		}

		return false;
	}

	/**
	 * An node is a VON neighbor if it is inside the nodes AOI or if it is an
	 * enclosing neighbor.
	 * 
	 * @param centerNode
	 * @param toCheckNode
	 * @param aoiRadius
	 * @return
	 */
	public boolean isVonNeighbor(VonID centerNode, VonID toCheckNode,
			int aoiRadius) {

		return isVonNeighbor(centerNode.getUniqueValue(), toCheckNode
				.getUniqueValue(), aoiRadius);
	}

	private VonNodeInfo[] getVonNeighbors(int centerNode, int aoiRadius) {
		LinkedList<Integer> neighbors = new LinkedList<Integer>();

		Integer[] sitesArray = getSitesArr();

		for (int i = 0; i < sitesArray.length; i++) {
			if (isVonNeighbor(centerNode, sitesArray[i], aoiRadius)) {
				neighbors.add(sitesArray[i]);
			}
		}

		return getAllNodeInfo(neighbors.toArray(new Integer[neighbors.size()]));
	}

	/**
	 * An node is a VON neighbor if it is inside the nodes AOI or if it is an
	 * enclosing neighbor.
	 * 
	 * @param centerNode
	 * @param aoiRadius
	 * @return
	 */
	public VonNodeInfo[] getVonNeighbors(VonID centerNode, int aoiRadius) {
		return getVonNeighbors(centerNode.getUniqueValue(), aoiRadius);
	}

	/**
	 * @author Julius Rueckert
	 * 
	 * @param idCenter
	 * @param idToCheck
	 * @return
	 */
	public boolean isEnclosingNeighbor(int idCenter, int idToCheck) {
		recompute();

		Vector<Integer> enc = getEnclosingNeighbors(idCenter);

		if (enc == null) {
			return false;
		}

		for (Object encNgb : enc) {
			if (((Integer) encNgb).intValue() == idToCheck) {
				return true;
			}
		}

		return false;
	}

	public boolean isEnclosingNeighbor(VonID idCenter, VonID idToCheck) {
		return isEnclosingNeighbor(idCenter.getUniqueValue(), idToCheck
				.getUniqueValue());
	}

	/**
	 * Get a list of enclosing neighbors
	 * 
	 * @param id
	 * @return
	 */
	public VonID[] getEnclosingNeighbors(VonID id) {

		Vector<Integer> enclosingIds = getEnclosingNeighbors(id
				.getUniqueValue());

		if (enclosingIds == null) {
			return new VonID[0];
		}

		VonID[] enclosings = new VonID[enclosingIds.size()];

		for (int i = 0; i < enclosingIds.size(); i++) {
			enclosings[i] = nodeInfos.get(enclosingIds.get(i)).getContact()
					.getOverlayID();
		}

		return enclosings;
	}

	public VonNodeInfo[] getEnclosingNeighborsInfo(VonID id) {

		return getAllNodeInfo(transFormToIntegerArr(getEnclosingNeighbors(id)));
	}

	public boolean isPresentInVoronoi(VonID id) {
		return sites.containsKey(id.getUniqueValue());
	}

	/**
	 * Get a list of enclosing neighbors
	 * 
	 * @param id
	 * @return
	 */
	public Vector<Integer> getEnclosingNeighbors(int id) {
		recompute();

		TreeMap<Point2d, Site> sitesTreeMap = getmSites();
		Vector<Line2d> edges = getmEdges();
		// Vector<Point2d> vertices = getmVertices();

		Point2d posOfSite = this.sites.get(Integer.valueOf(id));
		if (posOfSite == null) {
			return new Vector<Integer>();
		}

		Vector<Integer> enclosingNodes = new Vector<Integer>();

		if (sitesTreeMap.get(posOfSite) == null
				|| sitesTreeMap.get(posOfSite).edge_idxlist == null) {
			return new Vector<Integer>();
		}

		for (int edge_idx : sitesTreeMap.get(posOfSite).edge_idxlist) {

			if (edge_idx >= edges.size()) {
				continue;
			}
			Line2d line = edges.elementAt(edge_idx);

			// NOTE: bisecting has changed from storing node index to node id
			int idOfEnclosing = (line.bisectingID[0] == id ? line.bisectingID[1]
					: line.bisectingID[0]);
			enclosingNodes.add(Integer.valueOf(idOfEnclosing));
		}

		return enclosingNodes;
	}

	/**
	 * Commented out, because it is never used Check if a circle overlaps with a
	 * particular node
	 */
	// private boolean overlaps(int id, Point center, int radius) {
	// // if (sites.containsKey (new Integer(id)) == false)
	// // return false;
	// Point2d coord = sites.get(Integer.valueOf(id));
	// if (coord == null) {
	// return false;
	// }
	//
	// /*
	// * version 1 recompute(); sfv::point2d center (pt.x, pt.y); return
	// * _voronoi.collides (idx, center, (int)(radius+5));
	// */
	//
	// // version 2: simply check if it's within AOI
	// Point2d c = new Point2d(center.x, center.y);
	//
	// return (coord.distance(c) <= (radius) ? true : false);
	// }

	/**
	 * Get the site that is the closest to a given point
	 * 
	 * @param coord
	 * @return
	 */
	public VonNodeInfo getClosestToNodeInfo(Point coord) {
		return getNodeInfo(closestTo(coord));
	}

	/**
	 * Returns the closest node to a point
	 */
	private int closestTo(Point coord) {

		Object[] keys = sites.keySet().toArray();
		Object[] points = sites.values().toArray();
		Point2d p = new Point2d(coord.x, coord.y);

		if (keys.length == 0) {
			return -1;
		}

		// assume the first node is the closest
		int closest = ((Integer) keys[0]).intValue();
		double min_dist = p.distance((Point2d) points[0]);

		Point2d pt;
		double d;

		for (int i = 1; i < sites.size(); i++) {
			pt = (Point2d) points[i];

			if ((d = p.distance(pt)) < min_dist) {
				min_dist = d;
				closest = ((Integer) keys[i]).intValue();
			}
		}

		return closest;
	}

	/**
	 * Get all sites contained in this voronoi
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, Point2d> getSites() {
		return sites;
	}

	/**
	 * @author Julius Rueckert
	 * 
	 * @return
	 */
	private Integer[] getSitesArr() {
		Object[] objArr = sites.keySet().toArray();

		Integer[] intArr = new Integer[objArr.length];

		for (int i = 0; i < objArr.length; i++) {
			intArr[i] = (Integer) objArr[i];
		}

		return intArr;
	}

	/**
	 * @return the NodeInfo of all nodes contained in this voronoi
	 */
	public List<VonNodeInfo> getAllNodeInfo() {
		return new LinkedList<VonNodeInfo>(Arrays
				.asList(getAllNodeInfo(getSitesArr())));
	}

	/**
	 * @return the NodeInfo of all nodes contained in this voronoi
	 */
	public VonNodeInfo[] getAllNodeInfoArr() {
		return getAllNodeInfo(getSitesArr());
	}

	/**
	 * @author Julius Rueckert
	 * 
	 * @param id
	 * @return
	 */
	protected VonNodeInfo[] getBoundingOrEnclosing(int id, int aoiRadius) {
		Integer[] boundingOrEnclosing = getBoundingOrEnclosingIndices(id,
				aoiRadius);

		VonNodeInfo[] nodeInfoArray = getAllNodeInfo(boundingOrEnclosing);

		return nodeInfoArray;
	}

	public VonNodeInfo[] getBoundingOrEnclosing(VonID id, int aoiRadius) {
		return getBoundingOrEnclosing(id.getUniqueValue(), aoiRadius);
	}

	private Integer[] getBoundingOrEnclosingIndices(int id, int aoiRadius) {
		LinkedHashSet<Integer> boundingOrEnclosing = new LinkedHashSet<Integer>();

		boundingOrEnclosing.addAll(getEnclosingNeighbors(id));

		Integer[] sitesArray = getSitesArr();

		for (int i = 0; i < sitesArray.length; i++) {
			if (isBoundaryNeighborOf(id, sitesArray[i], aoiRadius)) {
				boundingOrEnclosing.add(sitesArray[i]);
			}
		}

		return boundingOrEnclosing.toArray(new Integer[boundingOrEnclosing
				.size()]);
	}

	public Integer[] getBoundingOrEnclosingIndices(VonID id, int aoiRadius) {
		return getBoundingOrEnclosingIndices(id.getUniqueValue(), aoiRadius);
	}

	public VonNodeInfo[] getAllNodeInfo(List<VonID> ids) {
		return getAllNodeInfo(transFormToIntegerArr(ids));
	}

	private static Integer[] transFormToIntegerArr(List<VonID> ids) {
		Integer[] intIds = new Integer[ids.size()];

		for (int i = 0; i < ids.size(); i++) {
			intIds[i] = ids.get(i).getUniqueValue();
		}
		return intIds;
	}

	private static Integer[] transFormToIntegerArr(VonID[] ids) {
		return transFormToIntegerArr(Arrays.asList(ids));
	}

	public VonNodeInfo[] getAllNodeInfo(Integer[] ids) {
		VonNodeInfo[] nodeInfoArray = new VonNodeInfo[ids.length];

		for (int i = 0; i < nodeInfoArray.length; i++) {
			nodeInfoArray[i] = getNodeInfo(ids[i]);
		}

		return nodeInfoArray;
	}

	public Long[] getAllNodeTimestamps(List<VonID> ids) {
		return getAllNodeTimestamps(transFormToIntegerArr(ids));
	}

	public Long[] getAllNodeTimestampsByInfo(List<VonNodeInfo> infos) {
		LinkedList<VonID> ids = new LinkedList<VonID>();

		for (VonNodeInfo info : infos) {
			ids.add(info.getContact().getOverlayID());
		}

		return getAllNodeTimestamps(transFormToIntegerArr(ids));
	}

	public Long[] getAllNodeTimestamps(Integer[] ids) {
		Long[] localTimestamps = new Long[ids.length];

		for (int i = 0; i < ids.length; i++) {
			if (ids[i].compareTo(this.owningNode.getUniqueValue()) == 0) {
				localTimestamps[i] = Simulator.getCurrentTime();
			} else {
				localTimestamps[i] = getTimestamp(ids[i]);
			}
		}

		return localTimestamps;
	}

	TreeMap<Point2d, Site> temp_mSites = mSites;

	Vector<Point2d> temp_mVertices = mVertices;

	Vector<Line2d> temp_mEdges = mEdges;

	protected synchronized void recompute() {

		if (!invalidated || currentlyRecomputing || sites.size() == 0) {
			return;
		}

		/*
		 * Create clones to allow access during the computation.
		 * 
		 * Was needed for the visualization which introduced a multi-threaded
		 * behavior.
		 */
		temp_mSites = mSites;
		temp_mVertices = (Vector<Point2d>) mVertices.clone();
		temp_mEdges = (Vector<Line2d>) mEdges.clone();

		currentlyRecomputing = true;

		// clearAll();
		// mSites.clear();
		// mEdges.clear();
		// mVertices.clear();
		mSites = new TreeMap<Point2d, Site>();
		mEdges = new Vector<Line2d>();
		mVertices = new Vector<Point2d>();

		triangulate = false;
		plot = true;
		readsites();
		curr_site = mSites.values().iterator();

		geominit();

		if (plot) {
			plotinit();
		}

		voronoi(triangulate);

		invalidated = false;
		currentlyRecomputing = false;
	}

	private boolean insideRegion(int id, Point p) {
		return insideRegion(id, new Point2d(p.x, p.y));
	}

	public boolean insideRegion(VonID id, Point p) {
		return insideRegion(id.getUniqueValue(), p);
	}

	/**
	 * Check if a particular point is inside a given Voronoi region
	 * 
	 * @param id
	 * @param p
	 * @return
	 */
	private boolean insideRegion(int id, Point2d p) {

		recompute();

		boolean b1, b2;
		Point2d siteP = sites.get(Integer.valueOf(id));

		if (siteP == null) {
			return false;
		}
		Site site = getmSites().get(siteP);

		Enumeration<?> e = site.edge_idxlist.elements();
		Point2d coord = site.coord;

		int edge_idx;
		Line2d edge;

		while (e.hasMoreElements()) {

			edge_idx = ((Integer) e.nextElement()).intValue();
			edge = mEdges.elementAt(edge_idx);

			b1 = (edge.a * coord.x + edge.b * coord.y > edge.c);
			b2 = (edge.a * p.x + edge.b * p.y > edge.c);

			if (Math.abs(edge.a * p.x + edge.b * p.y - edge.c) < 0.001) {
				b2 = b1;
			}
			if (b1 != b2) {
				return false;
			}
		}

		if (e.hasMoreElements() == false) {
			return true;
		} else {
			return false;
		}
	}

	private Site nextone() {

		if (!curr_site.hasNext()) {
			return null;
		}

		Site s = curr_site.next();

		return s;
	}

	/**
	 * Removes all sites from the voronoi that are outside the AOI and not
	 * boundary neighbors
	 * 
	 * @author Julius Rueckert
	 * 
	 * @param id
	 * @param radius
	 * 
	 * @return the number of removed contacts
	 */
	public synchronized LinkedList<VonNodeInfo> removeOutsideOfAOIAndNotEnclosing(
			VonID id, int radius) {
		Vector<Integer> enclosings = getEnclosingNeighbors(id.getUniqueValue());

		LinkedHashSet<Integer> removeFromVoroAtEnd = new LinkedHashSet<Integer>();

		Integer[] ids = this.getSitesArr();
		for (int i = 0; i < ids.length; i++) {
			removeFromVoroAtEnd.add(ids[i]);
		}

		// The node itself is never removed
		removeFromVoroAtEnd.remove(id.getUniqueValue());

		// Enclosing neighbors are never removed
		removeFromVoroAtEnd.removeAll(enclosings);

		LinkedList<VonNodeInfo> removed = new LinkedList<VonNodeInfo>();

		for (Integer site : removeFromVoroAtEnd) {
			if (!isAoiNeighbor(id.getUniqueValue(), site, radius)) {
				removed.add(remove(site));
			}
		}

		return removed;
	}

	private void readsites() {

		// find out the x & y ranges for all sites
		nsites = sites.size();

		Object[] points = sites.values().toArray();
		Object[] keys = sites.keySet().toArray();

		xmin = xmax = ((Point2d) points[0]).x;
		ymin = ymax = ((Point2d) points[0]).y;
		Point2d pt;
		Site s;

		// converting sites to mSites
		// NOTE: this is an important step as mSites needs to be sorted for
		// Voronoi construction
		for (int i = 0; i < nsites; i++) {

			pt = (Point2d) points[i];
			s = new Site(pt.x, pt.y);
			s.num = ((Integer) keys[i]).intValue();

			mSites.put(new Point2d(pt), s);

			if (pt.x < xmin) {
				xmin = pt.x;
			} else if (pt.x > xmax) {
				xmax = pt.x;
			}
			if (pt.y < ymin) {
				ymin = pt.y;
			} else if (pt.y > ymax) {
				ymax = pt.y;
			}

			// System.err.println ("storing num: [" + s.num + "] (" + pt.x +
			// ", " + pt.y + ")");
		}

		// ymin = ((Site)((Map.Entry)entries[0]).getValue()).coord.y;
		// ymax = ((Site)((Map.Entry)entries[nsites-1]).getValue()).coord.y;

		// Julius: Removed as I think this is not necessary
		// Iterator it = mSites.values().iterator();
		// while (it.hasNext()) {
		// s = (Site) it.next();
		// System.err.println ("sorted num: [" + s.num + "] (" + s.coord.x +
		// ", " + s.coord.y + ")");
		// }

		// System.err.println ("xmax=" + xmax + " xmin=" + xmin + " ymax=" +
		// ymax + " ymin=" + ymin);

	}

	// ////////////////////////////////////////////////////////////////////////
	// defs.h
	//

	// command-line flags

	private boolean triangulate, plot;

	private double xmin, xmax, ymin, ymax, deltax, deltay;

	private int nsites;

	private Iterator<Site> curr_site;

	private int sqrt_nsites;

	private int nvertices;

	private Site bottomsite; // Site *bottomsite;

	private int nedges;

	private Halfedge[] PQhash; // Halfedge *PQhash;

	private int PQhashsize;

	private int PQcount;

	private int PQmin;

	private Halfedge ELleftend, ELrightend; // Halfedge *ELleftend, *ELrightend;

	private int ELhashsize;

	private Halfedge[] ELhash; // Halfedge **ELhash;

	private final Edge DELETED; // special marker

	private final int le;

	private final int re;

	// ////////////////////////////////////////////////////////////////////////
	// geometry.c
	//
	private void geominit() {

		nvertices = 0;
		nedges = 0;
		double sn = nsites + 4;
		sqrt_nsites = (int) Math.sqrt(sn);
		deltay = ymax - ymin;
		deltax = xmax - xmin;
	}

	// find the bisecting edge for two sites (creating a new edge)
	private Edge bisect(Site s1, Site s2) {

		// log.debug ("bisecting (" + s1.coord.x + ", " + s1.coord.y +
		// ") (" + s2.coord.x + ", " + s2.coord.y + ")");
		double dx, dy, adx, ady; // deltas in coords and their absolute values
		Edge newedge = new Edge();

		newedge.reg[0] = s1;
		newedge.reg[1] = s2;
		ref(s1);
		ref(s2);

		newedge.ep[0] = null;
		newedge.ep[1] = null;

		dx = s2.coord.x - s1.coord.x;
		dy = s2.coord.y - s1.coord.y;
		adx = (dx > 0 ? dx : -dx);
		ady = (dy > 0 ? dy : -dy);

		newedge.c = s1.coord.x * dx + s1.coord.y * dy + (dx * dx + dy * dy)
				* 0.5;

		if (adx > ady) {
			newedge.a = 1.0;
			newedge.b = dy / dx;
			newedge.c /= dx;
		} else {
			newedge.b = 1.0;
			newedge.a = dx / dy;
			newedge.c /= dy;
		}

		newedge.num = nedges++;
		out_bisector(newedge);

		return newedge;
	}

	private Site intersect(Halfedge el1, Halfedge el2, Point2d p) {

		Edge e1, e2, e;
		Halfedge el;

		double d, xint, yint;

		e1 = el1.ELedge;
		e2 = el2.ELedge;

		if (e1 == null || e2 == null || (e1.reg[1] == e2.reg[1])) {
			return null;
		}

		d = e1.a * e2.b - e1.b * e2.a;

		if (-1.0e-10 < d && d < 1.0e-10) {
			return null;
		}

		xint = (e1.c * e2.b - e2.c * e1.b) / d;
		yint = (e2.c * e1.a - e1.c * e2.a) / d;

		if (e1.reg[1].coord.compareTo(e2.reg[1].coord) < 0) {
			el = el1;
			e = e1;
		} else {
			el = el2;
			e = e2;
		}

		boolean right_of_site = xint >= e.reg[1].coord.x;

		if ((right_of_site && el.ELpm == le)
				|| (!right_of_site && el.ELpm == re)) {
			return null;
		}

		Site v = new Site(xint, yint);
		v.ref_count = 0; // perhaps can remove?

		return v;
	}

	private boolean right_of(Halfedge el, Point2d p) {

		boolean right_of_site, above, fast;
		double dxp, dyp, dxs, t1, t2, t3, yl;

		Edge e = el.ELedge;
		Site topsite = e.reg[1];

		right_of_site = p.x > topsite.coord.x;

		if (right_of_site && el.ELpm == le) {
			return true;
		}

		if (!right_of_site && el.ELpm == re) {
			return false;
		}

		if (e.a == 1.0) {

			dyp = p.y - topsite.coord.y;
			dxp = p.x - topsite.coord.x;
			fast = false;

			if ((!right_of_site & (e.b < 0.0)) | (right_of_site & (e.b >= 0.0))) {
				fast = above = (dyp >= e.b * dxp);
			} else {
				above = p.x + p.y * e.b > e.c;
				if (e.b < 0.0) {
					above = !above;
				}
				if (!above) {
					fast = true;
				}
			}
			if (!fast) {

				dxs = topsite.coord.x - (e.reg[0]).coord.x;

				// joker: update, skip divide by zero 2005/05/27
				// need to further check what cases could cause divide by
				// 0
				if (dxs != 0) {
					above = e.b * (dxp * dxp - dyp * dyp) < dxs * dyp
							* (1.0 + 2.0 * dxp / dxs + e.b * e.b);
				} else {
					above = false;
				}

				if (e.b < 0.0) {
					above = !above;
				}
			}
		}
		// e.b==1.0
		else {
			yl = e.c - e.a * p.x;
			t1 = p.y - yl;
			t2 = p.x - topsite.coord.x;
			t3 = yl - topsite.coord.y;
			above = t1 * t1 > (t2 * t2 + t3 * t3);
		}

		return (el.ELpm == le ? above : !above);
	}

	private void endpoint(Edge e, int lr, Site s) {
		e.ep[lr] = s;
		ref(s);

		if (e.ep[re - lr] == null) {
			return;
		}

		out_ep(e);
		deref(e.reg[le]);
		deref(e.reg[re]);
		// makefree(e, &efl);
	}

	// return int change to void
	private void makevertex(Site v) {
		v.num = nvertices++;
		out_vertex(v);
	}

	private static void deref(Site v) {
		v.ref_count--;
	}

	private static void ref(Site v) {
		v.ref_count++;
	}

	/*
	 * //////////////////////////////////////////////////////////////////////////
	 * // memory.c // private void freeinit( Freelist *fl , int size); private
	 * char* getfree( Freelist *fl); private void makefree( Freenode *curr,
	 * Freelist *fl ); private char* myalloc(unsigned n);
	 */

	// ////////////////////////////////////////////////////////////////////////
	// output.c
	//

	private double pxmin, pxmax, pymin, pymax;

	private void out_bisector(Edge e) {

		// log.debug ("out_bisector [" + (float)e.a + ", " + (float)e.b
		// + ", " + (float)e.c + "]");

		Line2d line = new Line2d(e.a, e.b, e.c);

		line.bisectingID[0] = e.reg[le].num;
		line.bisectingID[1] = e.reg[re].num;

		Point2d pt1 = sites.get(Integer.valueOf(line.bisectingID[0]));
		Point2d pt2 = sites.get(Integer.valueOf(line.bisectingID[1]));

		if (mSites.get(pt1) != null && mSites.get(pt1).edge_idxlist != null) {
			Vector<Integer> new_edge_idxlist = new Vector<Integer>(mSites
					.get(pt1).edge_idxlist);
			new_edge_idxlist.add(Integer.valueOf(e.num));
			mSites.get(pt1).edge_idxlist = new_edge_idxlist;
		}
		if (mSites.get(pt2) != null && mSites.get(pt2).edge_idxlist != null) {
			Vector<Integer> new_edge_idxlist = new Vector<Integer>(mSites
					.get(pt2).edge_idxlist);
			new_edge_idxlist.add(Integer.valueOf(e.num));
			mSites.get(pt2).edge_idxlist = new_edge_idxlist;
		}
		mEdges.add(line);
	}

	private void out_ep(Edge e) {
		if (mEdges.size() > e.num && mEdges.elementAt(e.num) != null) {
			(mEdges.elementAt(e.num)).vertexIndex[0] = (e != null && e.ep[le] != null) ? (e.ep[le].num)
					: (-1);
			(mEdges.elementAt(e.num)).vertexIndex[1] = (e != null && e.ep[re] != null) ? (e.ep[re].num)
					: (-1);
		}
		if (!triangulate & plot) {
			clip_line(e);
		}
	}

	private void out_vertex(Site v) {

		mVertices.add(new Point2d(v.coord.x, v.coord.y));
	}

	// store output of a site
	// private void out_site (Site s) {}
	// private void out_triple (Site s1, Site s2, Site s3) {}

	private void plotinit() {
		double dy = ymax - ymin;
		double dx = xmax - xmin;
		double d = (dx > dy ? dx : dy) * 1.1;

		pxmin = xmin - (d - dx) / 2.0;
		pxmax = xmax + (d - dx) / 2.0;
		pymin = ymin - (d - dy) / 2.0;
		pymax = ymax + (d - dy) / 2.0;
	}

	// cut edges so that they are displayable
	private void clip_line(Edge e) {

		Site s1, s2;
		double x1, x2, y1, y2;

		if (e.a == 1.0 && e.b >= 0.0) {
			s1 = e.ep[1];
			s2 = e.ep[0];
		} else {
			s1 = e.ep[0];
			s2 = e.ep[1];
		}

		if (e.a == 1.0) {

			y1 = pymin;
			if (s1 != null && s1.coord.y > pymin) {
				y1 = s1.coord.y;
			}

			if (y1 > pymax) {
				return;
			}

			x1 = e.c - e.b * y1;
			y2 = pymax;

			if (s2 != null && s2.coord.y < pymax) {
				y2 = s2.coord.y;
			}

			if (y2 < pymin) {
				return;
			}

			x2 = e.c - e.b * y2;

			if (((x1 > pxmax) & (x2 > pxmax)) | ((x1 < pxmin) & (x2 < pxmin))) {
				return;
			}

			if (x1 > pxmax) {
				x1 = pxmax;
				y1 = (e.c - x1) / e.b;
			}

			if (x1 < pxmin) {
				x1 = pxmin;
				y1 = (e.c - x1) / e.b;
			}

			if (x2 > pxmax) {
				x2 = pxmax;
				y2 = (e.c - x2) / e.b;
			}

			if (x2 < pxmin) {
				x2 = pxmin;
				y2 = (e.c - x2) / e.b;
			}
		} else {

			x1 = pxmin;
			if (s1 != null && s1.coord.x > pxmin) {
				x1 = s1.coord.x;
			}

			if (x1 > pxmax) {
				return;
			}

			y1 = e.c - e.a * x1;
			x2 = pxmax;
			if (s2 != null && s2.coord.x < pxmax) {
				x2 = s2.coord.x;
			}

			if (x2 < pxmin) {
				return;
			}

			y2 = e.c - e.a * x2;

			if (((y1 > pymax) & (y2 > pymax)) | ((y1 < pymin) & (y2 < pymin))) {
				return;
			}

			if (y1 > pymax) {
				y1 = pymax;
				x1 = (e.c - y1) / e.a;
			}

			if (y1 < pymin) {
				y1 = pymin;
				x1 = (e.c - y1) / e.a;
			}

			if (y2 > pymax) {
				y2 = pymax;
				x2 = (e.c - y2) / e.a;
			}

			if (y2 < pymin) {
				y2 = pymin;
				x2 = (e.c - y2) / e.a;
			}
		}

		(mEdges.elementAt(e.num)).seg.p1 = new Point2d(x1, y1);
		(mEdges.elementAt(e.num)).seg.p2 = new Point2d(x2, y2);
		// log.debug ("[" + (float)e.a + ", " + (float)e.b + ", " +
		// (float)e.c + "]");
		// log.debug (" + x1 + ", " + y1 + ") (" + x2 + ",
		// " + y2 + ")");
	}

	// ////////////////////////////////////////////////////////////////////////
	// heap.c
	//

	private void PQinsert(Halfedge he, Site v, double offset) {
		Halfedge last, next;

		he.vertex = v;
		ref(v);
		he.ystar = v.coord.y + offset;

		last = PQhash[PQbucket(he)];

		while ((next = last.PQnext) != null
				&& (he.ystar > next.ystar || (he.ystar == next.ystar && v.coord.x > next.vertex.coord.x))) {
			last = next;
		}

		he.PQnext = last.PQnext;
		last.PQnext = he;
		PQcount++;
	}

	private void PQdelete(Halfedge he) {
		Halfedge last;

		if (he.vertex != null) {
			last = PQhash[PQbucket(he)];
			while (last.PQnext != null && last.PQnext != he) {
				last = last.PQnext;
			}

			last.PQnext = he.PQnext;

			PQcount--;
			deref(he.vertex);
			he.vertex = null;
		}
	}

	private int PQbucket(Halfedge he) {

		int bucket = (int) ((he.ystar - ymin) / deltay * PQhashsize);
		if (bucket < 0) {
			bucket = 0;
		}
		if (bucket >= PQhashsize) {
			bucket = PQhashsize - 1;
		}
		if (bucket < PQmin) {
			PQmin = bucket;
		}
		return bucket;
	}

	private boolean PQempty() {
		return (PQcount == 0);
	}

	private Point2d PQ_min() {

		Point2d answer = new Point2d();

		while (PQhash.length > PQmin && PQhash[PQmin].PQnext == null) {
			PQmin++;
		}

		answer.x = PQhash[PQmin].PQnext.vertex.coord.x;
		answer.y = PQhash[PQmin].PQnext.ystar;

		return answer;
	}

	private Halfedge PQextractmin() {
		Halfedge curr;

		curr = PQhash[PQmin].PQnext;
		PQhash[PQmin].PQnext = curr.PQnext;
		PQcount--;
		return curr;
	}

	private void PQinitialize() {

		PQcount = 0;
		PQmin = 0;
		PQhashsize = 4 * sqrt_nsites;
		PQhash = new Halfedge[PQhashsize];

		for (int i = 0; i < PQhashsize; i++) {
			PQhash[i] = new Halfedge();
			PQhash[i].PQnext = null;
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// edgelist.c
	//

	// initialize edgelist
	private void ELinitialize() {

		// freeinit (&hfl, sizeof (Halfedge));
		ELhashsize = 2 * sqrt_nsites;
		ELhash = new Halfedge[ELhashsize];

		for (int i = 0; i < ELhashsize; i++) {
			ELhash[i] = null;
		}

		ELleftend = HEcreate(null, 0);
		ELrightend = HEcreate(null, 0);

		ELleftend.ELleft = null;
		ELleftend.ELright = ELrightend;

		ELrightend.ELleft = ELleftend;
		ELrightend.ELright = null;

		ELhash[0] = ELleftend;
		ELhash[ELhashsize - 1] = ELrightend;
	}

	private static Halfedge HEcreate(Edge e, int pm) {

		Halfedge he = new Halfedge();

		he.ELedge = e;
		he.ELpm = pm;
		he.PQnext = null;
		he.vertex = null;
		he.ELref_count = 0;
		he.ystar = 0;

		return he;
	}

	// change arg2 to newH
	private static void ELinsert(Halfedge lb, Halfedge newH) {
		newH.ELleft = lb;
		newH.ELright = lb.ELright;
		lb.ELright.ELleft = newH;
		lb.ELright = newH;
	}

	private Halfedge ELgethash(int b) {

		Halfedge he;

		if (b < 0 || b >= ELhashsize) {
			return null;
		}

		he = ELhash[b];
		if (he == null || he.ELedge != DELETED) {
			return he;
		}

		/* Hash table points to deleted half edge. Patch as necessary. */
		ELhash[b] = null;

		// if ((he . ELrefcnt -= 1) == 0) makefree(he, &hfl);
		return null;
	}

	private Halfedge ELleftbnd(Point2d p) {

		// System.err.println ("ELleftbnd processing ("+ p.x + ", " + p.y +
		// ")");
		int i, bucket;
		Halfedge he;

		/* Use hash table to get close to desired halfedge */
		bucket = (int) ((p.x - xmin) / deltax * ELhashsize);
		if (bucket < 0) {
			bucket = 0;
		}
		if (bucket >= ELhashsize) {
			bucket = ELhashsize - 1;
		}
		he = ELgethash(bucket);

		if (he == null) {
			// System.err.println ("ELleftbnd: first he is null");
			for (i = 1; true; i++) {
				if ((he = ELgethash(bucket - i)) != null) {
					break;
				}
				if ((he = ELgethash(bucket + i)) != null) {
					break;
				}
			}
			// totalsearch += i;
		}
		// System.err.println ("ELleftbnd: bucket: " + bucket + " hsize: " +
		// ELhashsize + " he - elpm: " + he.ELpm + " ref_count: " +
		// he.ELref_count + " ystar: " + he.ystar);
		// ntry++;

		/* Now search linear list of halfedges for the correct one */
		if (he == ELleftend || (he != ELrightend && right_of(he, p))) {
			// System.err.println ("ELleftbnd: loop1");
			do {
				he = he.ELright;
			} while (he != ELrightend && right_of(he, p));

			he = he.ELleft;
		} else {
			do {
				he = he.ELleft;
			} while (he != ELleftend && !right_of(he, p));
		}

		// Update hash table and reference counts
		if (bucket > 0 && bucket < ELhashsize - 1) {
			if (ELhash[bucket] != null) {
				ELhash[bucket].ELref_count--;
			}
			ELhash[bucket] = he;
			ELhash[bucket].ELref_count++;
		}
		/*
		 * public int ELref_count; public int ELpm; public Site vertex; public
		 * double ystar; public Halfedge PQnext;
		 */
		// System.err.println ("ELleftbnd: elpm: " + he.ELpm + " ref_count: " +
		// he.ELref_count + " ystar: " + he.ystar);
		return he;
	}

	private void ELdelete(Halfedge he) {
		he.ELleft.ELright = he.ELright;
		he.ELright.ELleft = he.ELleft;
		he.ELedge = DELETED;
	}

	private static Halfedge ELright(Halfedge he) {
		return (he.ELright);
	}

	private static Halfedge ELleft(Halfedge he) {
		return (he.ELleft);
	}

	private Site leftreg(Halfedge he) {
		if (he.ELedge == null) {
			return bottomsite;
		}
		return (he.ELpm == le ? he.ELedge.reg[le] : he.ELedge.reg[re]);
	}

	private Site rightreg(Halfedge he) {
		if (he.ELedge == null) {
			// System.err.println ("rightreg..returning bottomesite");
			return bottomsite;
		}
		// System.err.println ("rightreg..returning other");
		return (he.ELpm == le ? he.ELedge.reg[re] : he.ELedge.reg[le]);
	}

	// ////////////////////////////////////////////////////////////////////////
	// voronoi.c
	//
	private void voronoi(boolean triangulated) {

		Site newsite, bot, top, temp, p;
		Site v;

		Point2d newintstar = new Point2d(); // perhaps no need to allocate?
		int pm;

		Halfedge lbnd, rbnd, llbnd, rrbnd, bisector;
		Edge e;

		PQinitialize();
		bottomsite = nextone();
		// out_site (bottomsite);
		ELinitialize();

		newsite = nextone();

		while (true) {

			if (!PQempty()) {
				newintstar = PQ_min();
			}

			if (newsite != null
					&& (PQempty() || newsite.coord.compareTo(newintstar) < 0)) {

				// new site is smallest
				// out_site(newsite);
				// System.err.println ("new site is smallest");

				lbnd = ELleftbnd(newsite.coord);
				rbnd = ELright(lbnd);
				bot = rightreg(lbnd);

				e = bisect(bot, newsite);
				bisector = HEcreate(e, le);

				ELinsert(lbnd, bisector);

				if ((p = intersect(lbnd, bisector, null)) != null) {
					PQdelete(lbnd);
					PQinsert(lbnd, p, p.dist(newsite));
				}

				lbnd = bisector;
				bisector = HEcreate(e, re);
				ELinsert(lbnd, bisector);

				if ((p = intersect(bisector, rbnd, null)) != null) {
					PQinsert(bisector, p, p.dist(newsite));
				}

				newsite = nextone();
			}

			// intersection is smallest
			else if (!PQempty()) {

				lbnd = PQextractmin();
				llbnd = ELleft(lbnd);
				rbnd = ELright(lbnd);
				rrbnd = ELright(rbnd);
				bot = leftreg(lbnd);
				top = rightreg(rbnd);
				// out_triple (bot, top, rightreg (lbnd));
				v = lbnd.vertex;

				makevertex(v);

				endpoint(lbnd.ELedge, lbnd.ELpm, v);
				endpoint(rbnd.ELedge, rbnd.ELpm, v);

				ELdelete(lbnd);
				PQdelete(rbnd);
				ELdelete(rbnd);

				pm = le;

				if (bot.coord.y > top.coord.y) {
					temp = bot;
					bot = top;
					top = temp;
					pm = re;
				}

				e = bisect(bot, top);
				bisector = HEcreate(e, pm);
				ELinsert(llbnd, bisector);
				endpoint(e, re - pm, v);
				deref(v);

				if ((p = intersect(llbnd, bisector, null)) != null) {
					PQdelete(llbnd);
					PQinsert(llbnd, p, p.dist(bot));
				}

				if ((p = intersect(bisector, rrbnd, null)) != null) {
					PQinsert(bisector, p, p.dist(bot));
				}
			} else {
				break;
			}

		} // end while (true)

		// print out the edges (here we store them in 'mEdges')
		for (lbnd = ELright(ELleftend); lbnd != ELrightend; lbnd = ELright(lbnd)) {
			e = lbnd.ELedge;
			out_ep(e);
		}

	}

	public TreeMap<Point2d, Site> getmSites() {
		if (currentlyRecomputing) {
			return temp_mSites;
		} else {
			recompute();
			return mSites;
		}
	}

	public Vector<Line2d> getmEdges() {
		if (currentlyRecomputing) {
			return temp_mEdges;
		} else {
			recompute();
			return mEdges;
		}
	}

	public Vector<Point2d> getmVertices() {
		if (currentlyRecomputing) {
			return temp_mVertices;
		} else {
			recompute();
			return mVertices;
		}
	}

	/**
	 * Remove all contacts who's timestamp is smaller the given value
	 * 
	 * @param removeBefore
	 */
	public synchronized void removeStaleContacts(long removeBefore) {
		Set<Integer> ids = timestamps.keySet();
		LinkedList<Integer> toRemove = new LinkedList<Integer>();

		for (Integer id : ids) {
			if (id.compareTo(owningNode.getUniqueValue()) == 0) {
				continue;
			}
			long timestamp = timestamps.get(id);

			if (timestamp < removeBefore) {
				toRemove.add(id);
			}
		}

		for (Integer id : toRemove) {
			remove(id);
		}

	}

}
