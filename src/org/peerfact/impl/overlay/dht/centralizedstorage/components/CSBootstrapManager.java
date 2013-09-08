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

package org.peerfact.impl.overlay.dht.centralizedstorage.components;

import java.util.Collections;
import java.util.List;

import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;


class CSBootstrapManager implements BootstrapManager<CSServerNode> {
	private CSServerNode serverNode;

	@Override
	public List<TransInfo> getBootstrapInfo() {
		if (serverNode == null) {
			return Collections.emptyList();
		} else {
			TransInfo t = serverNode.getHost().getTransLayer().getLocalTransInfo(
					serverNode.getPort());
			return Collections.singletonList(t);
		}
	}

	// public int getNumberOfActiveNodes() {
	// return (serverAddress == null) ? 0 : 1;
	// }

	// public void addActiveNode(OverlayIDImpl oId) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void registerNode(CSServerNode node) {
		this.serverNode = node;
	}

	@Override
	public void unregisterNode(CSServerNode node) {
		if (this.serverNode == node) {
			this.serverNode = null;
		} else {
			throw new IllegalArgumentException(
					"Cannot unregister an unknown node");
		}

	}

}
