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

import java.awt.Color;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.overlay.OverlayNodeMetric;
import org.peerfact.impl.analyzer.visualization2d.model.overlay.VisOverlayNode;


/**
 * Peer-Status für alle dht.centralized geeignet.
 * 
 * @version 3.0, 10.11.2008
 * 
 */
public class PeerStatusM extends OverlayNodeMetric {

	public PeerStatusM() {
		this.setColor(new Color(200, 0, 255));
	}

	@Override
	public String getValue(VisOverlayNode node) {
		Object attr = node.getAttribute("peer_status");

		if (attr != null) {
			return attr.toString();
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Peer-Status";
	}

	@Override
	public String getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

}
