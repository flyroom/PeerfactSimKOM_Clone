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

package org.peerfact.impl.analyzer.visualization2d.model.overlay;

import java.awt.Color;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.PositionInfo;


/**
 * Node (peer) in the overlay network.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class VisOverlayNode
		extends
		org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Node<VisOverlayNode, VisOverlayEdge>
		implements MetricObject, AttributeObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1932483666858398117L;

	Hashtable<String, Serializable> attributes = new Hashtable<String, Serializable>();

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/model/OverlayNode16_16.png");

	/**
	 * Name of the node
	 */
	String name;

	public VisOverlayNode(PositionInfo pos, String name) {
		super(pos);
		this.name = name;
	}

	/**
	 * Returns the name of the node, used in the visualization.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds an attribute. <b> Attention! All attributes must be
	 * serializable!</b>
	 * 
	 * @param attributeName
	 * @param value
	 */
	@Override
	public void insertAttribute(String attributeName, Serializable value) {
		this.attributes.put(attributeName, value);
	}

	@Override
	public Object getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	/**
	 * Returns a map of all attributes.
	 * 
	 * @return
	 */
	@Override
	public Map<String, Serializable> getAttributes() {
		return attributes;
	}

	/**
	 * Sets a name for this node to be used in the visualisation.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a set of metrics for this node in the form they are bounded to
	 * the node.
	 * 
	 * @return
	 */
	@Override
	public Vector<BoundMetric> getBoundMetrics() {
		Vector<BoundMetric> res = new Vector<BoundMetric>();
		for (OverlayNodeMetric m : org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase
				.forOverlayNodes().getListOfAllMetrics()) {
			res.add(m.getBoundTo(this));
		}
		return res;
	}

	@Override
	public Color getColor() {
		return Color.DARK_GRAY;
	}

	@Override
	public void iterate(ModelIterator it) {
		it.overlayNodeVisited(this);
	}

	@Override
	public String toString() {
		return this.getName() + "(" + this.getAttribute("NetID") + ")";
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}

}
