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

package org.peerfact.impl.analyzer.visualization2d.controller.player;

/**
 * Responding to events of the player.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface PlayerEventListener {

	/**
	 * Execution at the beginning of the playback operation
	 */
	public void play();

	/**
	 * Execution at pause
	 */
	public void pause();

	/**
	 * Execution when you stop the playback operation
	 */
	public void stop();

	/**
	 * Execution at forward
	 */
	public void forward();

	/**
	 * Execution at reverse
	 */
	public void reverse();

	/**
	 * Execution when the playback speed changes
	 * 
	 * @param speed
	 */
	public void speedChange(double speed);

	/**
	 * Execution when the quantization is changing
	 */
	public void quantizationChange(double quantization);

}
