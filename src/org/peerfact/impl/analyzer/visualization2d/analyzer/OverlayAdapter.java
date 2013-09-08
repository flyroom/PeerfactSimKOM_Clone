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
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.generic.FCFSPartitionRingPositioner;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayUniverseMetric;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Inspects an overlay and identifies events and state changes relevant for its
 * visualization.
 * 
 * See documentation on <http://www.peerfact.org>.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 23.10.2008
 * 
 */
public abstract class OverlayAdapter {

	final Set<Class<? extends Object>> overlayImpls = new LinkedHashSet<Class<? extends Object>>();

	protected static Logger log = SimLogger.getLogger(OverlayAdapter.class);

	VisAnalyzer analyzer = null;

	/**
	 * Adds a class of OverlayNodes or OverlayMessages the specified listener is
	 * interested in.
	 * 
	 * @param overlayImpl
	 */
	protected final void addOverlayImpl(Class<? extends Object> overlayImpl) {
		overlayImpls.add(overlayImpl);
	}

	/**
	 * Adds an OverlayNode metric that will be dynamically loaded with this
	 * OverlayAdapter.
	 * 
	 * @param m
	 */
	protected static final void addOverlayNodeMetric(
			Class<? extends OverlayNodeMetric> m) {
		MetricsBase.getDynamicMetricsPack().getNodes().addMetric(m);
	}

	/**
	 * Adds an OverlayEdge metric that will be dynamically loaded with this
	 * OverlayAdapter.
	 * 
	 * @param m
	 */
	protected static final void addOverlayEdgeMetric(
			Class<? extends OverlayEdgeMetric> m) {
		MetricsBase.getDynamicMetricsPack().getEdges().addMetric(m);
	}

	/**
	 * Adds an OverlayUniverse metric that will be dynamically loaded with this
	 * OverlayAdapter.
	 * 
	 * @param m
	 */
	protected static final void addOverlayUniverseMetric(
			Class<? extends OverlayUniverseMetric> m) {
		MetricsBase.getDynamicMetricsPack().getUniverse().addMetric(m);
	}

	/**
	 * Paints an overlay edge
	 * 
	 * @param from
	 * @param to
	 */
	protected EdgeHandle addEdge(NetID from, NetID to) {
		return addEdge(from, to, null, Color.GREEN, "");
	}

	/**
	 * Paints an overlay edge
	 * 
	 * @param from
	 * @param to
	 * @param color
	 * @param typeName
	 */
	protected EdgeHandle addEdge(NetID from, NetID to, Color color,
			String typeName) {
		return this.addEdge(from, to, null, color, typeName);
	}

	/**
	 * Paints an overlay edge
	 * 
	 * @param attributes
	 * @param from
	 * @param to
	 * @param color
	 * @param typeName
	 */
	protected EdgeHandle addEdge(NetID from, NetID to,
			Map<String, Serializable> attributes, Color color, String typeName) {
		Map<String, Serializable> tempAttributes = attributes;
		if (tempAttributes == null) {
			tempAttributes = new LinkedHashMap<String, Serializable>();
		}

		tempAttributes.put("overlay", this.getOverlayName());
		tempAttributes.put("type", typeName);
		return getTranslator()
				.overlayEdgeAdded(from, to, color, tempAttributes);
	}

	/**
	 * Paints a flash overlay edge
	 * 
	 * @param from
	 * @param to
	 */
	protected void flashEdge(NetID from, NetID to) {
		Map<String, Serializable> attributes = new LinkedHashMap<String, Serializable>();
		this.flashEdge(from, to, attributes, Color.GREEN, null);
	}

	/**
	 * Paints a flashing overlay edge
	 * 
	 * @param from
	 * @param to
	 */
	protected void flashEdge(NetID from, NetID to, Color color, String typeName) {
		Map<String, Serializable> attributes = new LinkedHashMap<String, Serializable>();
		this.flashEdge(from, to, attributes, color, typeName);
	}

	/**
	 * Paints a flashing overlay edge, along with its class name.
	 * 
	 * @param from
	 * @param to
	 */
	protected void flashEdge(NetID from, NetID to, Color color,
			String typeName, Class<?> msgClass) {
		Map<String, Serializable> attributes = new LinkedHashMap<String, Serializable>();
		attributes.put("msg_class", msgClass.getSimpleName());
		this.flashEdge(from, to, attributes, color, typeName);
	}

