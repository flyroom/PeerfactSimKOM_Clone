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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelFilter;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.FlashOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayEdge;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;
import org.peerfact.impl.analyzer.visualization2d.util.ColorToolkit;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.EdgeSp;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.GraphMath;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Node;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.VisRectangle;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.clicking.ClickBox;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.clicking.ClickBoxHandler;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Painter implements
		ModelIterator<VisOverlayNode, VisOverlayEdge, FlashOverlayEdge> {

	Graphics g;

	Point width_height;

	Simple2DVisualization vis;

	ClickBoxHandler clickBoxes = new ClickBoxHandler();

	Map<EdgeSp<Node<?, ?>>, Integer> edgesPerSpace = new LinkedHashMap<EdgeSp<Node<?, ?>>, Integer>();

	private final Color NODE_NAME_BG_COLOR = new Color(240, 240, 240);

	static final int METRICS_PADDING_X = 1;

	static final int METRICS_PADDING_Y = 1;

	static final int LABEL_PADDING_X = 60;

	static final int LABEL_PADDING_Y = 60;

	public Painter(Graphics g, Point width_height, Simple2DVisualization vis) {
		this.g = g;
		this.width_height = width_height;
		this.vis = vis;
	}

	@Override
	public void overlayEdgeVisited(VisOverlayEdge edge) {
		if (getFilter().typeActivated(edge)) {
			this.paintOverlayEdge(edge, DEFAULT_EDGE);
		}

	}

	@Override
	public void overlayNodeVisited(VisOverlayNode node) {
		this.paintOverlayNode(node);
	}

	@Override
	public void rectangleVisited(VisRectangle rect) {
		this.paintRectangle(rect);
	}

	@Override
	public void flashOverlayEdgeVisited(FlashOverlayEdge e) {
		if (getFilter().typeActivated(e)) {
			this.paintOverlayEdge(e, FLASH_EDGE);
		}
	}

	/*
	 * EDGES
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	static final float line_normal_stroke = 1f;

	static final float line_max_stroke = 4f;

	static final int DEFAULT_EDGE = 0;

	static final int FLASH_EDGE = 1;

	public void paintOverlayEdge(VisOverlayEdge e, int edgeType) {
		g.setColor(e.getColor());

		// Positions of the nodes is calculated in the window
		Point node1_win = getPositionInWindow(this.getSelectedNodePosition(e
				.getNodeA()));
		Point node2_win = getPositionInWindow(this.getSelectedNodePosition(e
				.getNodeB()));

		float strokeWidth = Simple2DVisualization.stroke_s.computeStrokeFor(vis
				.getStrokeMetric(), e, line_normal_stroke, line_max_stroke);

		((Graphics2D) g).setStroke(createStroke(strokeWidth, edgeType));

		EdgeSp<Node<?, ?>> space = new EdgeSp<Node<?, ?>>(e.getNodeA(),
				e.getNodeB());

		int spaceID = this.getSpaceCount(space);

		// log.debug("Drawn: " + e + " type: " + e.getTypeName() +
		// " sp: " + spaceID + " clr: " + e.getColor());

		Point offSetForMeta = drawEdgeLine(node1_win, node2_win, spaceID);

		this.incSpaceCount(space);

		Point metadataPoint = this.getEdgeLabelPosition(e);

		if (metadataPoint != null) {
			metadataPoint.translate(offSetForMeta.x, offSetForMeta.y);
			this.paintMetaData(e, g, metadataPoint, edgeType);
		}
	}

	private Point drawEdgeLine(Point node1, Point node2, int spaceID) {

		int beginPartOffsetH = 20;
		int beginPartOffsetV = 8;
		int arrowH = 8;
		int arrowV = 3;

		// draw with Bendpoints

		double dist = node1.distance(node2);

		double xUnit = (node2.x - node1.x) / dist;
		double yUnit = (node2.y - node1.y) / dist;

		int relBpX = (int) Math.rint(xUnit * beginPartOffsetH);
		int relBpY = (int) Math.rint(yUnit * beginPartOffsetH);
		double offsetX = yUnit * beginPartOffsetV;
		double offsetY = -xUnit * beginPartOffsetV;

		int orientation = (((spaceID & 1) == 1) ? 1 : -1)
				* getOrientation(node1, node2);
		int multOffsetX = (int) Math.rint(offsetX * orientation
				* ((spaceID + 1) / 2));
		int multOffsetY = (int) Math.rint(offsetY * orientation
				* ((spaceID + 1) / 2));

		Point bendpoint1 = new Point(node1.x + relBpX + multOffsetX, node1.y
				+ relBpY + multOffsetY);
		Point bendpoint2 = new Point(node2.x - relBpX + multOffsetX, node2.y
				- relBpY + multOffsetY);

		Point arrowStart = new Point((node1.x + node2.x) / 2 + multOffsetX,
				(node1.y + node2.y) / 2 + multOffsetY);

		int arrowEndX = (int) (yUnit * arrowV - xUnit * arrowH);
		int arrowEndY = (int) (-xUnit * arrowV - yUnit * arrowH);

		int arrowEnd2X = (int) (-yUnit * arrowV - xUnit * arrowH);
		int arrowEnd2Y = (int) (xUnit * arrowV - yUnit * arrowH);

		Point arrowEnd = new Point(arrowStart.x + arrowEndX, arrowStart.y
				+ arrowEndY);
		Point arrowEnd2 = new Point(arrowStart.x + arrowEnd2X, arrowStart.y
				+ arrowEnd2Y);

		g.drawLine(node1.x, node1.y, bendpoint1.x, bendpoint1.y);

		g.drawLine(bendpoint1.x, bendpoint1.y, bendpoint2.x, bendpoint2.y);

		g.drawLine(bendpoint2.x, bendpoint2.y, node2.x, node2.y);

		g.drawLine(arrowStart.x, arrowStart.y, arrowEnd.x, arrowEnd.y);

		g.drawLine(arrowStart.x, arrowStart.y, arrowEnd2.x, arrowEnd2.y);

		return new Point(multOffsetX, multOffsetY);
	}

	private static int getOrientation(Point a, Point b) {
		if (a.x == b.x) {
			return (a.y < b.y) ? -1 : 1;
		} else {
			return (a.x < b.x) ? -1 : 1;
		}
	}

	private static Stroke createStroke(float size, int edgeType) {
		if (edgeType == FLASH_EDGE) {
			return new BasicStroke(size, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 1.0f, new float[] { 5, 5 }, 0.0f);
		}
		return new BasicStroke(size, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 1.0f);
	}

	/**
	 * Returns the position of the edge labels. Can be NULL!
	 * 
	 * @param e
	 * @return
	 */
	private Point getEdgeLabelPosition(VisOverlayEdge e) {

		Coords centerPosition = new Coords(
				(this.getSelectedNodePosition(e.getNodeA()).x + this
						.getSelectedNodePosition(e.getNodeB()).x) / 2, (this
						.getSelectedNodePosition(e.getNodeA()).y + this
						.getSelectedNodePosition(e.getNodeB()).y) / 2);

		if (!isVisible(centerPosition)) {

			Coords aPosition = this.getSelectedNodePosition(e.getNodeA());
			Coords bPosition = this.getSelectedNodePosition(e.getNodeB());

			boolean aVisible = isVisible(aPosition);
			boolean bVisible = isVisible(bPosition);

			if (!aVisible && !bVisible) {
				return null;
			}

			if (aVisible && !bVisible || !aVisible && bVisible) {

				// Edges that lead out of the visible area get their label
				// painted
				// directly at the visible border.

				Point aPositionWnd = getPositionInWindow(aPosition);
				Point bPositionWnd = getPositionInWindow(bPosition);

				return GraphMath.getLineRectIntersection(aPositionWnd,
						bPositionWnd, getLabelBoundsRectangle());
			}
		}

		return getPositionInWindow(centerPosition);

	}

	private boolean isVisible(Coords c) {
		Point p = getPositionInWindow(c);

		return (p.x >= 0 && p.y >= 0 && p.x <= vis.getWidth() && p.y <= vis
				.getHeight());
	}

	/**
	 * Returns a rectangle, which returns the bounding box for drawing labels.
	 * 
	 * @return
	 */
	private Rectangle getLabelBoundsRectangle() {
		return new Rectangle(LABEL_PADDING_X, LABEL_PADDING_Y, width_height.x
				- LABEL_PADDING_X * 2, width_height.y - LABEL_PADDING_Y * 2);
	}

	/**
	 * Draw more things for the edge. relativeTo specifies the center of the
	 * line.
	 * 
	 * @param graphic
	 * @param relativeTo
	 * @param edgeType
	 */
	private void paintMetaData(VisOverlayEdge e, Graphics graphic,
			Point relativeTo,
			int edgeType) {

		int line_diff = graphic.getFontMetrics().getHeight();

		int i = 0;
		for (OverlayEdgeMetric m : MetricsBase.forOverlayEdges()
				.getListOfActivatedMetrics()) {
			String mValue = m.getValue(e);
			if (mValue != null) {
				drawTextOnRect(graphic,
						ColorToolkit.getLightColorFor(m.getColor()),
						m.getColor(), mValue, relativeTo.x + 10, relativeTo.y
								- i * line_diff, e);
				i++;
			}
		}

	}

	/*
	 * NODES
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */
	/**
	 * Color of the node
	 */
	protected static final java.awt.Color node_text_color = java.awt.Color.BLACK;

	/**
	 * Minimum radius of the representation of the node
	 */
	static final int NODE_RADIUS = 5;

	/**
	 * Maximum radius of the representation of the node
	 */
	static final int NODE_RADIUS_MAX = 10;

	/**
	 * Additional click-radius
	 */
	static final int CLICK_RADIUS = 4;

	public void paintOverlayNode(VisOverlayNode node) {

		g.setColor(node.getColor());
		Point position_win = getPositionInWindow(this
				.getSelectedNodePosition(node));

		int size = Simple2DVisualization.nodesize_s.computeNodeSizeFor(vis
				.getNodeSizeMetric(), node, NODE_RADIUS, NODE_RADIUS_MAX);

		g.fillOval(position_win.x - size, position_win.y - size, size * 2,
				size * 2);
		this.paintMetaData(node, g, position_win, size);

		int totalRad = NODE_RADIUS + CLICK_RADIUS;

		ClickBox cBox = new ClickBox(position_win.x - totalRad, position_win.y
				- totalRad, 2 * totalRad, 2 * totalRad);

		cBox.setLinkedObj(node);

		this.clickBoxes.addBox(cBox);

	}

	protected void paintMetaData(VisOverlayNode node, Graphics graphic,
			Point relativeTo, int size) {

		drawTextOnRect(graphic, NODE_NAME_BG_COLOR, node_text_color,
				node.getName(),
				relativeTo.x + size + 2, relativeTo.y + size, node);

		int line_diff = graphic.getFontMetrics().getHeight();

		int i = 0;
		for (OverlayNodeMetric m : MetricsBase.forOverlayNodes()
				.getListOfActivatedMetrics()) {
			String mValue = m.getValue(node);
			if (mValue != null) {
				drawTextOnRect(graphic,
						ColorToolkit.getLightColorFor(m.getColor()),
						m.getColor(), mValue, relativeTo.x - size, relativeTo.y
								- size - 5 - i * line_diff, node);
				i++;
			}

		}

	}

	/**
	 * Paints the given rectangle
	 * 
	 * @param rect
	 */
	public void paintRectangle(VisRectangle rect) {
		g.setColor(rect.getColor());

		g.drawRect(rect.getPoint1().x + Simple2DVisualization.GRAPH_BOUNDS,
				rect.getPoint1().y + Simple2DVisualization.GRAPH_BOUNDS, rect
						.getPoint2().x, rect.getPoint2().y);
	}

	/*
	 * UTILS
	 * --------------------------------------------------------------------
	 * ------
	 * --------------------------------------------------------------------
	 * ------------
	 */

	/**
	 * Draws a rounded rectangle and some text into it. Creates a Clickbox if
	 * the passed object is not null.
	 */
	private void drawTextOnRect(Graphics g2, Color rectColor, Color textColor,
			String text, int x, int y, MetricObject clickObj) {

		final Color selRectColor = new Color(100, 100, 0);

		FontMetrics metrics = g2.getFontMetrics();

		Rectangle2D bl = metrics.getStringBounds(text, g2);

		g2.setColor(rectColor);

		g2.fillRoundRect(x - METRICS_PADDING_X + (int) bl.getX(), y
				- METRICS_PADDING_Y + (int) bl.getY(), METRICS_PADDING_X * 2
				+ (int) bl.getWidth(), METRICS_PADDING_X * 2
				+ (int) bl.getHeight(), 5, 5);

		g2.setColor(textColor);

		g2.drawString(text, x, y);

		if (clickObj != null) {

			if (vis.getSelectedObject() == clickObj) {

				g2.setColor(selRectColor);

				g2.drawRoundRect(x - METRICS_PADDING_X * 2 + (int) bl.getX(), y
						- METRICS_PADDING_Y * 2 + (int) bl.getY(),
						METRICS_PADDING_X * 4 + (int) bl.getWidth(),
						METRICS_PADDING_X * 4 + (int) bl.getHeight(), 5, 5);
			}

			ClickBox cBox = new ClickBox(x - METRICS_PADDING_X
					+ (int) bl.getX(), y - METRICS_PADDING_Y + (int) bl.getY(),
					METRICS_PADDING_X * 2 + (int) bl.getWidth(),
					METRICS_PADDING_X * 2 + (int) bl.getHeight());

			cBox.setLinkedObj(clickObj);

			this.clickBoxes.addBox(cBox);

		}
	}

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
	 * Converts the topological position (virtual) to the real position of the
	 * window.
	 * 
	 * @param topoPos
	 * @return
	 */
	public Point getPositionInWindow(Coords topoPos) {

		return Simple2DVisualization.getPositionInWindow(topoPos, width_height,
				vis);
	}

	public static ModelFilter getFilter() {
		return Simple2DVisualization.getDataModel().getFilter();
	}

	@Override
	public boolean shallStop() {
		return false;
	}

	@Override
	public boolean onlyHighestPrio() {
		return true;
	}

	void incSpaceCount(EdgeSp<Node<?, ?>> space) {

		Integer count = edgesPerSpace.get(space);

		if (count == null) {
			edgesPerSpace.put(space, 1);
		} else {
			edgesPerSpace.put(space, count + 1);
		}
	}

	int getSpaceCount(EdgeSp<Node<?, ?>> space) {
		Integer count = edgesPerSpace.get(space);
		return (count == null) ? 0 : count;
	}

	public ClickBoxHandler getClickables() {
		return this.clickBoxes;
	}

}
