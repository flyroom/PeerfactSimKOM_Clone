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

package org.peerfact.impl.overlay.informationdissemination.mercury;

import java.awt.Point;
import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryListener;
import org.peerfact.impl.service.publishsubscribe.mercury.attribute.IMercuryAttribute;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryPayload;
import org.peerfact.impl.util.logging.SimLogger;


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
 * The implementation of the {@link MercuryListener} for IDO Mercury.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/28/2011
 * 
 */
public class MercuryIDOListener implements MercuryListener {
	/**
	 * The logger for this class
	 */
	final static Logger log = SimLogger.getLogger(MercuryIDOListener.class);

	/**
	 * The mercury ido node, which has this listener created.
	 */
	private MercuryIDONode node;

	/**
	 * Creates an Listener and set the Mercury IDO node.
	 * 
	 * @param node
	 *            The node, which has this Listener create.
	 */
	public MercuryIDOListener(MercuryIDONode node) {
		this.node = node;
	}

	@Override
	public void notificationReceived(MercuryPayload payload,
			List<IMercuryAttribute> attributes) {
		if (payload instanceof MercuryIDOPayload) {
			MercuryIDOPayload mPayload = (MercuryIDOPayload) payload;

			// take x and y from the payload.
			Integer x = null;
			Integer y = null;
			for (IMercuryAttribute attr : attributes) {
				if (attr.getName().equals("x")) {
					x = (Integer) attr.getValue();
				}
				if (attr.getName().equals("y")) {
					y = (Integer) attr.getValue();
				}
			}
			if (x == null || y == null) {
				log.error("x or y are not in payload! Abort the notification handling");
				return;
			}
			Point position = new Point(x, y);
			int aoi = mPayload.getAoi();
			OverlayID<BigInteger> id = mPayload.getId();

			// store the received information
			node.getStorage().addNodeInfo(
					new MercuryNodeInfo(position, aoi, id));
		}
	}
}
