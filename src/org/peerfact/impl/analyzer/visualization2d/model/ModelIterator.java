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

package org.peerfact.impl.analyzer.visualization2d.model;

import org.peerfact.impl.analyzer.visualization2d.model.overlay.FlashOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Edge;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Node;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.VisRectangle;

/**
 * Lässt sich über alle Elemente des Datenmodells iterieren, erst über Knoten,
 * dann über Kanten. Kann die Iteration stoppen.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface ModelIterator<TNode extends Node<TNode, TEdge>, TEdge extends Edge<TNode, TEdge>, TFlashEdge extends Edge<TNode, TEdge>> {

	/**
	 * Returns whether the iterator should be stopped at the next opportunity.
	 * 
	 * @return
	 */
	public boolean shallStop();

	/**
	 * Iterate only over the nodes and edges that have the highest priority?
	 * 
	 * @see VisOverlayEdge
	 * @return
	 */
	public boolean onlyHighestPrio();

	public void overlayEdgeVisited(TEdge edge);

	public void overlayNodeVisited(TNode node);

	public void rectangleVisited(VisRectangle rect);

	/**
	 * Call when visiting a FlashOverlayEdge
	 * 
	 * @see FlashOverlayEdge
	 * @param e
	 */
	public void flashOverlayEdgeVisited(TFlashEdge e);

}
