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

package org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.ModelRefreshListener;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.FPSChanger;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.ForwardButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.LoopButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.PlayPauseButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.ReverseButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.SpeedChanger;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.StopButton;


/**
 * The menu bar on player functions
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PlayerToolBar extends JToolBar implements ModelRefreshListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -195863955037291967L;

	JButton playPauseButton = new PlayPauseButton();

	JButton stopButton = new StopButton();

	JButton reverseButton = new ReverseButton();

	JButton forwardButton = new ForwardButton();

	public PlayerToolBar() {
		this.add(playPauseButton);
		this.add(stopButton);
		this.add(reverseButton);
		this.add(forwardButton);
		this.add(new LoopButton());
		this.add(new SpeedChanger());
		this.add(new FPSChanger());
		this.setVisible(Controller.getModel() != null);
		VisDataModel.addRefreshListener(this);
	}

	@Override
	public void modelNeedsRefresh(VisDataModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newModelLoaded(VisDataModel model) {
		this.setVisible(Controller.getModel() != null);
	}

	@Override
	public void simulationFinished(VisDataModel model) {
		// TODO Auto-generated method stub

	}

}
