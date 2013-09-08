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

package org.peerfact.email;

import java.util.Collection;

import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;


/**
 * Email client for sending and fetching emails.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 30.11.2007
 * 
 */
public interface EmailClient extends TransMessageListener {
	/**
	 * Link the client with the lower layer, which will be used to send messages
	 * throug the network.
	 * 
	 * @param transLayer
	 *            network layer
	 */
	public void setTransLayer(TransLayer transLayer);

	/**
	 * Set the address of the server, which will be used to send and fetch
	 * emails.
	 * 
	 * @param serverAddress
	 *            network address of the server
	 */
	public void setServerAddress(TransInfo serverAddress);

	/**
	 * Send an email on behalf of the user.
	 * 
	 * @param from
	 *            - the name of the sending user
	 * @param to
	 *            - the name of the user who will receive the message
	 * @param text
	 *            - the content of the email
	 */
	public void sendEmail(String from, String to, String text);

	/**
	 * Fetch all emails from the server and store them in the client.
	 * 
	 * @param username
	 *            - users name (the receiver of emails)
	 */
	public void fetchEmail(String username);

	/**
	 * List all clients fetched from server and stored locally.
	 * 
	 * @return list of fetched emails.
	 */
	public Collection<String> listEmails();

}
