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

package org.peerfact.impl.overlay.informationdissemination.mercury.operation;

import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.informationdissemination.mercury.MercuryIDOConfiguration;
import org.peerfact.impl.overlay.informationdissemination.mercury.MercuryIDONode;
import org.peerfact.impl.overlay.informationdissemination.mercury.NeighborStorage;

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
 * Clean old entries from the {@link NeighborStorage}. Old entries are: <br>
 * 
 * <ul>
 * <li>Not more in AOI of the node</li>
 * <li>Time is expired of the information. This mean, the information is to old.
 * </li>
 * </ul>
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/20/2011
 */
public class CleanOperation extends AbstractOperation<MercuryIDONode, Object> {

	/**
	 * Node, which started this operation
	 */
	private MercuryIDONode node;

	public CleanOperation(MercuryIDONode node) {
		super(node);
		this.node = node;
	}

	@Override
	protected void execute() {
		if (node.getStorage() != null) {
			node.getStorage().removeExpiredNodeInfos(
					MercuryIDOConfiguration.TIME_TO_VALID_OF_NODE_INFOS);
			node.getStorage().removeNotInAOINodeInfos(node.getPosition(),
					node.getAOI());
		}
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// Nothing to get Back.
		return null;
	}

}
