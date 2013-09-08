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

package org.peerfact.impl.overlay.unstructured.zeroaccess.message;

import java.util.LinkedList;

import org.peerfact.impl.overlay.unstructured.zeroaccess.ZeroAccessOverlayContact;
import org.peerfact.impl.overlay.unstructured.zeroaccess.ZeroAccessOverlayID;

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
public class RetLMessage extends BaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6242668576201187183L;

	private LinkedList<ZeroAccessOverlayContact> contacts = new LinkedList<ZeroAccessOverlayContact>();

	public RetLMessage(ZeroAccessOverlayID sender,
			ZeroAccessOverlayID receiver,
			LinkedList<ZeroAccessOverlayContact> contacts) {
		super(sender, receiver);
		this.contacts = contacts;
	}

	@Override
	public long getSize() {
		return super.getSize();
	}

	public LinkedList<ZeroAccessOverlayContact> getContacts()
	{
		return this.contacts;
	}

}
