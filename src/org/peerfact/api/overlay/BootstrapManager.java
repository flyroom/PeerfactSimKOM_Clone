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

package org.peerfact.api.overlay;

import java.util.List;

import org.peerfact.api.transport.TransInfo;


/**
 * General Bootstrap service for overlay nodes. Joined overlay nodes can
 * register themselves and other overlay nodes (willing to join the network) can
 * lookup the TransInfos of already joined nodes.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * 
 * @param <T>
 *            The concrete implementation of <code>OverlayNode</code> used.
 * @version 05/06/2011
 */
public interface BootstrapManager<T extends OverlayNode<?, ?>> {

	/**
	 * Node has joined the network.
	 * 
	 * @param node
	 */
	public void registerNode(T node);

	/**
	 * Node has left the network.
	 * 
	 * @param node
	 */
	public void unregisterNode(T node);

	/**
	 * TransInfos of (a subset of) connected nodes.
	 * 
	 * @return list of trans infos.
	 */
	public List<TransInfo> getBootstrapInfo();

}
