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

package org.peerfact.impl.overlay.informationdissemination.von;

import java.util.List;
import java.util.Vector;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.api.overlay.ido.IDOOracle;
import org.peerfact.impl.overlay.informationdissemination.von.voronoi.Voronoi;


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
public class VonOracle implements IDOOracle {

	private Voronoi globalVoronoi;

	public VonOracle() {
		this.globalVoronoi = new Voronoi(VonID.EMPTY_ID);
	}

	@Override
	public void insertNodeInfos(List<IDONodeInfo> nodeInfos) {
		for (IDONodeInfo info : nodeInfos) {
			VonNodeInfo temp = new VonNodeInfo(new VonContact(
					(VonID) info.getID(), null), info.getPosition(),
					info.getAoiRadius());
			globalVoronoi.insert(temp, 0l);
		}

	}

	@Override
	public void reset() {
		globalVoronoi = new Voronoi(VonID.EMPTY_ID);
	}

	@Override
	public List<IDONodeInfo> getAllNeighbors(OverlayID<?> id, int aoi) {
		VonNodeInfo[] neighbors = globalVoronoi
				.getVonNeighbors((VonID) id, aoi);
		List<IDONodeInfo> result = new Vector<IDONodeInfo>(neighbors.length);
		for (VonNodeInfo vonNodeInfo : neighbors) {
			result.add(vonNodeInfo);
		}
		return result;
	}
}
