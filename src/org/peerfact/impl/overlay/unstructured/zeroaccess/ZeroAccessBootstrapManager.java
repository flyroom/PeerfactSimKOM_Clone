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

package org.peerfact.impl.overlay.unstructured.zeroaccess;

import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.transport.TransInfo;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ZeroAccessBootstrapManager implements
		BootstrapManager<ZeroAccessOverlayNode> {

	private List<ZeroAccessOverlayNode> activeNodes = new LinkedList<ZeroAccessOverlayNode>();

	private List<ZeroAccessOverlayNode> activePeer = new LinkedList<ZeroAccessOverlayNode>();

	private final int MAXIMUM_BOOTSTRAP_PEERLIST_SIZE = 256;

	private List<ZeroAccessOverlayNode> bootstrapPeers = new LinkedList<ZeroAccessOverlayNode>();

	private static ZeroAccessBootstrapManager singletonInstance;

	public static ZeroAccessBootstrapManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new ZeroAccessBootstrapManager();
		}
		return singletonInstance;
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> bootstrapInfo = new LinkedList<TransInfo>();
		for (ZeroAccessOverlayNode bootstrapNode : bootstrapPeers) {
			bootstrapInfo.add(bootstrapNode.getTransLayer().getLocalTransInfo(
					bootstrapNode.getPort()));
		}
		return bootstrapInfo;
	}

	@Override
	public void registerNode(ZeroAccessOverlayNode node) {
		activeNodes.add(node);
	}

	@Override
	public void unregisterNode(ZeroAccessOverlayNode node) {
		activeNodes.remove(node);
	}

	public int getSize() {
		return activePeer.size();
	}

	public void registerPeer(ZeroAccessOverlayNode node) {
		if (!activePeer.contains(node)) {
			activePeer.add(node);
		}

		synchronized (bootstrapPeers)
		{
			if (bootstrapPeers.size() < MAXIMUM_BOOTSTRAP_PEERLIST_SIZE)
			{
				if (!bootstrapPeers.contains(node)) {
					bootstrapPeers.add(node);
				}
			}
		}
	}

	public void unregisterPeer(ZeroAccessOverlayNode node) {
		if (activePeer.contains(node)) {
			activePeer.remove(node);
		}
	}
}
