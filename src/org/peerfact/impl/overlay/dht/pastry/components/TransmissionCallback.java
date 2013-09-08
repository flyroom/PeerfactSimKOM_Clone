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

package org.peerfact.impl.overlay.dht.pastry.components;

import org.peerfact.api.common.Message;

/**
 * This callback is used in the implementation of Pastry to be able to notify
 * about failed or succeeded message transmissions.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface TransmissionCallback {

	/**
	 * This interface is to be realized to be able to function as callback for a
	 * message's failed transmission.
	 */
	public interface Failed extends TransmissionCallback {
		/**
		 * @param msg
		 *            the message that's transmission failed
		 */
		public void transmissionFailed(Message msg);
	}

	/**
	 * This interface is to be realized to be able to function as callback for a
	 * message's succeeded transmission.
	 */
	public interface Succeeded extends TransmissionCallback {
		/**
		 * @param msg
		 *            the message that's transmission succeeded
		 * @param reply
		 *            The Message received as a reply
		 */
		public void transmissionSucceeded(Message msg, Message reply);
	}
}
