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

package org.peerfact.impl.analyzer.visualization2d.controller;

import javax.swing.JComponent;

import org.peerfact.impl.analyzer.visualization2d.api.visualization.Visualization;
import org.peerfact.impl.analyzer.visualization2d.controller.player.Player;
import org.peerfact.impl.analyzer.visualization2d.metrics.MetricsBase;
import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.ui.common.UIMainWindow;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.gui.LookAndFeel;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.Simple2DVisualization;


/**
 * Controls the components of the visualization interface.
 * 
 * See documentation on <http://www.peerfact.org>.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Controller {

	/**
	 * The main UI window
	 */
	protected static UIMainWindow ui;

	/**
	 * The visualization component with its API
	 */
	protected static Visualization visualization_api;

	/**
	 * The visualization component as a SWING component
	 */
	protected static JComponent vis_component;

	/**
	 * The data model of visualization graphics
	 */
	protected static VisDataModel model = null; // new VisDataModel();

	/**
	 * the event timeline
	 */
	protected static EventTimeline timeline;

	/**
	 * the player
	 */
	protected static Player player;

	/**
	 * Initializes the application when it starts. waitingForSim specifies
	 * whether the application should wait for the processing of the simulator,
	 * or can start immediately.
	 */
	public static void init() {

		LookAndFeel.setLookAndFeel();

		MetricsBase.init();

		// model = new VisDataModel(new Coords(20f, 20f));

		player = new Player();
		// player.addEventListener(new ConsolePlayerNotifier()); //for debugging

		Simple2DVisualization vis2d = new Simple2DVisualization();
		visualization_api = vis2d;
		vis_component = vis2d;
		ui = new UIMainWindow(vis_component);
		ui.setVisible(true);

		/*
		 * Reports DetailsPane as a listener for clicks on the visualization.
		 */
		visualization_api.addVisActionListener(ui.getDetailsPane());

	}

	/**
	 * Shuts down the application down.
	 */
	public static void deinit() {
		ui.saveSettings();
		player.saveSettings();
		MetricsBase.saveSettings();

		Config.writeXMLFile();

		System.exit(0);
	}

	/**
	 * Provides the API for the visualization component, eg to add / modify
	 * nodes and edges at run time
	 * 
	 * @return
	 */
	public static Visualization getVisApi() {
		return visualization_api;
	}

	/**
	 * Returns the UI main window of the application.
	 * 
	 * @return
	 */
	public static UIMainWindow getUIMainWindow() {
		return ui;
	}

	/**
	 * Returns the currently loaded data model. None is loaded, null is
	 * returned.
	 * 
	 * @return
	 */
	public static VisDataModel getModel() {
		return model;
	}

	/**
	 * Resets the model and its view.
	 */
	public static void resetView() {
		getPlayer().reset();
		getModel().reset();
		getUIMainWindow().reset();
		VisDataModel.needsRefresh();
	}

	/**
	 * Returns the timeline. Is identical to getModel().getTimeline().
	 * 
	 * @return
	 */

	public static EventTimeline getTimeline() {
		return model.getTimeline();
	}

	/**
	 * Loading a record in the application. showName here is a name that loaded
	 * for the Record. is, for example "modell.peerfact" or "Untitled".
	 * 
	 * @param passedModel
	 * @param shownName
	 */
	public static void loadModelFrontend(VisDataModel passedModel) {
		loadModelBackend(passedModel);

		connectModelToUI();
	}

	public static void loadModelBackend(VisDataModel passedModel) {
		Controller.model = passedModel;
		VisDataModel.newModelLoaded();
	}

	public static void connectModelToUI() {
		player.setTimeline(model.getTimeline());
		ui.setTitleFileName(model.getName());
		resetView();
	}

	/**
	 * Returns the player
	 * 
	 * @return
	 */
	public static Player getPlayer() {
		return player;
	}

}
