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

package org.peerfact.impl.network.modular.st;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetProtocol;

/**
 * A network layer usually splits up the SDU into multiple fragments, if it is
 * too big. This strategy determines how many fragments are needed to service
 * the given payload.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface FragmentingStrategy extends ModNetLayerStrategy {

	/**
	 * Returns the number of fragments for the given payload in order to
	 * construct a NetMessage.
	 * 
	 * @param payload
	 *            , the payload passed by the transport layer
	 * @param receiver
	 *            , the receiver address of the future net message
	 * @param sender
	 *            , the sender address of the future net message
	 * @param netProtocol
	 *            , the network protocol used of the future net message.
	 * @return
	 */
	public int getNoOfFragments(Message payload, NetID receiver, NetID sender,
			NetProtocol netProtocol);

}
