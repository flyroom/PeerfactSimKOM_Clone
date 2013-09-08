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

package org.peerfact.impl.overlay.informationdissemination.psense.util;

import org.peerfact.impl.overlay.informationdissemination.psense.PSenseID;

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
 * A class containing the constants for the overlay pSense.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 */
public class Constants {

	/**
	 * Enum for Messages.
	 * 
	 * @author Christoph Muenker
	 * @version 10/15/2010
	 */
	public enum MSG_TYPE {
		/**
		 * Describes an identifier for a position update message
		 */
		POSITION_UPDATE,
		/**
		 * Describes an identifier for a sensor request message
		 */
		SENSOR_REQUEST,
		/**
		 * Describes an identifier for a forward message
		 */
		FORWORD,
		/**
		 * Describes an identifier for a sensor response message
		 */
		SENSOR_RESPONSE,
		/**
		 * Describes an identifier for an action message
		 */
		ACTION_MSG
	}

	/**
	 * An empty overlay ID in pSense.
	 */
	public final static PSenseID EMPTY_PSENSE_ID = new PSenseID(-1);

	/**
	 * Describes the size in bytes of the IP.
	 */
	public final static int BYTE_SIZE_OF_IP = 4;

	/**
	 * Describes the size in bytes of the port.
	 */
	public final static int BYTE_SIZE_OF_PORT = 2;

	/**
	 * Describes the size in byte of the counter for the number of hops in a
	 * message.
	 */
	public final static int BYTE_SIZE_OF_HOP_COUNT = 1;

	/**
	 * Describes the size in byte to distinguish a message.
	 */
	public final static int BYTE_SIZE_OF_MSG_TYPE = 1;

	/**
	 * Describes the size in byte of a sequence number. (short)
	 */
	public final static int BYTE_SIZE_OF_SEQ_NR = 2;

	/**
	 * Describes the size in bytes of the vision range (one integer).
	 */
	public final static int BYTE_SIZE_OF_RADIUS = 4;

	/**
	 * Describes the size in bytes of a Point in a 2D plane (two integers!).
	 */
	public final static int BYTE_SIZE_OF_POINT = 8;

	/**
	 * Describes the size in byte of the identifier of a sector.
	 */
	public final static int BYTE_SIZE_OF_SECTOR_ID = 1;
}
