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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04;

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
public class GnutellaBootstrapManager implements
		BootstrapManager<GnutellaOverlayNode> {

	private List<GnutellaOverlayNode> activeNodes = new LinkedList<GnutellaOverlayNode>();

	private List<GnutellaOverlayNode> activePeer = new LinkedList<GnutellaOverlayNode>();

	private static GnutellaBootstrapManager singletonInstance;

	public static GnutellaBootstrapManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GnutellaBootstrapManager();
		}
		return singletonInstance;
	}

	@Override
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> bootstrapInfo = new LinkedList<TransInfo>();
		for (GnutellaOverlayNode activeNode : activeNodes) {
			bootstrapInfo.add(activeNode.getTransLayer().getLocalTransInfo(
					activeNode.getPort()));
		}
		return bootstrapInfo;
	}

	@Override
	public void registerNode(GnutellaOverlayNode node) {
		activeNodes.add(node);
	}

	@Override
	public void unregisterNode(GnutellaOverlayNode node) {
		activeNodes.remove(node);
	}

	public int getSize() {
		return activePeer.size();
	}

	public void registerPeer(GnutellaOverlayNode node) {
		if (!activePeer.contains(node)) {
			activePeer.add(node);
		}
	}

	public void unregisterPeer(GnutellaOverlayNode node) {
		if (activePeer.contains(node)) {
			activePeer.remove(node);
		}
	}
}
