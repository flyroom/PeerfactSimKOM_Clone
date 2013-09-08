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

package org.peerfact.impl.overlay.dht.centralizedindex.vis;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.generic.ClientServerPartitionRingPositioner;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSServerNode;

/**
 * Der Server sitzt in der Mitte, die Clients sind um ihn rum angesiedelt. Für
 * alle dht-centralized-Implementierungen geeignet.
 * 
 * @author Leo Nobach
 * @version 3.0, 15.11.2008
 * 
 */
public class CIPositioner extends ClientServerPartitionRingPositioner {

	@Override
	public boolean isServer(Host host, OverlayNode<?, ?> nd) {
		return nd instanceof CSServerNode;
	}

}
