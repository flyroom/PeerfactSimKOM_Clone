/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.overlay.dht.centralizedstorage.messages;

import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayID;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayKey;

public class StoreRequestMsg extends AbstractRequestMsg<CSOverlayID> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 470242206584514969L;

	transient DHTObject value;

	public StoreRequestMsg(CSOverlayID sender,
			CSOverlayID receiver,
			CSOverlayKey key, DHTObject value, TransInfo nextHop) {
		super(sender, receiver, key);
		this.value = value;
	}

	public CSOverlayKey getKey() {
		return (CSOverlayKey) getContent();
	}

	public DHTObject getValue() {
		return value;
	}

	@Override
	public long getSize() {
		return super.getSize() + value.getTransmissionSize();
	}

}
