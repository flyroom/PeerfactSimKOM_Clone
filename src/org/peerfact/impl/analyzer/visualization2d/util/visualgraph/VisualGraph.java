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

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;


/**
 * <b>"Hyper Generic" Graph.</b>
 * 
 * The passed type must extend the abstract classes Node and Edge. The advantage
 * of using this graph is that in fact casts are never done.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <TNode>
 * @param <TEdge>
 * 
 * @version 05/06/2011
 */

public class VisualGraph<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2796546862703465617L;

	public Vector<Node<TNode, TEdge>> nodes = new Vector<Node<TNode, TEdge>>();

	public Vector<Edge<TNode, TEdge>> edges = new Vector<Edge<TNode, TEdge>>();

	public Vector<VisRectangle> rectangles = new Vector<VisRectangle>();

	public void addNode(Node<TNode, TEdge> node) {
		nodes.add(node);
		node.setGraph(this);
	}

	public void addEdge(Edge<TNode, TEdge> edge) {
		synchronized (edges) {
			edges.add(edge);
			edge.setGraph(this);
		}
	}

	public void removeNode(Node<TNode, TEdge> node) {

		synchronized (nodes) {
			synchronized (edges) {
				// Remove all edges which are connected to node
				Collection<Edge<TNode, TEdge>> tmpEdges = new Vector<Edge<TNode, TEdge>>();
				tmpEdges.addAll(node.edges);

				for (Edge<TNode, TEdge> e : tmpEdges) {
					removeEdge(e);
				}
				nodes.remove(node);
				node.unsetGraph();
			}
		}
	}

	public void removeEdge(Edge<TNode, TEdge> edge) {
		synchronized (edges) {
			edges.remove(edge);
			edge.unsetGraph();
		}
	}

	public void addRectangle(VisRectangle rect) {
		rectangles.add(rect);
	}

	public void removeRectangle(VisRectangle rect) {
		rectangles.remove(rect);
	}

	/**
	 * Iterates over the elements of the graph nodes first, then edges.
	 * 
	 * @param it
	 */

	public void iterate(ModelIterator<?, ?, ?> it, MetricObject selectedObject) {
		this.iterateRectangles(it);
		if (!it.shallStop()) {
			this.iterateNodes(it);
		}
		if (!it.shallStop()) {
			this.iterateEdges(it);
		}
	}

	/**
	 * Iterates over the elements of the graph edges first, then nodes.
	 * 
	 * @param it
	 */

	public void iterateBottomTop(ModelIterator<?, ?, ?> it) {
		this.iterateRectangles(it);
		if (!it.shallStop()) {
			this.iterateEdges(it);
		}
		if (!it.shallStop()) {
			this.iterateNodes(it);
		}
	}

	/**
	 * Iterates over all Edges
	 * 
	 * @param it
	 */
	public void iterateEdges(ModelIterator<?, ?, ?> it) {

		synchronized (edges) {
			for (Edge<TNode, TEdge> e : edges) {
				e.iterate(it);
				if (it.shallStop()) {
					break;
				}
			}
		}

	}

	/**
	 * Iterates over all Nodes
	 * 
	 * @param it
	 */
	public synchronized void iterateNodes(ModelIterator<?, ?, ?> it) {
		synchronized (nodes) {
			for (Node<TNode, TEdge> n : nodes) {
				n.iterate(it);
				if (it.shallStop()) {
					break;
				}
			}
		}
	}

	/**
	 * Iterates over all Rectangles
	 * 
	 * @param it
	 */
	public synchronized void iterateRectangles(ModelIterator<?, ?, ?> it) {
		for (VisRectangle rect : rectangles) {
			rect.iterate(it);
			if (it.shallStop()) {
				break;
			}
		}
	}

	/**
	 * Resets the graph and its elements.
	 */
	public synchronized void reset() {
		for (Node<TNode, TEdge> n : nodes) {
			n.edges.clear();
		}
		nodes.clear();
		edges.clear();
	}

}
