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

package org.peerfact.impl.overlay.kbr;

import java.util.LinkedHashMap;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupReplyMsg;
import org.peerfact.impl.overlay.kbr.operations.KBRLookupOperation;


/**
 * This abstract class augments the original <code>KBR</code> interface with a
 * generic key lookup mechanism based on the interface's methods.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * @version 05/06/2011
 */
public class KBRLookupProviderImpl<T extends OverlayID<?>, S extends OverlayContact<T>, K extends OverlayKey<?>> implements KBRLookupProvider<T, S, K> {

	private LinkedHashMap<Integer, KBRLookupOperation<T, S, K>> openLookupRequest = new LinkedHashMap<Integer, KBRLookupOperation<T, S, K>>();

	private KBRNode<T, S, K> kbrNode;

	/**
	 * @param kbrNode
	 *            the <code>KBR</code> node that belongs to the instance of this
	 *            class and wants to use the lookup functionality.
	 */
	public KBRLookupProviderImpl(KBRNode<T, S, K> kbrNode) {
		this.kbrNode = kbrNode;
	}

	/* (non-Javadoc)
	 * @see org.peerfact.impl.overlay.kbr.KBRLookupProvider#lookupKey(K, org.peerfact.api.common.OperationCallback)
	 */
	@Override
	public void lookupKey(K key, OperationCallback<S> callback) {

		KBRLookupOperation<T, S, K> op = new KBRLookupOperation<T, S, K>(
				kbrNode,
				key, callback);
		op.scheduleImmediately();

		/*
		 * Add this request to the list of open requests
		 */
		openLookupRequest.put(op.getOperationID(), op);

	}

	/* (non-Javadoc)
	 * @see org.peerfact.impl.overlay.kbr.KBRLookupProvider#lookupRequestArrived(org.peerfact.impl.overlay.kbr.messages.KBRLookupMsg)
	 */
	@Override
	public void lookupRequestArrived(KBRLookupMsg<T, S> msg) {

		S olContact = msg.getSenderContact();

		KBRLookupReplyMsg<T, S> replyMsg = new KBRLookupReplyMsg<T, S>(
				kbrNode.getLocalOverlayContact(), msg.getOperationID());

		/*
		 * Route the reply direct to the sender by using the method "route" and
		 * giving <code>null</code> as key and the senders contact as hint. This
		 * results in a direct message.
		 */
		kbrNode.route(null, replyMsg, olContact);
	}

	/* (non-Javadoc)
	 * @see org.peerfact.impl.overlay.kbr.KBRLookupProvider#lookupReplyArrived(org.peerfact.impl.overlay.kbr.messages.KBRLookupReplyMsg)
	 */
	@Override
	public void lookupReplyArrived(KBRLookupReplyMsg<T, S> msg) {

		S olContact = msg.getSenderContact();

		KBRLookupOperation<T, S, K> pendingOp = openLookupRequest.get(msg
				.getOperationID());

		if (pendingOp != null) {
			pendingOp.answerArrived(olContact);
		}
	}

}
