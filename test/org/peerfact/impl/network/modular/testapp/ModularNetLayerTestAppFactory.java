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
 *
 */

package org.peerfact.impl.network.modular.testapp;

import java.util.ArrayList;
import java.util.List;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.transport.TransInfo;


public class ModularNetLayerTestAppFactory implements ComponentFactory {

	HostPool hostPool = new HostPool();

	@Override
	public Component createComponent(Host host) {
		return new ModularNetLayerTestApp(host, hostPool, (short) 8080);
	}

	static class HostPool {
		List<TransInfo> hosts = new ArrayList<TransInfo>(1000);

		int c = 0;

		public void addHost(TransInfo host) {
			hosts.add(host);
		}

		public TransInfo getHost(TransInfo me) {
			if (hosts.isEmpty()) {
				throw new IllegalStateException("Host pool is empty.");
			}
			if (c >= hosts.size()) {
				c = 0;
			}
			TransInfo result = hosts.get(c);
			c++;
			if (me.equals(result)) {
				if (hosts.size() <= 1) {
					throw new IllegalStateException(
							"The host "
									+ me
									+ " is the only host in the host pool, it can not get another host from the pool.");
				}
				return getHost(me);
			}
			return result;
		}

	}

}
