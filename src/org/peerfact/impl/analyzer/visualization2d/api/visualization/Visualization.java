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

package org.peerfact.impl.analyzer.visualization2d.api.visualization;

import java.awt.event.KeyListener;

import javax.swing.JToolBar;

import org.peerfact.impl.analyzer.visualization2d.model.MetricObject;
import org.peerfact.impl.analyzer.visualization2d.model.flashevents.FlashEvent;


/**
 * Interface of a class that will be responsible for the visualization of a data
 * model.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface Visualization {

	/**
	 * A customized menu for the visualization component
	 */
	public JToolBar getVisualizationSpecificToolbar();

	public void addVisActionListener(VisActionListener val);

	public void queueFlashEvent(FlashEvent e);

	/**
	 * Sets a background image. If none is to be used, pass null.
	 * 
	 * @param path
	 */
	public void setBackgroundImagePath(String path);

	/**
	 * Returns the KeyListener that handles the keyboard input for the
	 * visualization interface
	 * 
	 * @return
	 */
	public KeyListener getVisKeyListener();

	/**
	 * Returns the currently selected object in the visualization window
	 * 
	 * @return
	 */
	public MetricObject getSelectedObject();

	public void setSimulatorStillRunning(boolean running);

}
