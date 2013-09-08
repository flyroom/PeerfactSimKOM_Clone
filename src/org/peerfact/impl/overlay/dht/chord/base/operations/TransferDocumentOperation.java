/*
 * Copyright (c) UPB - University of Paderborn
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.base.operations;

import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordConfiguration;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.overlay.dht.chord.base.messages.InitializeDownloadMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.InitializeDownloadReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.StartDownloadMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.TransferDownloadMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.TransferDownloadReplyMessage;
import org.peerfact.impl.simengine.Simulator;


/**
 * This Operation is called to transfer an object. from the receiver to the
 * sender
 * 
 * @author Philip Wette <info@peerfact.org>
 * @version 1.0, 06/21/2011
 */
public class TransferDocumentOperation extends
		AbstractChordOperation<List<AbstractChordContact>> implements
		TransMessageCallback {

	/**
	 * sender of the message
	 */
	AbstractChordContact sender;

	/**
	 * receiver of the message
	 */
	AbstractChordContact receiver;

	/**
	 * id of the document.
	 */
	ChordKey objectId;

	AbstractChordNode owner;

	long receivedData = 0;

	long totalData = 0;

	int numRetrys = 0;

	double currentDataRate;

	long sizeOfNextChunk = 0;

	long lastMessageSent = 0;

	int numTooSlowPackets = 0;

	Message toSend;

	boolean abortDownload = false;

	long firstByteReceivedAt = 0;

	public TransferDocumentOperation(
			OperationCallback<List<AbstractChordContact>> callback,
			AbstractChordNode component, AbstractChordContact sender,
			AbstractChordContact receiver,
			ChordKey objectId) {
		super(component, callback);
		this.sender = sender;
		this.receiver = receiver;
		this.owner = component;

		this.objectId = objectId;

	}

	private void sendMessage() {

		// first calculate current datarate to have an approx for the next
		// timeout!
		if (lastMessageSent != 0l) {
			// calc datarate:
			double duration = (Simulator.getCurrentTime() - firstByteReceivedAt)
					/ (double) Simulator.SECOND_UNIT;

			currentDataRate = receivedData / duration;

			if (currentDataRate < ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE) {
				numTooSlowPackets++;

				if (numTooSlowPackets > 3) {
					// abort download! got 3 too slow packets in a row...
					operationFinished(false);
					abortDownload = true;
					return;
				}

			} else {
				numTooSlowPackets = 0;
			}

		}
		lastMessageSent = Simulator.getCurrentTime();

		// calculate timeout:
		long timeout = (long) (sizeOfNextChunk / currentDataRate);
		if (timeout < 1) {
			timeout = 1;
		}
		timeout *= 3;

		this.owner.getTransLayer().sendAndWait(toSend, receiver.getTransInfo(),
				sender.getTransInfo().getPort(),
				ChordConfiguration.TRANSPORT_PROTOCOL, this,
				timeout * Simulator.SECOND_UNIT);
	}

	@Override
	protected void execute() {

		InitializeDownloadMessage msg = new InitializeDownloadMessage(
				this.sender, this.receiver, this.objectId);
		toSend = msg;

		sendMessage();
	}

	@Override
	public List<AbstractChordContact> getResult() {
		if (this.isSuccessful()) {
			return new LinkedList<AbstractChordContact>();
		} else {
			return null;
		}
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {

		if (abortDownload) {
			return;
		}

		numRetrys = 0;

		// update chunk size
		if (sizeOfNextChunk < ChordConfiguration.MAX_DOWNLOAD_CHUNK_SIZE) {
			sizeOfNextChunk *= 2;

			if (sizeOfNextChunk > ChordConfiguration.MAX_DOWNLOAD_CHUNK_SIZE) {
				sizeOfNextChunk = ChordConfiguration.MAX_DOWNLOAD_CHUNK_SIZE;
			}
		}

		// get init reply:
		if (msg instanceof InitializeDownloadReplyMessage) {
			InitializeDownloadReplyMessage reply = (InitializeDownloadReplyMessage) msg;
			totalData = reply.getDocumentSize();

			sizeOfNextChunk = ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE;
			currentDataRate = ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE; // assume
			// a
			// minimum
			// datarate
			// of
			// 1024
			// bit
			// -
			// if
			// smaller,
			// abort
			// download!

			// start Download:
			StartDownloadMessage sdm = new StartDownloadMessage(this.sender,
					this.receiver, this.objectId, totalData, sizeOfNextChunk);
			toSend = sdm;

			sendMessage();

		}

		// receive payload:
		if (msg instanceof TransferDownloadMessage) {
			TransferDownloadMessage tdm = (TransferDownloadMessage) msg;

			this.receivedData += tdm.getPayloadSize();

			// if download not finished: send reply.
			if (this.receivedData < this.totalData) {

				if (firstByteReceivedAt == 0) {
					firstByteReceivedAt = Simulator.getCurrentTime();
				}

				if (receivedData + sizeOfNextChunk > totalData) {
					sizeOfNextChunk = totalData - receivedData;
				}

				TransferDownloadReplyMessage rep = new TransferDownloadReplyMessage(
						this.sender, this.receiver, this.objectId,
						sizeOfNextChunk, totalData);
				toSend = rep;
				sendMessage();
			} else {
				// download finished!
				operationFinished(true);

			}

		}

	}

	@Override
	public void messageTimeoutOccured(int commId) {

		if (sizeOfNextChunk > ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE) {
			sizeOfNextChunk /= 2;

			if (sizeOfNextChunk < ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE) {
				sizeOfNextChunk = ChordConfiguration.INIT_DOWNLOAD_CHUNK_SIZE;
			}
		}

		if (!this.isFinished()) {
			if (numRetrys > ChordConfiguration.MESSAGE_RESEND) {
				// download failed!
				operationFinished(false);
				abortDownload = true;

			} else {
				numRetrys++;
				sendMessage();
			}
		}
	}

}
