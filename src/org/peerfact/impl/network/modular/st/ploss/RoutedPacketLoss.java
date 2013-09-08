/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.network.modular.st.ploss;

import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.ModularNetMessage;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.st.PLossStrategy;
import org.peerfact.impl.network.modular.subnet.RoutedSubnet;

public class RoutedPacketLoss implements PLossStrategy {

	@Override
	public void writeBackToXML(BackWriter bw) {
		/*
		 * there are no config-values for this strategy. It solely depends on
		 * the subnet!
		 */
	}

	@Override
	public boolean shallDrop(ModularNetMessage msg, ModularNetLayer nlSender,
			ModularNetLayer nlReceiver, NetMeasurementDB db) {

		if (!(nlSender.getSubnet() instanceof RoutedSubnet)
				|| !(nlReceiver.getSubnet() instanceof RoutedSubnet)) {
			throw new IllegalArgumentException(
					"The RoutedLatency-Strategy can only be used in conjunction with a Routing-Enabled Subnet!");
		}
		// Drop is provided by the subnet, which acts as a strategy
		RoutedSubnet rSubnet = (RoutedSubnet) nlSender.getSubnet();
		return rSubnet.shallDrop(msg, nlSender, nlReceiver, db);
	}

}
