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
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;
import org.peerfact.impl.analyzer.visualization2d.model.TypeObject;


/**
 * Edge (connection) in the overlay network.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VisOverlayEdge
		extends
		org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Edge<VisOverlayNode, VisOverlayEdge>
		implements TypeObject, MetricObject, AttributeObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6939096705519916055L;

	Hashtable<String, Serializable> attributes = new Hashtable<String, Serializable>();

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/model/OverlayEdgePermanent16_16.png");

	protected Color cl = Color.RED;

	int uniqueTypeIdentifier = -1;

	/**
	 * Edge of the overlays not specified for visualisation. Should only be
	 * generated from the visualization factory.
	 * 
	 * @param a
	 * @param b
	 */

	public VisOverlayEdge(VisOverlayNode a, VisOverlayNode b) {
		super(a, b);
	}

	/**
	 * Returns a set of metrics for that edge in the form they are bounded to
	 * the edge.
	 * 
	 * @see BoundMetric
	 * @return
	 */
	@Override
	public Vector<BoundMetric> getBoundMetrics() {
		Vector<BoundMetric> res = new Vector<BoundMetric>();
		for (OverlayEdgeMetric m : org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase
				.forOverlayEdges().getListOfAllMetrics()) {
			res.add(m.getBoundTo(this));
		}
		return res;
	}

	@Override
	public Color getColor() {
		return cl;
	}

	/**
	 * Sets the color of the edge in the visualization representation to a
	 * certain value.
	 * 
	 * @param c
	 */
	public void setColor(Color c) {
		this.cl = c;
	}

	/**
	 * Adds an attribute. <b>Attention! All attributes must be serializable!</b>
	 * 
	 * @param name
	 * @param value
	 */
	@Override
	public void insertAttribute(String name, Serializable value) {
		this.attributes.put(name, value);
	}

	/**
	 * Returns the attribute with the key <code>name</code>.
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
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

	@Override
	public void iterate(ModelIterator it) {
		it.overlayEdgeVisited(this);
	}

	@Override
	public String toString() {
		return node1.toString() + " - " + node2.toString();
	}

	/**
	 * Returns a unique identifier that identifies the type of message.
	 * Uncategorized messages are grouped in one type!
	 * 
	 * @return
	 */
	@Override
	public int getUniqueTypeIdentifier() {

		if (uniqueTypeIdentifier == -1) {

			uniqueTypeIdentifier = this.getClass().hashCode()
					+ getHashCodeForAttr("type")
					+ getHashCodeForAttr("overlay")
					+ getHashCodeForAttr("msg_class");

		}

		return uniqueTypeIdentifier;
	}

	/**
	 * Returns the name of the type.
	 * 
	 * @return
	 */
	@Override
	public String getTypeName() {
		String name = getAttributes().get("type").toString();
		if (name == null) {
			name = getAttributes().get("msg_class").toString();
			if (name == null) {
				name = getAttributes().get("overlay").toString();
				if (name == null) {
					name = "Uncategorized";
				}
			}
		}
		return name;
	}

	protected int getHashCodeForAttr(String attr) {
		Object attrib = getAttributes().get(attr);
		return (attrib == null) ? 0 : attrib.hashCode();
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}

}
