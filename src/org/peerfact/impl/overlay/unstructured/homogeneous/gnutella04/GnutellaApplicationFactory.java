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

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;

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
public class GnutellaApplicationFactory implements ComponentFactory {

	// private final static short port = 123;
	// private static long id = 0;

	private double propUp;

	private double propDel;

	private double propDown;

	private long downloadDelay;

	// private int numConn;
	// private long delayAcceptConnection;
	// private long refresh;
	// private long contactTimeout;
	// private long descriptorTimeout;

	@Override
	public Component createComponent(Host host) {
		// GnutellaOverlayNode node = newGnutellaOverlayNode(host);
		GnutellaOverlayNode node = (GnutellaOverlayNode) host
				.getOverlay(GnutellaOverlayNode.class);

		return new GnutellaApplication(node, this.propUp, this.propDel,
				this.propDown, this.downloadDelay);
	}

	// public GnutellaOverlayNode newGnutellaOverlayNode(Host host) {
	// return new GnutellaOverlayNode(host.getTransLayer(),
	// newGnutellaOverlayID(), this.numConn, this.delayAcceptConnection,
	// this.refresh, this.contactTimeout, this.descriptorTimeout, port);
	// }

	// public GnutellaOverlayID newGnutellaOverlayID() {
	// this.id += 1;
	// return new GnutellaOverlayID(BigInteger.valueOf(this.id));
	// }

	public void setPropUp(double propUp) {
		this.propUp = propUp;
	}

	public void setPropDel(double propDel) {
		this.propDel = propDel;
	}

	public void setPropDown(double propDown) {
		this.propDown = propDown;
	}

	public void setDownloadDelay(long downloadDelay) {
		this.downloadDelay = downloadDelay;
	}

	// public void setNumConn(int numConn) {
	// this.numConn = numConn;
	// }
	//
	// public void setDelayAcceptConnection(long delayAcceptConnection) {
	// this.delayAcceptConnection = delayAcceptConnection;
	// }
	//
	// public void setRefresh(long refresh) {
	// this.refresh = refresh;
	// }
	//
	// public void setContactTimeout(long contactTimeout) {
	// this.contactTimeout = contactTimeout;
	// }
	//
	// public void setDescriptorTimeout(long descriptorTimeout) {
	// this.descriptorTimeout = descriptorTimeout;
	// }

}
