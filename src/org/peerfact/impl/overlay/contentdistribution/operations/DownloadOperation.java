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
import org.peerfact.impl.overlay.contentdistribution.messages.DownloadRequestMsg;
import org.peerfact.impl.overlay.contentdistribution.messages.DownloadResultMsg;
import org.peerfact.impl.simengine.Simulator;


/**
 * This operation contains a whole download operation consisting of a download
 * request and a download result message.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class DownloadOperation extends
		AbstractOperation<DefaultContentDistribution, Document<OverlayKey<?>>>
		implements TransMessageCallback {

	List<TransInfo> peers;

	OverlayKey<?> key;

	private DefaultContentDistribution distStrategy;

	/**
	 * Document to be downloaded.
	 */
	private Document<OverlayKey<?>> document;

	public DownloadOperation(DefaultContentDistribution distStrategy,
			List<TransInfo> peers, OverlayKey<?> key,
			OperationCallback<Document<OverlayKey<?>>> callback) {
		super(distStrategy, callback);
		this.peers = peers;
		this.key = key;
		this.distStrategy = distStrategy;
	}

	@Override
	public void execute() {
		assert peers != null;
		assert !peers.isEmpty();
		TransInfo peer = peers.get(0);// TODO try next peers if download
		// fails?
		DownloadRequestMsg downloadRequest = new DownloadRequestMsg(key);
		log.info("Try to download " + key + " from " + peer);
		distStrategy.getTransLayer().sendAndWait(downloadRequest, peer,
				distStrategy.getPort(), TransProtocol.UDP, this,
				2 * Simulator.SECOND_UNIT);
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
		if (msg instanceof DownloadResultMsg) {
			DownloadResultMsg res = (DownloadResultMsg) msg;
			log.debug("received reply for key " + res.getDoc().getKey());
			document = res.getDoc();
			distStrategy.storeDocument(document);
			log.info("RECEIVED REQUESTED DOCUMENT " + document);
			operationFinished(true);
		} else {
			log.warn("Unknown msg received " + msg
					+ " wait for DownloadResult");
		}
	}

	@Override
	public Document<OverlayKey<?>> getResult() {
		return document;
	}

}
