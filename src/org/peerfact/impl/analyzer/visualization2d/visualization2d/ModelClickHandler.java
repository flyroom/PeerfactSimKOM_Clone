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

package org.peerfact.impl.analyzer.visualization2d.visualization2d;

import java.awt.Point;

import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelFilter;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.FlashOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Node;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.VisRectangle;


/**
 * Iterator over the model, which checks whether and when clicked on the model.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class ModelClickHandler implements
		ModelIterator<VisOverlayNode, VisOverlayEdge, FlashOverlayEdge> {

	Point clickPoint;

	Point width_height;

	MetricObject clickedObject;

	private final Simple2DVisualization vis;

	public ModelClickHandler(Point clickPoint, Point width_height,
			Simple2DVisualization vis) {
		super();
		this.clickPoint = clickPoint;
		this.width_height = width_height;
		this.vis = vis;
	}

	/**
	 * "Clicks" into the visualization window and returns the object that was
	 * clicked, otherwise <b>null</b>
	 * 
	 * @param clickedPoint
	 */
	public MetricObject getObjectFromClick(Point clickedPoint) {

		return clickedObject;

	}

	@Override
	public boolean shallStop() {
		return clickedObject != null;
	}

	/*
	 * EDGES
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	@Override
	public void overlayEdgeVisited(VisOverlayEdge edge) {
		if (getFilter().typeActivated(edge)
				&& overlayEdgeClicked(edge, clickPoint)) {
			clickedObject = edge;
		}

	}

	@Override
	public void flashOverlayEdgeVisited(FlashOverlayEdge edge) {
		this.overlayEdgeVisited(edge);
	}

	/**
	 * Area around the line that can be clicked on, to select it in pixels.
	 */
	static final int click_distance = 8;

	public boolean overlayEdgeClicked(VisOverlayEdge e, Point clickedPoint) {

		return org.peerfact.impl.analyzer.visualization2d.util.visualgraph.GraphMath
				.calculateDistanceFromLine(
						getPositionInWindow(getSelectedNodePosition(e
								.getNodeA())),
						getPositionInWindow(getSelectedNodePosition(e
								.getNodeB())), clickedPoint, click_distance);

	}

	/*
	 * NODES
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	/**
	 * Click-radius
	 */
	final static int CLICK_RADIUS = Painter.NODE_RADIUS + 4;

	@Override
	public void overlayNodeVisited(VisOverlayNode node) {
		if (overlayNodeClicked(node, clickPoint)) {
			clickedObject = node;
		}

	}

	public boolean overlayNodeClicked(VisOverlayNode node, Point clickedPoint) {
		return clickedPoint.distanceSq(this
				.getPositionInWindow(getSelectedNodePosition(node))) < CLICK_RADIUS
				* CLICK_RADIUS;
	}

	/*
	 * Rectangle
	 * ----------------------------------------------------------------
	 * ----------
	 * ----------------------------------------------------------------
	 * ----------------
	 */

	@Override
	public void rectangleVisited(VisRectangle rect) {
		// Nichts zu tun
	}

	/*
	 * UTILS
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	/**
	 * Returns the position that was selected to draw the node.
	 */
	public Coords getSelectedNodePosition(Node<?, ?> node) {
		if (vis.schematicPositionSet()) {
			return node.getSchematicPosition();
		}
		return node.getTopologicalPosition();
	}

	/**
	 * Converts a topological position to a position in the visualization window
	 * (in pixels).
	 * 
	 * @param topologicalPosition
	 */
	private Point getPositionInWindow(Coords topoPos) {
		return Simple2DVisualization.getPositionInWindow(topoPos, width_height,
				vis);
	}

	@Override
	public boolean onlyHighestPrio() {
		return true;
	}

	public static ModelFilter getFilter() {
		return Simple2DVisualization.getDataModel().getFilter();
	}

}
