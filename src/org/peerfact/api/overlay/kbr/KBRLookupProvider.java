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

package org.peerfact.api.overlay.kbr;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupReplyMsg;

public interface KBRLookupProvider<T extends OverlayID<?>, S extends OverlayContact<T>, K extends OverlayKey<?>> {

	/**
	 * This method enables a lookup of the node responsible for an
	 * <code>OverlayKey</code>. To perform the lookup the possibility of
	 * <code>KBR</code> compatible overlays are used. The result of the lookup
	 * is handed over to the caller through the given callback.
	 * 
	 * @param key
	 *            the key to be looked up
	 * @param callback
	 *            the callback for handing over the result of the lookup
	 */
	public abstract void lookupKey(K key, OperationCallback<S> callback);

	public abstract void lookupRequestArrived(KBRLookupMsg<T, S> msg);

	public abstract void lookupReplyArrived(KBRLookupReplyMsg<T, S> msg);

}