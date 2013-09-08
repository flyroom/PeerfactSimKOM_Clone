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

package org.peerfact.impl.analyzer.visualization2d.analyzer;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.network.NetID;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;
import org.peerfact.impl.analyzer.visualization2d.model.ModelFilter;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.model.events.AttributesChanged;
import org.peerfact.impl.analyzer.visualization2d.model.events.EdgeAdded;
import org.peerfact.impl.analyzer.visualization2d.model.events.EdgeFlashing;
import org.peerfact.impl.analyzer.visualization2d.model.events.EdgeRemoved;
import org.peerfact.impl.analyzer.visualization2d.model.events.Event;
import org.peerfact.impl.analyzer.visualization2d.model.events.MessageSent;
import org.peerfact.impl.analyzer.visualization2d.model.events.NodeAdded;
import org.peerfact.impl.analyzer.visualization2d.model.events.NodeRemoved;
import org.peerfact.impl.analyzer.visualization2d.model.events.RectangleAdded;
import org.peerfact.impl.analyzer.visualization2d.model.events.RectangleRemoved;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.FlashOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;
import org.peerfact.impl.analyzer.visualization2d.util.MultiMap;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Node;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.PositionInfo;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.VisRectangle;
import org.peerfact.impl.simengine.Simulator;


/**
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @edit Leo Nobach
 * 
 *       My idea: class receives information from Analyzers and passes them to
 *       the Timeline
 * 
 * See documentation on <http://www.peerfact.org>.
 * @version 05/06/2011
 */
public class Translator {

	/**
	 * Collection of all currently existing visualization nodes.
	 * 
	 * Is necessary to determine the appropriate instance of "AOverlayNode"
	 * using using the NetID in case of an absence of nodes. Only with this
	 * instance node can be removed from visualization.
	 */
	private final LinkedHashMap<NetID, VisOverlayNode> visNodes;

	/**
	 * For every node the edges they are connected with.
	 */
	private final MultiMap<Node<?, ?>, VisOverlayEdge> visEdges;

	/**
	 * Collection of all currently existing Rectangles
	 */
	private final LinkedHashMap<String, VisRectangle> rectangles;

	/**
	 * Timeline to store the performed actions
	 */
	private final EventTimeline timeline;

	private final ModelFilter filter;

	public Translator() {

		Controller.loadModelBackend(new VisDataModel("Unnamed Recording"));

		// Get timeline by Controller
		timeline = Controller.getTimeline();
		filter = Controller.getModel().getFilter();

		visNodes = new LinkedHashMap<NetID, VisOverlayNode>();

		visEdges = new MultiMap<Node<?, ?>, VisOverlayEdge>();

		rectangles = new LinkedHashMap<String, VisRectangle>();
	}

	/**
	 * Sets upper bound for appearing coordinates. This is necessary in order to
	 * visualize the scale correctly.
	 * 
	 * @param maxX
	 * @param maxY
	 */
	public static void setUpperBoundForCoordinates(float maxX, float maxY) {
		Coords oldBound = Controller.getModel().getUpperBounds();

		// would only renew Bound if it changes
		if (oldBound.x != maxX || oldBound.y != maxY) {
			Controller.getModel().setUpperBounds(new Coords(maxX, maxY));
		}
	}

	/**
	 * Determines the lower bound of the appearing coordinates the visualization
	 * interface.
	 * 
	 * @return
	 */
	public static Coords getLowerBoundForCoordinates() {
		return Controller.getModel().getLowerBounds();
	}

	/**
	 * Sets lower bound for appearing coordinates. This is necessary in order to
	 * visualize the scale correctly.
	 * 
	 * @param maxX
	 * @param maxY
	 */
	public static void setLowerBoundForCoordinates(float maxX, float maxY) {
		Coords oldBound = Controller.getModel().getLowerBounds();

		// would only renew Bound if it changes
		if (oldBound.x != maxX || oldBound.y != maxY) {
			Controller.getModel().setLowerBounds(new Coords(maxX, maxY));
		}
	}

