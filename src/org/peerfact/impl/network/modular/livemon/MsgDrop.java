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

package org.peerfact.impl.network.modular.livemon;

import org.peerfact.impl.util.LiveMonitoring.ProgressValue;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MsgDrop implements ProgressValue {

	long dropped = 0;

	long total = 0;

	private String ownerName;

	Object lock = new Object();

	public MsgDrop(String ownerName) {
		this.ownerName = ownerName;
	}

	public void droppedMessage() {
		synchronized (lock) {
			dropped++;
			total++;
		}
	}

	public void noDropMessage() {
		synchronized (lock) {
			total++;
		}
	}

	@Override
	public String getName() {
		return "Net: " + ownerName + " Msg Drop";
	}

	@Override
	public String getValue() {
		synchronized (lock) {
			if (total == 0) {
				return "Unknown";
			}
			return dropped
					+ ", quota: "
					+ NumberFormatToolkit.formatPercentage(dropped
							/ (double) total, 3);
		}
	}

}
