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

import java.io.Serializable;

import org.peerfact.api.common.Transmitable;
import org.peerfact.api.transport.TransInfo;

/**
 * An <code>OverlayContact</code> encapsulates the TransportAddress and the
 * OverlayID of an OverlayAgent in the system. The <code>OverlayContact</code>
 * equals the terminology of a nodehandle described in the paper of Dabek et al
 * (Towards a Common API for Structured Peer-to-Peer Overlays).
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 @param <T>
 *            The exact type of the overlay id, that should be overlay specific,
 *            e.g. PastryOverlayID
 */

/**
 * @author Thim
 * @version 1.0, mm/dd/2012
 * @param <T>
 */
public interface OverlayContact<T extends OverlayID<?>> extends
		Transmitable, Serializable {
	/**
	 * Returns the OverlayID of a particular OverlayAgent
	 * 
	 * @return the OverlayID of a particular OverlayAgent
	 */
	public T getOverlayID();

	/**
	 * Returns the TransportAddress of a particular OverlayAgent
	 * 
	 * @return the TransportAddress of a particular OverlayAgent
	 */
	public TransInfo getTransInfo();
}
