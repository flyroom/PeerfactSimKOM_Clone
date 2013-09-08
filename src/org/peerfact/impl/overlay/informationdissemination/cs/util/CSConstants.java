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

package org.peerfact.impl.overlay.informationdissemination.cs.util;

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
 * This class contains constants for the Clinet/Server IDO System.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 06/01/2011
 * 
 */
public class CSConstants {

	/**
	 * Enum for Messages.
	 * 
	 * @author Christoph Münker
	 */
	public enum MSG_TYPE {
		/**
		 * Describes an identifier for a join message
		 */
		JOIN_MESSAGE,
		/**
		 * Describes an identifier for a join response
		 */
		JOIN_RESPONSE,
		/**
		 * Describes an identifier for a leave message
		 */
		LEAVE_MESSAGE,
		/**
		 * Describes an identifier for a position update message for a client
		 */
		UPDATE_POSITION_CLIENT_MESSAGE,
		/**
		 * Describes an identifier for a position update message for a server
		 */
		UPDATE_POSITION_SERVER_MESSAGE,
		/**
		 * Describes an identifier for a error message of a server
		 */
		ERROR_MESSAGE
	}

	/**
	 * Enum for error messages.
	 * 
	 * @author Christoph Münker
	 */
	public enum ERROR_TYPES {
		/**
		 * The server is full.
		 */
		FULL_SERVER
	}
}
