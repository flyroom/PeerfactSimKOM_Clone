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

import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * To debug the player
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ConsolePlayerNotifier implements PlayerEventListener {
	private static Logger log = SimLogger.getLogger(PlayerEventListener.class);

	@Override
	public void forward() {
		log.debug("PLAYER: Forward");

	}

	@Override
	public void pause() {
		log.debug("PLAYER: Pause");
	}

	@Override
	public void play() {
		log.debug("PLAYER: Play");
	}

	@Override
	public void reverse() {
		log.debug("PLAYER: Reverse");
	}

	@Override
	public void stop() {
		log.debug("PLAYER: Stop");
	}

	@Override
	public void speedChange(double speed) {
		log.debug("Speed Change: " + speed + "ms");
	}

	@Override
	public void quantizationChange(double quantization) {
		log.debug("Quantization Change: " + quantization + "ms");
	}

}
