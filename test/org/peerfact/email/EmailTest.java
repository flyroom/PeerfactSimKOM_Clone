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

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.email.solution.EmailFactoryImpl;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.util.ComponentTest;


/**
 * The concrete subclass should select an implementation of the email system
 * through decision which <code>EmailServer</code> and <code>EmailClient</code>
 * to use.
 * 
 * @author Konstantin Pussep
 * 
 */
// FIXME reimplement Email-example. And this test accordingly
public class EmailTest extends ComponentTest {


	EmailClient client1, client2, client3;

	EmailServer server;

	private String user1;

	String user2;

	private String user3;

	/**
	 * TODO select your email factory here.
	 */
	private EmailFactory emailFactory = new EmailFactoryImpl(); // = null;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		assertNotNull(emailFactory);
		server = createServer();
		client1 = createClient();
		TransInfo serverAddress = server.getAddress();
		client1.setServerAddress(serverAddress);
		client2 = createClient();
		client2.setServerAddress(serverAddress);
		client3 = createClient();
		client3.setServerAddress(serverAddress);

		user1 = "moe";
		user2 = "homer";
		user3 = "fry";
	}

	/**
	 * Create complete client instance
	 */
	EmailClient createClient() {
		DefaultHost host = createEmptyHost();
		createNetworkWrapper(host);
		TransLayer trans = createTransLayer(host);
		EmailClient client = emailFactory.createClient();
		client.setTransLayer(trans);
		return client;
	}

	/**
	 * Create a server.
	 */
	EmailServer createServer() {
		DefaultHost host = createEmptyHost();
		createNetworkWrapper(host);
		TransLayer trans = createTransLayer(host);
		EmailServer eMailServer = emailFactory.createServer();
		eMailServer.setTransLayer(trans);
		return eMailServer;
	}

	/**
	 * Test sending of a single message.
	 * 
	 */
	@Ignore
	@Test
	public void testSend1() {
		Assert.assertEquals(0, server.listEmails().size());
		String msg1 = "hm...";
		client1.sendEmail(user1, user1, msg1);
		Set<String> expected = new LinkedHashSet<String>();
		expected.add(msg1);

		runSimulation(milliseconds(1000));
		Assert.assertEquals(expected, server.listEmails());
	}

	/**
	 * Test sending of several messages from several clients.
	 * 
	 */
	@Ignore
	@Test
	public void testSendMany() {
		Assert.assertEquals(0, server.listEmails().size());

		// send first message
		String msg1 = "Want some beer?";
		client1.sendEmail(user1, user2, msg1);
		Set<String> expected = new LinkedHashSet<String>();
		expected.add(msg1);

		// send second message
		String msg2 = "Give me a beer, Moe!!!";
		client1.sendEmail(user2, user1, msg2);
		expected.add(msg2);

		// send third message
		String msg3 = "Hey, I already saw you before!?";
		client1.sendEmail(user3, user2, msg3);
		expected.add(msg3);

		runSimulation(milliseconds(1000));
		// Assert.assertEquals(expected.size(), server.listEmails().size());
		Assert.assertEquals(expected, server.listEmails());
	}

	/**
	 * Test sending and fetching of a single message.
	 * 
	 */
	@Test
	public void testSendAndRetrieve() {
		Assert.assertEquals(0, server.listEmails().size());

		// send an email
		String msg1 = "Want some beer?";
		client1.sendEmail(user1, user2, msg1);

		// fetch an email, 10 seconds after the emails were sent to the server
		// this is necessary to be sure, that the server will have stored them
		// already
		scheduleEvent(milliseconds(100), new SimulationEventHandler() {
			@Override
			public void eventOccurred(SimulationEvent se) {
				client2.fetchEmail(user2);
			}
		});

		// run simulation
		runSimulation(milliseconds(1000));

		Set<String> expected = new LinkedHashSet<String>();
		expected.add(msg1);
		Assert.assertEquals(expected, client2.listEmails());
	}

	/**
	 * Test sending and fetching of several messages.
	 * 
	 */
	@Ignore
	@Test
	public void testSendAndRetrieveMany() {
		Assert.assertEquals(0, server.listEmails().size());

		// send emails
		String msg1 = "Want some beer?";
		client1.sendEmail(user1, user2, msg1);
		String msg2 = "Why are you so yellow?";
		client3.sendEmail(user3, user2, msg2);

		// send more, email which is unrelated to the expected result
		client1.sendEmail(user1, user3, "What's up?");

		// fetch an email, 10 seconds after the emails were sent to the server
		// this is necessary to be sure, that the server will have stored them
		// already
		scheduleEvent(seconds(10), new SimulationEventHandler() {
			@Override
			public void eventOccurred(SimulationEvent se) {
				client2.fetchEmail(user2);
			}
		});

		// run simulation
		runSimulation(seconds(15000));

		// expect client 2 to receive msg1 and msg2
		Set<String> expected = new LinkedHashSet<String>();
		expected.add(msg1);
		expected.add(msg2);
		Assert.assertEquals(expected, client2.listEmails());
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

}
