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

package org.peerfact.impl.overlay.informationdissemination.von.messages;

import java.awt.Point;

import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This message is used to inform neighbors about position changes.
 * 
 * Two sub-types of this message are distinguished by the flag
 * <code>toBoundary</code>. One is used to inform boundary neighbors, the other
 * is used for enclosing neighbors. This way the receivers do not have to check
 * again if they are boundary neighbors, which means they have to check for new
 * neighbors of the moving node.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MoveMsg extends AbstractVonMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6663032609172436796L;

	private final boolean toBoundary;

	private final Point position;

	private final int aoiRadius;

	/*
	 * Why sending a timestamp? Should it be used as the local timestamp in the
	 * voronoi? Makes no sense to me since the clocks normally are not
	 * synchronized and all used timestamps are determined at the node that
	 * stores it to keep track of the last point in time when he had contact to
	 * the node.
	 * 
	 * --> Christian agreed with me about this point - removing the timestamp?
	 */
	private final long timestamp;

	public MoveMsg(VonID sender, VonID receiver, boolean toBoundary,
			Point position, int aoiRadius, long timestamp) {
		super(sender, receiver);

		this.toBoundary = toBoundary;
		this.position = position;
		this.aoiRadius = aoiRadius;
		this.timestamp = timestamp;
	}

	@Override
	public Message getPayload() {
		// There is no payload
		return null;
	}

	@Override
	public long getSize() {
		/*
		 * size = sizeOfAbstractMsg + sizeOfPosition + sizeOfAoiRadius +
		 * sizeOfTimestamp = (2* sizeOfInt) + sizeOfInt + sizeOfTimestamp =
		 * sizeOfAbstractMsg + 8byte + 4byte + 4byte = sizeOfAbstractMsg +
		 * 16byte
		 * 
		 * sizeOfTimestamp = 4byte (according to the VON specifications)
		 */
		return getSizeOfAbstractMessage() + 16;
	}

	public boolean isToBoundary() {
		return toBoundary;
	}

	public Point getPosition() {
		return position;
	}

	public int getAoiRadius() {
		return aoiRadius;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
