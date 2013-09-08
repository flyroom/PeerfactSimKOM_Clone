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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsPack;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayGraph;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;


/**
 * Base class for the data model.
 * 
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */

public class VisDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2639836545144899408L;

	private Coords lowerBounds;

	private Coords upperBounds;

	public VisDataModel() {
		this("Unnamed");
	}

	private String name;

	private final VisOverlayGraph g;

	private final EventTimeline tl;

	/**
	 * All dynamically from e.g. OverlayAdapters loaded metrics. Only for saving
	 * and loading.
	 */
	MetricsPack specialMetrics = null;

	ModelFilter filter = new ModelFilter();

	volatile boolean unsaved = true;

	static boolean muted = false;

	static ArrayList<ModelRefreshListener> listeners = new ArrayList<ModelRefreshListener>();

	public VisDataModel(String name) {
		g = new VisOverlayGraph();
		tl = new EventTimeline();
		this.name = name;

		this.upperBounds = new Coords(Float.MIN_NORMAL, Float.MIN_VALUE);
		this.lowerBounds = new Coords(Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public VisOverlayGraph getOverlayGraph() {
		return g;
	}

	public ModelFilter getFilter() {
		return filter;
	}

	/**
	 * Sets the maximum geographical coordinates of the nodes.
	 * 
	 * @return
	 */
	public void setUpperBounds(Coords bounds) {
		this.upperBounds = bounds;
	}

	/**
	 * Returns the minimum width of the geographical coordinates of the nodes.
	 * 
	 * @return
	 */
	public Coords getLowerBounds() {
		return this.lowerBounds;
	}

	/**
	 * Sets the minimum geographical coordinates of the nodes.
	 * 
	 * @return
	 */
	public void setLowerBounds(Coords bounds) {
		this.lowerBounds = bounds;
	}

	/**
	 * Returns the maximum width of the geographical coordinates of the nodes.
	 * 
	 * @return
	 */
	public Coords getUpperBounds() {
		return this.upperBounds;
	}

	/**
	 * Iterates the iterator <code>it</code> over the whole model,
	 * "top to bottom"
	 * 
	 * @param it
	 */
	public void iterate(ModelIterator it, MetricObject selectedObject) {
		g.iterate(it, selectedObject);
	}

	/**
	 * Iterates the iterator <code>it</code> over the whole model,
	 * "top to bottom"
	 * 
	 * @param it
	 */
	public void iterateBottomTop(ModelIterator it) {
		g.iterateBottomTop(it);
	}

	/**
	 * Resets the model (empty model)
	 */
	public void reset() {
		g.reset();
		tl.reset();
	}

	public EventTimeline getTimeline() {
		return tl;
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
	}

	public void saveTo(File file) throws IOException {
		this.specialMetrics = MetricsBase.getDynamicMetricsPack();
		specialMetrics.clearInstances();
		ObjectOutputStream objOut = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(file))));

		objOut.writeObject(this);
		objOut.close();
		Controller.getModel().setUnsaved(false);
	}

	/*
	 * Static methods: The timeline can be exchanged as objects (open / save).
	 * What should not be replaced, make static:
	 */

	public static VisDataModel fromFile(File file) throws IOException {
		try {
			ObjectInputStream objIn = new ObjectInputStream(
					new BufferedInputStream(new GZIPInputStream(
							new FileInputStream(file))));
			VisDataModel model = (VisDataModel) objIn.readObject();
			objIn.close();
			model.setUnsaved(false);
			MetricsBase.setDynamicMetricsPack(model.specialMetrics);
			return model;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addRefreshListener(ModelRefreshListener l) {
		listeners.add(l);
	}

	public static void needsRefresh() {
		if (!muted) {
			for (ModelRefreshListener l : listeners) {
				l.modelNeedsRefresh(Controller.getModel());
			}
		}
	}

	public static void newModelLoaded() {
		if (!muted) {
			for (ModelRefreshListener l : listeners) {
				l.newModelLoaded(Controller.getModel());
			}
		}
	}

	public static void simulationFinished() {
		if (!muted) {
			for (ModelRefreshListener l : listeners) {
				l.simulationFinished(Controller.getModel());
			}
		}
	}

	/**
	 * Sets the data model "mute", so the listener will not be informed about
	 * new events. e.g. for Gnuplot export.
	 * 
	 * @param mute
	 */
	public static void mute(boolean mute) {
		muted = mute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
