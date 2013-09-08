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

package org.peerfact.impl.application.kbrapplication;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math.random.RandomGenerator;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.application.kbrapplication.messages.AnnounceNewDocumentMessage;
import org.peerfact.impl.application.kbrapplication.messages.QueryForDocumentMessage;
import org.peerfact.impl.application.kbrapplication.messages.QueryResultMessage;
import org.peerfact.impl.application.kbrapplication.messages.RequestDocumentMessage;
import org.peerfact.impl.application.kbrapplication.messages.TransferDocumentMessage;
import org.peerfact.impl.application.kbrapplication.operations.AnnounceNewDocumentOperation;
import org.peerfact.impl.application.kbrapplication.operations.QueryForDocumentOperation;
import org.peerfact.impl.application.kbrapplication.operations.QueryResultOperation;
import org.peerfact.impl.application.kbrapplication.operations.RequestDocumentOperation;
import org.peerfact.impl.application.kbrapplication.operations.TransferDocumentOperation;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.simengine.Simulator;


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
 * This is a dummy application to check some basic functionality of KBR
 * overlays.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KBRDummyApplication extends AbstractApplication implements
		KBRListener<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> {

	private final KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> node;

	private final LinkedHashMap<OverlayKey<?>, OverlayContact<OverlayID<?>>> localDB = new LinkedHashMap<OverlayKey<?>, OverlayContact<OverlayID<?>>>();

	private final OverlayID<?> localOverlayID;

	private static RandomGenerator rGen = Simulator.getRandom();

	/**
	 * @param node
	 */
	public KBRDummyApplication(
			KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> node) {
		this.node = node;
		localOverlayID = node.getLocalOverlayContact().getOverlayID();

		// Set the application as KBRListener
		node.setKBRListener(this);
	}

	@Override
	public void deliver(OverlayKey<?> key, Message msg) {
		if (key == null) {
			// It is a direct message to this node

			if (msg instanceof QueryResultMessage) {
				QueryResultMessage queryResMsg = (QueryResultMessage) msg;
				log.debug(localOverlayID + " - Received query result for rank "
						+ queryResMsg.getKey() + " from "
						+ queryResMsg.getSenderContact().getOverlayID());

				if (queryResMsg.getDocumentProvider() != null) {
					log.debug(localOverlayID + " - Got a query result for key "
							+ queryResMsg.getKey() + ": File provider is "
							+ queryResMsg.getDocumentProvider().getOverlayID());

					RequestDocumentOperation operation = new RequestDocumentOperation(
							this, queryResMsg.getKey(),
							(OverlayContact<OverlayID<?>>) queryResMsg
									.getDocumentProvider(),
							Operations.EMPTY_CALLBACK);

					operation.scheduleImmediately();

				} else {
					log.debug(localOverlayID + " - Got a query result for key "
							+ queryResMsg.getKey() + ": The root of the key ("
							+ queryResMsg.getSenderContact().getOverlayID()
							+ ") does not know about such a document.");
				}

			} else if (msg instanceof RequestDocumentMessage) {
				RequestDocumentMessage requestDocumentMsg = (RequestDocumentMessage) msg;
				log.debug(localOverlayID + " - Received request for document: "
						+ requestDocumentMsg.getKeyOfDocument());

				TransferDocumentOperation operation = new TransferDocumentOperation(
						this, requestDocumentMsg.getKeyOfDocument(),
						(OverlayContact<OverlayID<?>>) requestDocumentMsg
								.getSenderContact(),
						Operations.EMPTY_CALLBACK);

				operation.scheduleImmediately();
			} else if (msg instanceof TransferDocumentMessage) {

				TransferDocumentMessage transferDocumentMsg = (TransferDocumentMessage) msg;

				log.debug(localOverlayID + " - Received requested document: "
						+ transferDocumentMsg.getKeyOfDocument());
			}

		} else if (node.isRootOf(key)) {
			// It is a message delivered here because this is the root of the
			// key

			if (msg instanceof AnnounceNewDocumentMessage) {
				// As this message was delivered to this node, it must be the
				// root of the key.
				// So we store the information about the key and the node that
				// holds the key.

				AnnounceNewDocumentMessage announceMsg = (AnnounceNewDocumentMessage) msg;

				// Store key of file and sender of message in local database
				localDB.put(announceMsg.getDocumentKey(),
						(OverlayContact<OverlayID<?>>) announceMsg
								.getSenderContact());

				log.debug(localOverlayID
						+ " - Stored information about new file (Key: "
						+ announceMsg.getDocumentKey() + ")");
			} else if (msg instanceof QueryForDocumentMessage) {
				// As this message was delivered to this node, it must be the
				// root of the

				QueryForDocumentMessage queryMessage = (QueryForDocumentMessage) msg;
				log.debug(localOverlayID
						+ " - Received query message for rank:"
						+ queryMessage.getKey());

				OverlayContact<OverlayID<?>> documentProvider = (OverlayContact<OverlayID<?>>) lookupDocumentProviderInLocalDB(queryMessage
						.getKey());

				OverlayContact<OverlayID<?>> receiver = queryMessage
						.getSenderContact();
				QueryResultOperation operation = new QueryResultOperation(this,
						queryMessage.getKey(), documentProvider, receiver,
						Operations.EMPTY_CALLBACK);

				operation.scheduleImmediately();
			}
		} else {
			log.error(localOverlayID
					+ " - I received a message that is not for me!");
		}

	}

	@Override
	public void forward(
			KBRForwardInformation<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> information) {
		// Nothing to do here
	}

	@Override
	public void update(OverlayContact<OverlayID<?>> contact, boolean joined) {
		if (joined) {
			log.debug(contact.getOverlayID() + " - joined");
		} else {
			log.debug(contact.getOverlayID() + " - left");
		}
	}

	/**
	 * @return the KBR node on which the application is running
	 */
	public KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> getNode() {
		return node;
	}

	/**
	 * Stores an new document and announces the existence in the overlay.
	 * Documents have no names but ranks that identifies them.
	 * 
	 * @param rank
	 *            the rank oft the new document
	 * @param size
	 *            the size in bytes of the new document
	 */
	public void storeNewFile(int rank, int size) {
		// Create the document
		OverlayKey<?> key = node.getNewOverlayKey(rank);
		storeNewFile(key, size);
	}

	private void storeNewFile(OverlayKey<?> key, int size) {
		log.debug(localOverlayID + " - Store new file initiated (rank:" + key
				+ " size:" + size + ")");
		KBRDocument doc = new KBRDocument(key);
		doc.setSize(size);

		AnnounceNewDocumentOperation operation = new AnnounceNewDocumentOperation(
				this, doc, Operations.EMPTY_CALLBACK);
		operation.scheduleImmediately();
	}

	public void storeRandomNewFile() {

		OverlayKey<?> ranKey = getNode().getRandomOverlayKey();
		int ranSize = rGen.nextInt(200);

		storeNewFile(ranKey, ranSize);
	}

	/**
	 * Queries for document and transfers it to this host.
	 * 
	 * @param rank
	 *            the rank that identifies the document
	 */
	public void getDocument(int rank) {
		OverlayKey<?> key = node.getNewOverlayKey(rank);
		log.debug(localOverlayID
				+ " - Start procedure to get the document with rank " + rank
				+ " (key: " + key + ")");

		getDocument(key);
	}

	private void getDocument(OverlayKey<?> key) {
		QueryForDocumentOperation operation = new QueryForDocumentOperation(
				this, key, Operations.EMPTY_CALLBACK);
		operation.scheduleImmediately();
	}

	public void getRandomDocument() {
		OverlayKey<?> ranKey = getNode().getRandomOverlayKey();

		getDocument(ranKey);
	}

	private OverlayContact<?> lookupDocumentProviderInLocalDB(OverlayKey<?> key) {
		Set<Entry<OverlayKey<?>, OverlayContact<OverlayID<?>>>> dbAsSet = localDB
				.entrySet();

		// return localDB.get(key);

		for (Entry<OverlayKey<?>, OverlayContact<OverlayID<?>>> entry : dbAsSet) {
			if (entry.getKey().compareTo((OverlayKey) key) == 0) {
				return entry.getValue();
			}
		}
		return null;
	}

}
