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

package org.peerfact.impl.application.filesharing;

import java.util.List;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.transport.TransInfo;

/**
 * Represents a document announcement in a DHT.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @author Thim Strothmann
 * 
 * 
 */
public class Torrent implements DHTObject {

	private OverlayKey<?> documentKey;

	private List<OverlayContact<?>> ownerList;

	/**
	 * Creates new torrent.
	 * 
	 * @param documentKey
	 *            - key of the document to announce
	 * @param ownerID
	 *            - overlay id of the owning peer
	 * @param ownerInfo
	 *            - trans info of the owner
	 */
	public Torrent(OverlayKey<?> documentKey, List<OverlayContact<?>> ownerList) {
		this.documentKey = documentKey;
		this.ownerList = ownerList;
	}

	/**
	 * 
	 * @return key of the announced document
	 */
	public OverlayKey<?> getKey() {
		return documentKey;
	}

	/**
	 * Returns the Transinfo of the first owner.
	 * 
	 * @return overlay id of the owning peer
	 */
	public TransInfo getOwnerInfo() {
		return ownerList.get(0).getTransInfo();
	}

	/**
	 * Returns the OverlayID of the first owner
	 * 
	 * @return trans info of the owner
	 */
	public OverlayID<?> getOwnerID() {
		return ownerList.get(0).getOverlayID();
	}

	@Override
	public String toString() {
		return "Torrent {DocKey: " + this.documentKey + " FirstOwnerInfo: "
				+ ownerList.get(0).getTransInfo() + " FirstOwnerOverlayID: "
				+ ownerList.get(0).getOverlayID() + "}";
	}

	@Override
	public long getTransmissionSize() {
		return documentKey.getTransmissionSize() + ownerList.size()
				* ownerList.get(0).getTransInfo().getTransmissionSize();
	}

}