	/**
	 * Paints an overlay edge, along with specified attributes and class name.
	 * 
	 * @param attributes
	 * @param from
	 * @param to
	 * @param color
	 * @param typeName
	 */
	protected void flashEdge(NetID from, NetID to,
			Map<String, Serializable> attributes, Color color, String typeName) {

		if (!getTranslator().nodeExists(from)) {
			this.analyzer.checkHost(from);
		}

		if (!getTranslator().nodeExists(to)) {
			this.analyzer.checkHost(to);
		}

		attributes.put("overlay", this.getOverlayName());
		attributes.put("type", typeName);
		getTranslator().overlayEdgeFlash(from, to, color, attributes);
	}

	/**
	 * Removes an edge.
	 * 
	 * @param from
	 * @param to
	 */
	protected void removeEdge(VisOverlayEdge e) {
		getTranslator().overlayEdgeRemoved(e);
	}

	/**
	 * 
	 * If this OverlayAdapter is interested in the specified class of event or
	 * overlay implementation
	 * 
	 * @param overlayImpl
	 */
	public boolean isDedicatedOverlayImplFor(Class<? extends Object> overlayImpl) {

		/*
		 * Check whether the given class object is an assignable form of one of
		 * the overlay implementations. That includes the cases that the classes
		 * are the same and the given overlay implementation is any kind of a
		 * subclass of one of the overlay implementations.
		 */
		for (Class<? extends Object> clazz : overlayImpls) {
			if (clazz.isAssignableFrom(overlayImpl)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the name of the overlay of the node.
	 * 
	 * @return
	 */
	public abstract String getOverlayName();

	/**
	 * Returns a new positioner for the nodes that implement overlays supported
	 * by this OverlayAdapter. If null is returned, that means that this
	 * OverlayAdapter does not support schematic positioning.
	 * 
	 * <b>Should be overwritten for more specified behavior!</b>
	 * 
	 * @return
	 */
	@SuppressWarnings("static-method")
	public SchematicPositioner getNewPositioner() {
		return new FCFSPartitionRingPositioner();
	}

	/**
	 * Handles the entry of a host to the scenery BEFORE he is added to the
	 * scenario. Only here you can set attributes that can be set along with the
	 * node's spawn. Edges between the node and other nodes have to be set after
	 * the creation process (handleNewHostAfter). If you need to set attributes
	 * later, call the translator's nodeAttributeChanged() command.
	 * 
	 * @param host
	 * @param nodeOverlayImpl
	 * @param attributes
	 * @param translator
	 */
	public abstract void handleNewHost(Map<String, Serializable> attributes,
			Host host, OverlayNode<?, ?> overlayNode);

	/**
	 * Handles the entry of a host to the scenery AFTER he is added to the
	 * scenario. Only here you can add edges between the host and other hosts.
	 * If you want to add attributes to this host, do it by using handleNewHost.
	 * 
	 * @param host
	 * @param nodeOverlayImpl
	 * @param translator
	 */
	public abstract void handleNewHostAfter(Host host,
			OverlayNode<?, ?> overlayNode);

	/**
	 * Handles the leaving of a host from the network.
	 * 
	 * @param host
	 * @param translator
	 */
	public abstract void handleLeavingHost(Host host);

	/**
	 * Handles an overlay message from a host to another host.
	 * 
	 * Warning: The host parameter sometimes is null, this seems to be a bug in
	 * PeerfactSim.KOM.
	 * 
	 * @param omsg
	 * @param from
	 * @param to
	 */
	public abstract void handleOverlayMsg(Message omsg, Host from,
			NetID fromID, Host to, NetID toID);

	/**
	 * Returns the bootstrap manager for the specified overlay implementation.
	 * 
	 * You can return an object you want instead of the bootstrap manager, if it
	 * satisfies this formal condition:
	 * <b>node1.getBootstrapManager().equals(node2.getBootstrapManager()), iff
	 * the overlay nodes belong to the same network (instance of an overlay).
	 * 
	 * If you do not want to support multi-network-instances, just return null.
	 * 
	 * @param nd
	 * @return
	 */
	public abstract Object getBootstrapManagerFor(OverlayNode<?, ?> nd);

	/**
	 * Returns the translator
	 * 
	 * @param translator
	 */
	public Translator getTranslator() {
		return analyzer.getTranslator();
	}

	/**
	 * Sets the analyzer that uses this adapter-
	 * 
	 * @param analyzer
	 */
	public void setParentAnalyzer(VisAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * An operation was called by host. This method handles it.
	 * 
	 * @param host
	 * @param op
	 * @param finished
	 */
	public abstract void handleOperation(Host host, Operation<?> op,
			boolean finished);

}