	/**
	 * Determines the upper bound of the emerging coordinates the visualization
	 * interface
	 * 
	 * @return
	 */
	public static Coords getUpperBoundForCoordinates() {
		return Controller.getModel().getUpperBounds();
	}

	/**
	 * Lets an edge flash for a short time. Then the edge does not need be
	 * removed. edgeColor specifies the color of the edge attributes and the
	 * attributes of the edge.
	 * 
	 * @param from
	 * @param to
	 */
	public void overlayEdgeFlash(NetID from, NetID to) {
		overlayEdgeFlash(from, to, Color.red,
				new LinkedHashMap<String, Serializable>());
	}

	/**
	 * Lets an edge flash for a short time. Then the edge does not need be
	 * removed. edgeColor specifies the color of the edge attributes and the
	 * attributes of the edge.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 */
	public void overlayEdgeFlash(NetID from, NetID to, Color edgeColor) {
		overlayEdgeFlash(from, to, edgeColor,
				new LinkedHashMap<String, Serializable>());
	}

	/**
	 * Lets an edge flashes for a short time. Then the edge does not need be
	 * removed. edgeColor specifies the color of the edge attributes and the
	 * attributes of the edge.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 * @param attributes
	 */
	public void overlayEdgeFlash(NetID from, NetID to, Color edgeColor,
			Map<String, Serializable> attributes) {

		if (visNodes.containsKey(from) && visNodes.containsKey(to)) {

			FlashOverlayEdge edge = new FlashOverlayEdge(visNodes.get(from),
					visNodes.get(to));
			edge.setColor(edgeColor);

			for (String elem : attributes.keySet()) {
				edge.insertAttribute(elem, attributes.get(elem));
			}

			// regist edge at the filter

			filter.registerType(edge);

			// save the creation of the edge in the Timeline
			Event event = new EdgeFlashing(edge);
			timeline.insertEvent(event, Simulator.getCurrentTime());
		}
	}

	/**
	 * Similar to overlayEdgeAdded(NetID from, NetID to, Color edgeColor). The
	 * only difference: a default color is selected.
	 * 
	 * @param from
	 * @param to
	 */
	public void overlayEdgeAdded(NetID from, NetID to) {

		overlayEdgeAdded(from, to, Color.red,
				new LinkedHashMap<String, Serializable>());
	}

	/**
	 * Forwards information about the new edges, including the attributes of the
	 * edge, to the timeline.
	 * 
	 * @param from
	 * @param to
	 * @param attributes
	 * @return the handle of the added edge
	 */
	public EdgeHandle overlayEdgeAdded(NetID from, NetID to,
			Map<String, Serializable> attributes) {
		return overlayEdgeAdded(from, to, Color.red, attributes);
	}

	/**
	 * Forwards information about the new edges, including the color of the
	 * edge, to the timeline.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 * @return the handle of the added edge
	 */
	public EdgeHandle overlayEdgeAdded(NetID from, NetID to, Color edgeColor) {
		return overlayEdgeAdded(from, to, edgeColor, null);
	}

