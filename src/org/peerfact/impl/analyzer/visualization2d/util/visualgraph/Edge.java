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

package org.peerfact.impl.analyzer.visualization2d.util.visualgraph;

import java.awt.Color;
import java.io.Serializable;

import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;


/**
 * Abstract edge of a graph(previously non-directional)
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TNode>
 * @param <TEdge>
 * @version 05/06/2011
 */
public abstract class Edge<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3009908846848647477L;

	/**
	 * Color of the edge
	 */
	protected static final java.awt.Color edgecolor = java.awt.Color.RED;

	protected TNode node1;

	protected TNode node2;

	private VisualGraph<TNode, TEdge> graph;

	private int priority = 10;

	public Edge(TNode a, TNode b) {
		this.node1 = a;
		this.node2 = b;
	}

	public void setGraph(VisualGraph<TNode, TEdge> graph) {
		this.graph = graph;
		node1.edges.add(this);
		node2.edges.add(this);
	}

	public void unsetGraph() {
		this.graph = null;
		node1.edges.remove(this);
		node2.edges.remove(this);
	}

	public VisualGraph<TNode, TEdge> getGraph() {
		return this.graph;
	}

	public void setNodeA(TNode a) {
		this.node1 = a;
	}

	public void setNodeB(TNode b) {
		this.node2 = b;
	}

	public Node<TNode, TEdge> getNodeA() {
		return node1;
	}

	public Node<TNode, TEdge> getNodeB() {
		return node2;
	}

	public abstract void iterate(ModelIterator<?, ?, ?> it);

	public abstract Color getColor();

	/**
	 * Specifies whether the edge should be removed during the next cleanup
	 * process,
	 * 
	 * @see comment in Simple2DVisualization.paintDataModel
	 * @return
	 */
	public static boolean markedAsRemovable() {
		return false;
	}

	/**
	 * Returns the priority of the edge. Edges with <b>lower</b> priority are
	 * drawn preferred to edges with higher priorities. Default value is 10.
	 * 
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Sets the priority of the edge. Edges with <b>lower</b> priority are drawn
	 * preferred to edges with higher priorities. Default value is 10.
	 * 
	 * @return
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Returns whether the priority of this edge is higher than that of edge e.
	 * 
	 * @param e
	 * @return
	 */
	public boolean higherPriorityThan(Edge<TNode, TEdge> e) {
		return this.getPriority() > e.getPriority();
	}

	/*
	 * @Override public boolean equals(Object o) { if (o instanceof Edge) { Edge
	 * e = (Edge)o;
	 * 
	 * boolean equal = (e.getNodeA() == this.getNodeA() && e.getNodeB() ==
	 * this.getNodeB() || e.getNodeB() == this.getNodeA() && e.getNodeA() ==
	 * this.getNodeB());
	 * 
	 * //An edge is equal to another if it uses the same node. //Only to be used
	 * for drawing.
	 * 
	 * return equal; } return false; }
	 * 
	 * @Override public int hashCode() {
	 * 
	 * if (node1 == null || node2 == null) return super.hashCode(); return
	 * node1.hashCode() + node2.hashCode(); // consistent with equals, as A + B
	 * = B + A and thus have the opposite edges of the same hashCode.}
	 */
}
