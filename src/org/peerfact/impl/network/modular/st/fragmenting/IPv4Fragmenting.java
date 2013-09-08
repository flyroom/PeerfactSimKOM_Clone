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

package org.peerfact.impl.network.modular.st.fragmenting;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetProtocol;
import org.peerfact.impl.network.modular.st.FragmentingStrategy;

/**
 * 
 * The fragmenting is used as defined by IPv4 Based on code from Sebastian Kaune
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class IPv4Fragmenting implements FragmentingStrategy {

	public static final double MTU_MINUS_HEADER_SIZE = 1480; // 1500 - 20

	@Override
	public int getNoOfFragments(Message payload, NetID receiver, NetID sender,
			NetProtocol netProtocol) {

		return (int) Math.ceil(payload.getSize() / MTU_MINUS_HEADER_SIZE);

	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
