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

import java.util.Set;

import org.peerfact.Constants;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSContact;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSOverlayID;


/**
 * Simple ACK for a successfull store operation.
 * 
 * @author Kosta
 * 
 */
public class StoreResultMsg extends AbstractReplyMsg<CSOverlayID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8176868020587014106L;

	public static final String STORE_SUCCEEDED = "STORE_SUCCEEDED";

	public StoreResultMsg(StoreRequestMsg query) {
		super(query, STORE_SUCCEEDED);
	}

	public Set<CSContact> getDescription() {
		return (Set<CSContact>) getResult();
	}

	@Override
	public long getSize() {
		return super.getSize() + Constants.CHAR_SIZE * 15;
	}
}
