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

package org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements;

import javax.swing.ImageIcon;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.controller.commands.PlayPause;
import org.peerfact.impl.analyzer.visualization2d.controller.player.PlayerEventListener;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class PlayPauseButton extends SimpleToolbarButton implements
		PlayerEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3631493274100117920L;

	static ImageIcon iconPlay = new ImageIcon(Constants.ICONS_DIR
			+ "/PlayButton.png");

	static ImageIcon iconPause = new ImageIcon(Constants.ICONS_DIR
			+ "/PauseButton.png");

	public PlayPauseButton() {
		this.setIcon(iconPlay);
		// this.setText("Play");
		this.setToolTipText("Play");

		Controller.getPlayer().addEventListener(this);

		this.addCommand(new PlayPause());

	}

	public void setPlay() {
		this.setIcon(iconPlay);
	}

	@Override
	public void forward() {
		// Nothing to do
	}

	@Override
	public void pause() {
		this.setIcon(iconPlay);

	}

	@Override
	public void play() {
		this.setIcon(iconPause);

	}

	@Override
	public void reverse() {
		// Nothing to do
	}

	@Override
	public void stop() {
		this.setIcon(iconPlay);

	}

	@Override
	public void speedChange(double speed) {
		// Nothing to do
	}

	@Override
	public void quantizationChange(double quantization) {
		// TODO Auto-generated method stub

	}

}
