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

package org.peerfact.impl.transport;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.transport.DefaultTransLayer;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.util.ComponentTest;


/**
 * Test DefaultTransLayer implementation.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 30.11.2007
 * 
 */
public class DefaultTransLayerTest extends ComponentTest {

	List<Message> requests = new LinkedList<Message>();

	List<Message> replies = new LinkedList<Message>();

	TransLayer trans1, trans2, trans3;

	final static short port = 100;

	DummyMessage reqMsg;

	DummyMessage replyMsg;

	// DummyMessage msg1, msg2, msg3, msg4, msg5;

	static class DummyMessage implements Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3549810386550010012L;

		String value;

		DummyMessage(String value) {
			this.value = value;
		}

		@Override
		public Message getPayload() {
			return null;
		}

		@Override
		public long getSize() {
			return value.length();
		}

		@Override
		public String toString() {
			return "<" + value + ">";
		}
	}

	// private SimpleNetworkFactory netFactory;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		trans1 = createHostWithNetWrapperAndTransLayer();
		trans2 = createHostWithNetWrapperAndTransLayer();
		trans3 = createHostWithNetWrapperAndTransLayer();
		reqMsg = new DummyMessage("Request");
		replyMsg = new DummyMessage("Reply");
	}

	TransLayer createHostWithNetWrapperAndTransLayer() {
		DefaultHost host = createEmptyHost();
		createHostProperties(host);
		NetLayer net = createNetworkWrapper(host);
		TransLayer trans = new DefaultTransLayer(net);
		host.setTransport(trans);
		trans.addTransMsgListener(getTransListener(), port);
		return trans;
	}

	protected TransMessageListener getTransListener() {
		return new TransMessageListener() {

			@Override
			public void messageArrived(TransMsgEvent receivingEvent) {
				DefaultTransLayerTest.log.debug("Received "
						+ receivingEvent.getPayload() + " from "
						+ receivingEvent.getSenderTransInfo());
				Assert
						.assertTrue(receivingEvent.getPayload() instanceof DummyMessage);
				requests.add(receivingEvent.getPayload());
				trans2.sendReply(replyMsg, receivingEvent, port,
						TransProtocol.UDP);
			}

		};
	}

	protected TransMessageCallback getTransCallback() {
		return new TransMessageCallback() {

			@Override
			public void messageTimeoutOccured(int commId) {
				fail("Unexpected message timeout");
			}

			@Override
			public void receive(Message msg, TransInfo senderAddr, int commId) {
				replies.add(msg);
			}

		};
	}

	/**
	 * Send one message which should arrive.
	 * 
	 */
	@Test
	public void testSendSingleMsg() {

		trans1.send(reqMsg, trans2.getLocalTransInfo(port), port,
				TransProtocol.UDP);

		runSimulation(milliseconds(100));

		assertEquals(1, requests.size());
		assertTrue(requests.contains(reqMsg));
	}

	/**
	 * Send one message which should arrive.
	 * 
	 */
	@Test
	public void testSendToItself() {
		trans1.send(reqMsg, trans1.getLocalTransInfo(port), port,
				TransProtocol.UDP);

		runSimulation(milliseconds(100));

		assertEquals(1, requests.size());
		assertTrue(requests.contains(reqMsg));
	}

	/**
	 * Send request and reply and expect them to arrive. (it's poetry, isn't
	 * it?).
	 * 
	 */
	@Test
	public void testRequestReply() {

		TransInfo adr1 = trans1.getLocalTransInfo(port);
		TransInfo adr2 = trans2.getLocalTransInfo(port);

		int commID = trans1.sendAndWait(reqMsg, adr2, port, TransProtocol.UDP,
				getTransCallback(), milliseconds(100));

		runSimulation(milliseconds(100));

		assertEquals(1, requests.size());
		assertTrue(requests.contains(reqMsg));
		assertEquals(1, replies.size());
		assertTrue(replies.contains(replyMsg));

	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		requests.clear();
	}

}