	/**
	 * Forwards information about the new edges to the timeline. Edge can be
	 * controlled with the handle.
	 * 
	 * @param from
	 * @param to
	 * @param edgeColor
	 * @param attributes
	 * @return the handle of the added edge, <code>null</code> indicates that no
	 *         edge was added due to a missing adjacent node
	 */
	public EdgeHandle overlayEdgeAdded(NetID from, NetID to, Color edgeColor,
			Map<String, Serializable> attributes) {
		Map<String, Serializable> tempAttributes = attributes;
		if (tempAttributes == null) {
			tempAttributes = new LinkedHashMap<String, Serializable>();
		}

		if (visNodes.containsKey(from) && visNodes.containsKey(to)) {
			VisOverlayNode node1 = visNodes.get(from);
			VisOverlayNode node2 = visNodes.get(to);

			VisOverlayEdge edge = new VisOverlayEdge(node1, node2);

			edge.setColor(edgeColor);

			for (String elem : tempAttributes.keySet()) {
				edge.insertAttribute(elem, tempAttributes.get(elem));
			}

			// regist edge at the filter
			filter.registerType(edge);

			// generation of the node store in the timeline
			Event event = new EdgeAdded(edge);
			timeline.insertEvent(event, Simulator.getCurrentTime());

			visEdges.get(node1).add(edge);
			visEdges.get(node2).add(edge);

			return new EdgeHandle(edge, from, to);
		}

		return null;
	}

	/**
	 * Removes the given connection
	 * 
	 * @param from
	 * @param to
	 */
	public void overlayEdgeRemoved(VisOverlayEdge e) {

		if (e == null) {
			return;
		}

		Event event = new EdgeRemoved(e);

		// save the deletion in the timeline
		timeline.insertEvent(event, Simulator.getCurrentTime());

		if (visEdges.contains(e.getNodeA())) {
			visEdges.get(e.getNodeA()).remove(e);
		}
		if (visEdges.contains(e.getNodeB())) {
			visEdges.get(e.getNodeB()).remove(e);
		}

	}

	/**
	 * Forwards information about the new node, including a map of attributes,
	 * to the timeline.
	 * 
	 * @param id
	 * @param overlayGroupName
	 * @param coords
	 */
	public void overlayNodeAdded(NetID id, String overlayGroupName,
			PositionInfo coords) {
		overlayNodeAdded(id, overlayGroupName, coords,
				new LinkedHashMap<String, Serializable>());
	}

	/**
	 * Forwards information about the new node, including a map of attributes,
	 * to the timeline.
	 * 
	 * @param id
	 * @param overlayGroupName
	 * @param coords
	 * @param attributes
	 */
	public void overlayNodeAdded(NetID id, String overlayGroupName,
			PositionInfo coords, Map<String, Serializable> attributes) {

		if (!visNodes.containsKey(id)) { // Only insert, if not already
											// available

			// Create new nodes for visualization
			VisOverlayNode node = new VisOverlayNode(coords, overlayGroupName);

			for (String elem : attributes.keySet()) {
				node.insertAttribute(elem, attributes.get(elem));
			}

			// set attribute name of the node
			node.insertAttribute("NetID", id.toString());
			// changed by Leo because NetID is not serializable, but toString!

			// generation of the node store in the timeline
			Event event = new NodeAdded(node);
			timeline.insertEvent(event, Simulator.getCurrentTime());

			// Inserted a node in a LinkedHashMap
			visNodes.put(id, node);
		}
	}

	/**
	 * Passes information about the elimination of a node to the Timeline
	 * 
	 * @param id
	 */
	public void overlayNodeRemoved(NetID id) {
		if (visNodes.containsKey(id)) { // Only remove if present
			VisOverlayNode node = visNodes.get(id);

			// remove all edges to this node
			cleanUpEdgesForNodeDeletion(id);

			// save the deletion in the timeline
			Event event = new NodeRemoved(node);
			timeline.insertEvent(event, Simulator.getCurrentTime());

			// delete nodes from LinkedHashMap
			visNodes.remove(id);

		}
	}

	/**
	 * Removes all the edges to a given node and informs the timeline about this
	 * action.
	 * 
	 * This method has been created as an auxiliary method for deleting a node.
	 * 
	 * @param nodeID
	 *            NetID of the node which edges have to be removed
	 */
	private void cleanUpEdgesForNodeDeletion(NetID nodeID) {

		Node<?, ?> n = visNodes.get(nodeID);
		if (n == null) {
			return;
		}

		for (VisOverlayEdge e : visEdges.get(n)) {
			// insert event to the Timeline
			Event event = new EdgeRemoved(e);
			timeline.insertEvent(event, Simulator.getCurrentTime());
		}
	}

