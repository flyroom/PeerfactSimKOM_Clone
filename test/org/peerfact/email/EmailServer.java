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
 * Email server ccan store emails. All communication with the server should take
 * place over the network.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 30.11.2007
 * 
 */
public interface EmailServer extends TransMessageListener {
	/**
	 * By default the server will listen at this port for incoming connection
	 * requests.
	 */
	public final static short DEFAULT_PORT = 25;

	/**
	 * Set the link to the network layer.
	 * 
	 * @param transLayer
	 *            - network layer
	 */
	public void setTransLayer(TransLayer transLayer);

	/**
	 * List all e-mails which were stored on the server but not fetched by the
	 * users yet.
	 * 
	 * @return list of e-mails (e-mail text only)
	 */
	public Collection<String> listEmails();

	/**
	 * @return the address at which the server waits for incoming requests
	 */
	public TransInfo getAddress();

}
