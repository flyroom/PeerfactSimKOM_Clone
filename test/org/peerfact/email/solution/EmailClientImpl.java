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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.email.Email;
import org.peerfact.email.EmailClient;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Example solution.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 30.11.2007
 * 
 */
public class EmailClientImpl implements EmailClient {
	private static final Logger log = SimLogger
			.getLogger(EmailClientImpl.class);

	TransLayer transLayer;

	private TransInfo serverAddress;

	static short port = 100;

	Set<String> emails = new LinkedHashSet<String>();

	@Override
	public void sendEmail(String from, String to, String text) {
		transLayer.send(new Email(from, to, text), serverAddress, port,
				TransProtocol.UDP);
	}

	@Override
	public void setServerAddress(TransInfo serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public void setTransLayer(TransLayer transLayer) {
		this.transLayer = transLayer;
		transLayer.addTransMsgListener(this, port);
	}

	@Override
	public void fetchEmail(String username) {
		transLayer.send(new EmailRequestImpl(username), serverAddress, port,
				TransProtocol.UDP);
	}

	@Override
	public Collection<String> listEmails() {
		return Collections.unmodifiableSet(emails);
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		log.info("Client received " + receivingEvent.getPayload());
		Object payload = receivingEvent.getPayload();
		if (payload instanceof EmailReplyImpl) {
			EmailReplyImpl reply = (EmailReplyImpl) payload;
			for (Email email : reply.getEmails()) {
				emails.add(email.getText());
			}
		}
	}

}
