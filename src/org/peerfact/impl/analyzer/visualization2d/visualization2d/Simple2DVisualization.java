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

/**
 * Quite good: http://java.sun.com/docs/books/tutorial/2d/
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayEdgeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.api.visualization.VisActionListener;
import org.peerfact.impl.analyzer.visualization2d.api.visualization.Visualization;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.ModelFilter;
import org.peerfact.impl.analyzer.visualization2d.model.ModelRefreshListener;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.model.flashevents.FlashEvent;
import org.peerfact.impl.analyzer.visualization2d.model.flashevents.FlashEventHandler;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.clicking.ClickBoxHandler;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.toolbar.VisToolBar;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.TimeToolkit;


/**
 * Component to display the data model using AWT 2D tools.
 * 
 * @author Leo Nobach
 * 
 */
public class Simple2DVisualization extends JComponent implements Visualization,
		ModelRefreshListener {

	public static final TimeToolkit TIME_TOOLKIT = new TimeToolkit(
			Simulator.MILLISECOND_UNIT);

	/**
	 * Background color
	 */
	public static final Color BG_COLOR = Color.WHITE;

	/**
	 * Default foreground color
	 */
	public static final Color DEFAULT_FG_COLOR = Color.BLACK;

	/**
	 * Color of the Bound frame
	 */
	public static final Color BOUNDS_FRAME_COLOR = Color.LIGHT_GRAY;

	/**
	 * Color at the lower edge of the mini statistics
	 */
	public static final Color MINI_STATS_COLOR = Color.GRAY;

	/**
	 * Distance from the node Bounds to the window Bounds
	 */
	public static final int GRAPH_BOUNDS = 20;

	private String backgroundImagePath;

	private BufferedImage loadedBackgroundImage;

	private final boolean useBackgroundImage = Boolean.parseBoolean(Config
			.getValue("UI/BackgroundImageEnabled", "false"));

	Vector<VisActionListener> visActionListeners = new Vector<VisActionListener>();

	protected OverlayEdgeMetric strokeMetric;

	protected OverlayNodeMetric nodeSizeMetric;

	protected DefaultKeyboardHandler kbHandler = new DefaultKeyboardHandler(
			this);

	protected ClickBoxHandler clickHndlr = null;

	protected static final IPaintSizeStrategy stroke_s = new SimplePaintSizeStrategy();

	protected static final IPaintSizeStrategy nodesize_s = new SimplePaintSizeStrategy();

	protected MetricObject selectedObject;

	/**
	 * Time of the time line that was drawn last.
	 */
	private long lastPaintTime = -1;

	/**
	 * All not yet subscribed FlashEvents that are drawn at the next time frame.
	 */
	protected FlashEventHandler flashEvents = new FlashEventHandler();

	private boolean showSchematic;

	/**
	 * Describes the offset of the coordinates that results of the dragging.
	 */
	private static Point dragOffset = new Point(0, 0);

	private Coords upperBounds = new Coords(1, 1);

	private Coords lowerBounds = new Coords(0, 0);

	private static final long serialVersionUID = -3370831127492948210L;

	/**
	 * Default constructor
	 * 
	 * @param wait_for_sim
	 */
	public Simple2DVisualization() {
		DefaultMouseHandler mouseHandler = new DefaultMouseHandler(this);
		this.addMouseListener(mouseHandler);
		this.addMouseMotionListener(mouseHandler);
		this.addMouseWheelListener(mouseHandler);
		VisDataModel.addRefreshListener(this);

		if (Config.getValue("UI/BackgroundImageEnabled", false)) {
			backgroundImagePath = Config.getValue("UI/LastBackgroundImage", "");
		}
	}

	@Override
	public KeyListener getVisKeyListener() {
		return kbHandler;
	}

	/**
	 * Adds an ActionListener, which is then called when clicks occur on objects
	 * in the visualization.
	 * 
	 * @param al
	 */
	@Override
	public void addVisActionListener(VisActionListener al) {
		this.visActionListeners.add(al);
	}

	protected static VisDataModel getDataModel() {
		return Controller.getModel();
	}

	@Override
	public synchronized void paint(Graphics g) {

		// System.err.println("Paint bei: " + System.currentTimeMillis());

		if (getDataModel() != null) {
			paintNormal(g);
		} else {
			paintNoModel(g);
		}
	}

	private void paintNoModel(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		enableAntialiasingCond(g2d);

		g2d.setFont(new Font("sansserif", Font.PLAIN, Config.getValue(
				"Visualization/FontSize", 10)));
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(DEFAULT_FG_COLOR);
		g.drawString("No recording loaded.", 20, 20);
	}

	private static void enableAntialiasingCond(Graphics2D g2d) {

		boolean enableAntialiasing = Config.getValue("Engine/antiAliasing",
				true);

		if (enableAntialiasing) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	private void paintNormal(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		enableAntialiasingCond(g2d);

		g2d.setFont(new Font("sansserif", Font.PLAIN, Config.getValue(
				"Visualization/FontSize", 10)));

		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(DEFAULT_FG_COLOR);

		this.drawBoundsFrame(g);

		if (useBackgroundImage) {
			this.drawBackgroundImage(g);
		}

		this.drawSmallStats(
				g,
				"Time: "
						+ TIME_TOOLKIT.timeStringFromLong(Controller
								.getTimeline().getActualTime())
						+ " /"
						+ TIME_TOOLKIT.timeStringFromLong(Controller
								.getTimeline().getMaxTime()), "");

		this.paintDataModel(g);

	}

	private void paintDataModel(Graphics g) {

		flashEvents.setFilter(getFilter());

		Painter painter = new Painter(g, new Point(this.getWidth(),
				this.getHeight()), this);

		Simple2DVisualization.getDataModel().iterateBottomTop(painter);

		if (isProgressRepaint()) {
			flashEvents.iterateAndSetPainted(painter);
			// flashEvents.setEventsPainted();
		} else {
			flashEvents.iteratePaintedEvents(painter);
			// the old ones are drawn again, because just the image is refreshed
			// and the next frame is not drawn.
		}

		this.clickHndlr = painter.getClickables();

		this.lastPaintTime = Simple2DVisualization.getDataModel().getTimeline()
				.getActualTime();

	}

	/**
	 * Specifies whether the next repaint is a repaint, in which the time line
	 * is progressed or if it's just a routine refresh of the image. (Requires
	 * Flash Edges)
	 * 
	 * @return
	 */
	private boolean isProgressRepaint() {
		return lastPaintTime != Simple2DVisualization.getDataModel()
				.getTimeline()
				.getActualTime();
	}

	private void drawSmallStats(Graphics g2, String textleft, String textright) {
		g2.setColor(MINI_STATS_COLOR);
		Font f = g2.getFont();

		int textleftX = (int) Math.rint(f.getStringBounds(textright,
				((Graphics2D) g2).getFontRenderContext()).getWidth());

		g2.drawString(textleft, GRAPH_BOUNDS + 2, this.getHeight() - 2
				- GRAPH_BOUNDS);

		g2.drawString(textright,
				this.getWidth() - textleftX - GRAPH_BOUNDS - 2,
				this.getHeight() - 2 - GRAPH_BOUNDS);

	}

	private void drawBoundsFrame(Graphics g) {
		g.setColor(BOUNDS_FRAME_COLOR);
		g.drawRect(GRAPH_BOUNDS, GRAPH_BOUNDS, this.getWidth() - (GRAPH_BOUNDS)
				* 2, this.getHeight() - (GRAPH_BOUNDS) * 2);
	}

	private void drawBackgroundImage(Graphics g) {
		/*
		 * Do not draw the image if the visualization is in the schematic mode
		 */
		if (showSchematic) {
			return;
		}

		loadBGImage();

		/*
		 * The origin is located in the lower left corner
		 */
		Point origin = getPositionInWindow(
				new Coords(0, loadedBackgroundImage.getHeight()), new Point(
						this.getWidth(), this.getHeight()), this);

		/*
		 * The spanning point is located in the upper right corner
		 */
		Point imageSpanningPoint = getPositionInWindow(new Coords(
				loadedBackgroundImage.getWidth(), 0), new Point(
				this.getWidth(), this.getHeight()), this);

		/*
		 * The size of the image within the window (may be scaled and therefore
		 * not the original image size)
		 */
		Point size = new Point(imageSpanningPoint.x - origin.x,
				imageSpanningPoint.y - origin.y);

		/*
		 * Draw the image and take a potential dragging into account
		 */
		g.drawImage(loadedBackgroundImage, origin.x + dragOffset.x, origin.y
				+ dragOffset.y, size.x, size.y, this);
	}

	/**
	 * Load background image from the internally set path
	 */
	private void loadBGImage() {
		if (backgroundImagePath != null) {
			try {
				loadedBackgroundImage = ImageIO.read(new File(
						backgroundImagePath));
			} catch (IOException e) {
				System.err.println("Error while loading background image: "
						+ backgroundImagePath);
				loadedBackgroundImage = null;
			}
		}
	}

	@Override
	public void setSimulatorStillRunning(boolean running) {
		resetBounds();
		repaint();
	}

	/**
	 * Handles clicks on the view
	 * 
	 * @param clickPoint
	 */
	public void handleClick(Point clickPoint) {
		if (getDataModel() != null) {

			// OLD VERSION:
			/*
			 * ModelClickHandler handler = new ModelClickHandler(clickPoint, new
			 * Point(this.getWidth(), this.getHeight()), this);
			 * 
			 * Controller.getModel().iterate(handler);
			 * flashEvents.iteratePaintedEvents(handler); //überprüft, ob ein
			 * FlashEvent angeklickt wurde. //überprüft, ob ein FlashEvent
			 * angeklickt wurde. MetricObject o =
			 * handler.getObjectFromClick(clickPoint);
			 */
			MetricObject o = this.clickHndlr.getClickedObject(clickPoint);

			if (o == null) {
				o = Controller.getModel().getOverlayGraph();
			}

			selectedObject = o;

			for (VisActionListener val : visActionListeners) {
				if (o != null) {
					val.clickedOn(o);
				}
			}

			this.repaint();
		}
	}

	@Override
	public MetricObject getSelectedObject() {
		return selectedObject;
	}

	@Override
	public void modelNeedsRefresh(VisDataModel model) {
		this.repaint();
	}

	/**
	 * Converts a topologic position to a real position within the visualization
	 * window
	 * 
	 * @param topoPos
	 *            the topologic position
	 * @param width_height
	 * @param vis
	 *            the visualization to be used
	 * @return the position within the window
	 */
	public static Point getPositionInWindow(Coords topoPos, Point width_height,
			Simple2DVisualization vis) {

		Coords graphUpperBounds = vis.upperBounds;
		Coords graphLowerBounds = vis.lowerBounds;

		float divx = ((float) width_height.x - Simple2DVisualization.GRAPH_BOUNDS * 2)
				/ (graphUpperBounds.x - graphLowerBounds.x);
		float divy = ((float) width_height.y - Simple2DVisualization.GRAPH_BOUNDS * 2)
				/ (graphUpperBounds.y - graphLowerBounds.y);

		int resx_int = Simple2DVisualization.GRAPH_BOUNDS
				+ (int) Math.rint((topoPos.x - graphLowerBounds.x) * divx)
				+ dragOffset.x;

		/*
		 * Subtract the y value from the total height to flip the coordinate
		 * (after that the point of origin is in the lower left corner)
		 */
		int resy_int = width_height.y
				- (Simple2DVisualization.GRAPH_BOUNDS + (int) Math
						.rint((topoPos.y - graphLowerBounds.y) * divy))
				+ dragOffset.y;

		return new Point(resx_int, resy_int);
	}

	@Override
	public JToolBar getVisualizationSpecificToolbar() {
		return new VisToolBar(this);
	}

	/**
	 * Metric, which affects the thickness of the edges
	 * 
	 * @param m
	 */
	public void setStrokeMetric(OverlayEdgeMetric m) {
		this.strokeMetric = m;
	}

	/**
	 * The metric that describes the thickness of the Strokes at the moment. Can
	 * be NULL!
	 * 
	 * @return
	 */
	public OverlayEdgeMetric getStrokeMetric() {
		return this.strokeMetric;
	}

	/**
	 * The metric that describes the thickness of the node at the moment. Can be
	 * NULL!
	 * 
	 * @return
	 */
	public OverlayNodeMetric getNodeSizeMetric() {
		return nodeSizeMetric;
	}

	/**
	 * Sets the metric that describes the thickness of the nodes. Can be NULL,
	 * then constant.
	 * 
	 * @param nodeSizeMetric
	 */
	public void setNodeSizeMetric(OverlayNodeMetric nodeSizeMetric) {
		this.nodeSizeMetric = nodeSizeMetric;
	}

	@Override
	public void newModelLoaded(VisDataModel model) {
		this.selectedObject = getDataModel().getOverlayGraph();
		resetBounds();
	}

	/**
	 * Sets the background image path
	 * 
	 * @param path
	 */
	@Override
	public void setBackgroundImagePath(String path) {
		backgroundImagePath = path;
		loadBGImage();
		repaint();
	}

	/**
	 * Moves the view to the value pDiff
	 * 
	 * @param pDiff
	 */
	public void shiftView(Point pDiff) {
		// Disable shifting when showing a background image
		if (useBackgroundImage && !showSchematic) {
			return;
		}

		Coords cDiff = new Coords(pDiff.x * (upperBounds.x - lowerBounds.x)
				/ this.getWidth(), -pDiff.y * (upperBounds.y - lowerBounds.y)
				/ this.getHeight());

		this.lowerBounds = new Coords(lowerBounds.x - cDiff.x, lowerBounds.y
				- cDiff.y);
		this.upperBounds = new Coords(upperBounds.x - cDiff.x, upperBounds.y
				- cDiff.y);

		this.repaint();
	}

	/**
	 * Zooms in. (zoomIn) or out (! ZoomIn) zoomFocus is the point at which he
	 * zooming. Is this <b> null </ b>, zooms to the center.
	 * 
	 * @param zoomIn
	 * @param zoomFocus
	 */
	public void zoom(boolean zoomIn, Point zoomFocus) {
		// Disable zooming when showing a background image
		if (useBackgroundImage && !showSchematic) {
			return;
		}

		// Get the dimension of the visualization area
		Dimension componentSize = this.getSize();
		componentSize.width -= 2 * GRAPH_BOUNDS;
		componentSize.height -= 2 * GRAPH_BOUNDS;

		// Get current mouse position on area
		Point currentPos = (zoomFocus != null) ? zoomFocus : new Point(
				this.getBounds().width / 2, this.getBounds().height / 2);
		currentPos.x -= GRAPH_BOUNDS;
		currentPos.y -= GRAPH_BOUNDS;

		// Flip the y coordinate, that the origin is in the lower left corner
		currentPos.y = (componentSize.height - 2 * GRAPH_BOUNDS) - currentPos.y;

		// Calculate the relative position of the mouse
		// float relativeX = ((float)currentPos.x)/componentSize.width;
		// float relativeY = ((float)currentPos.y)/componentSize.height;
		float relativeX;
		float relativeY;
		if (zoomFocus == null) {
			relativeX = 0.5f;
			relativeY = 0.5f;
		} else {
			relativeX = (float) zoomFocus.x / (float) componentSize.width;
			relativeY = (float) zoomFocus.y / (float) componentSize.height;
		}

		/*
		 * Get the current bounds of the visualization area (the sub-area of the
		 * scenario that can be seen at the moment)
		 */
		Coords lower = lowerBounds;
		Coords upper = upperBounds;

		// Calculate the dimension of the visualized sub-are in
		// scenario-coordinates
		float scenarioDimensionX = upper.x - lower.x;
		float scenarioDimensionY = upper.y - lower.y;

		// Calculate the amount of pixels that will be added or removed to the
		// bound when zooming
		float zoomAmountX = scenarioDimensionX * 0.1f;
		float zoomAmountY = scenarioDimensionY * 0.1f;

		if (!zoomIn) { // Zoom out

			// Subtract the zoomAount weighted by the relative position of the
			// mouse

			lower.x = lower.x - (zoomAmountX / 2 * relativeX);
			lower.y = lower.y - (zoomAmountY / 2 * relativeY);

			upper.x = upper.x + (zoomAmountX / 2 * (1 - relativeX));
			upper.y = upper.y + (zoomAmountY / 2 * (1 - relativeY));

		} else { // Zoom in

			// Add the zoomAount weighted by the relative position of the mouse

			lower.x = lower.x + (zoomAmountX * relativeX);
			lower.y = lower.y + (zoomAmountY * relativeY);

			upper.x = upper.x - (zoomAmountX * (1 - relativeX));
			upper.y = upper.y - (zoomAmountY * (1 - relativeY));

		}

		dbg("Bounds before: " + lowerBounds + "|" + upperBounds
				+ " Bounds after: " + lower + "|" + upper);

		// Set new bounds
		lowerBounds = lower;
		upperBounds = upper;

		repaint();
	}

	@Override
	public void queueFlashEvent(FlashEvent e) {
		flashEvents.addFlashEvent(e);
	}

	/**
	 * Whether the user wants display to the schematic position.
	 * 
	 * @return
	 */
	public boolean schematicPositionSet() {
		return showSchematic;
	}

	/**
	 * Switches the view between the schematic and topological position. If set
	 * to true, the schematic is selected.
	 * 
	 * @param schematicSet
	 */
	public void setSchematic(boolean schematicSet) {
		this.showSchematic = schematicSet;

		resetBounds();

		VisDataModel.needsRefresh();
	}

	private void resetBounds() {
		if (schematicPositionSet() || Controller.getModel() == null) {
			upperBounds = new Coords(1, 1);
			lowerBounds = new Coords(0, 0);
		} else {
			upperBounds = Controller.getModel().getUpperBounds();
			lowerBounds = Controller.getModel().getLowerBounds();
		}
	}

	/**
	 * Debug output
	 * 
	 * @param o
	 */
	public void dbg(Object o) {
		// System.err.println(this.getClass().getSimpleName() + ": " +
		// (o==null?"null":o.toString()));
	}

	@Override
	public void simulationFinished(VisDataModel model) {
		// TODO: respond by EventListener to the event that the simulation is
		// over.
	}

	public static ModelFilter getFilter() {
		return getDataModel().getFilter();
	}

}
