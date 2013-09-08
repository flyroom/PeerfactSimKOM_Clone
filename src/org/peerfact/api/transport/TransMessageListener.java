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

package org.peerfact.api.transport;

import org.peerfact.impl.transport.TransMsgEvent;

/**
 * TransMessageListeners acts as event handlers for incoming TransMsgEvents
 * triggered by the TransLayer. In particular, TransMsgEvents comprises among
 * other things a payload message which in turn comprises data necessary to
 * implement the (virtual) communication between higher layers such as the
 * overlay or application. For instance, the payload field might represent an
 * overlay or application messages.
 * 
 * In other words, the transport layers strips off the header information of the
 * transport message and passes all relevant data to the receiving higher layer
 * in terms of TransMsgEvents.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 03.12.2007
 * 
 */
public interface TransMessageListener {

	/**
	 * Upon receiving a transport message, the transport layer strips off the
	 * header information of this message and passes all relevant data to a
	 * given TransMessageListener in terms of TransMsgEvent.
	 * 
	 * @param receivingEvent
	 *            the TransMsgEvent containing all relevant data
	 */
	public void messageArrived(TransMsgEvent receivingEvent);
}
