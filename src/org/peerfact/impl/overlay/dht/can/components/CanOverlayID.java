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

package org.peerfact.impl.overlay.dht.can.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import org.jfree.util.Log;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.util.toolkits.HashToolkit;

/**
 * Creates the ID of the peer. Implements OverlayID. The ID is created as a
 * hashvalue of the TransInfo and is stored as a BigInteger.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanOverlayID implements OverlayID<BigInteger> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4381021412256486863L;

	private BigInteger id;

	/**
	 * Creates a new id, from the hash value of the TransInfo
	 * 
	 * @param transInfo
	 */
	public CanOverlayID(TransInfo transInfo) {
		this.id = HashToolkit.getSHA1Hash(transInfo, 20);
	}

	/**
	 * Creates a new id from the param
	 * 
	 * @param id
	 *            gives the new id.
	 */
	public CanOverlayID(BigInteger id) {
		this.id = id;
	}

	@Override
	public byte[] getBytes() {
		byte[] output = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();
			output = bos.toByteArray();

		} catch (IOException e) {
			Log.error("IOException!", e);
		}
		return output;
	}

	@Override
	public BigInteger getUniqueValue() {
		return id;
	}

	public BigInteger getValue() {
		return id;
	}

	public void setID(BigInteger newID) {
		this.id = newID;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	/**
	 * Compares two IDs.
	 */
	@Override
	public int compareTo(OverlayID<BigInteger> arg0) {
		CanOverlayID ID = (CanOverlayID) arg0;
		return this.id.compareTo(ID.getValue());
	}

	@Override
	public long getTransmissionSize() {
		return getBytes().length;
	}
}
