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

package org.peerfact.impl.service.dhtstorage.storage;

import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.impl.service.dhtstorage.AbstractDHTService;

/**
 * Provides a Storage for DHT-Objects, mainly to act as a replacement for
 * LinkedHashMaps in OverlayNodes if DHTServices are to be used. It is used to
 * preserve compatibility to older configuration files which do not specify a
 * DHTService but instead rely on the implementation of the specific Overlay
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SimpleDHTService<K extends DHTKey<?>> extends
		AbstractDHTService<K> {

	public SimpleDHTService() {
		super(null);
	}

}
