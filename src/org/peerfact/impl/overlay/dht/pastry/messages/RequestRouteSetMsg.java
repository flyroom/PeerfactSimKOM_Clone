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

package org.peerfact.impl.overlay.dht.pastry.messages;

import org.peerfact.Constants;
import org.peerfact.impl.overlay.dht.pastry.components.PastryID;

/**
 * Request for the RoutingTable Entry R_r/c, with r being the row and c the
 * column.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 03/22/2011
 */
public class RequestRouteSetMsg extends PastryBaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7105230701287824873L;

	private int row;

	private int col;

	public RequestRouteSetMsg(PastryID sender, PastryID receiver,
			int row, int col) {
		super(sender, receiver);
		this.col = col;
		this.row = row;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public long getSize() {
		return super.getSize() + 2 * Constants.INT_SIZE;
	}

}
