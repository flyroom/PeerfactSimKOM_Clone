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

package org.peerfact.impl.overlay.dht.chord.base.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;


/**
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordOverlayUtil {

	/**
	 * This method returns the responsible node for key
	 * 
	 * @param peerList
	 *            : the current participants
	 * @param key
	 *            : searching key
	 * @return responsible node contact for the key
	 */
	public static AbstractChordContact getResponsibleNodeContact(
			List<AbstractChordContact> peerList, BigInteger key) {

		ArrayList<AbstractChordContact> copyList = new ArrayList<AbstractChordContact>(
				peerList);
		ArrayList<AbstractChordContact> sortedContacts = new ArrayList<AbstractChordContact>(
				copyList);
		Collections.sort(sortedContacts);

		for (int index = 0; index < sortedContacts.size(); index++) {
			if (sortedContacts.get(index).getOverlayID().getValue()
					.compareTo(key) >= 0) {
				return sortedContacts.get(index);
			}
		}
		// return the first node
		return sortedContacts.get(0);
	}

	/**
	 * This method returns the responsible node for a key
	 * 
	 * @param peerList
	 *            : the current participants
	 * @param key
	 *            : searching key
	 * @return responder node for the key
	 */
	public static AbstractChordNode getResponsibleNode(
			List<AbstractChordNode> peerList,
			BigInteger key) {

		ArrayList<AbstractChordContact> contactList = new ArrayList<AbstractChordContact>();
		for (AbstractChordNode node : peerList) {
			contactList.add(node.getLocalOverlayContact());
		}
		AbstractChordContact contact = getResponsibleNodeContact(contactList,
				key);
		for (AbstractChordNode node : peerList) {
			if (node.getLocalOverlayContact().equals(contact)) {
				return node;
			}
		}
		return null;
	}

}
