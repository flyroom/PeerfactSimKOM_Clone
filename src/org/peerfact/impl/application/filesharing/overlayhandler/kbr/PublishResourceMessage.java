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

package org.peerfact.impl.application.filesharing.overlayhandler.kbr;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayKey;

/**
 * This message is sent to the responsible node, if a resource shall be
 * published in the overlay. Furthermore, this message is relayed from the
 * receiver node as a <b>replication</b> message to the nodes responsible for
 * holding replicas of the given key according to the specific KBR application.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PublishResourceMessage extends FilesharingMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -482871406451590137L;

	long queryUID;

	OverlayKey<?> key2publish;

	OverlayContact<?> initiator;

	/**
	 * Declares this message as a replication message.
	 */
	boolean replicationFlag = false;

	public boolean hasReplicationFlag() {
		return replicationFlag;
	}

	/**
	 * Sets whether this message is a replication message, i.e. a message sent
	 * from the node responsible for the given key to each node of its replica
	 * set.
	 * 
	 * @param replicationFlag
	 */
	public void setReplicationFlag(boolean replicationFlag) {
		this.replicationFlag = replicationFlag;
	}

	/**
	 * Default Constructor
	 * 
	 * @param key2publish
	 *            : the key that shall be published
	 * @param initiator
	 *            : the initiator of this publish, i.e. the contact that sent
	 *            this message, if this is not a replication message.
	 */
	public PublishResourceMessage(OverlayKey<?> key2publish,
			OverlayContact<?> initiator) {
		super();
		this.queryUID = generateQueryUID();
		this.key2publish = key2publish;
		this.initiator = initiator;
	}

	@Override
	public Message getPayload() {
		return this;
	}

	@Override
	public long getSize() {
		return 6;
	}

	/**
	 * Returns the UID that uniquely identifies the publish being made.
	 * 
	 * @return
	 */
	public long getQueryUID() {
		return queryUID;
	}

	/**
	 * Returns the key that shall be published
	 * 
	 * @return
	 */
	public OverlayKey<?> getKey2publish() {
		return key2publish;
	}

	/**
	 * Returns the initiator of this publish, i.e. the contact that sent this
	 * message, if this is not a replication message.
	 * 
	 * @return
	 */
	public OverlayContact<?> getInitiator() {
		return initiator;
	}

	@Override
	public String toString() {
		return "(Publish : " + key2publish + " init: "
				+ initiator.getTransInfo().getNetId() + ")";
	}

}
