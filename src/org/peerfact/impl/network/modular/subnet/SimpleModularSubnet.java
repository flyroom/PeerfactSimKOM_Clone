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

package org.peerfact.impl.network.modular.subnet;

import org.peerfact.api.network.NetLayer;
import org.peerfact.impl.network.modular.AbstractModularSubnet;
import org.peerfact.impl.network.modular.IStrategies;
import org.peerfact.impl.network.modular.ModularNetLayer;

/**
 * Simple Subnet (former ModularSubnet), default if no Subnet is specified.
 * Subnets allow for routing of messages and different transmission paths like
 * for example WiFi and Ad-Hoc. This subnet implements the "big cloud"
 * assumption and specifies no network topology.
 * 
 * @author Leo Nobach (moved into this package and slightly modified by Bjoern
 *         <peerfact@kom.tu-darmstadt.de> Richerzhagen)
 * @version 05/06/2011
 */
public class SimpleModularSubnet extends AbstractModularSubnet {

	public SimpleModularSubnet(IStrategies strategies) {
		super(strategies);
	}

	@Override
	protected boolean isConnectionPossible(ModularNetLayer nlSender,
			ModularNetLayer nlReceiver) {
		return true; // All connections are possible (no routing)
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// no types to write back
	}

	@Override
	protected void netLayerWentOnline(NetLayer net) {
		// nothing to do here.
	}

	@Override
	protected void netLayerWentOffline(NetLayer net) {
		// nothing to do here.
	}

}
