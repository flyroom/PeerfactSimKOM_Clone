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

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.unstructured.zeroaccess.components.ZeroAccessOverlayID;

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
public class GetLMessage extends BaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6242668576201187183L;

	private OverlayContact<ZeroAccessOverlayID> contact;

	private long bot_software_version;

	private boolean recheck = false;

	public GetLMessage(ZeroAccessOverlayID sender,
			ZeroAccessOverlayID receiver,
			OverlayContact<ZeroAccessOverlayID> contact,
			long bot_software_version) {
		super(sender, receiver);
		this.contact = contact;
		this.bot_software_version = bot_software_version;
	}

	public boolean isRecheck() {
		return recheck;
	}

	public GetLMessage(ZeroAccessOverlayID sender,
			ZeroAccessOverlayID receiver,
			OverlayContact<ZeroAccessOverlayID> contact, boolean recheck_par,
			long bot_software_version) {
		super(sender, receiver);
		this.contact = contact;
		this.recheck = recheck_par;
		this.bot_software_version = bot_software_version;
	}

	public OverlayContact<ZeroAccessOverlayID> getContact() {
		return contact;
	}

	public long getBot_software_version() {
		return bot_software_version;
	}

	@Override
	public long getSize() {
		return super.getSize();
	}
}
