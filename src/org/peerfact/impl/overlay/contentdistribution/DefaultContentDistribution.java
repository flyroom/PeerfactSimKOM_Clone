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

package org.peerfact.impl.overlay.contentdistribution;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.api.overlay.cd.Document;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.contentdistribution.messages.DownloadRequestMsg;
import org.peerfact.impl.overlay.contentdistribution.messages.DownloadResultMsg;
import org.peerfact.impl.overlay.contentdistribution.messages.UploadMsg;
import org.peerfact.impl.overlay.contentdistribution.messages.UploadResultMsg;
import org.peerfact.impl.overlay.contentdistribution.operations.DownloadOperation;
import org.peerfact.impl.overlay.contentdistribution.operations.UploadOperation;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * Very simple distribution strategy, which waits for incoming download requests
 * and answers them as they come.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 3.0, 30.11.2007
 * 
 */
public class DefaultContentDistribution extends
		AbstractOverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>>
		implements TransMessageListener, ContentDistribution<OverlayKey<?>> {

	static final Logger log = SimLogger
			.getLogger(DefaultContentDistribution.class);

	Map<OverlayKey<?>, Document<OverlayKey<?>>> documents = new LinkedHashMap<OverlayKey<?>, Document<OverlayKey<?>>>();

	private Host host;

	private TransInfo transInfo;

	private TransLayer transLayer;

	public DefaultContentDistribution(TransLayer trans, short port) {
		super(null, port);
		if (trans == null) {
			throw new IllegalStateException("TransLayer not initialized");
		}
		this.transLayer = trans;
		this.transLayer.addTransMsgListener(this, port);
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof DownloadRequestMsg) {
			DownloadRequestMsg req = (DownloadRequestMsg) msg;
			log.debug("received request for doc " + req.getKey());
			if (containsDocument(req.getKey())) {
				Document<OverlayKey<?>> copy = loadDocument(req.getKey())
						.copy();
				getTransLayer().sendReply(new DownloadResultMsg(copy),
						receivingEvent, this.getPort(), TransProtocol.UDP);
			} else {
				log.error("document with key " + req.getKey()
						+ " not available at node");
			}

		} else if (msg instanceof UploadMsg) {
			UploadMsg uploadMsg = (UploadMsg) msg;
			log.debug("receive upload for doc " + uploadMsg.getDoc());
			Document<OverlayKey<?>> doc = uploadMsg.getDoc();
			storeDocument(doc);
			getTransLayer().sendReply(new UploadResultMsg(doc.getKey()),
					receivingEvent, this.getPort(), TransProtocol.UDP);
		} else {
			log.warn("Received unknown msg type: " + msg.getClass().getName()
					+ " accept only " + DownloadRequestMsg.class.getName()
					+ " and " + UploadMsg.class.getName());
		}
	}

	@Override
	public Collection<Document<OverlayKey<?>>> listDocuments() {
		return new LinkedHashSet<Document<OverlayKey<?>>>(documents.values());
	}

	@Override
	public void storeDocument(Document<OverlayKey<?>> doc) {
		OverlayKey<?> key = doc.getKey();
		documents.put(key, doc);
		log.debug("store " + doc + " at " + this);
	}

	@Override
	public Document<OverlayKey<?>> loadDocument(OverlayKey<?> key) {
		return documents.get(key);
	}

	@Override
	public Collection<OverlayKey<?>> listDocumentKeys() {
		return Collections.unmodifiableSet(documents.keySet());
	}

	@Override
	public boolean containsDocument(OverlayKey<?> key) {
		return documents.containsKey(key);
	}

	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public int downloadDocument(OverlayKey<?> key, List<TransInfo> peers,
			OperationCallback<Document<OverlayKey<?>>> callback) {
		DownloadOperation op = new DownloadOperation(this, peers, key, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public int uploadDocument(Document<OverlayKey<?>> document,
			List<TransInfo> peers, OperationCallback<List<TransInfo>> callback) {
		UploadOperation op = new UploadOperation(this, peers, document,
				callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	@Override
	public TransInfo getTransInfo() {
		if (this.transInfo == null) {
			this.transInfo = transLayer.getLocalTransInfo(getPort());
		}
		return this.transInfo;
	}

	@Override
	public String toString() {
		return "Content Distribution (" + getTransInfo() + ")";
	}

	@Override
	public TransLayer getTransLayer() {
		return this.transLayer;
	}

	@Override
	public NeighborDeterminator<OverlayContact<OverlayID<?>>> getNeighbors() {
		// no neighbors supported
		return null;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		// nothing to do
	}

}
