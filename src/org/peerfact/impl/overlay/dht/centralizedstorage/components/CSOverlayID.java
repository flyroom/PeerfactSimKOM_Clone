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

package org.peerfact.impl.overlay.dht.centralizedstorage.components;

import org.peerfact.Constants;
import org.peerfact.api.overlay.OverlayID;

/**
 * OverlayID for the centralized DHT. There are only two distinct ids - server
 * id and client id.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 */
public class CSOverlayID implements OverlayID<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8323216854772037397L;

	// TransportAddress addr;
	boolean isServer = false;

	CSOverlayID(boolean isServer) {
		// this.addr = new DefaultTransportAddress(netID, port);
		this.isServer = isServer;
	}

	// public TransportAddress getAddress(){
	// return this.addr;
	// }

	@Override
	public byte[] getBytes() {
		// return
		// (addr.getNetId().toString()+Short.toString(addr.getPort())).getBytes();
		return null;
	}

	@Override
	public Object getUniqueValue() {
		return this;
	}

	@Override
	public int compareTo(OverlayID<Object> o) {
		throw new UnsupportedOperationException("implement it");
	}

	@Override
	public String toString() {
		if (isServer) {
			return "server";
		} else {
			return "client";
		}
	}

	boolean isServer() {
		return isServer;
	}

	@Override
	public long getTransmissionSize() {
		return Constants.BOOLEAN_SIZE;
	}

}
