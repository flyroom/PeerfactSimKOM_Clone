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

package org.peerfact.impl.overlay.informationdissemination.cs;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.api.overlay.ido.IDOOracle;
import org.peerfact.impl.overlay.informationdissemination.cs.exceptions.FullServerException;


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
 * An Oracle for the Client Server System. It derives the set of ideal nodes for
 * the Client Server System, which should be known by a client. For that, it use
 * the global knowledge.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class CSOracle implements IDOOracle {

	/**
	 * The storage, which stores the global knowledge
	 */
	private ServerStorage storage;

	/**
	 * Create a server Storage with {@link Integer.MAX_VALUE} entries.
	 */
	public CSOracle() {
		storage = new ServerStorage(Integer.MAX_VALUE);
	}

	@Override
	public void insertNodeInfos(List<IDONodeInfo> nodeInfos) {
		for (IDONodeInfo nodeInfo : nodeInfos) {
			Point position = nodeInfo.getPosition();
			int aoi = nodeInfo.getAoiRadius();
			ClientID id = (ClientID) nodeInfo.getID();
			ClientNodeInfo clientNodeInfo = new ClientNodeInfo(position, aoi,
					id);
			try {
				storage.addClient(id, null, clientNodeInfo);
			} catch (FullServerException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void reset() {
		storage = new ServerStorage(Integer.MAX_VALUE);
	}

	@Override
	public List<IDONodeInfo> getAllNeighbors(OverlayID<?> id, int aoi) {
		List<IDONodeInfo> result = new Vector<IDONodeInfo>(
				storage.findNeighbors((ClientID) id, aoi));
		return result;
	}

}
