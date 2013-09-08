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

package org.peerfact.impl.application.filesharing.overlayhandler.kbr;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.api.common.Message;
import org.peerfact.impl.simengine.Simulator;


/**
 * All classes in this package are only for use by the KBR application. Abstract
 * KBR filesharing message.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class FilesharingMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2804412366573275292L;
	private static RandomGenerator rGen = Simulator.getRandom();

	/**
	 * Generates a new Query UID
	 * 
	 * @return
	 */
	public static long generateQueryUID() {
		return rGen.nextInt() << 32 + rGen.nextInt();
	}

}
