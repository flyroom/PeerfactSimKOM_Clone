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

package org.peerfact.email.solution;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.email.Email;
import org.peerfact.email.EmailServer;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


class EmailServerImpl implements EmailServer {
	private static final Logger log = SimLogger
			.getLogger(EmailClientImpl.class);

	private static final short PORT = 143;

	TransLayer transLayer;

	List<Email> emails = new LinkedList<Email>();

	@Override
	public Collection<String> listEmails() {
		Set<String> texts = new LinkedHashSet<String>();
		for (Email email : emails) {
			texts.add(email.getText());
		}
		return texts;
	}

	@Override
	public void setTransLayer(TransLayer transLayer) {
		this.transLayer = transLayer;
		transLayer.addTransMsgListener(this, PORT);
	}

	@Override
	public TransInfo getAddress() {
		return transLayer.getLocalTransInfo(PORT);
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Object payload = receivingEvent.getPayload();
		System.err.println("Received: " + payload);
		if (payload instanceof Email) {
			Email email = (Email) payload;
			emails.add(email);
		}
		if (payload instanceof EmailRequestImpl) {
			EmailRequestImpl req = (EmailRequestImpl) payload;
			String user = req.getUsername();
			Set<Email> reply = new LinkedHashSet<Email>();
			for (Email email : emails) {
				if (email.getTo().equals(user)) {
					log.debug("email " + email + " match user " + user);
					reply.add(email);
				}
			}
			transLayer.send(new EmailReplyImpl(reply), receivingEvent
					.getSenderTransInfo(), PORT, TransProtocol.UDP);
		}

	}

}
