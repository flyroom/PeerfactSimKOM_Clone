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

package org.peerfact.impl.overlay.contentdistribution;

import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;

/**
 * Factory used to create simple content distribution.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 3.0, 30.11.2007
 * 
 */
public class ContentDistributionFactory implements ComponentFactory {

	private short port = 120;

	/**
	 * Port whether the (next) strategy instance will wait for incoming
	 * requests.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = (short) port;
	}

	@Override
	public OverlayNode<?, ?> createComponent(Host host) {
		return new DefaultContentDistribution(host.getTransLayer(), port);
	}

	@Override
	public String toString() {
		return "Content Distribution Factory";
	}
}