	/**
	 * Forwards the information about a transmitted message to the timeline.
	 * 
	 * @param from
	 * @param to
	 * @param messageType
	 */
	public void overlayMessageSent(NetID from, NetID to, String messageType) {
		if (visNodes.containsKey(from) && visNodes.containsKey(to)) {
			VisOverlayNode node1 = visNodes.get(from);
			VisOverlayNode node2 = visNodes.get(to);

			timeline.insertEvent(new MessageSent(node1, node2, messageType),
					Simulator.getCurrentTime());
		}
	}

	public void nodeAttributeChanged(NetID node, String key, Serializable value) {
		Map<String, Serializable> attrs = new LinkedHashMap<String, Serializable>();
		attrs.put(key, value);
		this.nodeAttributesChanged(node, attrs);
	}

	public void nodeAttributesChanged(NetID node,
			Map<String, Serializable> attrs) {
		VisOverlayNode visNode = visNodes.get(node);
		if (visNode != null) {
			timeline.insertEvent(new AttributesChanged(visNode, attrs),
					Simulator.getCurrentTime());
		}
	}

	/**
	 * Draws a rectangle on the visualization. This feature only makes sense
	 * when working with bitmap based visualizations where the zoom is fixed
	 * since the rectangles are painted with absolut pixel positions on the
	 * visualization.
	 * 
	 * In order to be able to remove the rectangle later, you have to provide a
	 * String which identifies the rectangle. This String is needed when calling
	 * the method "removeRectangle(String ID)".
	 * 
	 * @param point1
	 * @param point2
	 * @param color
	 * @param ID
	 */
	public void drawRectangle(Point point1, Point point2, Color color, String ID) {
		VisRectangle rect = new VisRectangle(point1, point2, color);
		RectangleAdded event = new RectangleAdded(rect);

		rectangles.put(ID, rect);

		timeline.insertEvent(event, Simulator.getCurrentTime());
	}

	/**
	 * Remove a rectangle that is identified by the provided String. This String
	 * was defined when adding the rectangle.
	 * 
	 * @param ID
	 * @param minTimeToShowEdge
	 */
	public void removeRectangle(String ID, int minTimeToShowEdge) {
		if (rectangles.containsKey(ID)) {
			VisRectangle rect = rectangles.get(ID);

			RectangleRemoved event = new RectangleRemoved(rect);

			timeline.insertEvent(event, Simulator.getCurrentTime()
					+ (minTimeToShowEdge * Simulator.SECOND_UNIT));

			rectangles.remove(ID);
		}
	}

	/**
	 * @param nodeID
	 * @return wheter the node already exists or not
	 */
	public boolean nodeExists(NetID nodeID) {
		return visNodes.containsKey(nodeID);
	}

	public static void notifyFinished() {
		Controller.init();
		Controller.connectModelToUI();
	}

	public class EdgeHandle {
		private final VisOverlayEdge e;

		private final NetID from;

		private final NetID to;

		EdgeHandle(VisOverlayEdge e, NetID from, NetID to) {
			this.e = e;
			this.from = from;
			this.to = to;
		}

		public NetID getFrom() {
			return from;
		}

		public NetID getTo() {
			return to;
		}

		/**
		 * Triggers the removal of this edge at the Translator
		 */
		public void remove() {
			overlayEdgeRemoved(e);
		}

		/*
		 * Implementation of equals and hashcode
		 */

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (obj.getClass() != this.getClass())) {
				return false;
			}

			EdgeHandle edge = (EdgeHandle) obj;
			return from.equals(edge.from) && to.equals(edge.to);
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 31 * hash + (null == from ? 0 : from.hashCode());
			hash = 31 * hash + (null == to ? 0 : to.hashCode());
			return hash;
		}

	}

}
