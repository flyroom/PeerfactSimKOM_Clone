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

package org.peerfact.api.overlay.unstructured;

import org.peerfact.api.overlay.JoinLeaveOverlayNode;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;

/**
 * Currently, unstructured overlays should implement this interface to be marked
 * as unstructured overlays. Later versions of this interface will probably
 * define some general functionality as e.g. <code>DHTNode</code>.
 * 
 * @author Sebastian Kaune (extended by Dominik Stingl)
 *         <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface UnstructuredOverlayNode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends JoinLeaveOverlayNode<T, S> {
	// marker interface
}
