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

package org.peerfact.impl.overlay.contentdistribution.operations;

import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.Document;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.contentdistribution.DefaultContentDistribution;
import org.peerfact.impl.overlay.contentdistribution.messages.UploadMsg;
import org.peerfact.impl.overlay.contentdistribution.messages.UploadResultMsg;


/**
 * This operation contains a whole upload operation consisting of an upload
 * message.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class UploadOperation extends
		AbstractOperation<DefaultContentDistribution, List<TransInfo>>
		implements TransMessageCallback {

	List<TransInfo> peers;

	private DefaultContentDistribution distStrategy;

	/**
	 * Document to be uploaded.
	 */
	private Document<OverlayKey<?>> document;

	public UploadOperation(DefaultContentDistribution distStrategy,
			List<TransInfo> peers, Document<OverlayKey<?>> document,
			OperationCallback<List<TransInfo>> callback) {
		super(distStrategy, callback);
		this.peers = peers;
		this.document = document;
		this.distStrategy = distStrategy;
	}

	@Override
	public void execute() {
		assert peers != null;
		assert !peers.isEmpty();

		for (TransInfo peer : peers) {
			// upload document to all peers
			UploadMsg uploadMsg = new UploadMsg(document);
			log.info("Try to upload " + document + " to " + peer);
			distStrategy.getTransLayer().sendAndWait(uploadMsg, peer,
					distStrategy.getPort(), TransProtocol.UDP, this,
					-1);
		}
	}

	@Override
	protected void operationTimeoutOccured() {
		operationFinished(false);
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		operationFinished(false);
	}

	@Override
	public void receive(Message msg, TransInfo senderAddr, int commId) {
		if (msg instanceof UploadResultMsg) {
			UploadResultMsg res = (UploadResultMsg) msg;
			log.debug("received reply for key " + res.getKey());
			operationFinished(true);
		} else {
			log.warn("Unknown msg received " + msg
					+ " wait for UploadResult");
		}
	}

	@Override
	public List<TransInfo> getResult() {
		return peers;
	}

}
